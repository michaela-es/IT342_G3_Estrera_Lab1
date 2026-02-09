
An application with Spring Boot backend and React frontend for user authentication.
---

## Backend Setup

```bash
cd backend

# Configure your database in application.properties

# Build and run the backend
mvn clean install
mvn spring-boot:run

# Or run directly via IDE: EstreraApplication.java
```

---

## Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Run the frontend
npm run dev
```

---

## API Endpoints

### 1. Register a new user

**POST** `/api/auth/register`

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
        "username": "your_username",
        "email": "your_email@example.com",
        "password": "YourSecurePass123!"
      }'
```

---

### 2. Login

**POST** `/api/auth/login`

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
        "usernameOrEmail": "your_username",
        "password": "YourSecurePass123!"
      }'
```

---

### 3. Get current user (protected)

**GET** `/api/user/me`

TBA
