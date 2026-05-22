-- liquibase formatted sql

--changeset caroline:1
--comment MA-3 Create patient and address tables

CREATE TABLE IF NOT EXISTS address
(
    address_id    BIGSERIAL PRIMARY KEY,
    street_number VARCHAR(20)  NOT NULL,
    street        VARCHAR(255) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    state         VARCHAR(100) NOT NULL,
    postal_code   VARCHAR(20)  NOT NULL,
    country       VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS patient
(
    patient_id   BIGSERIAL PRIMARY KEY,
    last_name    VARCHAR(100) NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    birth_date   TIMESTAMP    NOT NULL,
    gender       VARCHAR(10)  NOT NULL,
    phone_number VARCHAR(20),
    address_id   BIGINT,
    CONSTRAINT fk_patient_address
        FOREIGN KEY (address_id) REFERENCES address (address_id)
);


