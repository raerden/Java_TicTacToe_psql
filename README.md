# 🎮 Tic-Tac-Toe Game

A web-based implementation of the classic Tic-Tac-Toe game with computer opponent using the **Minimax algorithm**. Built with Spring Boot, layered architecture (MVC), and PostgreSQL for data persistence.

---

## 📋 Table of Contents

1. [Features](#1-features)
2. [Technology Stack](#2-technology-stack)
3. [Getting Started](#3-getting-started)
4. [API Endpoints](#4-api-endpoints)
5. [Game Flow](#5-game-flow)
6. [Project Structure](#6-project-structure)
7. [Database Schema](#7-database-schema)
8. [Testing with cURL](#8-testing-with-curl)
9. [AI Algorithm](#9-ai-algorithm)
10. [Future Enhancements](#10-future-enhancements)
11. [License](#11-license)

---

## 1. Features

- Play against an AI-powered computer opponent
- Clean **multi-layer architecture** (web, domain, datasource, DI)
- **RESTful API** for all game operations
- **PostgreSQL** database for persistent game storage
- **Minimax algorithm** for optimal computer moves
- **Stateless** game validation to prevent cheating
- Multiple simultaneous games support
- Board stored as compact string format (9 characters)

---

## 2. Technology Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 17+, Spring Boot 3.x |
| **Web** | Spring MVC, REST Controllers |
| **Database** | PostgreSQL, Spring Data JPA, Hibernate |
| **Build Tool** | Gradle (Kotlin DSL) |
| **Server** | Embedded Tomcat |
| **DI Container** | Spring IoC (Annotation + Java Config) |

---

## 3. Getting Started
1. Clone the repository
2. Run `mvn spring-boot:run` or `./gradlew bootRun`
3. Open `http://localhost:8080` in your browser
4. GET http://localhost:8080/game

### 3.1 Prerequisites

- Java 17 or higher
- PostgreSQL 14+ (running locally)

### 3.2 Installation

**Step 1: Clone the repository**
``` bash
git clone https://github.com/raerden/tictactoe.git
cd tictactoe
```

**Step 2: Configure PostgreSQL**
- Create a database named *tictactoe*
- Update src/main/resources/application.properties with your credentials:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/tictactoe
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Step 3: Run the application**
```./gradlew bootRun```

**Step 4: Open your browser**
- Main page: http://localhost:8080/
- Create new game: GET http://localhost:8080/game

## 4. API Endpoints

### 4.1 Endpoints Overview

| Method | Endpoint | Description | Authentication | Request Body | Response |
|--------|----------|-------------|----------------|--------------|----------|
| `GET` | `/` | Health check | ❌ No | - | `"Крестики-нолики запущены!"` |
| `GET` | `/game` | Create a new game | ❌ No | - | `GameDto` |
| `GET` | `/game/{id}` | Get game state by UUID | ❌ No | - | `GameDto` |
| `POST` | `/game/{id}` | Make a move | ❌ No | `GameDto` | `GameDto` |

### 4.2 Data Models

#### GameDto Structure

```
json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "board": {
        "matrix": [
            [1, 0, 2],
            [0, 1, 0],
            [0, 0, 1]
        ]
    },
    "currentPlayer": 1,
    "winner": 0,
    "gameOver": false
}
```

### 4.1 Create New Game

curl -X GET http://localhost:8080/game

Response (200 OK):
```
json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "board": {
        "matrix": [
            [0, 0, 0],
            [0, 0, 0],
            [0, 0, 0]
        ]
    },
    "currentPlayer": 1,
    "winner": 0,
    "gameOver": false
}
```

### 4.3.2 Get Game State

curl -X GET http://localhost:8080/game/550e8400-e29b-41d4-a716-446655440000

Response (200 OK): 
```
json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "board": {
        "matrix": [
            [1, 0, 2],
            [0, 1, 0],
            [0, 0, 0]
        ]
    },
    "currentPlayer": 1,
    "winner": 0,
    "gameOver": false
}
```

### 4.3.3 Make a Move

POST /game/{id}


**Request Body: Full GameDto with player's move applied**

```
curl -X POST http://localhost:8080/game/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "board": {
        "matrix": [
            [1, 0, 0],
            [0, 1, 0],
            [0, 0, 0]
        ]
    },
    "currentPlayer": 1,
    "winner": 0,
    "gameOver": false
}'
```

**Response (200 OK) — Game state after computer's move:**

```
json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "board": {
        "matrix": [
            [1, 0, 2],
            [0, 1, 0],
            [0, 0, 0]
        ]
    },
    "currentPlayer": 1,
    "winner": 0,
    "gameOver": false
}
```