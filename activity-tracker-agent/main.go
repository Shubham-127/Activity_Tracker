package main

import (
	"fmt"
	"log"
	"os"
	"os/exec"
	"path/filepath"
	"time"

	"activity-agent/buffer"
	"activity-agent/client"
	"activity-agent/config"
	"activity-agent/model"

	"github.com/google/uuid"
)

const installToken = "agent-clean-run"
const taskName = "ActivityTrackerAgent"

func main() {
	logFile, err := os.OpenFile("C:\\ActivityAgentConfig\\agent.log", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err == nil {
		log.SetOutput(logFile)
		defer logFile.Close()
	}

	if len(os.Args) > 1 {
		switch os.Args[1] {
		case "install":
			installTask()
			return
		case "uninstall":
			uninstallTask()
			return
		case "start":
			startTask()
			return
		case "stop":
			stopTask()
			return
		}
	}

	runAgent()
}

func exePath() string {
	path, err := os.Executable()
	if err != nil {
		log.Fatal("Failed to get executable path: ", err)
	}
	return path
}

func installTask() {
	cmd := exec.Command("schtasks", "/Create",
		"/TN", taskName,
		"/TR", "\""+exePath()+"\"",
		"/SC", "ONLOGON",
		"/RL", "LIMITED",
		"/F")
	output, err := cmd.CombinedOutput()
	if err != nil {
		fmt.Println(string(output))
		log.Fatal("Install failed: ", err)
	}
	fmt.Println("Task installed successfully.")
}

func uninstallTask() {
	cmd := exec.Command("schtasks", "/Delete", "/TN", taskName, "/F")
	output, err := cmd.CombinedOutput()
	if err != nil {
		fmt.Println(string(output))
		log.Fatal("Uninstall failed: ", err)
	}

	// schtasks /Create defaults to requiring AC power - disable that
	// so the agent actually runs on laptops running on battery.
	psCmd := exec.Command("powershell", "-NoProfile", "-Command",
		`$s = New-ScheduledTaskSettingsSet -DisallowStartIfOnBatteries:$false -StopIfGoingOnBatteries:$false; `+
			`Set-ScheduledTask -TaskName "`+taskName+`" -Settings $s`)
	if psOut, err := psCmd.CombinedOutput(); err != nil {
		fmt.Println(string(psOut))
		log.Println("Warning: failed to clear battery restriction:", err)
	}
	fmt.Println("Task uninstalled successfully.")
}

func startTask() {
	cmd := exec.Command("schtasks", "/Run", "/TN", taskName)
	output, err := cmd.CombinedOutput()
	if err != nil {
		fmt.Println(string(output))
		log.Fatal("Start failed: ", err)
	}
	fmt.Println("Task started.")
}

func stopTask() {
	cmd := exec.Command("schtasks", "/End", "/TN", taskName)
	output, err := cmd.CombinedOutput()
	if err != nil {
		fmt.Println(string(output))
		log.Fatal("Stop failed: ", err)
	}
	fmt.Println("Task stopped.")
}

func runAgent() {

	cfg, err := config.Load()
	if err != nil {
		log.Fatal("Failed to load config: ", err)
	}

	if cfg == nil {
		deviceID, jwt, err := client.Register("real-agent-device-2", "windows", installToken)
		if err != nil {
			log.Fatal("Registration failed: ", err)
		}
		cfg = &config.AgentConfig{DeviceID: deviceID, JWT: jwt}
		if err := config.Save(cfg); err != nil {
			log.Fatal("Failed to save config: ", err)
		}
		log.Println("Registered. Device ID:", cfg.DeviceID)
	} else {
		log.Println("Loaded existing config. Device ID:", cfg.DeviceID)
	}

	buf := buffer.New()
	stop := make(chan struct{})

	go watchActiveWindow(buf, stop)
	go sendLoop(buf, cfg.JWT, stop)

	select {}
}

func isPaused() bool {
	path := pauseFlagPath()
	_, err := os.Stat(path)
	return err == nil
}

func pauseFlagPath() string {
	dir, _ := os.UserConfigDir()
	return filepath.Join(dir, "activity-agent", "paused.flag")
}

func watchActiveWindow(buf *buffer.EventBuffer, stop chan struct{}) {
	var lastTitle string
	var lastStart time.Time

	ticker := time.NewTicker(2 * time.Second)
	defer ticker.Stop()

	lastTitle = getActiveWindowTitle()
	lastStart = time.Now()

	for {
		select {
		case <-stop:
			return
		case <-ticker.C:
			if isPaused() {
				continue
			}
			currentTitle := getActiveWindowTitle()
			idle := getIdleDuration()
			log.Println("Detected title:", currentTitle, "| idle:", idle)

			if currentTitle != lastTitle {
				event := model.ActivityEvent{
					EventID:     uuid.New().String(),
					AppName:     lastTitle,
					WindowTitle: lastTitle,
					Domain:      "",
					StartedAt:   lastStart,
					EndedAt:     time.Now(),
					Idle:        idle > 5*time.Minute,
				}
				buf.Add(event)
				lastTitle = currentTitle
				lastStart = time.Now()
			}
		}
	}
}

func sendLoop(buf *buffer.EventBuffer, jwt string, stop chan struct{}) {
	ticker := time.NewTicker(60 * time.Second)
	defer ticker.Stop()

	for {
		select {
		case <-stop:
			return
		case <-ticker.C:
			events := buf.DrainAll()
			if len(events) == 0 {
				continue
			}
			log.Println("Attempting to send", len(events), "events")
			if err := client.SendBatch(events, jwt); err != nil {
				log.Println("Send failed, re-buffering:", err)
				for _, e := range events {
					buf.Add(e)
				}
			} else {
				log.Println("Batch sent successfully")
			}
		}
	}
}
