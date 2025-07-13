# 🏨 Hotel Review Ingestion Microservice

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

## Project Structure

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

Built by **Arvind Singh Pal** | [LinkedIn](https://www.linkedin.com/in/arvindsinghpal/) 
