package buffer

import (
	"sync"

	"activity-agent/model"
)

type EventBuffer struct {
	mu     sync.Mutex
	events []model.ActivityEvent
}

func New() *EventBuffer {
	return &EventBuffer{
		events: make([]model.ActivityEvent, 0),
	}
}

func (b *EventBuffer) Add(event model.ActivityEvent) {
	b.mu.Lock()
	defer b.mu.Unlock()
	b.events = append(b.events, event)
}

func (b *EventBuffer) DrainAll() []model.ActivityEvent {
	b.mu.Lock()
	defer b.mu.Unlock()

	drained := b.events
	b.events = make([]model.ActivityEvent, 0)
	return drained
}