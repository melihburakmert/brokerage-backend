# Brokerage Backend API

This project provides a backend API for a brokerage system, including asset management, order processing, and user authentication.

## Table of Contents
- [Overview](#overview)
- [API Endpoints](#api-endpoints)
    - [Authentication API](#authentication-api)
    - [Asset API](#asset-api)
    - [Order API](#order-api)
- [Setup](#setup)
- [Running in Docker Locally](#running-in-docker-locally)

## Overview

The Brokerage Backend API is built using Spring Boot and provides the following features:
- User authentication and registration
- Asset management for customers
- Order management for stock trading

## API Endpoints

### Authentication API

#### `POST /api/auth/login`
- **Description**: Authenticate a user and return a JWT token.
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Responses**:
    - `200 OK`: Returns a JWT token.
    - `401 Unauthorized`: Invalid credentials.

#### `POST /api/auth/register`
- **Description**: Register a new user and return a JWT token.
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Responses**:
    - `201 Created`: Returns a JWT token.
    - `409 Conflict`: Username already exists.

---

### Asset API

#### `GET /api/assets`
- **Description**: List all assets owned by a customer.
- **Query Parameters**:
    - `customerId` (required): The unique identifier of the customer.
- **Responses**:
    - `200 OK`: Returns a list of assets.
    - `404 Not Found`: Customer not found.

#### `POST /api/admin/assets/balance`
- **Description**: Set the TRY balance for a customer (admin only).
- **Request Body**:
  ```json
  {
    "customerId": "string",
    "balance": 10000.0
  }
  ```
- **Responses**:
    - `201 Created`: Balance set successfully.
    - `400 Bad Request`: Invalid parameters.
    - `403 Forbidden`: Admin privileges required.

---

### Order API

#### `GET /api/orders`
- **Description**: List orders for a customer with optional date range filtering.
- **Query Parameters**:
    - `customerId` (required): The unique identifier of the customer.
    - `fromDate` (optional): Start date for filtering orders (inclusive).
    - `toDate` (optional): End date for filtering orders (inclusive).
- **Responses**:
    - `200 OK`: Returns a list of orders.
    - `404 Not Found`: Customer not found.

#### `POST /api/orders`
- **Description**: Create a new buy or sell order for a customer.
- **Request Body**:
  ```json
  {
    "customerId": "string",
    "assetName": "string",
    "orderSide": "BUY or SELL",
    "size": 10.0,
    "price": 150.75
  }
  ```
- **Responses**:
    - `201 Created`: Order created successfully.
    - `400 Bad Request`: Validation error or insufficient assets.

#### `DELETE /api/orders/{orderId}`
- **Description**: Cancel a pending order.
- **Path Parameters**:
    - `orderId` (required): The unique identifier of the order.
- **Responses**:
    - `204 No Content`: Order successfully canceled.
    - `400 Bad Request`: Order cannot be canceled.
    - `404 Not Found`: Order does not exist.

#### `POST /api/admin/orders/{orderId}/match`
- **Description**: Match a pending order (admin only).
- **Path Parameters**:
    - `orderId` (required): The unique identifier of the order.
- **Responses**:
    - `200 OK`: Order matched successfully.
    - `400 Bad Request`: Order cannot be matched.
    - `404 Not Found`: Order does not exist.

---

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/brokerage-backend.git
   ```
2. Navigate to the project directory:
   ```bash
   cd brokerage-backend
   ```
3. Set up JWT secret key in `application.properties`. You can use https://jwtsecret.com/generate to generate a secret key.
   ```properties
   jwt.secret=your_jwt_secret_key
   ```

4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Running in Docker Locally

1. Add .env file under the docker directory with your environment variables:
   ```env
   JWT_SECRET=your_jwt_secret_key
   ```
2. Build the image:
    ```bash
    mvn jib:dockerBuild
    ```
3. Navigate to the docker directory:
    ```bash
    cd docker
    ```
4. Deploy the image to Docker:
    ```bash
      docker-compose up -d brokerage-backend
     ```