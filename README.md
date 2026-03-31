# Inventory Reservation System

A Spring Boot application for managing inventory and order reservations with thread-safe concurrency handling.

## Project Structure

```
inventory-reservation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── order/
│   │   │           └── management/
│   │   │               └── inventoryreservation/
│   │   │                   ├── InventoryReservationApplication.java   # Main application entry point
│   │   │                   ├── controller/
│   │   │                   │   └── InventoryController.java          # REST API controller
│   │   │                   ├── dto/
│   │   │                   │   ├── OrderDto.java                     # Order data transfer object
│   │   │                   │   └── ProductDto.java                   # Product data transfer object
│   │   │                   ├── entity/
│   │   │                   │   ├── Order.java                        # Order entity
│   │   │                   │   └── Product.java                      # Product entity
│   │   │                   ├── exception/
│   │   │                   │   └── EntryNotFoundException.java       # Custom exception
│   │   │                   ├── repository/
│   │   │                   │   ├── OrderRepository.java             # Order repository
│   │   │                   │   └── ProductRepository.java            # Product repository with pessimistic lock
│   │   │                   ├── service/
│   │   │                   │   └── InventoryService.java            # Business logic
│   │   │                   └── util/
│   │   │                       └── StandardResponseDto.java          # Standard response DTO
│   │   └── resources/
│   │       └── application.properties         # Application configuration
│   └── test/
│       └── java/
│           └── com/
│               └── order/
│                   └── management/
│                       └── inventoryreservation/
│                           └── InventoryReservationApplicationTests.java
├── pom.xml                                          # Maven dependencies
└── mvnw / mvnw.cmd                                 # Maven wrapper scripts
```

## Technologies Used

| Category | Technology |
|----------|------------|
| **Framework** | Spring Boot 4.0.5 |
| **Language** | Java 21 |
| **Build Tool** | Apache Maven |
| **Database** | H2 (In-Memory) |
| **ORM** | Spring Data JPA / Hibernate |
| **Validation** | Spring Validation |
| **Lombok** | Annotation processing for boilerplate code |
| **DevTools** | Spring Boot DevTools |

## API Endpoints

### Base URL
```
http://localhost:9090/api/v1/inventory
```

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/products` | Create a new product | [`ProductDto`](#productdto) |
| `POST` | `/reserve` | Reserve product and create order | [`OrderDto`](#orderdto) |

### Request DTOs

#### ProductDto
```json
{
  "productName": "string",
  "availableQuantity": number
}
```

#### OrderDto
```json
{
  "productId": "string",
  "quantity": number
}
```

### Response Format
```json
{
  "code": number,
  "message": "string",
  "data": object | null
}
```

## Concurrency Handling

> ⚡ **This project implements concurrency handling using Pessimistic Locking**

The system uses **Pessimistic Locking** to handle concurrent reservation requests safely. This prevents race conditions when multiple users try to reserve the same product simultaneously.

### How It Works

1. **Database-Level Lock**: When a reservation request comes in, the system acquires an exclusive row-level lock on the product using `@Lock(LockModeType.PESSIMISTIC_WRITE)`.

2. **Atomic Operations**: The lock ensures that only one transaction can modify a product's quantity at a time, preventing:
   - Overselling (reserving more than available)
   - Lost updates
   - Race conditions

3. **Implementation**: In [`ProductRepository.java`](inventory-reservation/src/main/java/com/order/management/inventoryreservation/repository/ProductRepository.java:14):
   ```java
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("SELECT p FROM Product p WHERE p.productId = :id")
   Product findByIdForUpdate(@Param("id") String id);
   ```

4. **Transaction Management**: The [`InventoryService.java`](inventory-reservation/src/main/java/com/order/management/inventoryreservation/service/InventoryService.java:21) uses `@Transactional` to ensure atomic operations.

### Benefits
- Prevents concurrent modification conflicts
- Ensures data integrity
- Simple to implement and maintain
- Works reliably under high load

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Build and Run

```bash
# Navigate to project directory
cd inventory-reservation

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:9090`

### H2 Console
Access the in-memory database console at: `http://localhost:9090/h2-console`
- JDBC URL: `jdbc:h2:mem:inventory`
- Username: `user`
- Password: `user`

---

**Author**: Pubudu Sanka | pubudusanka79@gmail.com
