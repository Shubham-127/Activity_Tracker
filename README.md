# Activity Tracker

Activity Tracker is a system that collects user/system activity through a lightweight **Agent** and sends the activity data to a centralized **Backend** for processing and storage.

> **Note:** This project contains only the **Backend** and **Agent**. No dashboard/frontend is included.

## Architecture

```text
Agent
  │
  │ REST API
  ▼
Backend
  │
  ▼
Database
```

## Components

### Agent

* Collects activity data
* Creates activity events
* Sends events to the Backend
* Handles API communication and retries

### Backend

* Provides REST APIs
* Authenticates Agents
* Validates and processes activity data
* Stores activity records in the database

## Tech Stack

**Backend**

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* Spring Security / JWT
* PostgreSQL
* Maven

**Agent**

* [Agent Technology]
* REST API Client

## Project Structure

```text
activity-tracker/
├── backend/
└── agent/
```

## Activity Flow

```text
User/System Activity
        ↓
      Agent
        ↓
   REST API Request
        ↓
     Backend
        ↓
    PostgreSQL
```

## API

### Send Activity

```http
POST /api/v1/activities
```

Example:

```json
{
  "activityType": "APPLICATION",
  "application": "IntelliJ IDEA",
  "action": "ACTIVE",
  "timestamp": "2026-09-24T17:30:00Z"
}
```

## Running the Project

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Agent

```bash
cd agent
# Configure backend URL
# Start the agent
```

## Scope

* ✅ Activity Agent
* ✅ Backend REST API
* ✅ Authentication
* ✅ Activity processing
* ✅ Database persistence


## Author

**Shubham Srivastava**
