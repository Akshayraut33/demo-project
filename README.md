# Splitwise Expense Tracker

A simple Spring Boot application for tracking expenses and money owed between users, similar to Splitwise. This app helps you keep records of who paid for what and how much each person owes.

## 🎯 Project Aim

This application allows users to:
- Create user accounts
- Record expenses with multiple participants
- Automatically calculate how much each person owes or is owed
- Track balances between users
- Split bills fairly among friends and groups

## 🛠️ Prerequisites

### Java
- **Version**: Java 17 or higher
- **Download**: [Oracle JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/)

### PostgreSQL Database
- **Version**: PostgreSQL 12 or higher
- **Download**: [PostgreSQL Downloads](https://www.postgresql.org/download/)

### pgAdmin (Database Management Tool)
- **Download**: [pgAdmin Downloads](https://www.pgadmin.org/download/)
- **Purpose**: GUI tool to manage PostgreSQL databases

## 📋 Installation & Setup

### 1. Install Java 17
```bash
# Verify Java installation
java -version
# Should show: Java 17.x.x
```

### 2. Install PostgreSQL
1. Download and install PostgreSQL from the official website
2. During installation:
   - Set password for user `postgres`: `akshay@45`
   - Keep default port: `5432`
   - Install pgAdmin along with PostgreSQL

### 3. Setup Database
1. Open pgAdmin
2. Connect to PostgreSQL server (password: `akshay@45`)
3. Create a new database named `campus-db`

### 4. Clone/Download Project
```bash
cd your-workspace-directory
# Copy the project files to this location
```

### 5. Configure Database Connection
The application is pre-configured with these database settings in `src/main/resources/application.properties`:

```properties
# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/campus-db
spring.datasource.username=postgres
spring.datasource.password=akshay@45
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA (Hibernate) Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server Configuration
server.port=8085
server.ssl.enabled=true
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-alias=tomcat
```

**Note**: SSL certificate (`keystore.p12`) is already generated in the project.

## 🚀 Running the Application

1. **Navigate to project directory**:
   ```bash
   cd path/to/demo-project
   ```

2. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Verify startup**:
   - Look for: `Tomcat started on port 8085 (https)`
   - Application will be available at: `https://localhost:8085`

## 📚 API Documentation

All APIs use **HTTPS** and are available at `https://localhost:8085/api/...`

### Authentication
Currently, no authentication is implemented. All endpoints are public.

### Users API

#### Create User
- **Method**: `POST`
- **Endpoint**: `/api/users`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com"
  }
  ```
- **Response**: Created user with ID
- **Example**:
  ```bash
  curl -X POST https://localhost:8085/api/users \
    -H "Content-Type: application/json" \
    -d '{"name":"Alice","email":"alice@example.com"}' \
    -k
  ```

#### Get All Users
- **Method**: `GET`
- **Endpoint**: `/api/users`
- **Response**: Array of all users
- **Example**:
  ```bash
  curl -X GET https://localhost:8085/api/users -k
  ```

#### Get User by ID
- **Method**: `GET`
- **Endpoint**: `/api/users/{id}`
- **Path Parameter**: `id` (Long) - User ID
- **Response**: Single user object or 404 if not found
- **Example**:
  ```bash
  curl -X GET https://localhost:8085/api/users/1 -k
  ```

### Expenses API

#### Create Expense
- **Method**: `POST`
- **Endpoint**: `/api/expenses`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "description": "Dinner at Restaurant",
    "amount": 75.50,
    "paidById": 1,
    "participantIds": [1, 2, 3]
  }
  ```
- **Response**: Created expense with ID
- **Notes**:
  - `paidById`: ID of the user who paid
  - `participantIds`: Array of user IDs who participated (including payer)
  - Amount is split equally among participants
- **Example**:
  ```bash
  curl -X POST https://localhost:8085/api/expenses \
    -H "Content-Type: application/json" \
    -d '{"description":"Lunch","amount":30.00,"paidById":1,"participantIds":[1,2]}' \
    -k
  ```

#### Get All Expenses
- **Method**: `GET`
- **Endpoint**: `/api/expenses`
- **Response**: Array of all expenses
- **Example**:
  ```bash
  curl -X GET https://localhost:8085/api/expenses -k
  ```

#### Get User Balances
- **Method**: `GET`
- **Endpoint**: `/api/expenses/balances/{userId}`
- **Path Parameter**: `userId` (Long) - User ID to check balances for
- **Response**: Map of user IDs to balance amounts
  - **Positive amount**: Money owed to the user
  - **Negative amount**: Money the user owes
- **Example**:
  ```bash
  curl -X GET https://localhost:8085/api/expenses/balances/1 -k
  ```

## 🧪 Testing with Postman

1. **Import Collection**:
   - Open Postman
   - Import `Splitwise_Postman_Collection.json` from project root

2. **SSL Configuration**:
   - Go to Settings > General
   - Turn OFF "SSL certificate verification"

3. **Test Flow**:
   1. Create users using "Create User" request
   2. Create expenses using "Create Expense" request
   3. Check balances using "Get User Balances" request

## 📊 Database Schema

### Tables Created Automatically

#### users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);
```

#### expenses
```sql
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    description TEXT,
    amount DECIMAL(10,2),
    date TIMESTAMP,
    paid_by_id BIGINT REFERENCES users(id)
);
```

#### expense_participants
```sql
CREATE TABLE expense_participants (
    expense_id BIGINT REFERENCES expenses(id),
    user_id BIGINT REFERENCES users(id),
    PRIMARY KEY (expense_id, user_id)
);
```

## 🔧 Configuration Files

### application.properties
Location: `src/main/resources/application.properties`

Contains database connection, JPA settings, and SSL configuration.

### pom.xml
Location: `pom.xml`

Contains project dependencies:
- Spring Boot Web
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- Validation
- Actuator

## 🐛 Troubleshooting

### Common Issues

1. **Port 8085 already in use**:
   ```bash
   # Find process using port
   netstat -ano | findstr :8085
   # Kill the process or change port in application.properties
   ```

2. **Database connection failed**:
   - Ensure PostgreSQL is running
   - Verify database `campus-db` exists
   - Check username/password in `application.properties`

3. **SSL Certificate warnings**:
   - This is normal for self-signed certificates
   - Use `-k` flag with curl or disable SSL verification in Postman

4. **Java version issues**:
   ```bash
   java -version
   # Should show Java 17
   ```

### Logs
Application logs are displayed in the console when running. Look for:
- `Started SplitwiseApplication` - Application started successfully
- `Tomcat started on port 8085` - Server is ready
- Database connection messages

## 📝 Example Usage

1. **Create Users**:
   ```bash
   # Alice
   curl -X POST https://localhost:8085/api/users -H "Content-Type: application/json" -d '{"name":"Alice","email":"alice@example.com"}' -k

   # Bob
   curl -X POST https://localhost:8085/api/users -H "Content-Type: application/json" -d '{"name":"Bob","email":"bob@example.com"}' -k
   ```

2. **Create Expense**:
   ```bash
   # Alice pays $20 for lunch with both
   curl -X POST https://localhost:8085/api/expenses -H "Content-Type: application/json" -d '{"description":"Lunch","amount":20.00,"paidById":1,"participantIds":[1,2]}' -k
   ```

3. **Check Balances**:
   ```bash
   # Alice's balances (should show Bob owes $10)
   curl -X GET https://localhost:8085/api/expenses/balances/1 -k
   ```

## 🤝 Contributing

This is a demo project. For improvements:
1. Fork the repository
2. Create a feature branch
3. Make changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is for educational purposes. Feel free to use and modify as needed.