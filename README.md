# Review Ingestion Microservice

A Spring Boot-based event-driven microservice that ingests review data from `.jl` (JSON Lines) files stored in AWS S3. It is triggered by file metadata received via Kafka, parses and validates the reviews, and persists them to PostgreSQL with full logging and idempotency support.

---

## Features

- **Kafka Consumer**: Consumes S3 file metadata messages
- **S3 Integration**: Reads `.jl` files line-by-line from S3
- **Robust Parsing**: Handles deeply nested review JSONs
- **Schema Validation**: Validates required review fields
- **Idempotency**: Uses SHA-256 hash to avoid duplicate inserts
- **PostgreSQL Storage**: Stores structured reviews and raw JSON
- **Dead Letter Queue (DLQ)**: Retry mechanism (configurable)
- **Monitoring Logs**: Pushes processing results to Kafka topic `monitoring_logs`
- **REST API**: Retrieve reviews via `/reviews/{hotelId}`
- **Swagger Docs**: Auto-generated API documentation
- **Test Coverage**: Unit & Integration tests included
- **Docker + LocalStack Ready**: Supports local S3 & Kafka testing

---
## HL Architecure 
<img width="1265" height="766" alt="image" src="https://github.com/user-attachments/assets/862f45b8-8ea3-44ca-b069-b29d5729e703" />

<img width="1260" height="692" alt="image" src="https://github.com/user-attachments/assets/422c6c0b-2789-4096-a5e1-d66532d5ac61" />

---

## Event-Driven Architecture Flow

### **1. Files Uploaded to S3**
- JSONL (`.jl`) review files are uploaded to an S3 bucket.

### **2. Lambda Triggered by S3**
- Extracts metadata (bucket name, object key, timestamp).
- Publishes metadata to Kafka topic `review_ingest`.
- Monitorning message to another topic 'monitoring_logs'

### **3. Kafka Message Received by Spring Boot Service**
- Spring Boot consumer listens to `review_ingest` topic.
- Uses metadata to fetch file contents from S3.

### **4. Process File Line-by-Line**
- Each `.jl` line is:
  - Parsed and validated
  - Logged to a `monitoring_logs` topic
  - Stored to PostgreSQL if valid
  - Rejected if invalid
  - pushed to topic 'monitoring_logs'

### **5. Retry + DLQ Handling**
- Retries on failure (2 times)
- Then moved to DLQ Kafka topic 'review_dlq'
- A separate Lambda can retry from DLQ

### **6. Processed File Moved**
- Once complete, the file is moved to `processed` S3 bucket

### **7. API Exposure**
- REST API `/reviews?hotelId=10984` allows querying reviews

### **7. Monitoring **
- Any monitoring tool like ELK can be integration on 'monitoring_logs'

---

## 🔧 Technologies Used
- **Java 17 / Spring Boot 3**
- **Apache Kafka**
- **PostgreSQL**
- **AWS S3 / Lambda / EKS**
- **Terraform + Kubernetes**
- **Docker / LocalStack / GitHub Actions**

---


## Tech Stack

| Layer         | Technology            |
|---------------|------------------------|
| Language      | Java 17                |
| Framework     | Spring Boot, Kafka     |
| Messaging     | Apache Kafka           |
| Storage       | PostgreSQL             |
| Object Store  | AWS S3 (or LocalStack) |
| Build Tool    | Gradle                 |
| Logging       | Kafka (`monitoring_logs`) |
| API Docs      | Swagger (Springdoc)    |
| Tests         | JUnit + Mockito        |

---

## Top level Project Structure
```
review-processor/
├── lambda/
│   └── ingest-metadata-lambda/
│       ├── src/
│       ├── build.gradle
│       └── README.md

├── review-service/
│   ├── src/
│   ├── build.gradle
│   ├── Dockerfile
│   └── k8s/                          # Kubernetes manifests (YAMLs)
│       ├── deployment.yaml
│       └── service.yaml

├── infra/                            # Terraform infrastructure code
│   ├── eks/                          
│   ├── review-service/
│   │   └── main.tf                   # Deployment and service Terraform
│   └── variables.tf / outputs.tf

├── .github/
│   └── workflows/
│       └── ci-cd.yml

├── docker-compose.yml
├── README.md                         # Top-level monorepo readme
└── .dockerignore


```

## review-service Project Structure
```
hotel-review-processor/
├── src/
│   ├── main/
│   │   ├── java/com/zuzu/reviewservice/
│   │   │   ├── controller/          # REST APIs
│   │   │   ├── consumer/            # Kafka consumer logic
│   │   │   ├── entity/              # JPA Entities
│   │   │   ├── service/             # Business logic
│   │   │   ├── config/              # Kafka, AWS, S3 configs
│   ├── test/                        # Unit and integration tests
├── build.gradle
├── application.yml / application.properties
```

---

## API Endpoints

- `GET /reviews/{hotelId}` — Returns list of reviews for a given hotel
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Configuration
 `application.properties`:

# Spring Datasource
spring.datasource.url=jdbc:postgresql://localhost:5432/reviewdb
spring.datasource.username=review
spring.datasource.password=review
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Kafka
kafka.bootstrap-servers=localhost:9092

# AWS + LocalStack
aws.region=us-east-1
s3.endpoint=http://host.docker.internal:4566


---

## Run Tests

```bash
./gradlew test
```

---

## Local Development

### Start Kafka and S3 (LocalStack) via Docker Compose

```bash
docker-compose up -d
```

### Run Spring Boot Service

```bash
./gradlew bootRun
```

---

## Idempotency Logic

- A SHA-256 hash is generated from `hotelId + reviewDate + reviewerCountry`
- Duplicates are skipped to prevent re-ingestion
- Stored in column `review_hash` (unique index applied)

---

## Not supported <TBD>

- Supports reviews from **multiple providers**
- Handles **nested rating systems**
- Allows for **multilingual extensions**
- Raw JSON stored for **schema evolution**

---

## Deployment Ready

- Dockerfile and Helm chart available
- Terraform templates to deploy Kafka, PostgreSQL, and EKS (optional)
- CI/CD via GitHub Actions (coming soon)

---

## License

MIT License. Use freely, contributions welcome.

---

## Author

Built by **Arvind Singh Pal** | [![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin)](https://www.linkedin.com/in/arvind-singh-pal/)
