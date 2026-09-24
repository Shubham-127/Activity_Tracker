package model
 
import "time"

type ActivityEvent struct{
	EventID string   `json:"eventId"`
	AppName string   `json:"appName"`
	WindowTitle string `json:"windowTitle"`
	Domain string `json:"domain"`
	StartedAt time.Time `json:"startedAt"`
	EndedAt     time.Time `json:"endedAt"`
	Idle        bool      `json:"idle"`

}