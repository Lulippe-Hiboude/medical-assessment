# NOTE-MS

## Summary
Microservice responsible for managing medical notes attached to a patient's history. 
Before creating a note, it checks (via a Feign call to patient-ms)that the target patient exists, returning a 404 if not. 
It also exposes an endpoint returning only note content (used by risk-ms to scan notes for diabetes risk triggers). 


Data is persisted in **MongoDB**

## Endpoints

| Method | Path | Description | Role |
|---|---|---|---|
| POST | `/notes` | Create a note for a patient | DOCTOR |
| GET | `/notes/patient/{patientId}` | Get all notes for a patient | DOCTOR |
| GET | `/notes/patient/{patientId}/note-content` | Get all notes' content only for a patient (used by risk-ms) | DOCTOR |

## CURL EXAMPLE
```bash
curl -X 'GET' \
  'http://localhost:8080/notes/patient/1' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer <token>'
```

Response:
[
    {
        "id": "123456789",
        "patientId": 1,
        "content": "This is a note about the patient's condition.",
        "createdAt": "2025-06-01T12:00Z"
    }
]