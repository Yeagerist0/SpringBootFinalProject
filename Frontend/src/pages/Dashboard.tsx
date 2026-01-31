import { useEffect, useState } from 'react';
import { analyticsService } from '@/services/analyticsService';
import { budgetService } from '@/services/budgetService';
import { AnalyticsSummary, Budget } from '@/types';
import { formatCurrency } from '@/utils/helpers';
import { TrendingUp, TrendingDown, Wallet, AlertTriangle } from 'lucide-react';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';

const Dashboard = () => {
  const [summary, setSummary] = useState<AnalyticsSummary | null>(null);
  const [budgetAlerts, setBudgetAlerts] = useState<Budget[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [summaryData, alerts] = await Promise.all([
        analyticsService.getDashboardSummary(),
        budgetService.getBudgetAlerts(),
      ]);
      setSummary(summaryData);
      setBudgetAlerts(alerts);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64">Loading...</div>;
  }

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Income</p>
              <p className="text-2xl font-bold text-green-600">
                {formatCurrency(summary?.totalIncome || 0)}
              </p>
            </div>
            <TrendingUp className="w-10 h-10 text-green-600" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Expenses</p>
              <p className="text-2xl font-bold text-red-600">
                {formatCurrency(summary?.totalExpense || 0)}
              </p>
            </div>
            <TrendingDown className="w-10 h-10 text-red-600" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Net Savings</p>
              <p className="text-2xl font-bold text-blue-600">
                {formatCurrency(summary?.netSavings || 0)}
              </p>
            </div>
            <Wallet className="w-10 h-10 text-blue-600" />
          </div>
        </div>
      </div>

      {/* Budget Alerts */}
      {budgetAlerts.length > 0 && (
        <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 rounded-lg">
          <div className="flex items-start">
            <AlertTriangle className="w-6 h-6 text-yellow-400 mr-3 flex-shrink-0" />
            <div className="flex-1">
              <h3 className="text-sm font-medium text-yellow-800">Budget Alerts</h3>
              <div className="mt-2 text-sm text-yellow-700">
                <ul className="list-disc list-inside space-y-1">
                  {budgetAlerts.map((budget) => (
                    <li key={budget.id}>
                      {budget.category.name} budget has exceeded {budget.alertThreshold}%
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Category Breakdown Chart */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Category Breakdown</h2>
        {summary?.categoryBreakdown && summary.categoryBreakdown.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={summary.categoryBreakdown}
                dataKey="totalAmount"
                nameKey="categoryName"
                cx="50%"
                cy="50%"
                outerRadius={100}
                label
              >
                {summary.categoryBreakdown.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip formatter={(value: number) => formatCurrency(value)} />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        ) : (
          <p className="text-center text-gray-500">No data available</p>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
