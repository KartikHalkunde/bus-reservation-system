# Bus Reservation System (CLI + JDBC)

A CLI-based bus reservation system built with Java, JDBC, and MySQL. This project shows fundamentals like SQL queries, transactions, validation, and clean separation between CLI, service, and DAO layers.

## Features
- Register/login with unique username and hashed passwords (PBKDF2).
- List buses by route or view all buses.
- ASCII seat layout with booked/available legend.
- Seat booking with validation and transaction safety.
- View booking history and cancel with refund.
- Edit profile and change password from the account menu.
- Prevents booking for past dates.

## Tech Stack
- Java
- JDBC
- MySQL

## Setup
1. Copy `src/db.properties.example` to `src/db.properties`.
2. Update with your database credentials.
3. Ensure the MySQL JDBC driver exists at `lib/mysql-connector-j-9.5.0.jar`.
4. Create the database and tables using the SQL below.

## Database Schema (MySQL)
```sql
CREATE DATABASE IF NOT EXISTS bus_reservation;
USE bus_reservation;

CREATE TABLE passengers (
    passenger_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_id INT NOT NULL,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_passenger
        FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id)
);

CREATE TABLE buses (
    bus_id INT AUTO_INCREMENT PRIMARY KEY,
    bus_no VARCHAR(20) NOT NULL UNIQUE,
    source VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    fare DECIMAL(10,2) NOT NULL
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    bus_id INT NOT NULL,
    passenger_id INT NOT NULL,
    travel_date DATE NOT NULL,
    seat_no INT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_bus
        FOREIGN KEY (bus_id) REFERENCES buses(bus_id),
    CONSTRAINT fk_bookings_passenger
        FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id),
    CONSTRAINT uniq_seat UNIQUE (bus_id, travel_date, seat_no)
);
```

## Sample Data
```sql
INSERT INTO buses (bus_no, source, destination, capacity, fare) VALUES
('BUS-101', 'Pune', 'Mumbai', 40, 450.00),
('BUS-102', 'Mumbai', 'Goa', 36, 900.00),
('BUS-103', 'Delhi', 'Agra', 32, 350.00);
```

## Run
From the `src` folder:
```bash
javac -cp ".:../lib/mysql-connector-j-9.5.0.jar" *.java
java -cp ".:../lib/mysql-connector-j-9.5.0.jar" Main
```

## Usage
- Register a user with a unique username.
- Login with username/password.
- List buses, book seats by bus number, view bookings, and cancel for refund.
- Use View Account to edit profile or change password.

## Future Improvements
- Connection pooling with HikariCP.
- Unit tests for validation and DAO helpers.
- Admin flows for bus CRUD operations.
