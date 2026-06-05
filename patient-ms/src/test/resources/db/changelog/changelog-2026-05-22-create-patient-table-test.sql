-- liquibase formatted sql

--changeset caroline:1
--comment MA-3 Create patient table

CREATE TABLE IF NOT EXISTS patient
(
    patient_id   BIGSERIAL PRIMARY KEY,
    last_name    VARCHAR(100) NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    birth_date   DATE         NOT NULL,
    gender       VARCHAR(10)  NOT NULL,
    phone_number VARCHAR(20),
    address      VARCHAR(255)
);
