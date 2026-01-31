import { useEffect, useState } from 'react';
import { expenseService, ExpenseFilters } from '@/services/expenseService';
import { categoryService } from '@/services/categoryService';
import { Expense, Category } from '@/types';
import { formatCurrency, formatDate } from '@/utils/helpers';
import { Plus, Search, Filter, Upload, Trash2, Edit } from 'lucide-react';
import toast from 'react-hot-toast';

const Expenses = () => {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedExpense, setSelectedExpense] = useState<Expense | null>(null);
  const [filters, setFilters] = useState<ExpenseFilters>({ page: 0, size: 10 });
  const [formData, setFormData] = useState({
    amount: '',
    description: '',
    categoryId: '',
    date: new Date().toISOString().split('T')[0],
  });

  useEffect(() => {
    fetchData();
  }, [filters]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [expenseData, categoryData] = await Promise.all([
        expenseService.getExpenses(filters),
        categoryService.getCategories(),
      ]);
      setExpenses(expenseData.content);
      setCategories(categoryData.filter((c) => c.type === 'EXPENSE'));
    } catch (error) {
      toast.error('Failed to fetch expenses');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const expense = {
        amount: parseFloat(formData.amount),
        description: formData.description,
        category: { id: formData.categoryId },
        date: formData.date,
      };

      if (selectedExpense) {
        await expenseService.updateExpense(selectedExpense.id, expense);
        toast.success('Expense updated successfully');
      } else {
        await expenseService.createExpense(expense);
        toast.success('Expense created successfully');
      }

      setShowModal(false);
      setSelectedExpense(null);
      resetForm();
      fetchData();
    } catch (error) {
      toast.error('Failed to save expense');
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this expense?')) {
      try {
        await expenseService.deleteExpense(id);
        toast.success('Expense deleted successfully');
        fetchData();
      } catch (error) {
        toast.error('Failed to delete expense');
      }
    }
  };

  const handleEdit = (expense: Expense) => {
    setSelectedExpense(expense);
    setFormData({
      amount: expense.amount.toString(),
      description: expense.description,
      categoryId: expense.category.id,
      date: expense.date.split('T')[0],
    });
    setShowModal(true);
  };

  const resetForm = () => {
    setFormData({
      amount: '',
      description: '',
      categoryId: '',
      date: new Date().toISOString().split('T')[0],
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900">Expenses</h1>
        <button
          onClick={() => {
            resetForm();
            setSelectedExpense(null);
            setShowModal(true);
          }}
          className="btn-primary flex items-center gap-2"
        >
          <Plus className="w-5 h-5" />
          Add Expense
        </button>
      </div>

      {/* Expense List */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Description</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Category</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {expenses.map((expense) => (
              <tr key={expense.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{formatDate(expense.date)}</td>
                <td className="px-6 py-4 text-sm text-gray-900">{expense.description}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span
                    className="px-2 py-1 text-xs rounded-full"
                    style={{ backgroundColor: expense.category.color + '20', color: expense.category.color }}
                  >
                    {expense.category.name}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-red-600">
                  {formatCurrency(expense.amount)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button onClick={() => handleEdit(expense)} className="text-primary-600 hover:text-primary-900 mr-3">
                    <Edit className="w-4 h-4" />
                  </button>
                  <button onClick={() => handleDelete(expense.id)} className="text-red-600 hover:text-red-900">
                    <Trash2 className="w-4 h-4" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
            <h2 className="text-xl font-bold mb-4">{selectedExpense ? 'Edit Expense' : 'Add Expense'}</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Amount</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.amount}
                  onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                  required
                  className="input-field"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
                <input
                  type="text"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  required
                  className="input-field"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
                <select
                  value={formData.categoryId}
                  onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                  required
                  className="input-field"
                >
                  <option value="">Select category</option>
                  {categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Date</label>
                <input
                  type="date"
                  value={formData.date}
                  onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                  required
                  className="input-field"
                />
              </div>
              <div className="flex gap-3 pt-4">
                <button type="submit" className="btn-primary flex-1">
                  {selectedExpense ? 'Update' : 'Create'}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setSelectedExpense(null);
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

export default Expenses;
