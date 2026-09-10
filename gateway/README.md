# GATEWAY

## Summary

Spring Cloud Gateway acting as the single entry point for the whole application (port 8080). 
It only routes requests to the appropriate microservice based on path prefix.

## Routes

| Path prefix | Target service | Port |
|---|---|---|
| `/auth/**` | auth-ms | 8082 |
| `/patient/**` | patient-ms | 8081 |
| `/notes/**` | note-ms | 8083 |
| `/risk/**` | risk-ms | 8084 |