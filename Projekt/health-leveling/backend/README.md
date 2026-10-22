# Health Leveling – Backend

Spring Boot 4.1 mit Java 21 und Maven.

## Starten

```bash
./mvnw spring-boot:run                    # H2 In-Memory (Standard)
```

- Health-Check: http://localhost:8080/actuator/health
- H2-Konsole: http://localhost:8080/h2-console (JDBC-URL `jdbc:h2:mem:healthleveling`, User `sa`)

Mit PostgreSQL:

```bash
cd ..
docker compose up -d
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Tests und Reports

```bash
./mvnw test        # Unit und Integration Tests (JUnit 5, Mockito, Spring Boot Test)
./mvnw verify      # zusätzlich Reports erzeugen
```

- Coverage (JaCoCo): `target/site/jacoco/index.html`
- Testreport (Surefire): `target/reports/surefire.html`
