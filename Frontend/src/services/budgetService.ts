import apiClient from './api';
import { Budget, PageResponse, ApiResponse } from '@/types';

export const budgetService = {
  getBudgets: async (page = 0, size = 10): Promise<PageResponse<Budget>> => {
    const response = await apiClient.get<PageResponse<Budget>>(
      `/budgets?page=${page}&size=${size}`
    );
    return response.data;
  },

  getBudget: async (id: string): Promise<Budget> => {
    const response = await apiClient.get<ApiResponse<Budget>>(`/budgets/${id}`);
    return response.data.data;
  },

  createBudget: async (budget: Partial<Budget>): Promise<Budget> => {
    const response = await apiClient.post<ApiResponse<Budget>>('/budgets', budget);
    return response.data.data;
  },

  updateBudget: async (id: string, budget: Partial<Budget>): Promise<Budget> => {
    const response = await apiClient.put<ApiResponse<Budget>>(
      `/budgets/${id}`,
      budget
    );
    return response.data.data;
  },

  deleteBudget: async (id: string): Promise<void> => {
    await apiClient.delete(`/budgets/${id}`);
  },

  getBudgetAlerts: async (): Promise<Budget[]> => {
    const response = await apiClient.get<ApiResponse<Budget[]>>('/budgets/alerts');
    return response.data.data;
  },
};
