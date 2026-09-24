package config

import (
	"encoding/json"
	"os"
	
	"path/filepath"
)

type AgentConfig struct{
	DeviceID int64 `json:"deviceId"`
	JWT string `json:"jwt"`
}
func configPath() (string, error) {
	appDir := `C:\ActivityAgentConfig`
	if err := os.MkdirAll(appDir, 0700); err != nil {
		return "", err
	}
	return filepath.Join(appDir, "config.json"), nil
}

func Load() (*AgentConfig, error){
	path, err := configPath()
	if err != nil {
		return nil, err
	}

	data, err := os.ReadFile(path)
	if err != nil{
		if os.IsNotExist(err){
			return nil, nil
		}
		return nil, err
	}

	var cfg AgentConfig
	if err := json.Unmarshal(data, &cfg); err != nil{
		return nil, err
	}
	return &cfg, nil
}
func Save(cfg *AgentConfig)error{
	path, err := configPath()
	if err != nil{
		return err
	}

	data, err := json.MarshalIndent(cfg, "", " ")
	if err != nil {
		return err
	}
	return os.WriteFile(path, data, 0600)

}