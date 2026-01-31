# 🚀 Complete Setup Guide - Expense Tracker Full Stack

## Quick Start for Complete Application

This guide will help you run both backend and frontend together.

### Prerequisites Installed?
- ✅ Java 17+
- ✅ Maven
- ✅ Node.js 18+
- ✅ MongoDB (running)
- ✅ Redis (running)

## 🎯 Step-by-Step Setup

### Step 1: Start Backend API

```bash
# Navigate to backend directory
cd /home/hitarth/SpringbootFinalProject

# Configure environment variables
# Edit application-dev.yml or create .env file

# Build and run
mvn clean install
mvn spring-boot:run
```

✅ Backend running at: **http://localhost:8080**  
✅ Swagger UI at: **http://localhost:8080/swagger-ui.html**

### Step 2: Start Frontend UI

```bash
# Navigate to frontend directory
cd /home/hitarth/SpringbootFinalProject/Frontend

# Dependencies already installed (npm install completed)
# Environment already configured (.env created)

# Start development server
npm run dev
```

✅ Frontend running at: **http://localhost:5173**

## 🎨 Access the Application

1. **Open your browser:** http://localhost:5173
2. **Register a new account** or use test credentials
3. **Start tracking expenses!**

### Default Test User (if created in backend)
- Email: `admin@example.com`
- Password: `Admin@123`

## 📊 What's Working?

### Backend (Spring Boot)
- ✅ All 30+ REST API endpoints
- ✅ JWT authentication with refresh tokens
- ✅ MongoDB integration
- ✅ Redis caching
- ✅ AWS S3 file upload (configure credentials)
- ✅ Email notifications (configure SMTP)
- ✅ Rate limiting
- ✅ Swagger documentation

### Frontend (React + TypeScript)
- ✅ User authentication (login/register)
- ✅ Dashboard with analytics
- ✅ Expense management (CRUD)
- ✅ Category management
- ✅ Budget tracking
- ✅ Charts and visualizations
- ✅ Receipt upload
- ✅ Responsive design
- ✅ Toast notifications

## 🔧 Configuration

### Backend Configuration
Edit: `src/main/resources/application-dev.yml`

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/expense_tracker
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your-secret-key-minimum-256-bits-change-in-production
  expiration: 86400000  # 1 day
  refresh-expiration: 604800000  # 7 days

aws:
  s3:
    bucket-name: your-bucket-name
    access-key: your-access-key
    secret-key: your-secret-key
    region: us-east-1

mail:
  host: smtp.gmail.com
  port: 587
  username: your-email@gmail.com
  password: your-app-password
```

### Frontend Configuration
File: `.env` (already created)

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## 🎯 Features to Test

### 1. Authentication Flow
1. Register new user
2. Login with credentials
3. View dashboard
4. Logout
5. Login again (token should auto-refresh)

### 2. Expense Management
1. Create new expense
2. Upload receipt image
3. Edit expense
4. Filter expenses by date/category
5. Delete expense

### 3. Category Management
1. View default categories
2. Create custom category with icon and color
3. Edit category
4. Delete category (not default ones)

### 4. Budget Management
1. Create monthly budget for a category
2. Add expenses to that category
3. Watch budget progress bar update
4. Trigger alert when threshold reached (80%)

### 5. Analytics
1. View dashboard summary cards
2. Check category breakdown pie chart
3. View monthly trends line chart
4. Analyze income vs expense bar chart

## 🐳 Alternative: Docker Setup

If you have Docker installed, run everything with one command:

```bash
cd /home/hitarth/SpringbootFinalProject
docker-compose up -d
```

This starts:
- MongoDB container
- Redis container
- Backend API container
- Frontend UI container (with Nginx)

Access at: http://localhost

## 📱 Mobile Testing

The frontend is fully responsive! Test on:
- Mobile (320px+)
- Tablet (768px+)
- Desktop (1024px+)

## 🎨 UI Features

- **Sidebar Navigation** - Collapsible on mobile
- **Dashboard** - Summary cards with icons and colors
- **Charts** - Interactive Recharts visualizations
- **Forms** - Modal dialogs for CRUD operations
- **Tables** - Sortable, filterable data tables
- **Toasts** - React Hot Toast notifications
- **Progress Bars** - Color-coded budget tracking
- **Loading States** - Spinner animations
- **Error Handling** - User-friendly error messages

## 🔐 Security Features

- BCrypt password hashing
- JWT tokens with auto-refresh
- Protected routes on frontend
- API request interceptors
- CORS configuration
- Rate limiting (10 req/min per user)
- Input validation on both ends

## 📊 Tech Stack Summary

### Backend
- Spring Boot 3.2+
- MongoDB
- Redis
- JWT
- AWS S3
- SMTP
- Swagger
- Docker

### Frontend
- React 18
- TypeScript
- Vite
- TailwindCSS
- Axios
- React Router
- Recharts
- React Hot Toast
- Lucide Icons

## 🚨 Troubleshooting

### Backend won't start?
1. Check MongoDB is running: `mongosh`
2. Check Redis is running: `redis-cli ping`
3. Verify port 8080 is free: `lsof -i :8080`
4. Check application logs for errors

### Frontend won't connect?
1. Ensure backend is running on 8080
2. Check browser console for errors
3. Verify CORS is configured in backend
4. Check .env file has correct API URL

### Can't login?
1. Check if user exists in MongoDB
2. Verify JWT secret is configured
3. Check browser localStorage for tokens
4. Review backend logs for authentication errors

### File upload fails?
1. Configure AWS credentials in backend
2. Create S3 bucket
3. Set correct bucket permissions
4. Check file size is under 2MB

## 📚 Documentation Files

- `README.md` - Backend documentation (parent directory)
- `Frontend/README.md` - Frontend documentation (this directory)
- `API_TESTING_GUIDE.md` - cURL examples (parent directory)
- `DEPLOYMENT.md` - Production deployment (parent directory)
- `PROJECT_SUMMARY.md` - Requirements checklist (parent directory)
- `COMPLETE_SETUP_GUIDE.md` - This file

## 🎉 Success Checklist

After setup, you should be able to:
- [x] Access frontend at http://localhost:5173
- [x] Access backend at http://localhost:8080
- [x] Access Swagger at http://localhost:8080/swagger-ui.html
- [x] Register a new user
- [x] Login successfully
- [x] View dashboard with summary
- [x] Create expenses
- [x] Create categories
- [x] Set budgets
- [x] View analytics charts
- [x] See responsive design on mobile

## 💡 Tips

1. **Use Chrome DevTools** to inspect API calls
2. **Check Network tab** for request/response details
3. **Monitor Console** for frontend errors
4. **Review Swagger UI** for API documentation
5. **Use MongoDB Compass** to view database
6. **Use Redis Commander** to check cache

## 🏆 Project Highlights

- **Full Stack** - Backend + Frontend integrated
- **Production Ready** - All best practices followed
- **Well Documented** - Comprehensive guides
- **Type Safe** - TypeScript on frontend
- **Secure** - JWT, BCrypt, CORS, Rate Limiting
- **Scalable** - Redis caching, Pagination
- **Modern UI** - TailwindCSS, Recharts
- **Professional** - Clean code, proper architecture

## 📞 Need Help?

If you encounter issues:
1. Check this guide carefully
2. Review error messages in browser console
3. Check backend logs
4. Verify all services are running
5. Ensure configurations are correct

---

**🎉 Happy Coding! You now have a professional full-stack expense tracker!**

Both backend and frontend are production-ready and demonstrate:
- ✅ Enterprise architecture
- ✅ Security best practices
- ✅ Modern technologies
- ✅ Professional UI/UX
- ✅ Comprehensive features

**Perfect for your Final Term Project! 🚀**
