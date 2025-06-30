# Library Management System

## 📚 Overview

This is a **Library Management System** built as an educational project to demonstrate backend development using Spring Boot. The system provides a REST API with comprehensive functionality for managing books, authors, users, and borrowing operations in a library setting.

## 🎓 Educational Purpose

**⚠️ IMPORTANT DISCLAIMER:**
This project is created **for educational and learning purposes**. It is designed to help understand:
- Spring Boot application architecture
- RESTful API design and implementation
- JWT-based authentication and authorization

**This project should NOT be used in production environments without proper security auditing and enhancements.**

## ✨ Features

### Core Functionality
- **User Management**: Registration, authentication, and profile management
- **Book Management**: Add, update, delete, and search books
- **Author Management**: Manage author information and book associations
- **Category Management**: Organize books by categories
- **Borrowing System**: Book checkout and return functionality
- **Fine Management**: Automatic fine calculation for overdue books
- **JWT Authentication**: Secure API access with token-based authentication


## 🛠️ Technology Stack

- **Java 17+** - Programming language
- **Spring Boot 3.x** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **JWT** - Token-based authentication
- **Maven** - Dependency management and build tool
- **PostgreSQL** - Database

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/MU-N/library-management-system-spring-boot
cd library
```

### 2. Run the Application

#### Option 1: Using Maven Wrapper (Recommended)
```bash
./mvnw spring-boot:run
```

#### Option 2: Using Maven directly
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access the API
You can test the API using:
- **Postman** or **Insomnia** for API testing
- **curl** commands
- **Database console** or **pgAdmin** (for PostgreSQL management)


## 🏗️ Project Structure

```
library/
├── src/main/java/com/nasser/library/
│   ├── config/              # Configuration classes
│   ├── controller/          # REST controllers
│   ├── model/
│   │   ├── entity/         # JPA entities
│   │   └── dto/            # Data Transfer Objects
│   ├── repository/         # Data access layer
│   ├── service/            # Business logic layer
│   ├── util/               # Utility classes
│   └── LibraryApplication.java
├── src/main/resources/
│   └── application.properties
└── src/test/               # Test files
```

## 🔐 Security Features

- **Password Encryption**: BCrypt hashing
- **JWT Authentication**: Secure token-based authentication
- **Role-based Access**: Different permissions for users and admins
- **Input Validation**: Comprehensive request validation

