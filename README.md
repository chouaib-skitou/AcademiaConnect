# AcademiaConnect

## Overview
AcademiaConnect is a microservices-based e-learning platform designed with Angular (frontend), NestJS, and Spring Boot (backend microservices). It includes secure authentication, course management, video streaming, quizzes, real-time updates, analytics, and intuitive user interfaces.

---

## Project Structure

```
AcademiaConnect/
├── frontend/
├── services/
│   ├── auth-service (Spring Boot)
│   ├── course-service (Spring Boot)
│   ├── video-service (Spring Boot)
│   ├── quiz-service (NestJS)
│   ├── notification-service (NestJS)
│   └── analytics-service (Spring Boot)
├── api-gateway (Spring Cloud Gateway)
├── deployment/
│   ├── docker-compose.yml
│   └── kubernetes/
│       ├── auth-service.yaml
│       ├── course-service.yaml
│       └── ...
└── monitoring/
    ├── prometheus/
    ├── grafana/
    └── elk-stack/
```

---

## Frontend Structure (Angular)

```
src/
├── app/
│   ├── core/ (Singleton services, interceptors, guards)
│   ├── modules/
│   │   ├── auth/ (Login, Register, Forgot password)
│   │   ├── courses/ (Course browsing, enrollment, content viewing)
│   │   ├── quizzes/ (Interactive quizzes and exams)
│   │   └── shared/ (Reusable UI components: Modals, Cards, Buttons, Flash Messages)
│   ├── app-routing.module.ts
│   └── app.module.ts
├── assets/ (Static files: images, icons)
├── environments/ (Configuration for prod and dev)
└── styles/ (Global styling and theming)
```

---

## Backend Microservices

### 1. Authentication Service (NestJS)
- JWT-based authentication
- OAuth integration (Google, LinkedIn)
- User registration and profile management
- Roles and Permissions

### 2. Course Management Service (Spring Boot)
- Course creation, updates, deletion
- Course categories and lessons
- Enrollment management
- PostgreSQL/MySQL database

### 3. Video Streaming Service (Spring Boot)
- Video upload and processing (transcoding)
- Cloud storage integration (AWS S3, Cloudinary)
- Secure video streaming

### 4. Quiz Service (NestJS)
- Quiz creation, multiple-choice questions
- Instant scoring and feedback
- Progress tracking

### 5. Notification Service (NestJS + WebSockets)
- Real-time notifications (new content, quiz results)
- User alerts and reminders

### 6. Analytics Service (Spring Boot)
- Data analytics (course engagement, quiz performance)
- Reporting and insights generation

---

## API Gateway
- Routes API requests to appropriate microservices
- Authentication validation
- Load balancing and security enhancements

---

## Deployment

### Docker Compose
- Local development and testing environment
- Easy orchestration of microservices and frontend

Run via:
```shell
docker-compose up
```

### Kubernetes
- Production-ready deployment
- Scalable and highly available
- Kubernetes manifests available in `/deployment/kubernetes`

---

## Monitoring

### Tools:
- **Prometheus**: Metrics collection and alerting
- **Grafana**: Visualization and dashboards
- **ELK Stack (Elasticsearch, Logstash, Kibana)**: Log aggregation, storage, and visualization

Setup located in `/monitoring`

---

## Communication & Integration
- Microservices communication via REST APIs
- Optional asynchronous communication using Kafka or RabbitMQ
- Docker & Kubernetes for deployment

---

## Getting Started
1. Clone the repository
2. Configure environment variables
3. Run microservices via Docker Compose:
```shell
docker-compose up
```
4. Start frontend application:
```shell
cd frontend
npm install
ng serve
```

---

## Technologies Used
- **Frontend:** Angular 19, RxJS, Angular Material
- **Backend:** NestJS, Spring Boot, Java, TypeScript
- **Database:** PostgreSQL/MySQL
- **Video Storage & CDN:** AWS S3, Cloudflare CDN
- **Deployment:** Docker, Kubernetes
- **Monitoring:** Prometheus, Grafana, ELK Stack

---

## Conclusion
This architecture provides an efficient, scalable, and maintainable structure suitable for modern enterprise-level e-learning platforms.

