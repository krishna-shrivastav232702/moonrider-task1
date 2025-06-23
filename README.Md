# Identity Reconciliation Service

A sophisticated contact deduplication and linking service that consolidates customer identities across multiple purchases with different contact information.

## 🎯 Problem Statement

Customers often use different email addresses and phone numbers for different purchases. This service intelligently links these separate identities to create a unified customer profile.

## 🚀 Features

- **Smart Contact Linking**: Automatically links contacts sharing email or phone number
- **Primary/Secondary Hierarchy**: Maintains data integrity with oldest contact as primary
- **Dynamic Reconciliation**: Real-time contact consolidation with each API call
- **Robust Error Handling**: Secure error responses to protect system integrity

## 🛠️ Tech Stack

- **Java 17** with Spring Boot 3.2.0
- **Spring Data JPA** for database operations
- **H2 Database** (in-memory for development)
- **Maven** for dependency management
- **Lombok** for reducing boilerplate code

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

## 🏃‍♂️ Quick Start

### 1. Clone & Build
```bash
git clone git@github.com:krishna-shrivastav232702/moonrider-task1.git
cd identity-reconciliation
mvn clean install
```

### 2. Run Application
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

### 3. Test the API
```bash
# Health Check
curl http://localhost:8080/api/health

# Identity Reconciliation
curl -X POST http://localhost:8080/api/identify \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doc@timetravel.com",
    "phoneNumber": "9876543210"
  }'
```

## 📡 API Endpoints

### POST `/api/identify`
Processes contact information and returns consolidated identity.

**Request Body:**
```json
{
  "email": "user@example.com",
  "phoneNumber": "1234567890"
}
```

**Response:**
```json
{
  "contact": {
    "primaryContactId": 1,
    "emails": ["user@example.com", "user2@example.com"],
    "phoneNumbers": ["1234567890", "0987654321"],
    "secondaryContactIds": [2, 3]
  }
}
```

### GET `/api/health`
Returns service health status.

## 🧠 How It Works

1. **New Contact**: If no matching email/phone exists, creates a PRIMARY contact
2. **Partial Match**: If email OR phone matches existing contact, creates SECONDARY contact linked to primary
3. **Multi-Match**: If multiple contacts match, consolidates all under oldest contact as primary
4. **Response**: Returns unified view with all linked emails, phones, and contact IDs

## 🗄️ Database Schema

```sql
contacts (
  id BIGINT PRIMARY KEY,
  phone_number VARCHAR(20),
  email VARCHAR(255),
  linked_id BIGINT,           -- References primary contact
  link_precedence ENUM,       -- 'PRIMARY' or 'SECONDARY'
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted_at TIMESTAMP
)
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## 🔧 Configuration

Key configurations in `application.yml`:
- Database: H2 in-memory (development)
- Server port: 8080
- JPA: Auto-create tables
- Logging: INFO level for application logs

## 📊 Database Console

Access H2 console at: `http://localhost:8080/h2-console`
- URL: `jdbc:h2:mem:identity_db`
- Username: `sa`
- Password: (empty)

## 🚀 Production Deployment

For production deployment:
1. Replace H2 with PostgreSQL
2. Configure proper database credentials
3. Set appropriate logging levels
4. Enable SSL/TLS
5. Configure monitoring endpoints

## 🏗️ Architecture Decisions

- **In-Memory Database**: Fast development and testing
- **JPA Repositories**: Clean data access layer
- **Transaction Management**: Ensures data consistency
- **Optimized Queries**: Indexed columns for performance
- **Lombok**: Reduces boilerplate code

## 📝 Future Enhancements

- [ ] Add caching layer (Redis)
- [ ] Implement soft delete functionality
- [ ] Add comprehensive audit logging
- [ ] Performance monitoring integration
- [ ] API rate limiting