import apiClient from './api';
import { Expense, PageResponse, ApiResponse } from '@/types';

export interface ExpenseFilters {
  categoryId?: string;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export const expenseService = {
  getExpenses: async (filters: ExpenseFilters = {}): Promise<PageResponse<Expense>> => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined) {
        params.append(key, value.toString());
      }
    });

    const response = await apiClient.get<PageResponse<Expense>>(
      `/expenses?${params.toString()}`
    );
    return response.data;
  },

  getExpense: async (id: string): Promise<Expense> => {
    const response = await apiClient.get<ApiResponse<Expense>>(`/expenses/${id}`);
    return response.data.data;
  },

  createExpense: async (expense: Partial<Expense>): Promise<Expense> => {
    const response = await apiClient.post<ApiResponse<Expense>>('/expenses', expense);
    return response.data.data;
  },

  updateExpense: async (id: string, expense: Partial<Expense>): Promise<Expense> => {
    const response = await apiClient.put<ApiResponse<Expense>>(
      `/expenses/${id}`,
      expense
    );
    return response.data.data;
  },

  deleteExpense: async (id: string): Promise<void> => {
    await apiClient.delete(`/expenses/${id}`);
  },

  uploadReceipt: async (id: string, file: File): Promise<Expense> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post<ApiResponse<Expense>>(
      `/expenses/${id}/receipt`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data.data;
  },
};
