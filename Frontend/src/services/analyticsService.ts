import apiClient from './api';
import { AnalyticsSummary, CategoryBreakdown, MonthlyTrend, ApiResponse } from '@/types';

export const analyticsService = {
  getDashboardSummary: async (startDate?: string, endDate?: string): Promise<AnalyticsSummary> => {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await apiClient.get<ApiResponse<AnalyticsSummary>>(
      `/analytics/dashboard?${params.toString()}`
    );
    return response.data.data;
  },

  getCategoryBreakdown: async (startDate?: string, endDate?: string): Promise<CategoryBreakdown[]> => {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await apiClient.get<ApiResponse<CategoryBreakdown[]>>(
      `/analytics/category-breakdown?${params.toString()}`
    );
    return response.data.data;
  },

  getMonthlyTrends: async (months = 6): Promise<MonthlyTrend[]> => {
    const response = await apiClient.get<ApiResponse<MonthlyTrend[]>>(
      `/analytics/monthly-trends?months=${months}`
    );
    return response.data.data;
  },
};
