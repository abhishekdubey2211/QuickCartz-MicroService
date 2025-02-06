# QuickCarts Microservice Project Documentation

## Overview
The QuickCarts microservice architecture consists of multiple independent services that interact with each other to provide a seamless shopping experience. The key services include:

- **User Service**: Manages user accounts, authentication, and user-related details. It supports user registration, retrieval, update, and management of user addresses.
- **Product Service**: Handles product creation, retrieval, updating, and deletion. It also supports inventory management, categorization, brand filtering, and custom search filters.
- **Order Service**: Manages order placement, retrieval of order details, cancellation of orders, and individual item removal from orders.
- **Cart Service**: Facilitates adding products to the cart, removing individual items, flushing the cart, and associating a cart with a user.
- **API Gateway**: Provides a centralized entry point for user-related operations and ensures seamless interaction between microservices.
- **Configuration Service**: Manages application configurations and system settings, including fetching actuator information and application-specific configurations.

## Microservices & API Endpoints

### 1. User Service
**Base URL**: `http://localhost:8085/api/user`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | POST | Add a new user |
| `/{userId}` | GET | Get user details by ID |
| `/` | PUT | Update user details |
| `/getall_users` | GET | Retrieve all users |
| `/api/address/user/{userId}` | POST | Add address for a user |
| `/api/address/user/{userId}` | PUT | Update address by user ID |
| `/api/address/user/{userId}/{addressId}` | GET | Get address by user and address ID |
| `/api/address/user/{userId}` | GET | Get all addresses for a user |

### 2. Product Service
**Base URL**: `http://localhost:8086/api/product`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | POST | Add a new product |
| `/{productId}` | GET | Get product details by ID |
| `/` | PUT | Update product details |
| `/paginated` | GET | Get paginated product list |
| `/in-stock` | GET | Get all in-stock products |
| `/out-of-stock` | GET | Get out-of-stock products |
| `/category/{categoryName}` | GET | Get products by category |
| `/brand/{brandName}` | GET | Get products by brand |
| `/{productId}` | DELETE | Delete a product by ID |
| `/add/filter` | POST | Apply custom filters to product search |

### 3. Order Service
**Base URL**: `http://localhost:8087/api/order`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/{userId}` | POST | Place an order for a user |
| `/details/{orderId}` | GET | Get order details by ID |
| `/user/{userId}` | GET | Get all orders by user ID |
| `/cancel/{orderId}` | DELETE | Cancel an order |
| `/cancel_item/{orderId}/{productId}` | DELETE | Cancel a specific product from an order |

### 4. Cart Service
**Base URL**: `http://localhost:8088/api/cart`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/{userId}` | POST | Add a product to user’s cart |
| `/flushproduct/{cartId}` | DELETE | Remove all products from cart |
| `/{cartId}/{productId}` | DELETE | Remove a specific product from cart |
| `/create/{userId}` | POST | Associate a cart to a user |

### 5. API Gateway
**Base URL**: `http://localhost:9093/api`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/user/{userId}` | GET | Fetch user details through API Gateway |

### 6. Configuration Service
**Base URL**: `http://localhost:9095`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/actuator/info` | GET | Fetch actuator information |
| `/application/user` | GET | Fetch default user configuration |

## Architecture Overview
The QuickCarts microservice system follows a distributed architecture where each service operates independently and communicates via REST APIs. The system leverages:
- **Spring Boot** for microservice development
- **Spring Cloud Gateway** for API management
- **Eureka Service Registry** for service discovery
- **MySQL** for persistent storage
- **Redis** for caching frequently accessed data
- **JWT** for authentication and security

## Conclusion
This document provides an in-depth overview of the QuickCarts microservices and their respective API endpoints. Each service is designed for scalability, maintainability, and flexibility for future enhancements. Additional details, including database schema, request validations, and authentication mechanisms, can be added as needed.

