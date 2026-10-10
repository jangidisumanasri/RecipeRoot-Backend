# RecipeRoot – Full-Stack Recipe Discovery and Meal Planning Platform

## About the Project

RecipeRoot is a full-stack web application designed to help users discover, organize, and manage recipes. It provides a simple interface for exploring recipes and supports features such as user registration and login, saved recipes, weekly meal planning, ratings and reviews, and administrative management, depending on the features implemented in the application.

## Technology Stack

**Frontend**
- React
- Vite
- HTML and CSS
- JavaScript

**Backend**
- Java
- Spring Boot
- Maven
- REST APIs

**Database**
- MySQL

## Main Features

- Browse and discover recipes
- Register and log in to an account
- Save favourite recipes
- Plan meals for the week
- Rate and review recipes
- Manage recipes through administrative functionality

## Project Structure

```text
RecipeRoot-Backend/
├── src/
├── .mvn/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Running the Backend Locally

1. Install Java and Maven-compatible build tools.
2. Configure your MySQL database and application environment variables.
3. Open a terminal in the backend project directory.
4. Run:

```bash
mvn spring-boot:run
```

5. Configure the frontend to use the backend's local URL.

## Repository

Backend source code for the RecipeRoot project.

## Note

Database credentials and other sensitive configuration values should be supplied through environment variables and must not be committed to the repository.
