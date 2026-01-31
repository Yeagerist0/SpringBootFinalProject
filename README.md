# Expense Tracker

A full-stack expense tracking application built with Spring Boot and React.

## Features

- User authentication and authorization
- Track expenses with categories
- Set and monitor budgets
- View spending analytics
- Upload receipts
- Email notifications for budget alerts

## Tech Stack

**Backend:**
- Spring Boot 3.2.2
- MongoDB
- Redis (for caching)
- JWT Authentication
- AWS S3 (file storage)

**Frontend:**
- React 18 with TypeScript
- Vite
- TailwindCSS
- Recharts

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB 4.4+
- Redis 6.0+
- Node.js 18+

## Setup

### Backend

1. Clone the repository:
```bash
git clone https://github.com/Yeagerist0/SpringBootFinalProject.git
cd SpringBootFinalProject
```

2. Configure environment variables in `application-dev.yml`:
   - MongoDB connection string
   - Redis configuration
   - JWT secret key
   - AWS credentials (optional)
   - Email settings (optional)

3. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend

1. Navigate to the frontend directory:
```bash
cd Frontend
```

2. Install dependencies:
```bash
npm install
```

3. Create `.env` file:
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

4. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:5173`

## API Documentation

Once the backend is running, visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Main Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh-token` - Refresh access token

### Expenses
- `GET /api/expenses` - Get all expenses
- `POST /api/expenses` - Create expense
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}` - Delete expense

### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create category

### Budgets
- `GET /api/budgets` - Get all budgets
- `POST /api/budgets` - Create budget
- `GET /api/budgets/active` - Get active budgets

### Analytics
- `GET /api/analytics` - Get spending analytics
- `GET /api/analytics/monthly-comparison` - Monthly comparison

## Project Structure

```
????????? src/main/java/com/expensetracker/
???   ????????? config/          # Configuration classes
???   ????????? controller/      # REST controllers
???   ????????? service/         # Business logic
???   ????????? repository/      # Data access
???   ????????? model/           # Domain models
???   ????????? dto/             # Data transfer objects
???   ????????? security/        # Security components
???   ????????? util/            # Utility classes
????????? Frontend/
???   ????????? src/
???   ???   ????????? components/  # React components
???   ???   ????????? pages/       # Page components
???   ???   ????????? services/    # API services
???   ???   ????????? types/       # TypeScript types
???   ????????? ...
????????? ...
```

## Docker Support

Build and run with Docker:

```bash
# Build backend
docker build -t expense-tracker-backend .

# Run with docker-compose
docker-compose up
```

## License

This project is open source and available under the MIT License.
