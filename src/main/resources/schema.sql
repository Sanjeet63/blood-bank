-- Blood Bank Management System - MySQL schema

CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(10)  NOT NULL          -- ADMIN / DONOR / HOSPITAL
);

CREATE TABLE IF NOT EXISTS donor (
    donor_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NULL,                      -- NULL for walk-in donors added by admin
    name          VARCHAR(100) NOT NULL,
    age           INT          NOT NULL,
    blood_group   VARCHAR(3)   NOT NULL,
    phone         VARCHAR(15),
    city          VARCHAR(50),
    last_donation DATE NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS blood_stock (
    stock_id    INT AUTO_INCREMENT PRIMARY KEY,
    blood_group VARCHAR(3) NOT NULL,
    units       INT        NOT NULL,
    expiry_date DATE       NOT NULL
);

CREATE TABLE IF NOT EXISTS donation (
    donation_id   INT AUTO_INCREMENT PRIMARY KEY,
    donor_id      INT  NOT NULL,
    units         INT  NOT NULL,
    donation_date DATE NOT NULL,
    FOREIGN KEY (donor_id) REFERENCES donor(donor_id)
);

CREATE TABLE IF NOT EXISTS blood_request (
    request_id   INT AUTO_INCREMENT PRIMARY KEY,
    hospital_id  INT          NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    blood_group  VARCHAR(3)   NOT NULL,
    units        INT          NOT NULL,
    status       VARCHAR(10)  NOT NULL DEFAULT 'PENDING',   -- PENDING / ISSUED / REJECTED
    request_date DATE         NOT NULL,
    FOREIGN KEY (hospital_id) REFERENCES users(user_id)
);

-- Tables are created automatically at startup by AppListener.
