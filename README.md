# Hospital Management System

A secure, scalable, and production-ready Hospital Management System backend built using Spring Boot. This comprehensive application manages patient records, doctor schedules, and appointments for hospitals and clinics.

## Features

- **Patient Management**: CRUD operations for patient records with medical history tracking
- **Doctor Management**: Doctor profile management with specialization and department organization
- **Appointment Booking**: Advanced booking system with pessimistic locking to prevent overbooking
- **Concurrency Control**: Pessimistic locking mechanism for safe concurrent appointment bookings
- **Data Validation**: Comprehensive validation using Jakarta Bean Validation API
- **Role-Based Access Control**: Spring Security with JWT authentication (ADMIN, DOCTOR, PATIENT roles)
- **API Documentation**: Swagger UI integration for interactive API testing
- **SMS Notifications**: Twilio integration for appointment confirmations and reminders
- **Pagination & Sorting**: Spring Data support for efficient data retrieval
- **Caching**: Redis integration for performance optimization
- **Error Handling**: Centralized exception handling with detailed error responses
- **Docker Support**: Containerized deployment with Docker and Docker Compose

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Java**: JDK 17
- **ORM**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security + JWT
- **API Docs**: SpringDoc OpenAPI (Swagger UI)
- **Notifications**: Twilio SDK
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Build Tool**: Maven

## Project Structure

```
hospital-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/hospital/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA Entities
│   │   │   ├── exception/        # Custom Exceptions & Global Handler
│   │   │   ├── repository/       # Data Access Layer
│   │   │   ├── security/         # JWT & Authentication
│   │   │   └── service/          # Business Logic
│   │   └── resources/
│   │       └── application.yml   # Configuration
│   └── test/
│       └── java/com/hospital/    # Unit & Integration Tests
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Redis 7+ (optional, for caching)
- Docker & Docker Compose (for containerized deployment)

### Local Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd hospital-management-system
   ```

2. **Configure database (application.yml):**
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/hospital_db
       username: postgres
       password: postgres
   ```

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the APIs:**
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - API Docs: http://localhost:8080/api/v3/api-docs

### Docker Deployment

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

See [DOCKER.md](DOCKER.md) for detailed Docker instructions.

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Validate JWT token

### Patient Management
- `POST /api/patients` - Create patient
- `GET /api/patients` - Get all patients (pageable)
- `GET /api/patients/{id}` - Get patient by ID
- `PUT /api/patients/{id}` - Update patient
- `DELETE /api/patients/{id}` - Delete patient
- `GET /api/patients/{id}/history` - Get patient medical history

### Doctor Management
- `POST /api/doctors` - Create doctor
- `GET /api/doctors` - Get all doctors (pageable)
- `GET /api/doctors/{id}` - Get doctor by ID
- `GET /api/doctors/specialization/{specialization}` - Find doctors by specialization
- `GET /api/doctors/department/{department}` - Find doctors by department
- `PUT /api/doctors/{id}` - Update doctor
- `DELETE /api/doctors/{id}` - Delete doctor
- `GET /api/doctors/{id}/availability` - Check doctor availability

### Appointment Management
- `POST /api/appointments` - Book appointment (with pessimistic locking)
- `GET /api/appointments` - Get all appointments (pageable)
- `GET /api/appointments/{id}` - Get appointment by ID
- `GET /api/appointments/patient/{patientId}` - Get patient appointments
- `GET /api/appointments/doctor/{doctorId}` - Get doctor appointments
- `PUT /api/appointments/{id}/status` - Update appointment status
- `DELETE /api/appointments/{id}` - Cancel appointment

### Medical Records
- `POST /api/medical-records` - Create medical record
- `GET /api/medical-records/{id}` - Get record by ID
- `GET /api/medical-records/patient/{patientId}` - Get patient records
- `GET /api/medical-records/patient/{patientId}/history` - Get patient history
- `GET /api/medical-records/doctor/{doctorId}` - Get doctor's records
- `PUT /api/medical-records/{id}` - Update record
- `DELETE /api/medical-records/{id}` - Delete record

## Authentication

The API uses JWT (JSON Web Tokens) for authentication.

1. **Login to get token:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password"}'
   ```

2. **Use token in requests:**
   ```bash
   curl -X GET http://localhost:8080/api/patients \
     -H "Authorization: Bearer <your-token>"
   ```

## Key Implementation Details

### Concurrency Control (Pessimistic Locking)
The appointment booking feature uses pessimistic locking to prevent race conditions:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Appointment> findByIdWithLock(Long id);
```

### Transactional Operations
All service methods are marked with `@Transactional` for data consistency:
```java
@Transactional
public AppointmentDTO bookAppointment(AppointmentDTO appointmentDTO) { ... }
```

### Validation
Comprehensive input validation using Jakarta Bean Validation:
```java
@Future(message = "Appointment date must be in the future")
private LocalDateTime appointmentDateTime;
```

### SMS Notifications
Twilio integration for sending appointment notifications:
```java
smsNotificationService.sendAppointmentConfirmation(phone, doctorName, dateTime);
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
# Database
spring.datasource.url: jdbc:postgresql://localhost:5432/hospital_db
spring.datasource.username: postgres
spring.datasource.password: postgres

# Redis
spring.data.redis.host: localhost
spring.data.redis.port: 6379

# JWT
jwt.secret: your-secret-key-min-32-chars
jwt.expiration: 86400000 # 24 hours

# Twilio
twilio.account-sid: your-account-sid
twilio.auth-token: your-auth-token
twilio.phone-number: +1234567890

# Hospital Settings
hospital.appointment.max-slots-per-day: 10
```

## Testing

Run unit and integration tests:
```bash
mvn test
```

Example test for appointment booking:
```java
@Test
void testBookAppointmentSuccess() {
    // Arrange, Act, Assert
    AppointmentDTO result = appointmentService.bookAppointment(appointmentDTO);
    assertNotNull(result);
}
```

## Performance Optimization

- **Pagination**: All list endpoints support pagination for efficient data retrieval
- **Caching**: Redis integration for frequently accessed data
- **Indexing**: Database indexes on frequently queried columns
- **Connection Pooling**: HikariCP for database connection management
- **Lazy Loading**: JPA lazy loading for related entities

## Security Best Practices

- JWT authentication with secure token generation
- Password encryption using BCrypt
- SQL injection prevention through prepared statements
- CORS configuration for API security
- Role-based access control (RBAC)
- Input validation and sanitization

## Deployment

### Local Deployment
```bash
mvn clean package
java -jar target/hospital-management-system-1.0.0.jar
```

### Docker Deployment
```bash
docker build -t hospital-management-system:latest .
docker run -p 8080:8080 hospital-management-system:latest
```

### Cloud Deployment
See [DOCKER.md](DOCKER.md) for AWS ECS and GCP Cloud Run deployment instructions.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support

For issues, feature requests, or questions, please create an issue on GitHub.

## Authors

Hospital Management Team - support@hospital.com

---

**Status**: Production Ready | **Version**: 1.0.0 | **Last Updated**: December 2025
