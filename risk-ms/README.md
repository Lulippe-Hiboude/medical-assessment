# RISK-MS

## Summary
Microservice computing a diabetes risk level for a patient. 
It aggregates data from two other services: 
    - notes content from note-ms (to count risk triggers) and 
    - age/gender from patient-ms. 

To respect the project's Green Code constraint, if the trigger count already determines `NONE` (0 or 1 trigger), the age/gender Feign call to patient-ms is skipped entirely. 

Stateless — no database of its own.

## Risk levels

`NONE`, `BORDERLINE`, `IN_DANGER`, `EARLY_ONSET`  per age/gender/trigger-count rules  
two edge cases were added : 
    - `UNKNOW` (trigger count below the < 30 age bracket's minimum threshold, not covered by the business rules),
    - `NO_DATA` (no notes found for the patient).

## Endpoints

| Method | Path | Description | Role |
|---|---|---|---|
| GET | `/risk/{id}` | Compute and return the risk level for a patient | DOCTOR |

## curl example

```bash
curl -X 'GET' \
'http://localhost:8080/risk/1' \
-H 'accept: application/json' \
-H 'Authorization: Bearer <token>'
```

Response :
"NONE"

