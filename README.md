# Java EE Middleware & Application-Level WAF Architecture (Dockerized)

A containerized enterprise Java EE middleware project built on **Apache TomEE Plus**, **ActiveMQ**, **EJB 3**, **OpenJPA**, and **MySQL 8.0**, featuring an application-level **Web Application Firewall (WAF)** with asynchronous security event logging.

## Architecture Overview

```text
[HTTP Client / Browser]
         │  (GET /robWAF/teszt?id=1 UNION SELECT...)
         ▼
┌─────────────────────────────────────────────────────────┐
│  Apache TomEE 8.0.16 Plus (Container: rob-tomee)        │
│                                                         │
│  1. RobWafFilter (@WebFilter)                           │
│     ├─ Inspects & URL-decodes incoming HTTP requests    │
│     ├─ Blocks SQLi & XSS payloads -> HTTP 403 Forbidden │
│     └─ Publishes security alert to JMS Queue            │
│                         │                               │
│                         ▼                               │
│  2. Embedded ActiveMQ Broker (FeladatQueue)             │
│                         │                               │
│                         ▼                               │
│  3. RobQueueMdb (@MessageDriven)                        │
│     └─ Consumes alert & invokes @Stateless EJB          │
│                         │                               │
│                         ▼                               │
│  4. NaploKezeloBean (JTA + OpenJPA)                     │
└─────────────────────────┼───────────────────────────────┘
                          │ (JDBC / JTA DataSource: RobDS)
                          ▼
┌─────────────────────────────────────────────────────────┐
│  MySQL 8.0 (Container: rob-mysql)                       │
│  └─ Database: robdb | Table: SzerverNaplo               │
└─────────────────────────────────────────────────────────┘
```

## Key Features
- **Application-Level Security Filter (`RobWafFilter`):** Intercepts all HTTP requests (`/*`), normalizes URL-encoded payloads, detects SQL Injection and XSS attack vectors, and blocks malicious traffic with `403 Forbidden`.
- **Asynchronous Event Pipeline (JMS / ActiveMQ):** Decouples HTTP filtering from database I/O by publishing security events to `FeladatQueue`.
- **Enterprise Persistence (EJB & OpenJPA):** Consumes alerts via a Message-Driven Bean (`RobQueueMdb`) and persists audit logs into MySQL using container-managed JTA transactions.
- **Containerized Deployment:** Multi-container orchestration via `docker-compose.yml` with database healthchecks.

