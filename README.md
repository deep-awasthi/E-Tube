# E-Tube Streaming Platform Backend

E-Tube is a high-performance, event-driven video streaming backend built using Spring Boot. It leverages a hybrid database model (PostgreSQL + MongoDB), distributed caching (Redis), and asynchronous event broker queuing (RabbitMQ) to easily support 1K+ concurrent users with high throughput and low response times.

---

## Key System Features & Concurrency Architecture

### 1. High Concurrency View Processing (RabbitMQ + NoSQL + SQL)
* **Problem**: Updating a single video's view counts in a relational database during high traffic causes row-level write locks. This limits performance and leads to request timeouts.
* **Solution**: When a user plays a video, the request returns a `202 Accepted` response immediately. The view event is pushed to RabbitMQ. The `EventConsumer` asynchronously:
  1. Increments `view_count` in PostgreSQL.
  2. Saves a detailed request audit record (`ViewLog`) to MongoDB.
  3. Evicts the cached video details in Redis.

### 2. Low-Latency Retrieval (Redis Cache)
* Popular video details are cached in Redis. Requests are served in sub-millisecond times directly from memory.
* Cache entries are automatically evicted on updates or video-view updates to maintain data consistency.

### 3. Smooth Range-Request Video Streaming (`206 Partial Content`)
* Video streams support chunk-based Range queries (e.g. `Range: bytes=0-1048576`). 
* Utilizes Spring's `ResourceRegion` to read only the requested byte segments of the video file instead of buffering the whole file in memory. This supports seamless video seeking/scrubbing and drastically reduces server memory utilization.

---

## Technology Stack
* **Framework**: Spring Boot 3.3.0
* **Language**: Java 21
* **SQL Database**: PostgreSQL (Transactional Metadata)
* **NoSQL Database**: MongoDB (Comments, Views & Log Analytics)
* **Cache**: Redis
* **Message Broker**: RabbitMQ
* **Orchestration**: Docker & Docker Compose

---

## Project Structure

```text
E-Tube/
├── docker-compose.yml       # Sets up SQL, NoSQL, Redis, RabbitMQ & Spring Boot App
├── Dockerfile               # Multi-stage Docker packaging configuration
├── pom.xml                  # Maven Dependency build file
├── run.sh                   # Helper script to run the application with a single command
├── requests.http            # HTTP request integration tests
├── sample.mp4               # Test video payload file
└── src/
    ├── main/
    │   ├── java/com/etube/
    │   │   ├── EtubeApplication.java     # Main application launcher
    │   │   ├── config/                   # Configuration (Redis, RabbitMQ)
    │   │   ├── controller/               # Controllers (Videos, Comments)
    │   │   ├── model/                    # Data models (SQL & NoSQL)
    │   │   ├── repository/               # Repositories (JPA & MongoRepository)
    │   │   ├── messaging/                # RabbitMQ Publishers & Consumers
    │   │   └── service/                  # Business Logic (Streaming, Comments)
    │   └── resources/
    │       └── application.yml           # Application configuration properties
    └── test/
        └── java/com/etube/
            └── service/
                └── VideoServiceTest.java # Unit tests for core streaming services
```

---

## Setup & Running Guide

### System Requirements
* Docker Desktop installed and running.
* That's it! (Java and Maven are packaged within Docker, so local installation is optional).

### Run Everything with a Single Command
To build and run all services (App, PostgreSQL, MongoDB, Redis, RabbitMQ) concurrently in the background, run the following command from the project root:

```bash
./run.sh
```

*Alternative standard command:*
```bash
docker compose up --build -d
```

### Stopping the Services
To stop and clean up containers and networks:
```bash
docker compose down
```

---

## Verifying the Services

Once started, you can inspect and use the following services:

| Component | Endpoint | Authentication |
| :--- | :--- | :--- |
| **Spring Boot Application** | `http://localhost:8080` | None |
| **RabbitMQ Administration UI** | `http://localhost:15672` | Username: `guest` / Password: `guest` |
| **PostgreSQL Database** | `localhost:5432` | DB: `etube`, User: `etube_user`, Pass: `etube_password` |
| **MongoDB Database** | `localhost:27017` | DB: `etube` |
| **Redis Cache Store** | `localhost:6379` | None |

---

## API Testing with `requests.http`

We have provided a complete suite of integration test cases in [requests.http](file:///Users/deepawasthi/Developer/E-Tube/requests.http).

You can run these requests directly inside your IDE using plugins like **REST Client** (VS Code) or the built-in HTTP client in IntelliJ IDEA.

### Execution Flow:
1. **Upload Video** (API saves record in PostgreSQL as `PENDING` status, publishes event to RabbitMQ, and consumer updates status to `READY` after 3 seconds).
2. **Fetch Metadata** (Initial hit does SQL query; subsequent hits read from Redis Cache).
3. **Stream Video** (Requests range chunks `bytes=0-100` and `bytes=101-300` to verify `206 Partial Content` headers).
4. **Submit & Retrieve Comments** (Interacts with MongoDB collection).
5. **Simulate Views** (Pushes message to RabbitMQ, increments views count, logs analytic view records in MongoDB, and evicts Redis cache).
