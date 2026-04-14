# 🚀 Silicon Valley Trail Game

A full-stack simulation game where players guide a startup team through Silicon Valley, managing resources and making strategic decisions to successfully reach San Francisco.

---

## 🧠 Quick Start

### 1. Clone the repository
git clone https://github.com/your-username/your-repo-name.git  
cd your-repo-name

### 2. Open in IntelliJ

### 3. Configure Database (PostgreSQL)

Update your application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/your_db  
spring.datasource.username=your_username  
spring.datasource.password=your_password  
spring.jpa.hibernate.ddl-auto=update

### 4. Run the Application

Run the main Spring Boot application:
SiliconValGameApplication

### 5. Open the Frontend

http://localhost:8080/index.html

---

## 🔑 API Keys / Running with Mocks

This project uses the Open-Meteo API, which does NOT require an API key.

If the API fails:
- The game safely falls back to "Weather unavailable"
- Gameplay continues without crashing

---

## 🏗️ Architecture Overview

This project follows a layered architecture:

- Controller Layer:  
  Handles HTTP requests and exposes endpoints

- Service Layer:  
  Contains all business logic (game loop, moves, events)

- Repository Layer:  
  Uses Spring Data JPA for database access

- DTO Layer:  
  Sends only necessary data to the frontend (hides internal fields like IDs)

---

## 📦 Dependencies

- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- Lombok
- JUnit 5
- Mockito

---

## 🧪 How to Run Tests

Using Maven:
mvn test

Or in IntelliJ:
Right-click test class → Run

Tests cover:
- Game logic
- Resource updates
- Win/Loss conditions

---

## 🎮 Example Commands / Inputs

### Start Game
POST /api/v1/games

### Get Game State
GET /api/v1/games/{id}

### Make Move
POST /api/v1/games/{id}/move

Request Body:
{
"move": "TRAVEL"
}

Available Moves:
- TRAVEL
- REST
- WORK
- MARKETING

---

## 🤖 AI Usage

AI was used as a development aid to:
- Understand backend architecture patterns
- Debug issues during development
- Improve structure and readability

All code and logic were reviewed and adjusted manually.

---

## 📝 Design Notes

### 🎮 Game Loop & Balance

Each turn:
1. Player selects an action
2. Resources update
3. Random event may occur
4. Weather affects gameplay

Trade-offs:
- Work reduces bugs but lowers morale
- Marketing increases hype but costs money
- Travel progresses the game but adds risk

---

### 🌦️ API Choice & Gameplay Impact

The Open-Meteo API was chosen because:
- Free and no API key required
- Provides real-time weather data

Weather affects gameplay:
- High wind → reduces coffee
- Low temperature → reduces morale
- Clear weather → boosts morale

Weather is also displayed to the user.

---

### 🗃️ Data Modeling

Game state includes:
- Cash
- Morale
- Coffee
- Hype
- Bugs

Location is tracked internally but displayed as city name.

Game status:
- IN_PROGRESS
- WON
- LOST

Enums are used for moves and events.

Game sessions are stored in a database.

---

### ⚠️ Error Handling

- Weather API failures return "Weather unavailable"
- Null checks prevent crashes
- Game continues even if API fails

---

### 🔄 Tradeoffs & “If I Had More Time”

Tradeoffs:
- Weather API is called multiple times
- UI is simple for faster development

If I had more time:
- Cache weather data
- Improve UI/UX
- Add more dynamic events
- Add user accounts and persistent saves

---

## 🧪 Tests

Unit tests cover:
- Core game mechanics
- Resource updates
- Game-ending conditions

Mockito is used to mock dependencies.

---

## 🏁 Summary

This project demonstrates:
- Full-stack development
- Clean architecture
- API integration
- Unit testing
- Game logic design

---

## 👨‍💻 Author

Abdule Touray