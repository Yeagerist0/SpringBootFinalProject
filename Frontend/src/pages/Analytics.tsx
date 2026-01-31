import { useEffect, useState } from 'react';
import { analyticsService } from '@/services/analyticsService';
import { MonthlyTrend } from '@/types';
import { formatCurrency } from '@/utils/helpers';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

const Analytics = () => {
  const [monthlyTrends, setMonthlyTrends] = useState<MonthlyTrend[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const trends = await analyticsService.getMonthlyTrends(12);
      setMonthlyTrends(trends);
    } catch (error) {
      console.error('Error fetching analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64">Loading...</div>;
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Analytics</h1>

      {/* Monthly Trends Line Chart */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Monthly Trends</h2>
        <ResponsiveContainer width="100%" height={400}>
          <LineChart data={monthlyTrends}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="month" />
            <YAxis />
            <Tooltip formatter={(value: number) => formatCurrency(value)} />
            <Legend />
            <Line type="monotone" dataKey="income" stroke="#10b981" strokeWidth={2} name="Income" />
            <Line type="monotone" dataKey="expense" stroke="#ef4444" strokeWidth={2} name="Expense" />
            <Line type="monotone" dataKey="savings" stroke="#3b82f6" strokeWidth={2} name="Savings" />
          </LineChart>
        </ResponsiveContainer>
      </div>

      {/* Income vs Expense Bar Chart */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Income vs Expense Comparison</h2>
        <ResponsiveContainer width="100%" height={400}>
          <BarChart data={monthlyTrends}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="month" />
            <YAxis />
            <Tooltip formatter={(value: number) => formatCurrency(value)} />
            <Legend />
            <Bar dataKey="income" fill="#10b981" name="Income" />
            <Bar dataKey="expense" fill="#ef4444" name="Expense" />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Summary Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <h2 className="text-lg font-semibold text-gray-900 p-6 pb-4">Monthly Summary</h2>
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Month</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Income</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Expense</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Savings</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {monthlyTrends.map((trend, index) => (
              <tr key={index} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{trend.month}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-right text-green-600">
                  {formatCurrency(trend.income)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-right text-red-600">
                  {formatCurrency(trend.expense)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-right font-medium text-blue-600">
                  {formatCurrency(trend.savings)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Analytics;
