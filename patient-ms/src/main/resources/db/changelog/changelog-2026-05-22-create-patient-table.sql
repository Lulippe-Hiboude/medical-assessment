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

-- changeset caroline:2
-- comment MA-8 Seed patient test data

INSERT INTO patient (last_name, first_name, birth_date, gender, address, phone_number)
VALUES
    ('TestNone', 'Test', '1966-12-31', 'F', '1 Brookside St', '100-222-3333'),
    ('TestBorderline', 'Test', '1945-06-24', 'M', '2 High St', '200-333-4444'),
    ('TestInDanger', 'Test', '2004-06-18', 'M', '3 Club Road', '300-444-5555'),
    ('TestEarlyOnset', 'Test', '2002-06-28', 'F', '4 Valley Dr', '400-555-6666');


