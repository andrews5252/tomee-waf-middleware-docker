**# Java EE Middleware \& Application-Level WAF Architecture (Dockerized)**



**A containerized, enterprise-grade Java EE middleware demonstration project built on \*\*Apache TomEE Plus\*\*, \*\*ActiveMQ\*\*, \*\*EJB 3\*\*, \*\*OpenJPA\*\*, and \*\*MySQL 8.0\*\*, featuring a custom application-level \*\*Web Application Firewall (WAF)\*\* with asynchronous security event logging.**



**## Architecture Overview**



**```text**

**\[HTTP Client / Browser]**

&#x20;        **│  (GET /robWAF/teszt?id=1 UNION SELECT...)**

&#x20;        **▼**

**┌─────────────────────────────────────────────────────────┐**

**│  Apache TomEE 8.0.16 Plus (Container: rob-tomee)        │**

**│                                                         │**

**│  1. RobWafFilter (@WebFilter)                           │**

**│     ├─ Inspects \& URL-decodes incoming HTTP requests    │**

**│     ├─ Blocks SQLi \& XSS payloads -> HTTP 403 Forbidden │**

**│     └─ Publishes security alert to JMS Queue            │**

**│                         │                               │**

**│                         ▼                               │**

**│  2. Embedded ActiveMQ Broker (FeladatQueue)             │**

**│                         │                               │**

**│                         ▼                               │**

**│  3. RobQueueMdb (@MessageDriven)                        │**

**│     └─ Consumes alert \& invokes @Stateless EJB          │**

**│                         │                               │**

**│                         ▼                               │**

**│  4. NaploKezeloBean (JTA + OpenJPA)                     │**

**└─────────────────────────┼───────────────────────────────┘**

&#x20;                         **│ (JDBC / JTA DataSource: RobDS)**

&#x20;                         **▼**

**┌─────────────────────────────────────────────────────────┐**

**│  MySQL 8.0 (Container: rob-mysql)                       │**

**│  └─ Database: robdb | Table: SzerverNaplo               │**

**└─────────────────────────────────────────────────────────┘**

**```**



**## Key Features**

**- \*\*Application-Level Security Filter (`RobWafFilter`):\*\* Intercepts all HTTP requests (`/\*`), normalizes URL-encoded payloads, detects common SQL Injection and XSS attack vectors, and immediately terminates malicious requests with `403 Forbidden`.**

**- \*\*Asynchronous Event Pipeline (JMS / ActiveMQ):\*\* Decouples HTTP request filtering from database persistence by dispatching security alerts to `FeladatQueue` without adding latency to client responses.**

**- \*\*Enterprise Persistence (EJB \& OpenJPA):\*\* Processes queue messages via a Message-Driven Bean (`RobQueueMdb`) and persists audit logs into MySQL using container-managed JTA transactions.**

**- \*\*Full Containerization:\*\* Reproducible multi-container setup via `docker-compose.yml` with healthcheck-based startup ordering.**



**## Quick Start**



**1. Start the environment:**

&#x20;  **```bash**

&#x20;  **docker compose up -d**

&#x20;  **```**

**2. Simulate an SQL Injection attack:**

&#x20;  **```text**

&#x20;  **http://localhost:8080/robWAF/teszt?id=1%20UNION%20SELECT%20password%20FROM%20users**

&#x20;  **```**

**3. Verify the blocked event in MySQL:**

&#x20;  **```bash**

&#x20;  **docker exec -it rob-mysql mysql -uroot -p<PASSWORD> -D robdb -e "SELECT \* FROM SzerverNaplo;"**

&#x20;  **```**



