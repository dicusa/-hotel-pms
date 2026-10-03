# Hotel Property Management System (PMS)

This repository contains the initial setup for the Hotel PMS project.

## Project Structure

- `src/main/java` – Application source code.
- `src/test/java` – Unit tests.
- `docs` – Documentation files.
- `pom.xml` – Maven build configuration.

## Building the Project

```bash
mvn clean test        # Run unit tests
mvn package           # Build the JAR (output in target/)
```

## Running the Application

```bash
java -jar target/hotel-pms-0.1.0.jar
```

You should see the output:

```
Hotel PMS started
```

Further development will expand this skeleton into a full-featured property management system.
