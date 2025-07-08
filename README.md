# Tree Sales Management System - Web Application

A modern Spring Boot web application converted from the original JavaFX desktop application for managing tree sales, scouts, and tree types.

## 🌟 Features

- **Tree Management**: Track tree inventory with barcodes, status monitoring, and type categorization
- **Scout Management**: Manage scout profiles, troop organization, and status tracking
- **Tree Type Management**: Define tree types with pricing and barcode prefixes
- **RESTful API**: Comprehensive REST API for all operations
- **Modern Web UI**: Bootstrap-powered dashboard with real-time statistics
- **Database Integration**: MariaDB/MySQL support with JPA/Hibernate

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MariaDB or MySQL database
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd tree-sales-web
   ```

2. **Set up the database**
   ```sql
   CREATE DATABASE tree_sales_db;
   CREATE USER 'treesales'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON tree_sales_db.* TO 'treesales'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure database connection**
   
   Create `src/main/resources/application-local.properties`:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/tree_sales_db
   spring.datasource.username=treesales
   spring.datasource.password=password
   ```

   Or set environment variables:
   ```bash
   export DB_USERNAME=treesales
   export DB_PASSWORD=password
   ```

4. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the application**
   - Dashboard: http://localhost:8080
   - API Base URL: http://localhost:8080/api

## 📊 Dashboard

The main dashboard provides:
- Real-time statistics for trees, scouts, and tree types
- Status breakdown (Available, Sold, Damaged trees)
- Scout activity monitoring
- Quick access to API documentation

## 🔧 API Documentation

### Trees API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/trees` | Get all trees |
| GET | `/api/trees/{barcode}` | Get tree by barcode |
| POST | `/api/trees` | Create new tree |
| PUT | `/api/trees/{barcode}` | Update tree |
| DELETE | `/api/trees/{barcode}` | Delete tree |
| POST | `/api/trees/{barcode}/sell` | Mark tree as sold |
| POST | `/api/trees/{barcode}/damage` | Mark tree as damaged |
| GET | `/api/trees/available` | Get available trees |
| GET | `/api/trees/status/{status}` | Get trees by status |
| GET | `/api/trees/search/barcode?pattern={pattern}` | Search by barcode |

#### Tree Object Structure
```json
{
  "barcode": "string (max 20 chars)",
  "treeType": "string (required)",
  "notes": "string (max 200 chars)",
  "status": "Available|Sold|Damaged|Inactive",
  "dateStatusUpdated": "yyyy-MM-dd"
}
```

### Scouts API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/scouts` | Get all scouts |
| GET | `/api/scouts/{id}` | Get scout by ID |
| POST | `/api/scouts` | Create new scout |
| PUT | `/api/scouts/{id}` | Update scout |
| DELETE | `/api/scouts/{id}` | Delete scout |
| GET | `/api/scouts/active` | Get active scouts |
| GET | `/api/scouts/troop/{troopId}` | Get scouts by troop |
| POST | `/api/scouts/{id}/inactive` | Set scout inactive |
| GET | `/api/scouts/search/name?name={name}` | Search by name |

#### Scout Object Structure
```json
{
  "id": "number (auto-generated)",
  "firstName": "string (required, max 20 chars)",
  "middleName": "string (max 20 chars)",
  "lastName": "string (required, max 20 chars)",
  "dateOfBirth": "yyyy-MM-dd",
  "phoneNumber": "string",
  "email": "string (valid email)",
  "troopId": "string",
  "status": "Active|Inactive",
  "dateStatusUpdated": "yyyy-MM-dd"
}
```

### Tree Types API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tree-types` | Get all tree types |
| GET | `/api/tree-types/{id}` | Get tree type by ID |
| POST | `/api/tree-types` | Create new tree type |
| PUT | `/api/tree-types/{id}` | Update tree type |
| DELETE | `/api/tree-types/{id}` | Delete tree type |
| GET | `/api/tree-types/ordered/cost` | Get ordered by cost |
| GET | `/api/tree-types/most-expensive` | Get most expensive |

#### Tree Type Object Structure
```json
{
  "id": "number (auto-generated)",
  "typeDescription": "string (required)",
  "cost": "number (required, > 0)",
  "barcodePrefix": "string"
}
```

## 🧪 Testing the API

### Using curl

1. **Create a tree type**
   ```bash
   curl -X POST http://localhost:8080/api/tree-types \
     -H "Content-Type: application/json" \
     -d '{
       "typeDescription": "Noble Fir",
       "cost": 45.99,
       "barcodePrefix": "NF"
     }'
   ```

2. **Create a tree**
   ```bash
   curl -X POST http://localhost:8080/api/trees \
     -H "Content-Type: application/json" \
     -d '{
       "barcode": "NF001",
       "treeType": "Noble Fir",
       "notes": "Premium quality tree",
       "status": "Available"
     }'
   ```

3. **Create a scout**
   ```bash
   curl -X POST http://localhost:8080/api/scouts \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "John",
       "lastName": "Doe",
       "email": "john.doe@example.com",
       "troopId": "TROOP123"
     }'
   ```

4. **Sell a tree**
   ```bash
   curl -X POST http://localhost:8080/api/trees/NF001/sell
   ```

## 🗄️ Database Schema

The application automatically creates the following tables:

- **Tree**: Stores tree inventory information
- **Scout**: Stores scout profiles and contact information  
- **TreeType**: Stores tree type definitions and pricing

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mariadb://localhost:3306/tree_sales_db
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:password}

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server
server.port=8080

# Logging
logging.level.com.treesales=DEBUG
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `password` |
| `SERVER_PORT` | Application port | `8080` |

## 🐳 Docker Support

### Using Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  database:
    image: mariadb:10.6
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: tree_sales_db
      MYSQL_USER: treesales
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_USERNAME: treesales
      DB_PASSWORD: password
      SPRING_DATASOURCE_URL: jdbc:mariadb://database:3306/tree_sales_db
    depends_on:
      - database

volumes:
  db_data:
```

Run with:
```bash
docker-compose up -d
```

## 🔄 Migration from JavaFX

This web application maintains compatibility with the original JavaFX system:

### Key Changes
- **UI**: JavaFX desktop interface → Bootstrap web interface
- **Architecture**: Monolithic desktop app → Spring Boot web application
- **Data Access**: Custom JDBC → Spring Data JPA
- **API**: Internal methods → RESTful web API

### Data Migration
1. Export data from the original system
2. Use the API endpoints to import data into the new system
3. Or directly migrate database tables (ensure schema compatibility)

## 📈 Monitoring & Health

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Application Metrics
Add Spring Boot Actuator dependency for detailed metrics:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📝 License

This project maintains the same license as the original JavaFX application.

## 🆘 Support

- **Issues**: Report bugs or request features via GitHub Issues
- **Documentation**: Additional API documentation available at `/swagger-ui.html` (if Swagger is enabled)
- **Original Project**: Based on the JavaFX Tree Sales Management System

## 🔮 Future Enhancements

- React/Angular frontend application
- Real-time WebSocket updates
- Advanced reporting and analytics
- Mobile application support
- Integration with payment systems
- Inventory forecasting and analytics



