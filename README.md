# Fee Calculation

This project uses Docker Compose to manage an Apache Airflow environment with other dependent services like PostgreSQL, Redis, and a custom application (fee-app).

## Project Structure

The project is structured around Docker and Docker Compose, with the following key services:

- Apache Airflow (version 2.7.1)
- PostgreSQL (version 13)
- Redis (latest version)
- Custom application (fee-app)
  - Spring Boot (version 3.1.3)
  - Java 17 as the target JVM
  - Kotlin (version 1.8.22)
  - H2 Database
  - Jackson Module for Kotlin

## Unit Tests

The application includes unit tests to ensure code quality and correctness. The tests can be run using the following command:
bash

```bash
./gradlew test
```

## Airflow Workflows

The project uses Apache Airflow to manage complex tasks. You'll find the Airflow workflows, also known as directed acyclic graphs (DAGs), in the dags directory.

Currently, we have one workflow file, fees_workflow.py. This file controls a series of tasks related to fees processing.


## Prerequisites

- Docker and Docker Compose installed on your machine.
- Basic understanding of Docker and Docker Compose.

## Getting Started

1. Clone the repository to your local machine.

2. Navigate to the project directory.

3. Build the Docker images:

```bash
docker-compose build
```

4. Start the services:

```bash
docker-compose up
```

The Apache Airflow webserver will be accessible at `localhost:8080`, and the custom application (fee-app) will be accessible at `localhost:8008`.

## Services

The Docker Compose file defines the following services:

- `fee-app`: This is a custom application. It's built from the Dockerfile in the current directory.

- `postgres`: This service uses the `postgres:13` Docker image and serves as the database for the Airflow application.

- `redis`: This service uses the latest Redis Docker image and serves as the message broker for Celery.

- `airflow-webserver`, `airflow-scheduler`, `airflow-worker`, `airflow-triggerer`, and `airflow-init`: These services make up the Apache Airflow environment.

## Note

The `airflow-init` service performs some initial setup tasks, such as checking the Airflow version and ensuring there's enough resources to run Airflow. Please make sure your machine meets the requirements specified in the docker-compose file.
