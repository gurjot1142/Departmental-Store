# Departmental-Store

This is a departmental management system that tracks product inventory, customer information, and orders placed by the customers. The system allows customers to place orders for products, with support for backorders when inventory is unavailable.

## Table of Contents

- [Requirements](#requirements)
- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)
- [Contributing](#contributing)

## Requirements

- Java 11 or higher
- MySQL server 8 or higher

## Features

1. Product Inventory
   - Stores information about products, including ID, description, name, price, expiry, count, and availability.
   - Allows updating the inventory count and availability.
   - Search for a product by its name.
   - Add product info via excel.
2. Customer
   - Stores customer details such as ID, full name, address, and contact number.
   - Email and contact validation with regex.
3. Order
   - Tracks orders with information like order ID, product ID, customer ID, order timestamp, and quantity.
   - Supports placing orders for products, and also placing them as backorders when inventory count is zero or availability is false.
4. Backorders
   - Keeps a record of orders for products that are currently unavailable.
   - Backorders are removed when product quantity gets sufficient via cronjob.

## Technologies

The inventory management system is built using the following technologies:

- Programming Language: Java
- Database: MySQL
- Frameworks/Libraries: Spring Boot
- Testing: Postman
- Swagger: http://localhost:9111/swagger-ui.html

## Setup

1. Clone the repository to your local machine.
   ```
   git clone https://github.com/gem-gurjotsingh/DepartmentalStoreCrud.git
   ```
2. Import the project into your preferred IDE (such as Eclipse or IntelliJ) as a Maven project.
3. Update the `application.properties` file in the `src/main/resources` directory with your MySQL database configuration.
4. Build and run the application using Maven. From the command line, navigate to the project directory and run:
   ```
   mvn spring-boot:run
   ```
5. Access the application by visiting `http://localhost:9111` in your web browser.

## Contributing

If you find a bug or have an idea for a new feature, feel free to open an issue or submit a pull request. Contributions are always welcome!
