An application focused on user authorization. Implemented with Spring Boot backend, React frontend, and MySQL database.

Maven

Backend Setup
bash
cd backend
# Configure database in application.properties
mvn clean install
mvn spring-boot:run
# Or run EstreraApplication.java directly
Frontend Setup
bash
cd frontend
npm install
npm run dev


ENDPOINTS

 /api/auth/register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"your_username\",\"email\":\"your_email@example.com\",\"password\":\"YourSecurePass123!\"}"

 /api/auth/login
curl -X POST http://localhost:8080/api/auth/login \ -H "Content-Type: application/json" \ -d "{\"usernameOrEmail\":\"your_username\",\"password\":\"YourSecurePass123!\"}"

 /api/user/me (protected)
