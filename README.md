# Earthquake Data Visualization API

A Spring Boot REST API that fetches real-time earthquake data from the USGS service, filters relevant events, stores them in a PostgreSQL database, and exposes endpoints for retrieval and analysis.

---

## Features

- Fetch live earthquake data from USGS GeoJSON API
- Filter earthquakes by magnitude (> 2.0)
- Filter earthquakes by time (last hour / custom timestamp)
- Store processed data in PostgreSQL
- REST API for querying stored earthquake data
- Clean layered architecture (Controller / Service / Repository)

---


## Project Structure

src/main/java/org/example/earthquakeapp
├── Model
├── Repository
├── Service
└── Web

---

## ⚙️ Setup Instructions

### 1. Clone repository

```bash
git clone https://github.com/your-username/earthquake-app.git
cd earthquake-app
```

### 2. Create a local database (ex. CREATE DATABASE earthquakes;)
### 3. Configure application
Add an application.properties file in the ```resources``` folder with the following contents:

```
spring.application.name=EarthquakeApp
spring.datasource.url=jdbc:postgresql://localhost:5432/<dbName>
spring.datasource.username=postgres
spring.datasource.password=<yourPassword>

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

4. Build and run
```
mvn clean install
mvn spring-boot:run
```

5. Visit the http://localhost:8080/landing_page.html to access all functionalities described above through the app's UI.
   
