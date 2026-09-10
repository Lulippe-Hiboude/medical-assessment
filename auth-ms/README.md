# AUTH-MS

## SUMMARY
Authentication microservice
It exposes a login endpoint that verifies a user's credentials (stored in memory : `organizer` , `doctor`) and
on success, generates a stateless JWT used by the other microservices to authenticate and authorise requests (via their own JWT Filter).

## ENDPOINTS

| Method | Path          |Description |
|--------|---------------|------------|
| POST   | `/auth/login` | Authenticates a user (username/ password) and return a JWT on sucess |

## CURL EXAMPLE
```bash
curl -X 'POST' \
'http://localhost:8080/auth/login' \
-H 'accept: application/json' \
-H 'Content-Type: application/json' \
-d '{
"username": "doctor",
"password": "password"
}'
```

Response :
{
    "token":"eyJhbGciOiJIUzI..."
}


