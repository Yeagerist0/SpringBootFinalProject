import { useEffect, useState } from 'react';
import { budgetService } from '@/services/budgetService';
import { categoryService } from '@/services/categoryService';
import { Budget, Category } from '@/types';
import { formatCurrency, formatDate, calculatePercentage } from '@/utils/helpers';
import { Plus, Edit, Trash2, AlertCircle } from 'lucide-react';
import toast from 'react-hot-toast';

const Budgets = () => {
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedBudget, setSelectedBudget] = useState<Budget | null>(null);
  const [formData, setFormData] = useState({
    categoryId: '',
    limitAmount: '',
    period: 'MONTHLY' as 'MONTHLY' | 'QUARTERLY' | 'YEARLY',
    alertThreshold: '80',
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [budgetData, categoryData] = await Promise.all([
        budgetService.getBudgets(),
        categoryService.getCategories(),
      ]);
      setBudgets(budgetData.content);
      setCategories(categoryData);
    } catch (error) {
      toast.error('Failed to fetch budgets');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const budget = {
        category: { id: formData.categoryId },
        limitAmount: parseFloat(formData.limitAmount),
        period: formData.period,
        alertThreshold: parseFloat(formData.alertThreshold),
      };

      if (selectedBudget) {
        await budgetService.updateBudget(selectedBudget.id, budget);
        toast.success('Budget updated successfully');
      } else {
        await budgetService.createBudget(budget);
        toast.success('Budget created successfully');
      }

      setShowModal(false);
      setSelectedBudget(null);
      resetForm();
      fetchData();
    } catch (error) {
      toast.error('Failed to save budget');
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this budget?')) {
      try {
        await budgetService.deleteBudget(id);
        toast.success('Budget deleted successfully');
        fetchData();
      } catch (error) {
        toast.error('Failed to delete budget');
      }
    }
  };

  const handleEdit = (budget: Budget) => {
    setSelectedBudget(budget);
    setFormData({
      categoryId: budget.category.id,
      limitAmount: budget.limitAmount.toString(),
      period: budget.period,
      alertThreshold: budget.alertThreshold.toString(),
    });
    setShowModal(true);
  };

  const resetForm = () => {
    setFormData({
      categoryId: '',
      limitAmount: '',
      period: 'MONTHLY',
      alertThreshold: '80',
    });
  };

  const getProgressColor = (percentage: number) => {
    if (percentage >= 100) return 'bg-red-600';
    if (percentage >= 80) return 'bg-yellow-600';
    return 'bg-green-600';
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900">Budgets</h1>
        <button
          onClick={() => {
            resetForm();
            setSelectedBudget(null);
            setShowModal(true);
          }}
          className="btn-primary flex items-center gap-2"
        >
          <Plus className="w-5 h-5" />
          Add Budget
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {budgets.map((budget) => {
          const percentage = calculatePercentage(budget.spentAmount, budget.limitAmount);
          return (
            <div key={budget.id} className="bg-white rounded-lg shadow-md p-6">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="font-semibold text-gray-900">{budget.category.name}</h3>
                  <span className="text-xs text-gray-500">{budget.period}</span>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleEdit(budget)} className="text-primary-600 hover:text-primary-900">
                    <Edit className="w-4 h-4" />
                  </button>
                  <button onClick={() => handleDelete(budget.id)} className="text-red-600 hover:text-red-900">
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Spent</span>
                  <span className="font-medium">{formatCurrency(budget.spentAmount)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Limit</span>
                  <span className="font-medium">{formatCurrency(budget.limitAmount)}</span>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Progress</span>
                  <span className="font-semibold">{percentage}%</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className={`h-2 rounded-full transition-all ${getProgressColor(percentage)}`}
                    style={{ width: `${Math.min(percentage, 100)}%` }}
                  ></div>
                </div>
              </div>

              {percentage >= budget.alertThreshold && (
                <div className="mt-4 flex items-center gap-2 text-yellow-700 bg-yellow-50 p-2 rounded">
                  <AlertCircle className="w-4 h-4" />
                  <span className="text-xs">Alert threshold reached</span>
                </div>
              )}

              <div className="mt-4 text-xs text-gray-500">
                <div>Period: {formatDate(budget.startDate)} - {formatDate(budget.endDate)}</div>
              </div>
            </div>
          );
        })}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
            <h2 className="text-xl font-bold mb-4">{selectedBudget ? 'Edit Budget' : 'Add Budget'}</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
                <select
                  value={formData.categoryId}
                  onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                  required
                  className="input-field"
                >
                  <option value="">Select category</option>
                  {categories.filter(c => c.type === 'EXPENSE').map((cat) => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Limit Amount</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.limitAmount}
                  onChange={(e) => setFormData({ ...formData, limitAmount: e.target.value })}
                  required
                  className="input-field"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Period</label>
                <select
                  value={formData.period}
                  onChange={(e) => setFormData({ ...formData, period: e.target.value as 'MONTHLY' | 'QUARTERLY' | 'YEARLY' })}
                  className="input-field"
                >
                  <option value="MONTHLY">Monthly</option>
                  <option value="QUARTERLY">Quarterly</option>
                  <option value="YEARLY">Yearly</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Alert Threshold (%)</label>
                <input
                  type="number"
                  min="0"
                  max="100"
                  value={formData.alertThreshold}
                  onChange={(e) => setFormData({ ...formData, alertThreshold: e.target.value })}
                  required
                  className="input-field"
                />
              </div>
              <div className="flex gap-3 pt-4">
                <button type="submit" className="btn-primary flex-1">
                  {selectedBudget ? 'Update' : 'Create'}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setSelectedBudget(null);
                    resetForm();
                  }}
                  className="btn-secondary flex-1"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Budgets;
