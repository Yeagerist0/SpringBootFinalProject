# Expense Tracker Frontend

A professional React TypeScript frontend application for the Expense Tracker system. Built with modern technologies and best practices.

## 🚀 Technologies

- **React 18** - Modern React with hooks
- **TypeScript** - Type-safe JavaScript
- **Vite** - Lightning-fast build tool
- **TailwindCSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Axios** - HTTP client with interceptors
- **Recharts** - Beautiful charts and analytics
- **React Hook Form** - Form validation
- **React Hot Toast** - Toast notifications
- **Lucide React** - Beautiful icons

## 📁 Project Structure

```
src/
├── components/          # Reusable components
│   ├── Layout.tsx      # Main layout with sidebar
│   └── PrivateRoute.tsx # Protected route wrapper
├── context/            # React Context providers
│   └── AuthContext.tsx # Authentication context
├── pages/              # Page components
│   ├── Login.tsx       # Login page
│   ├── Register.tsx    # Registration page
│   ├── Dashboard.tsx   # Dashboard with analytics
│   ├── Expenses.tsx    # Expense management
│   ├── Categories.tsx  # Category management
│   ├── Budgets.tsx     # Budget management
│   └── Analytics.tsx   # Analytics and reports
├── services/           # API service layer
│   ├── api.ts          # Axios instance with interceptors
│   ├── authService.ts  # Authentication API
│   ├── expenseService.ts # Expense API
│   ├── categoryService.ts # Category API
│   ├── budgetService.ts # Budget API
│   └── analyticsService.ts # Analytics API
├── types/              # TypeScript type definitions
│   └── index.ts        # All type definitions
├── utils/              # Utility functions
│   └── helpers.ts      # Helper functions
├── App.tsx             # Main App component with routing
├── main.tsx            # Application entry point
└── index.css           # Global styles with Tailwind
```

## 🛠️ Setup Instructions

### Prerequisites

- Node.js 18+ and npm
- Backend API running on `http://localhost:8080`

### Installation

1. Install dependencies:
```bash
npm install
```

2. Create `.env` file (optional):
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

3. Start development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

## 🎯 Features

### Authentication
- Login and registration with JWT tokens
- Automatic token refresh
- Protected routes
- Persistent sessions

### Dashboard
- Total income, expenses, and savings summary
- Category breakdown pie chart
- Budget alerts
- Recent activity overview

### Expense Management
- Create, edit, and delete expenses
- Upload receipt images
- Filter by date, category, and amount
- Pagination and sorting
- Search functionality

### Category Management
- Create custom categories
- Color and icon customization
- Income and expense categories
- Default categories from backend

### Budget Management
- Set budget limits per category
- Monthly, quarterly, and yearly periods
- Alert threshold configuration
- Real-time progress tracking
- Visual progress bars with color coding

### Analytics
- Monthly trends line chart
- Income vs expense bar chart
- Category breakdown
- Summary tables
- Exportable data

## 🔧 Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## 🎨 UI/UX Features

- Fully responsive design (mobile, tablet, desktop)
- Modern and clean interface
- Smooth animations and transitions
- Toast notifications for user feedback
- Loading states for async operations
- Error handling with user-friendly messages
- Color-coded categories and budgets
- Interactive charts and visualizations

## 🔐 Security

- JWT token authentication
- Automatic token refresh on 401 errors
- Secure token storage in localStorage
- Protected API endpoints
- CORS configuration
- Input validation

## 📱 Responsive Design

- Mobile-first approach
- Collapsible sidebar on mobile
- Touch-friendly UI elements
- Optimized for all screen sizes

## 🚀 Deployment

### Build for Production

```bash
npm run build
```

The build output will be in the `dist/` directory.

### Deploy with Docker

See the main project README for Docker deployment instructions.

## 🔄 API Integration

The frontend automatically proxies API requests to the backend:

- Development: `http://localhost:8080/api`
- Production: Configure `VITE_API_BASE_URL` environment variable

All API calls include:
- JWT Bearer token in Authorization header
- Automatic token refresh on expiry
- Error handling with toast notifications

## 📊 Charts and Visualizations

- **Dashboard**: Pie chart for category breakdown
- **Analytics**: Line chart for monthly trends
- **Analytics**: Bar chart for income vs expense comparison
- **Budgets**: Progress bars with color coding

## 🎯 Best Practices

- TypeScript for type safety
- Modular component structure
- Service layer for API calls
- Context API for state management
- Custom hooks for reusable logic
- Utility functions for common operations
- Consistent naming conventions
- Comprehensive error handling

## 🤝 Contributing

This project is part of a Final Term Project for Backend Engineering course.

## 📝 License

This project is for educational purposes.

## 👨‍💻 Author

Developed as part of Final Term Project - Backend Engineering (Spring Boot)
