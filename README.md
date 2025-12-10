# Courier Tracking (Extended Version)

A small Spring Boot service that tracks courier locations, calculates the distance to nearby Migros stores, and logs store entrance events.  
This **v2 / extended** version adds a real database, UUID-based entities, auditing fields, and a cleaner service / repository architecture.

---

## 1. Project Goals

- Accept location updates for couriers.
- Calculate the distance to each store using the **Haversine** formula.
- Detect when a courier enters a store radius and persist this as a **store entrance log**.
- Allow querying a courier’s entrance history via REST APIs.
- Keep the codebase simple enough for demos, interviews, and experiments.

---

## 2. What Changed Compared to the First Version?

Compared to the initial “in-memory” version of the project:

1. **Relational database added**
   - Uses **H2 in-memory** by default (`jdbc:h2:mem:courierdb`).
   - Can be re-configured for PostgreSQL / other RDBMS through Liquibase.

2. **Liquibase migrations**
   - All schema changes and initial data are defined under  
     `src/main/resources/db/changelog/db.changelog-master.xml`.
   - Creates tables:
     - `courier`
     - `store`
     - `store_entrance_log`
   - Inserts initial stores (Ataşehir, Novada, Beylikdüzü, Ortaköy, Caddebostan, …).

3. **UUID entities with audit fields**
   - All entities extend a common `BaseEntity` with:
     - `id : UUID`
     - `createTime : Instant`
     - `createUser : String`
     - `lastModifiedTime : Instant`
     - `lastModifiedUser : String`

4. **Domain model extended**
   - `Courier`, `Store`, `StoreEntranceLog` entities are now persisted.
   - Store / courier info is no longer only in `stores.json`.

5. **Service / repository separation**
   - Read/write logic lives in services:
     - `LocationService`
     - `CourierQueryService`
   - Persistence handled via Spring Data JPA repositories:
     - `CourierRepository`
     - `StoreRepository`
     - `StoreEntranceLogRepository`
     - `CourierTrackRepository` (if present)

6. **DTOs as Java records**
   - REST layer uses immutable **records** for request / response models
     (in the `dto` package), instead of exposing entities.

---

## 3. Tech Stack

- **Language:** Java 17  
- **Framework:** Spring Boot 4.x (Web, Data JPA, Validation, DevTools)
- **Database (default):** H2 in-memory (`MODE=PostgreSQL`)
- **Schema management:** Liquibase 5.x
- **API docs:** springdoc-openapi + Swagger UI
- **Build tool:** Maven (with Maven Wrapper `mvnw`)

---

## 4. Project Structure

Key packages:

- `org.courier.couriertracking`
  - `config` – configuration (H2 console, etc.)
  - `controller` – REST controllers
  - `distance` – Haversine distance calculation and helpers
  - `domain` – JPA entities (`Courier`, `Store`, `StoreEntranceLog`, `BaseEntity`)
  - `dto` – request/response records
  - `event` – domain events & handlers (location updated, store entrance, distance)
  - `repository` – Spring Data JPA repositories
  - `service` – `LocationService`, `CourierQueryService`
- `src/main/resources`
  - `application.properties` – DB & Liquibase config
  - `db/changelog/db.changelog-master.xml` – Liquibase master changelog
  - `stores.json` – original store list (still kept as reference/sample)

---

## 5. Running the Application

### 5.1 Prerequisites

- JDK **17**
- Git (optional, if cloning)
- No need to install Maven; project uses the **Maven Wrapper (`mvnw`)**

### 5.2 Optional: Personal Maven settings

If you have a personal Maven settings file (for proxies, private repos, etc.), you can define:

```bash
export PERSONAL_SETTINGS="$HOME/.m2/settings-personal.xml"
```

Then use this small helper when building:

```bash
if [ -n "$PERSONAL_SETTINGS" ]; then
  ./mvnw -s "$PERSONAL_SETTINGS" clean install
else
  ./mvnw clean install
fi
```

If you don’t need special settings, just run:

```bash
./mvnw clean install
```

### 5.3 Start the application

From the project root:

```bash
./mvnw spring-boot:run
```

or run `CourierTrackingApplication` from your IDE.

The app will start on:

- `http://localhost:8080`

---

## 6. Database & Liquibase

### 6.1 Default configuration (H2 in-memory)

`src/main/resources/application.properties` (simplified):

```properties
spring.application.name=courier-tracking

# H2 datasource
spring.datasource.url=jdbc:h2:mem:courierdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

# H2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### 6.2 H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:courierdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
- User: `sa`
- Password: *(empty)*

Once connected, you should see tables such as:

- `COURIER`
- `STORE`
- `STORE_ENTRANCE_LOG`

and initial rows in `STORE` for the Migros locations.

---

## 7. Domain Model (Extended Version)

### 7.1 BaseEntity

All entities extend a common base with UUID ID and audit fields (mapped to columns like `id`, `create_time`, `create_user`, etc.).  
The ID is generated via `UUID` (not numeric sequences).

### 7.2 Entities

- **Courier**
  - `code` (e.g. `"c1"`)
  - optional additional metadata
  - one-to-many relation with `StoreEntranceLog`

- **Store**
  - `name` (`unique`)
  - `lat`, `lng`
  - one-to-many relation with `StoreEntranceLog`

- **StoreEntranceLog**
  - `courier` (ManyToOne)
  - `store` (ManyToOne)
  - `entranceTime` (`Instant`)
  - `distanceMeters` (Double; distance when the courier entered)
  - extends `BaseEntity` (UUID + audit fields)

### 7.3 DTOs (records)

REST endpoints do *not* expose entities directly. Instead, they use Java **records**, for example:

- `CourierEntranceResponse`
- `StoreSummary`
- `LocationUpdateRequest`
- etc.

These live in the `dto` package and are mapped from/to entities in the service layer.

---

## 8. HTTP API (High-Level)

Typical endpoints:

- **Send a location update**

  ```http
  POST /locations
  Content-Type: application/json

  {
    "courierCode": "c1",
    "latitude": 40.99,
    "longitude": 29.12,
    "timestamp": "2025-12-09T20:47:44.999Z"
  }
  ```

  Flow:
  1. `LocationService` persists the track / fires a `LocationUpdatedEvent`.
  2. A listener calculates distances to all stores using Haversine.
  3. If a store is within the configured radius, a `StoreEntranceLog` is created.

- **Get a courier’s entrance logs**

  ```http
  GET /couriers/{courierCode}/entrances
  ```

  Returns a list of records (DTOs) describing when and where the courier entered each store.

- **API documentation**

  Swagger UI should be available at:

  - `http://localhost:8080/swagger-ui.html`

---

## 9. Possible Next Steps

Ideas for further improvement:

- Add authentication / API keys.
- Add pagination / filtering to query endpoints.
- Externalize entrance radius and store list into configuration.
- Add profiles for **PostgreSQL** or **Oracle** instead of H2.
- Export metrics (Micrometer / Prometheus) for distances and entrance counts.

---
