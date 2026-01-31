import apiClient from './api';
import { Category, ApiResponse } from '@/types';

export const categoryService = {
  getCategories: async (): Promise<Category[]> => {
    const response = await apiClient.get<ApiResponse<Category[]>>('/categories');
    return response.data.data;
  },

  getCategory: async (id: string): Promise<Category> => {
    const response = await apiClient.get<ApiResponse<Category>>(`/categories/${id}`);
    return response.data.data;
  },

  createCategory: async (category: Partial<Category>): Promise<Category> => {
    const response = await apiClient.post<ApiResponse<Category>>(
      '/categories',
      category
    );
    return response.data.data;
  },

  updateCategory: async (id: string, category: Partial<Category>): Promise<Category> => {
    const response = await apiClient.put<ApiResponse<Category>>(
      `/categories/${id}`,
      category
    );
    return response.data.data;
  },

  deleteCategory: async (id: string): Promise<void> => {
    await apiClient.delete(`/categories/${id}`);
  },
};
