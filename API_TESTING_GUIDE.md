# API Testing Guide

## Quick Start

### 1. Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@1234",
    "firstName": "Test",
    "lastName": "User",
    "phoneNumber": "+1234567890",
    "currency": "USD"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "550e8400-...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": "65a...",
      "username": "testuser",
      "email": "test@example.com",
      "firstName": "Test",
      "lastName": "User",
      "roles": ["ROLE_USER"],
      "currency": "USD"
    }
  }
}
```

---

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "Test@1234"
  }'
```

**Save the JWT token from the response for subsequent requests.**

---

### 3. Get All Categories

```bash
curl -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 4. Create an Expense

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": "CATEGORY_ID_FROM_STEP_3",
    "amount": 150.50,
    "currency": "USD",
    "description": "Grocery shopping at Walmart",
    "date": "2024-01-15",
    "paymentMethod": "CREDIT_CARD",
    "merchant": "Walmart",
    "notes": "Weekly groceries",
    "tags": ["food", "essentials"]
  }'
```

---

### 5. Get All Expenses (Paginated)

```bash
curl -X GET "http://localhost:8080/api/expenses?page=0&size=10&sortBy=date&sortDir=desc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 6. Filter Expenses by Date Range

```bash
curl -X GET "http://localhost:8080/api/expenses/filter/date-range?startDate=2024-01-01&endDate=2024-01-31&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 7. Upload Receipt

```bash
curl -X POST http://localhost:8080/api/expenses/EXPENSE_ID/receipt \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/receipt.jpg"
```

---

### 8. Create a Budget

```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": "CATEGORY_ID",
    "amount": 1000.00,
    "currency": "USD",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "period": "MONTHLY",
    "alertEnabled": true,
    "alertThreshold": 80
  }'
```

---

### 9. Get Active Budgets

```bash
curl -X GET http://localhost:8080/api/budgets/active \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 10. Get Analytics

```bash
curl -X GET "http://localhost:8080/api/analytics?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 11. Get Current Month Analytics

```bash
curl -X GET http://localhost:8080/api/analytics/current-month \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 12. Get Last 30 Days Analytics

```bash
curl -X GET http://localhost:8080/api/analytics/last-30-days \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 13. Create Custom Category

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Investment",
    "description": "Stock and mutual fund investments",
    "icon": "📈",
    "color": "#4CAF50",
    "type": "EXPENSE"
  }'
```

---

### 14. Update Expense

```bash
curl -X PUT http://localhost:8080/api/expenses/EXPENSE_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": "CATEGORY_ID",
    "amount": 175.00,
    "currency": "USD",
    "description": "Updated grocery shopping",
    "date": "2024-01-15",
    "paymentMethod": "DEBIT_CARD",
    "merchant": "Walmart",
    "notes": "Updated notes"
  }'
```

---

### 15. Delete Expense

```bash
curl -X DELETE http://localhost:8080/api/expenses/EXPENSE_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 16. Refresh Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

---

### 17. Logout

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Postman Collection

Import this Postman collection for easy testing:

1. Open Postman
2. Click "Import"
3. Select "Raw text"
4. Paste the collection JSON (available in repository)
5. Update environment variables with your JWT token

---

## Testing with Swagger UI

1. Start the application
2. Navigate to `http://localhost:8080/swagger-ui.html`
3. Click "Authorize" button
4. Enter: `Bearer YOUR_JWT_TOKEN`
5. Test all APIs interactively

---

## Common Issues

### 401 Unauthorized
- Check if JWT token is valid
- Ensure Bearer prefix is included: `Bearer YOUR_TOKEN`
- Token might be expired, use refresh token endpoint

### 400 Bad Request
- Check request body format
- Validate all required fields
- Ensure date format is yyyy-MM-dd

### 429 Too Many Requests
- Rate limit exceeded
- Wait 60 seconds and try again
- Default limit: 100 requests per minute

---

## Environment Variables for Testing

```bash
export JWT_TOKEN="your_jwt_token_here"

# Then use in curl:
curl -X GET http://localhost:8080/api/expenses \
  -H "Authorization: Bearer $JWT_TOKEN"
```
