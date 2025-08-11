# Job Board API Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Technology Stack](#technology-stack)
4. [Database Schema](#database-schema)
5. [Setup & Configuration](#setup--configuration)
6. [Authentication & Security](#authentication--security)
7. [API Endpoints](#api-endpoints)
8. [User Roles & Permissions](#user-roles--permissions)
9. [Error Handling](#error-handling)
10. [Features](#features)
11. [Deployment](#deployment)

## Project Overview

The Job Board API is a comprehensive RESTful web service built with Spring Boot that enables job seekers to find employment opportunities and employers to post job listings. The platform supports multiple user roles (Job Seekers, Employers, and Admins) with role-based access control and features like job applications, saved jobs, and email notifications.

### Key Features
- **User Management**: Registration, authentication, role-based access
- **Job Management**: CRUD operations for job postings
- **Application System**: Job seekers can apply to jobs
- **Saved Jobs**: Bookmark functionality for job seekers
- **Job Alerts**: Email notifications for new job postings
- **Advanced Search**: Filter and search jobs by multiple criteria
- **File Upload**: Resume and company logo uploads via Cloudinary
- **OAuth2 Integration**: Google OAuth2 authentication support
- **Email Services**: Password reset and job alerts via email

## System Architecture

The application follows a layered architecture pattern:

```
┌─────────────────┐
│   Controllers   │  ← REST API Layer
├─────────────────┤
│    Services     │  ← Business Logic Layer
├─────────────────┤
│  Repositories   │  ← Data Access Layer
├─────────────────┤
│    Database     │  ← PostgreSQL Database
└─────────────────┘
```

### Component Overview
- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic and orchestrate operations
- **Repositories**: Handle database operations using Spring Data JPA
- **DTOs**: Data Transfer Objects for API communication
- **Models**: JPA entities representing database tables
- **Security**: JWT-based authentication and authorization

## Technology Stack

### Backend Framework
- **Spring Boot 3.4.2** - Main framework
- **Java 17** - Programming language
- **Maven** - Dependency management

### Database
- **PostgreSQL** - Primary database
- **Spring Data JPA** - ORM framework
- **Hibernate** - JPA implementation

### Security
- **Spring Security 6** - Security framework
- **JWT (JSON Web Tokens)** - Authentication tokens
- **OAuth2** - Google authentication integration
- **BCrypt** - Password hashing

### External Services
- **Cloudinary** - File storage for resumes and logos
- **Redis** - Caching and session management
- **Gmail SMTP** - Email service for notifications

### Additional Libraries
- **Lombok** - Reduce boilerplate code
- **Thymeleaf** - Template engine for email templates
- **Jackson** - JSON processing
- **Validation API** - Input validation

## Database Schema

### Core Tables

#### Users Table
```sql
users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK(role IN ('JOB_SEEKER', 'EMPLOYER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

#### Job Seekers Table
```sql
job_seekers (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    resume_url VARCHAR(255),
    skills TEXT,
    experiences TEXT
)
```

#### Employers Table
```sql
employers (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    company_description TEXT,
    logo_url VARCHAR(255),
    website_url VARCHAR(255)
)
```

#### Jobs Table
```sql
jobs (
    id SERIAL PRIMARY KEY,
    employer_id INT NOT NULL REFERENCES employers(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255),
    category VARCHAR(255),
    salary NUMERIC(10,2),
    posted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    status VARCHAR(50) NOT NULL CHECK(status IN ('ACTIVE', 'FILLED', 'EXPIRED'))
)
```

#### Applications Table
```sql
applications (
    id SERIAL PRIMARY KEY,
    job_id INT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    job_seeker_id INT NOT NULL REFERENCES job_seekers(id) ON DELETE CASCADE,
    resume_url VARCHAR(255) NOT NULL,
    cover_letter TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' 
           CHECK(status IN ('PENDING', 'REVIEWED', 'REJECTED', 'ACCEPTED')),
    CONSTRAINT unique_job_application UNIQUE (job_id, job_seeker_id)
)
```

#### Additional Tables
- `saved_jobs` - Job bookmarks for job seekers
- `job_alerts` - Email alert preferences
- `tokens` - JWT refresh tokens
- `reset_token` - Password reset tokens

## Setup & Configuration

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Redis server
- Maven 3.6+
- Cloudinary account
- Gmail account for SMTP

### Environment Configuration

Create `application.properties` with the following configuration:

```properties
# Server Configuration
server.port=8081

# Database Configuration
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.url=jdbc:postgresql://localhost:5432/job_board

# JWT Configuration
jwt.secret=your-base64-encoded-secret-key
jwt.access-token-expiration=3600000
jwt.refresh-token-expiration=15768000000

# OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8081/login/oauth2/code/google
spring.security.oauth2.client.registration.google.scope=email,profile

# Cloudinary Configuration
cloud-name=your-cloudinary-cloud-name
api-secret=your-cloudinary-api-secret
api-key=your-cloudinary-api-key

# File Upload Configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd jobboard
```

2. **Set up the database**
```bash
# Create PostgreSQL database
createdb job_board

# Run the SQL scripts
psql -d job_board -f src/main/resources/db/tables.sql
psql -d job_board -f src/main/resources/db/enums.sql
```

3. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at `http://localhost:8081`

## Authentication & Security

### Authentication Methods

1. **JWT Authentication** (Primary)
   - Access tokens (1 hour expiration)
   - Refresh tokens (6 months expiration)
   - Secure HTTP-only cookies for refresh tokens

2. **OAuth2 Google Authentication**
   - Single Sign-On integration
   - Automatic user creation for new Google users

### Security Features

- **Password Encryption**: BCrypt with strength 12
- **Role-based Access Control**: Three user roles with specific permissions
- **JWT Security**: Signed tokens with configurable expiration
- **CORS Protection**: Configured for cross-origin requests
- **Input Validation**: Request validation using Bean Validation
- **SQL Injection Protection**: JPA/Hibernate parameterized queries

### Token Management

```java
// Access Token Generation
String accessToken = jwtService.generateAccessToken(email);

// Refresh Token Generation  
String refreshToken = jwtService.generateRefreshToken(email);

// Token Validation
boolean isValid = jwtService.validateToken(token, userDetails);
```

## API Endpoints

### Authentication Endpoints (`/api/auth`)

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123",
    "role": "JOB_SEEKER"
}
```

**Response:**
```json
{
    "status": "CREATED",
    "message": "register",
    "data": {
        "accessToken": "jwt-token-here",
        "userInfo": {
            "id": 1,
            "email": "user@example.com",
            "role": "JOB_SEEKER"
        }
    }
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```

#### Change Password
```http
PUT /api/auth/change-password
Authorization: Bearer {token}
Content-Type: application/json

{
    "oldPassword": "oldpass123",
    "newPassword": "newpass123",
    "email": "user@example.com"
}
```

#### Refresh Token
```http
GET /api/auth/refresh-token
Authorization: Bearer {refresh-token}
```

#### Password Reset
```http
POST /api/auth/reset-token
Content-Type: application/json

{
    "email": "user@example.com"
}
```

```http
POST /api/auth/reset-password
Content-Type: application/json

{
    "resetToken": "uuid-token-here",
    "newPassword": "newpassword123"
}
```

### Public Job Endpoints (`/api/v1/jobs`)

#### Get All Jobs (Paginated)
```http
GET /api/v1/jobs?page=0&size=10&sort=postedAt,desc
```

**Response:**
```json
{
    "content": [
        {
            "id": 1,
            "companyName": "Tech Corp",
            "companyDescription": "Leading tech company",
            "websiteUrl": "https://techcorp.com",
            "title": "Software Engineer",
            "description": "Full-stack development position",
            "location": "San Francisco, CA",
            "category": "Technology",
            "salary": 120000.00,
            "postedAt": "2024-01-15T10:00:00",
            "expiresAt": "2024-02-15T10:00:00",
            "status": "ACTIVE"
        }
    ],
    "pageable": {
        "sort": {"sorted": true},
        "pageNumber": 0,
        "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1
}
```

#### Get Job by ID
```http
GET /api/v1/jobs/{id}
```

#### Search Jobs by Title
```http
GET /api/v1/jobs/search/title?title=engineer&page=0&size=10
```

#### Advanced Search
```http
GET /api/v1/jobs/search/advanced?search=java developer&page=0&size=10
```

#### Filter Jobs
```http
GET /api/v1/jobs/filter?title=engineer&location=california&minSalary=50000&maxSalary=150000&category=technology&page=0&size=10
```

### Job Seeker Endpoints (`/api/v1`)

#### Create Job Seeker Profile
```http
POST /api/v1/user/job-seeker
Authorization: Bearer {token}
Content-Type: multipart/form-data

email: user@example.com
firstName: John
lastName: Doe
skills: Java, Spring Boot, React
experiences: 5 years of software development
resume: [file]
```

#### Update Job Seeker Profile
```http
PUT /api/v1/user/job-seeker/{id}
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

#### Get Job Seeker Profile
```http
GET /api/v1/user/job-seeker
Authorization: Bearer {token}
```

#### Apply for Job
```http
POST /api/v1/jobs/{jobId}/apply
Authorization: Bearer {token}
Content-Type: multipart/form-data

coverLetter: I am interested in this position...
resume: [file]
```

#### Save Job
```http
POST /api/v1/jobs/{jobId}/save
Authorization: Bearer {token}
```

#### Get Saved Jobs
```http
GET /api/v1/saved-jobs
Authorization: Bearer {token}
```

#### Delete Saved Job
```http
DELETE /api/v1/saved-job/{id}
Authorization: Bearer {token}
```

#### Set Job Alerts
```http
POST /api/v1/job-alerts
Authorization: Bearer {token}
Content-Type: application/json

{
    "searchedQuery": "java developer",
    "frequency": "DAILY"
}
```

#### Delete Job Alerts
```http
DELETE /api/v1/job-alerts/{id}
Authorization: Bearer {token}
```

### Employer Endpoints (`/api/v1/employer`)

#### Create Employer Profile
```http
POST /api/v1/user/employer
Authorization: Bearer {token}
Content-Type: multipart/form-data

email: employer@company.com
companyName: Tech Corporation
companyDescription: Leading technology company
websiteUrl: https://techcorp.com
companyLogo: [file]
```

#### Update Employer Profile
```http
PUT /api/v1/user/employer/{id}
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

#### Get Employer Profile
```http
GET /api/v1/user/employer
Authorization: Bearer {token}
```

#### Post Job
```http
POST /api/v1/employer/jobs
Authorization: Bearer {token}
Content-Type: application/json

{
    "title": "Senior Software Engineer",
    "description": "We are looking for an experienced software engineer...",
    "location": "Remote",
    "category": "Technology",
    "salary": 120000.00,
    "expiresAt": "2024-03-15T23:59:59"
}
```

#### Update Job
```http
PUT /api/v1/employer/jobs/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

#### Delete Job
```http
DELETE /api/v1/employer/jobs/{id}
Authorization: Bearer {token}
```

### Application Management Endpoints (`/api/v1`)

#### Get All Applications (Employer)
```http
GET /api/v1/jobs/applications
Authorization: Bearer {token}
```

#### Get Applications for Specific Job (Employer)
```http
GET /api/v1/jobs/{jobId}/applications
Authorization: Bearer {token}
```

#### Get Job Seeker Applications
```http
GET /api/v1/my-applications
Authorization: Bearer {token}
```

#### Update Application Status (Employer)
```http
PUT /api/v1/applications/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
    "status": "REVIEWED"
}
```

### Admin Endpoints (`/api/v1/admin`)

#### Get All Users with Data
```http
GET /api/v1/admin/all/users-data
Authorization: Bearer {token}
```

#### Get All Users
```http
GET /api/v1/admin/all/users
Authorization: Bearer {token}
```

#### Get All Job Seekers
```http
GET /api/v1/admin/all/job-seekers
Authorization: Bearer {token}
```

#### Get All Employers
```http
GET /api/v1/admin/all/employers
Authorization: Bearer {token}
```

#### Delete Job Seeker
```http
DELETE /api/v1/admin/job-seeker/{id}
Authorization: Bearer {token}
```

#### Delete Employer
```http
DELETE /api/v1/admin/employer/{id}
Authorization: Bearer {token}
```

## User Roles & Permissions

### Job Seeker Role (`JOB_SEEKER`)
**Permissions:**
- Create and update job seeker profile
- View and search all job listings
- Apply for jobs
- Save/bookmark jobs
- Set up job alerts
- View application history
- Update application materials

**Restricted Actions:**
- Cannot post jobs
- Cannot view other users' applications
- Cannot access admin functions

### Employer Role (`EMPLOYER`)
**Permissions:**
- Create and update employer profile
- Post, edit, and delete job listings
- View applications for their jobs
- Update application status (review, accept, reject)
- View job seeker profiles who applied

**Restricted Actions:**
- Cannot apply for jobs
- Cannot access admin functions
- Cannot view other employers' data

### Admin Role (`ADMIN`)
**Permissions:**
- View all users and their data
- Delete any user account
- View all job seekers and employers
- Access system analytics
- Manage platform-wide settings

**Restricted Actions:**
- Cannot post jobs (unless also has employer role)
- Cannot apply for jobs (unless also has job seeker role)

## Error Handling

### Standard Error Response Format
```json
{
    "status": "BAD_REQUEST",
    "message": "Validation failed",
    "data": null
}
```

### Common HTTP Status Codes

| Status Code | Meaning | Common Scenarios |
|-------------|---------|------------------|
| 200 | OK | Successful GET, PUT requests |
| 201 | Created | Successful POST requests |
| 400 | Bad Request | Validation errors, malformed requests |
| 401 | Unauthorized | Invalid or expired tokens |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate email, application exists |
| 422 | Unprocessable Entity | Business logic violations |
| 500 | Internal Server Error | Server-side errors |

### Custom Exceptions

```java
// User-related exceptions
UserAlreadyExists - Email already registered
UserDoesNotExists - User not found
CustomNotAuthorized - Insufficient permissions

// Validation exceptions
CustomBadRequest - Invalid input data
InvalidEntity - Entity validation failed

// Business logic exceptions
MultiRoleException - User cannot have multiple roles
SingleRoleBasedException - Action requires specific single role
```

## Features

### 1. User Management
- **Registration & Authentication**: Email/password and OAuth2 Google login
- **Profile Management**: Separate profiles for job seekers and employers
- **Role-based Access**: Dynamic permissions based on user role
- **Password Management**: Secure password reset via email

### 2. Job Management
- **CRUD Operations**: Complete job lifecycle management
- **Advanced Search**: Multi-criteria filtering and full-text search
- **Job Status Management**: Active, filled, expired status tracking
- **Pagination**: Efficient data retrieval with pagination support

### 3. Application System
- **Job Applications**: Resume upload and cover letter submission
- **Application Tracking**: Status updates (pending, reviewed, accepted, rejected)
- **Duplicate Prevention**: One application per job per user
- **Application History**: Complete application timeline for users

### 4. Enhanced Features
- **Saved Jobs**: Bookmark functionality for job seekers
- **Job Alerts**: Email notifications for new matching jobs
- **File Upload**: Cloudinary integration for resumes and logos
- **Email Services**: Automated email notifications and alerts
- **Caching**: Redis integration for improved performance

### 5. Security Features
- **JWT Authentication**: Secure token-based authentication
- **Role-based Authorization**: Granular permission control
- **Input Validation**: Comprehensive request validation
- **Password Security**: BCrypt encryption with configurable strength
- **Token Management**: Refresh token rotation and revocation

## Deployment

### Production Configuration

1. **Environment Variables**
```bash
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL=your-production-db-url
export JWT_SECRET=your-production-jwt-secret
export CLOUDINARY_URL=your-cloudinary-url
export REDIS_URL=your-redis-url
export MAIL_USERNAME=your-email
export MAIL_PASSWORD=your-email-password
```

2. **Database Setup**
```bash
# Run migrations
java -jar target/jobboard-0.0.1-SNAPSHOT.jar --spring.jpa.hibernate.ddl-auto=validate
```

3. **Docker Deployment**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/jobboard-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Performance Considerations

1. **Database Optimization**
   - Index on frequently queried fields (email, job title, location)
   - Connection pooling configuration
   - Query optimization for job search

2. **Caching Strategy**
   - Redis for session management
   - Cache frequently accessed job listings
   - Cache user profile data

3. **File Storage**
   - Cloudinary CDN for global file delivery
   - Optimized image sizes for logos
   - Resume file format validation

### Monitoring & Logging

1. **Application Metrics**
   - Spring Boot Actuator endpoints
   - Database connection monitoring
   - API response time tracking

2. **Logging Configuration**
   - Structured logging with JSON format
   - Security event logging
   - Error tracking and alerting

## API Rate Limiting

To protect the API from abuse, consider implementing rate limiting:

```java
// Example rate limiting configuration
@RateLimiter(name = "jobsApi", fallbackMethod = "fallbackResponse")
public ResponseEntity<?> getAllJobs(Pageable pageable) {
    // Implementation
}
```

## Testing

### Test Categories

1. **Unit Tests**
   - Service layer business logic
   - Utility functions
   - Custom validators

2. **Integration Tests**
   - API endpoint testing
   - Database operations
   - Security configurations

3. **Security Tests**
   - Authentication flows
   - Authorization checks
   - Input validation

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow coding standards and add tests
4. Submit a pull request with detailed description

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Note**: This documentation is comprehensive but may need updates as the project evolves. Always refer to the latest codebase for the most current implementation details.
