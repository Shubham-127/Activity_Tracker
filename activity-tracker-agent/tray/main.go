package main

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/getlantern/systray"
)

func main() {
	systray.Run(onReady, onExit)
}

func pauseFlagPath() string {
	dir, _ := os.UserConfigDir()
	return filepath.Join(dir, "activity-agent", "paused.flag")
}

func onReady() {
	systray.SetTitle("Activity Tracker")
	systray.SetTooltip("Activity Tracker Agent — monitoring active")

	mStatus := systray.AddMenuItem("Monitoring: Active", "Current status")
	mStatus.Disable()

	systray.AddSeparator()

	mPause := systray.AddMenuItem("Pause Monitoring", "Temporarily pause tracking")
	mQuit := systray.AddMenuItem("Quit", "Close this tray icon")

	paused := false

	go func() {
		for {
			select {
			case <-mPause.ClickedCh:
				paused = !paused
				if paused {
					os.WriteFile(pauseFlagPath(), []byte("paused"), 0600)
					mPause.SetTitle("Resume Monitoring")
					mStatus.SetTitle("Monitoring: Paused")
					fmt.Println("Monitoring paused by user")
				} else {
					os.Remove(pauseFlagPath())
					mPause.SetTitle("Pause Monitoring")
					mStatus.SetTitle("Monitoring: Active")
					fmt.Println("Monitoring resumed by user")
				}
			case <-mQuit.ClickedCh:
				systray.Quit()
				return
			}
		}
	}()
}

func onExit() {
	// cleanup if needed when tray icon closes
}