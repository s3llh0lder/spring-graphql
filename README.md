# Spring GraphQL Microservice

A comprehensive example of a Spring Boot application with GraphQL API, demonstrating CRUD operations with JPA entities and comprehensive testing.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Database](#database)
- [GraphQL Playground](#graphql-playground)
- [Example Queries and Mutations](#example-queries-and-mutations)
- [Contributing](#contributing)

## Overview

This project demonstrates how to build a modern GraphQL API using Spring Boot. It includes:

- User management with CRUD operations
- UUID-based entity identification
- JPA integration with H2 database
- Comprehensive unit and integration tests
- GraphQL schema-first approach

## Features

- ✅ **GraphQL API** - Query and mutation operations
- ✅ **Spring Data JPA** - Database operations with repository pattern
- ✅ **UUID Primary Keys** - Globally unique identifiers
- ✅ **H2 Database** - In-memory database for development and testing
- ✅ **Comprehensive Testing** - Unit tests and integration tests
- ✅ **GraphQL Playground** - Interactive API exploration
- ✅ **Error Handling** - Proper exception handling and validation
- ✅ **Spring Boot Actuator** - Monitoring and health checks

## Technologies Used

- **Java 24** - Programming language
- **Spring Boot 3.5.4** - Application framework
- **Spring GraphQL** - GraphQL integration
- **Spring Data JPA** - Data access layer
- **H2 Database** - In-memory database
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Gradle** - Build tool

## Project Structure

## Getting Started

### Prerequisites

- Java 24 or higher
- Gradle 7.x or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url> cd spring-graphql

2. Build the project:
```bash
./gradlew build

3. Run the application:
```bash
./gradlew bootRun

4. The application will start on 'http://localhost:8080'

## API Documentation

### GraphQL Schema

The API supports the following operations:

#### Queries
- `users`: Get all users
- `user(id: ID!)`: Get a user by UUID

#### Mutations
- `createUser(name: String!, email: String!)`: Create a new user
- `updateUser(id: ID!, name: String!, email: String!)`: Update an existing user
- `deleteUser(id: ID!)`: Delete a user

#### Types

## Testing

The project includes comprehensive testing at multiple levels:

### Run All Tests
```bash
./gradlew test
```

### Testing

1. **Unit Tests**
    - `UserServiceTest` - Tests service layer logic
    - `UserControllerTest` - Tests controller logic with mocks

2. **Integration Tests**
    - `UserControllerIntegrationTest` - Tests GraphQL endpoints end-to-end
    - `SpringGraphqlApplicationTests` - Tests application context loading

### Test Coverage

- Service layer: Complete CRUD operations, error handling
- Controller layer: GraphQL resolvers, UUID validation
- Integration: Full GraphQL query/mutation testing

## Database

### H2 Database Configuration

The application uses H2 in-memory database for development:

- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`
- **Console**: Available at `http://localhost:8080/h2-console`

### Database Schema

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);
```

## GraphQL Playground

Access the GraphQL Playground at: `http://localhost:8080/graphiql`

This provides an interactive interface to:
- Explore the schema
- Write and test queries
- View documentation
- Execute mutations

## Example Queries and Mutations

### Query All Users
```graphql
query {
  users {
    id
    name
    email
  }
}
```

### Query User by ID
```graphql
query GetUser($id: ID!) {
  user(id: $id) {
    id
    name
    email
  }
}
```

**Variables:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000"
}
```

### Create User
```graphql
mutation CreateUser($name: String!, $email: String!) {
  createUser(name: $name, email: $email) {
    id
    name
    email
  }
}
```

**Variables:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

### Update User
