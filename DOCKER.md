# Hospital Management System - Docker Environment

## Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Java 17+ (for local development)
- Maven (for local development)

### Build and Run with Docker Compose

1. **Clone/Navigate to the project directory:**
   ```bash
   cd hospital-management-system
   ```

2. **Build the Docker image (optional, docker-compose does this automatically):**
   ```bash
   docker build -t hospital-management-system:latest .
   ```

3. **Start all services:**
   ```bash
   # Set environment variables (Twilio credentials)
   export TWILIO_ACCOUNT_SID=your_account_sid
   export TWILIO_AUTH_TOKEN=your_auth_token
   export TWILIO_PHONE_NUMBER=+1234567890
   
   # Start services
   docker-compose up -d
   ```

4. **Check service status:**
   ```bash
   docker-compose ps
   ```

5. **View logs:**
   ```bash
   # All services
   docker-compose logs -f
   
   # Specific service
   docker-compose logs -f app
   ```

6. **Access the application:**
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - API Docs: http://localhost:8080/api/v3/api-docs

7. **Stop services:**
   ```bash
   docker-compose down
   ```

### Environment Variables

Create a `.env` file in the project root:
```
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_PHONE_NUMBER=+1234567890
```

### Database Access

**PostgreSQL:**
- Host: localhost
- Port: 5432
- Username: postgres
- Password: postgres
- Database: hospital_db

**Redis:**
- Host: localhost
- Port: 6379

### Troubleshooting

**Port already in use:**
```bash
# Change ports in docker-compose.yml or kill existing process
lsof -i :8080  # Check what's using port 8080
```

**Database connection issues:**
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Reset database
docker-compose down -v
docker-compose up -d
```

**Application won't start:**
```bash
# Check application logs
docker-compose logs app

# Rebuild image
docker-compose build --no-cache app
docker-compose up -d
```

### Deployment to Cloud

**AWS ECS:**
```bash
# Create ECR repository
aws ecr create-repository --repository-name hospital-management-system

# Push image to ECR
docker tag hospital-management-system:latest <account-id>.dkr.ecr.<region>.amazonaws.com/hospital-management-system:latest
docker push <account-id>.dkr.ecr.<region>.amazonaws.com/hospital-management-system:latest
```

**GCP Cloud Run:**
```bash
# Build and push to Google Container Registry
gcloud builds submit --tag gcr.io/<project-id>/hospital-management-system

# Deploy to Cloud Run
gcloud run deploy hospital-management-system \
  --image gcr.io/<project-id>/hospital-management-system \
  --platform managed \
  --region us-central1
```
