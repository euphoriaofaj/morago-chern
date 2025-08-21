# MORAGO Backend

**MORAGO** is a mobile and web application designed to assist foreigners living in Korea who do not speak the Korean language. The app connects users with freelance interpreters and provides on-demand voice translation services via internet calls.

## 📦 Features

- **User registration & authentication** via JWT  
- **Profile creation & role management** (user / interpreter / admin)  
- **Call setup & processing** between users and interpreters  
- **Real-time notifications** via WebSocket (Socket.io)  
- **File storage & upload** (documents, avatars) using AWS S3 or local filesystem  
- **API documentation** with Swagger  
- **WebRTC** for voice calls  
- Deployment on free hosting (e.g., Heroku, Railway)

## 🛠 Technology Stack

| Layer            | Technologies           |
| ---------------- | ---------------------- |
| **Language**     | Java                   |
| **Framework**    | Spring Boot            |
| **Database**     | MySQL                  |
| **API Type**     | REST                   |
| **Auth**         | JWT                    |
| **Docs**         | Swagger (OpenAPI 3)    |
| **Notifications**| WebSocket (Socket.io)  |
| **Calls**        | WebRTC                 |
| **Storage**      | AWS S3 / Local FS      |

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+ (for development and production)
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd morago-backend
   ```

2. **Set up environment variables**
   
   Create a `.env` file in the root directory or set environment variables:
   ```bash
   # Database Configuration
   DB_URL=jdbc:mysql://localhost:3306/morago_dev
   DB_USER=your_db_username
   DB_PASS=your_db_password
   
   # JWT Configuration
   JWT_ACCESS_SECRET=your-super-secret-access-key-here
   JWT_REFRESH_SECRET=your-super-secret-refresh-key-here
   JWT_ACCESS_EXPIRATION_MS=900000
   JWT_REFRESH_EXPIRATION_MS=604800000
   
   # Profile Configuration
   SPRING_PROFILES_ACTIVE=dev
   ```

3. **Build the project**
   ```bash
   ./mvnw clean compile
   ```

4. **Run the application**
   ```bash
   # Development mode
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   
   # Or with environment variable
   SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
   ```

## 🔧 Configuration Profiles

The application supports multiple profiles for different environments:

### Development Profile (`dev`)
- **File**: `application-dev.properties`
- **Features**: 
  - Debug logging enabled
  - Swagger UI available at `/swagger`
  - H2 console enabled (if using H2)
  - Hot reload with Spring DevTools
  - Detailed error messages

### Production Profile (`prod`)
- **File**: `application-prod.properties`
- **Features**:
  - Optimized for performance
  - Security headers enabled
  - Swagger UI disabled
  - Minimal logging
  - SSL support

### Test Profile (`test`)
- **File**: `application-test.properties`
- **Features**:
  - In-memory H2 database
  - Fast startup and teardown
  - Minimal logging
  - Optimized for unit/integration tests

## 🏃‍♂️ Running the Application

### Development Mode
```bash
# Using Maven wrapper
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Using environment variable
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

### Production Mode
```bash
# Build the JAR
./mvnw clean package -DskipTests

# Run with production profile
java -jar -Dspring.profiles.active=prod target/backend-0.0.1-SNAPSHOT.jar
```

### Testing
```bash
# Run all tests
./mvnw test

# Run tests with specific profile
./mvnw test -Dspring.profiles.active=test
```

## 📚 API Documentation

When running in development mode, API documentation is available at:
- **Swagger UI**: `http://localhost:8080/swagger`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Authentication
The API uses JWT (JSON Web Tokens) for authentication:

1. **Login**: `POST /auth/login`
   ```json
   {
     "username": "01012345673",
     "password": "123456"
   }
   ```

2. **Use the returned access token** in subsequent requests:
   ```
   Authorization: Bearer <your-access-token>
   ```

3. **Refresh token**: `POST /auth/refresh_token`
4. **Logout**: `POST /auth/logout`

## 🔌 WebSocket Endpoints

The application provides real-time communication via WebSocket:

### Connection Endpoints
- **SockJS**: `ws://localhost:8080/ws`
- **Native WebSocket**: `ws://localhost:8080/ws-native`

### Message Destinations
- **Call Signaling**: `/app/call.*`
- **Notifications**: `/app/notification.send`
- **Subscriptions**: 
  - `/queue/calls` (personal call messages)
  - `/queue/notifications` (personal notifications)
  - `/topic/call-room/{callId}` (call room broadcasts)
  - `/topic/notifications` (global notifications)

## 🗄️ Database Setup

### Development Database
```sql
CREATE DATABASE morago_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'morago_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON morago_dev.* TO 'morago_user'@'localhost';
FLUSH PRIVILEGES;
```

### Production Database
Follow your production database setup guidelines and ensure proper security measures.

## 🔒 Security Features

- **JWT Authentication** with access and refresh tokens
- **Role-based Authorization** (USER, TRANSLATOR, ADMIN)
- **Password Encryption** using BCrypt
- **CORS Configuration** for cross-origin requests
- **Security Headers** in production mode
- **Input Validation** using Bean Validation

## 🚀 Deployment

### Docker Deployment (Recommended)
```dockerfile
FROM openjdk:21-jre-slim
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
```

### Traditional Deployment
1. Build the application: `./mvnw clean package -DskipTests`
2. Copy the JAR file to your server
3. Set environment variables
4. Run with production profile: `java -jar -Dspring.profiles.active=prod backend-0.0.1-SNAPSHOT.jar`

## 🧪 Testing

### Running Tests
```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=UserServiceTest

# Integration tests
./mvnw test -Dtest=*IntegrationTest
```

### Test Coverage
```bash
./mvnw jacoco:report
# Report available at: target/site/jacoco/index.html
```

## 📊 Monitoring and Health Checks

### Health Check Endpoints
- **Application Health**: `GET /actuator/health`
- **WebSocket Health**: `GET /ws-health`
- **Application Info**: `GET /actuator/info` (dev only)

### Logging
- **Development**: Console logging with DEBUG level
- **Production**: File logging (`logs/morago-backend.log`) with INFO level
- **Test**: Minimal console logging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📝 Environment Variables Reference

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` | No |
| `DB_URL` | Database connection URL | - | Yes |
| `DB_USER` | Database username | - | Yes |
| `DB_PASS` | Database password | - | Yes |
| `JWT_ACCESS_SECRET` | JWT access token secret | - | Yes |
| `JWT_REFRESH_SECRET` | JWT refresh token secret | - | Yes |
| `JWT_ACCESS_EXPIRATION_MS` | Access token expiration (ms) | `900000` | No |
| `JWT_REFRESH_EXPIRATION_MS` | Refresh token expiration (ms) | `604800000` | No |
| `SERVER_PORT` | Server port | `8080` | No |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | varies by profile | No |
