# patient-ms

## Summary
Microservice responsible for managing patient records. 
It exposes CRUD-style endpoints for patient data, plus two lightweight endpoints used internally by other microservices via Feign: 
`/exists` (used by note-ms before creating a note) and 
`/risk-profile` (used by risk-ms, returns only age + gender to minimize data exposure, per the project's Green Code constraint). 


Data is persisted in **PostgreSQL**.

## Endpoints

| Method | Path | Description | Role |
|---|---|---|---|
| GET | `/patient` | List all patients | ORGANIZER |
| POST | `/patient` | Create a new patient | ORGANIZER |
| GET | `/patient/{id}` | Get a patient by id | ORGANIZER, DOCTOR |
| PATCH | `/patient/{id}` | Partially update a patient | ORGANIZER |
| GET | `/patient/{id}/risk-profile` | Get age + gender of a patient by id | DOCTOR |
| GET | `/patient/{id}/exists` | Check whether a patient exists by id | DOCTOR |

## CURL EXAMPLE 
```bash
curl -X 'GET' \
'http://localhost:8080/patient/1' \
-H 'accept: application/json' \
-H 'Authorization: Bearer <token>'
```
Response : 

{
"id": 1,
"firstName": "John",
"lastName": "Doe",
"birthDate": "1980-05-02",
"gender": "M",
"address": "1 High St",
"phoneNumber": "200-333-4444"
}

