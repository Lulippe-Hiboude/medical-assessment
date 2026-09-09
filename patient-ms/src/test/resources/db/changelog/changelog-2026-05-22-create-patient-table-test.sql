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

--changeset caroline:2
--comment MA-3 Seed test patients for integration tests
INSERT INTO patient (patient_id, last_name, first_name, birth_date, gender, phone_number, address)
VALUES
(1,'Doe', 'John', '1980-01-01', 'M', '123-456-7890', '1 High Street'),
(2,'Smith', 'Sandra', '1990-05-12', 'F', '234-567-8901', '12 Baker Street');

--changeset caroline:3
--comment MA-3 Resync patient_id sequence to avoid conflicts with future inserts
SELECT setval('patient_patient_id_seq', (SELECT MAX(patient_id) FROM patient));
