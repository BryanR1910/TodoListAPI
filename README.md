# Todo List API

RESTful API for managing personal to-do lists with secure authentication, authorization, refresh token rotation, rate limiting, pagination, and filtering.

This project is based on the Todo List API challenge from roadmap.sh (https://roadmap.sh/projects/todo-list-api
) and extends the original scope by incorporating production-level backend concerns such as JWT security, refresh token persistence, rate limiting, validation, structured error handling, and comprehensive unit testing.
---

## Features

Authentication & Security

* User registration with password hashing
* Login with credential validation
* JWT-based authentication
* Refresh token rotation with revocation
* Authorization per resource owner
* Global exception handling
* Input validation
* Rate limiting using Bucket4j

To-Do Management

* Create tasks
* Update tasks (owner-only)
* Delete tasks (owner-only)
* Paginated retrieval
* Filtering and sorting support

Testing

* Unit tests with JUnit 5
* Mockito for dependency isolation
* JaCoCo for coverage reporting
* Service layer fully tested in isolation

---

## Tech Stack

* Java
* Spring Boot
* Spring Security
* JWT
* JUnit 5
* Mockito
* JaCoCo
* Bucket4j
* Docker & Docker Compose
* Relational Database

---

## API Endpoints

### Authentication

POST `/register`

Request:

```json
{
  "name": "John Doe",
  "email": "john@doe.com",
  "password": "password"
}
```

Response:

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token"
}
```

---

POST `/login`

Request:

```json
{
  "email": "john@doe.com",
  "password": "password"
}
```

Response:

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token"
}
```

---

POST `/refresh`

Request:

```json
{
  "refreshToken": "refresh-token"
}
```

Response:

```json
{
  "accessToken": "new-access-token",
  "refreshToken": "new-refresh-token"
}
```

---

### To-Do Operations

All endpoints require:

```
Authorization: Bearer <accessToken>
```

Create To-Do
POST `/todos`

Update To-Do
PUT `/todos/{id}`
(returns 403 if user is not the owner)

Delete To-Do
DELETE `/todos/{id}`
(returns 204 on success)

Get To-Dos
GET `/todos?page=1&limit=10`

Response:

```json
{
  "data": [],
  "page": 1,
  "limit": 10,
  "total": 25
}
```

Supports pagination, filtering and sorting.

---

## Error Handling

Standardized HTTP responses:

* 400 – Validation error
* 401 – Unauthorized
* 403 – Forbidden
* 404 – Not found

---

## Running the Application

The project is fully containerized.

### Requirements

* Docker
* Docker Compose

### Start the application

From the root directory:

```
docker compose up --build
```

The API will be available at:

```
http://localhost:8080
```

The database is automatically provisioned and connected via Docker Compose.

No additional local setup is required.

---

## Testing

To run unit tests locally:

```
./mvnw test
```

JaCoCo coverage report will be generated after test execution.