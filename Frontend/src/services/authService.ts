import apiClient from './api';
import { AuthResponse, LoginRequest, RegisterRequest, ApiResponse } from '@/types';

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(
      '/auth/login',
      credentials
    );
    return response.data.data;
  },

  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(
      '/auth/register',
      userData
    );
    return response.data.data;
  },

  logout: async (): Promise<void> => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
      await apiClient.post('/auth/logout', { refreshToken });
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  refreshToken: async (refreshToken: string): Promise<string> => {
    const response = await apiClient.post<ApiResponse<{ accessToken: string }>>(
      '/auth/refresh',
      { refreshToken }
    );
    return response.data.data.accessToken;
  },
};
