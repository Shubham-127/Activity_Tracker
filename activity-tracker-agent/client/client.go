package client

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"

	"activity-agent/model"
)

const backendBaseURL = "http://localhost:8080"

type registerRequest struct {
	DeviceHash  string `json:"deviceHash"`
	OS          string `json:"os"`
	InstallToken string `json:"installToken"`
}

type registerResponse struct {
	DeviceID int64  `json:"deviceId"`
	JWT      string `json:"jwt"`
}

func Register(deviceHash, os, installToken string) (int64, string, error) {
	reqBody := registerRequest{
		DeviceHash:   deviceHash,
		OS:           os,
		InstallToken: installToken,
	}

	body, err := json.Marshal(reqBody)
	if err != nil {
		return 0, "", fmt.Errorf("failed to marshal register request: %w", err)
	}

	resp, err := http.Post(backendBaseURL+"/api/v1/devices/register", "application/json", bytes.NewBuffer(body))
	if err != nil {
		return 0, "", fmt.Errorf("register request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return 0, "", fmt.Errorf("registration failed with status: %s", resp.Status)
	}

	var result registerResponse
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return 0, "", fmt.Errorf("failed to decode register response: %w", err)
	}

	return result.DeviceID, result.JWT, nil
}

func SendBatch(events []model.ActivityEvent, jwt string) error {
	body, err := json.Marshal(events)
	if err != nil {
		return fmt.Errorf("failed to marshal events: %w", err)
	}

	req, err := http.NewRequest("POST", backendBaseURL+"/api/v1/activity/batch", bytes.NewBuffer(body))
	if err != nil {
		return fmt.Errorf("failed to build request: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+jwt)

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		return fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	fmt.Println("Backend responded with status:", resp.Status)
	return nil
}