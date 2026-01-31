export interface User {
  id: string;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface Category {
  id: string;
  name: string;
  type: 'INCOME' | 'EXPENSE';
  color: string;
  icon: string;
  description?: string;
  userId: string;
  default: boolean;
  createdAt: string;
}

export interface Expense {
  id: string;
  amount: number;
  description: string;
  category: Category;
  date: string;
  receiptUrl?: string;
  userId: string;
  createdAt: string;
  updatedAt: string;
}

export interface Budget {
  id: string;
  category: Category;
  limitAmount: number;
  spentAmount: number;
  period: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  startDate: string;
  endDate: string;
  alertThreshold: number;
  alertSent: boolean;
  userId: string;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface AnalyticsSummary {
  totalIncome: number;
  totalExpense: number;
  netSavings: number;
  categoryBreakdown: CategoryBreakdown[];
  monthlyTrends: MonthlyTrend[];
}

export interface CategoryBreakdown {
  categoryName: string;
  totalAmount: number;
  percentage: number;
  color: string;
}

export interface MonthlyTrend {
  month: string;
  income: number;
  expense: number;
  savings: number;
}
