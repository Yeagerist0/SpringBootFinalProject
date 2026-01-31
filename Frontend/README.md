# Expense Tracker Frontend

A modern React TypeScript frontend for managing personal expenses and budgets.

## Tech Stack

- React 18 with TypeScript
- Vite for fast development
- TailwindCSS for styling
- React Router for navigation
- Axios for API calls
- Recharts for data visualization

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

## Features

- User authentication with JWT
- Expense tracking with categories
- Budget management and alerts
- Analytics and visualizations
- Receipt upload support
- Responsive design

## Setup

1. Install dependencies:
```bash
npm install
```

2. Create `.env` file:
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

3. Start the development server:
```bash
npm run dev
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build

## Project Structure

```
src/
├── components/     # Reusable UI components
├── context/        # React Context (Auth)
├── pages/          # Page components
├── services/       # API services
├── types/          # TypeScript types
└── utils/          # Helper functions
```

