# 💰 Expense Tracker Backend

A **production-grade** Spring Boot backend system for expense tracking with advanced features including JWT authentication, MongoDB, Redis caching, AWS S3 file upload, email notifications, rate limiting, and comprehensive analytics.

---

## 🚀 Features

### ✅ Core Features
- ✨ **User Management** - Registration, Login, JWT-based authentication
- 📊 **Expense Tracking** - CRUD operations with pagination, sorting, and filtering
- 🏷️ **Category Management** - Custom categories with default templates
- 💵 **Budget Management** - Monthly budgets with automatic alerts
- 📈 **Advanced Analytics** - Spending reports, trends, category breakdowns

### ⚡ Advanced Features
- 🔐 **JWT Authentication** - Secure token-based auth with refresh tokens
- 🗄️ **MongoDB Integration** - NoSQL database with complex aggregation queries
- 🚄 **Redis Caching** - Performance optimization with distributed caching
- 📁 **File Upload** - AWS S3 integration for receipt/invoice storage
- 📧 **Email Notifications** - Budget alerts, welcome emails, monthly reports
- ⚠️ **Rate Limiting** - API rate limiting with Bucket4j
- 🛡️ **Global Exception Handling** - Centralized error handling
- ✅ **Input Validation** - Jakarta Bean Validation
- 📚 **API Documentation** - Swagger/OpenAPI 3.0
- 🎯 **Role-Based Access Control** - Admin/User roles

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Backend Framework** | Spring Boot 3.2.2 |
| **Language** | Java 17 |
| **Build Tool** | Maven |
| **Database** | MongoDB |
| **Caching** | Redis |
| **Authentication** | JWT (jjwt 0.12.3) |
| **File Storage** | AWS S3 |
| **Email** | Spring Mail |
| **API Documentation** | SpringDoc OpenAPI |
| **Rate Limiting** | Bucket4j |
| **Validation** | Jakarta Validation |

---

## 📁 Project Structure

```
src/main/java/com/expensetracker/
├── config/              # Configuration classes
│   ├── SecurityConfig.java
│   ├── RedisConfig.java
│   ├── OpenApiConfig.java
│   └── AsyncConfig.java
├── controller/          # REST Controllers
│   ├── AuthController.java
│   ├── ExpenseController.java
│   ├── CategoryController.java
│   ├── BudgetController.java
│   └── AnalyticsController.java
├── service/             # Business Logic
│   ├── AuthService.java
│   ├── ExpenseService.java
│   ├── CategoryService.java
│   ├── BudgetService.java
│   └── AnalyticsService.java
├── repository/          # Data Access Layer
│   ├── UserRepository.java
│   ├── ExpenseRepository.java
│   ├── CategoryRepository.java
│   ├── BudgetRepository.java
│   └── RefreshTokenRepository.java
├── model/               # Domain Models
│   ├── User.java
│   ├── Expense.java
│   ├── Category.java
│   ├── Budget.java
│   └── RefreshToken.java
├── dto/                 # Data Transfer Objects
│   ├── auth/
│   ├── expense/
│   ├── category/
│   ├── budget/
│   ├── analytics/
│   └── common/
├── security/            # Security Components
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   ├── UserPrincipal.java
│   └── CustomUserDetailsService.java
├── exception/           # Exception Handling
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
└── util/                # Utility Classes
    ├── RateLimitUtil.java
    ├── FileUploadUtil.java
    └── EmailUtil.java
```

---

## 🚦 Getting Started

### Prerequisites
- ☕ Java 17+
- 📦 Maven 3.6+
- 🍃 MongoDB 4.4+
- 🔴 Redis 6.0+
- ☁️ AWS Account (for S3)
- 📧 SMTP Server (Gmail/SendGrid)

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd SpringbootFinalProject
```

2. **Configure Environment Variables**

Create `.env` file in the root directory:

```env
# MongoDB
MONGODB_URI=mongodb://localhost:27017/expense_tracker

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=YourSuperSecretJWTKey256BitsLongForHS512Algorithm
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# AWS S3
AWS_ACCESS_KEY=your-aws-access-key
AWS_SECRET_KEY=your-aws-secret-key
AWS_REGION=us-east-1
AWS_S3_BUCKET=expense-tracker-receipts

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_FROM=noreply@expensetracker.com

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
```

3. **Update `application.yml`**

The application is configured to use environment variables. Update `src/main/resources/application.yml` if needed.

4. **Build the project**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

Or use the active profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 📡 API Documentation

Once the application is running, access:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`

---

## 🔑 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/refresh-token` | Refresh access token |
| POST | `/api/auth/logout` | User logout |

### Expenses
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/expenses` | Create expense |
| GET | `/api/expenses` | Get all expenses (paginated) |
| GET | `/api/expenses/{id}` | Get expense by ID |
| PUT | `/api/expenses/{id}` | Update expense |
| DELETE | `/api/expenses/{id}` | Delete expense |
| POST | `/api/expenses/{id}/receipt` | Upload receipt |
| GET | `/api/expenses/filter/date-range` | Filter by date |
| GET | `/api/expenses/filter/category/{id}` | Filter by category |

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/categories` | Create category |
| GET | `/api/categories` | Get all categories |
| GET | `/api/categories/{id}` | Get category by ID |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

### Budgets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/budgets` | Create budget |
| GET | `/api/budgets` | Get all budgets |
| GET | `/api/budgets/active` | Get active budgets |
| GET | `/api/budgets/{id}` | Get budget by ID |
| PUT | `/api/budgets/{id}` | Update budget |
| DELETE | `/api/budgets/{id}` | Delete budget |

### Analytics
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics` | Get comprehensive analytics |
| GET | `/api/analytics/category/{id}` | Category analytics |
| GET | `/api/analytics/monthly-comparison` | Monthly comparison |
| GET | `/api/analytics/current-month` | Current month analytics |
| GET | `/api/analytics/last-30-days` | Last 30 days analytics |

---

## 🧪 Testing APIs

### Using cURL

**1. Register a new user**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "Test@1234",
    "firstName": "John",
    "lastName": "Doe",
    "currency": "USD"
  }'
```

**2. Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "johndoe",
    "password": "Test@1234"
  }'
```

**3. Create Expense** (use token from login)
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": "CATEGORY_ID",
    "amount": 50.00,
    "currency": "USD",
    "description": "Grocery shopping",
    "date": "2024-01-15",
    "paymentMethod": "CREDIT_CARD"
  }'
```

---

## ⚙️ Configuration

### MongoDB Setup
```bash
# Start MongoDB
mongod --dbpath /path/to/data

# Or use Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### Redis Setup
```bash
# Start Redis
redis-server

# Or use Docker
docker run -d -p 6379:6379 --name redis redis:latest
```

### AWS S3 Setup
1. Create an S3 bucket
2. Configure IAM user with S3 access
3. Update environment variables with credentials

### Email Setup (Gmail)
1. Enable 2-factor authentication
2. Generate App Password
3. Use App Password in `MAIL_PASSWORD`

---

## 🎯 Key Features Explained

### 1. **JWT Authentication**
- Secure token-based authentication
- Access tokens (24 hours)
- Refresh tokens (7 days)
- Automatic token refresh

### 2. **Rate Limiting**
- 100 requests per minute per user
- Prevents API abuse
- Configurable limits

### 3. **Caching**
- Redis-based distributed caching
- 10-minute TTL
- Cache invalidation on updates

### 4. **Budget Alerts**
- Email alerts at threshold (default 80%)
- Automatic budget tracking
- Real-time spent amount calculation

### 5. **File Upload**
- AWS S3 integration
- Supports images (JPG, PNG) and PDF
- 10MB file size limit
- Automatic file validation

### 6. **Analytics**
- Category-wise breakdown
- Monthly trends
- Payment method analysis
- Top expenses tracking

---

## 🔒 Security

- ✅ Password encryption with BCrypt
- ✅ JWT-based stateless authentication
- ✅ Role-based access control
- ✅ Input validation
- ✅ SQL injection prevention (NoSQL)
- ✅ XSS protection
- ✅ CORS configuration
- ✅ Rate limiting

---

## 📊 Database Schema

### Users Collection
```json
{
  "_id": "ObjectId",
  "username": "String",
  "email": "String",
  "password": "String (BCrypt)",
  "firstName": "String",
  "lastName": "String",
  "roles": ["ROLE_USER"],
  "currency": "USD",
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

### Expenses Collection
```json
{
  "_id": "ObjectId",
  "userId": "String",
  "categoryId": "String",
  "amount": "Decimal128",
  "currency": "String",
  "description": "String",
  "date": "Date",
  "paymentMethod": "Enum",
  "receiptUrls": ["String"],
  "tags": ["String"],
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

---

## 🐳 Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build
docker build -t expense-tracker:latest .

# Run
docker run -p 8080:8080 --env-file .env expense-tracker:latest
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📝 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- MongoDB Documentation
- AWS Documentation
- Redis Documentation

---

## 📞 Support

For support, email your.email@example.com or create an issue in the repository.

---

**Made with ❤️ using Spring Boot**
