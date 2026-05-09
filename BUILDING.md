# Building and Setup

## Prerequisites
- Java (JDK 8+)
- MySQL 8+
- MySQL JDBC connector JAR

## Get the MySQL JDBC connector
This repo does not include the driver JAR. Download the MySQL Connector/J from the official site and place it at:

`lib/mysql-connector-j-9.5.0.jar`

If you choose a different version, update the JAR name in the run commands below.

## Configure database
1. Copy `src/db.properties.example` to `src/db.properties`.
2. Update the file with your MySQL credentials.

## Create schema
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

## Seed sample data (optional)
```sql
INSERT INTO buses (bus_no, source, destination, capacity, fare) VALUES
('BUS-101', 'Pune', 'Mumbai', 40, 450.00),
('BUS-102', 'Mumbai', 'Goa', 36, 900.00),
('BUS-103', 'Delhi', 'Agra', 32, 350.00);
```

## Build and run
Run the commands from the `src` folder:
```bash
javac -cp ".:../lib/mysql-connector-j-9.5.0.jar" *.java
java -cp ".:../lib/mysql-connector-j-9.5.0.jar" Main
```
