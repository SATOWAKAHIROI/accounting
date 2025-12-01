import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import fiscalPeriodService from '../../services/fiscalPeriodService';

export default function FiscalPeriodList() {
  const [periods, setPeriods] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingPeriod, setEditingPeriod] = useState(null);
  const [formData, setFormData] = useState({
    periodYear: '',
    periodNumber: '',
    periodName: '',
    startDate: '',
    endDate: '',
  });

  useEffect(() => {
    fetchPeriods();
  }, []);

  const fetchPeriods = async () => {
    try {
      setLoading(true);
      const response = await fiscalPeriodService.getAll();
      setPeriods(response || []);
      setError(null);
    } catch (err) {
      setError('データの取得に失敗しました: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingPeriod(null);
    setFormData({
      periodYear: new Date().getFullYear(),
      periodNumber: 1,
      periodName: '',
      startDate: '',
      endDate: '',
    });
    setShowModal(true);
  };

  const handleEdit = (period) => {
    setEditingPeriod(period);
    setFormData({
      periodYear: period.periodYear,
      periodNumber: period.periodNumber,
      periodName: period.periodName,
      startDate: period.startDate,
      endDate: period.endDate,
    });
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingPeriod) {
        await fiscalPeriodService.update(editingPeriod.id, formData);
      } else {
        await fiscalPeriodService.create(formData);
      }
      setShowModal(false);
      fetchPeriods();
    } catch (err) {
      alert('保存に失敗しました: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleClose = async (period) => {
    if (window.confirm(`${period.periodName}を締めますか？締めると仕訳の追加・編集ができなくなります。`)) {
      try {
        await fiscalPeriodService.close(period.id);
        fetchPeriods();
      } catch (err) {
        alert('期間締めに失敗しました: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleReopen = async (period) => {
    if (window.confirm(`${period.periodName}を再開しますか？`)) {
      try {
        await fiscalPeriodService.reopen(period.id);
        fetchPeriods();
      } catch (err) {
        alert('期間再開に失敗しました: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleDelete = async (period) => {
    if (period.isClosed) {
      alert('締められた会計期間は削除できません');
      return;
    }
    if (window.confirm(`${period.periodName}を削除しますか？`)) {
      try {
        await fiscalPeriodService.delete(period.id);
        fetchPeriods();
      } catch (err) {
        alert('削除に失敗しました: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  if (loading) {
    return (
      <MainLayout>
        <div className="p-6">
          <div className="text-center text-gray-500">読み込み中...</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="p-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold">会計期間管理</h1>
          <button
            onClick={handleCreate}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            新規作成
          </button>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
            {error}
          </div>
        )}

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  年度
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  期
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  期間名
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  開始日
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  終了日
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  状態
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  操作
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {periods.length > 0 ? (
                periods.map((period) => (
                  <tr key={period.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {period.periodYear}年度
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      第{period.periodNumber}期
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {period.periodName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {period.startDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {period.endDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {period.isClosed ? (
                        <span className="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                          締め済み
                        </span>
                      ) : (
                        <span className="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                          未締め
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                      {!period.isClosed && (
                        <>
                          <button
                            onClick={() => handleEdit(period)}
                            className="text-blue-600 hover:text-blue-900"
                          >
                            編集
                          </button>
                          <button
                            onClick={() => handleClose(period)}
                            className="text-yellow-600 hover:text-yellow-900"
                          >
                            締める
                          </button>
                          <button
                            onClick={() => handleDelete(period)}
                            className="text-red-600 hover:text-red-900"
                          >
                            削除
                          </button>
                        </>
                      )}
                      {period.isClosed && (
                        <button
                          onClick={() => handleReopen(period)}
                          className="text-green-600 hover:text-green-900"
                        >
                          再開
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="7" className="px-6 py-8 text-center text-gray-500">
                    会計期間が登録されていません
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* モーダル */}
        {showModal && (
          <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
            <div className="relative top-20 mx-auto p-5 border w-full max-w-md shadow-lg rounded-md bg-white">
              <div className="mb-4">
                <h3 className="text-lg font-medium text-gray-900">
                  {editingPeriod ? '会計期間編集' : '会計期間新規作成'}
                </h3>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        年度 <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="number"
                        value={formData.periodYear}
                        onChange={(e) => setFormData({ ...formData, periodYear: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        期 <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="number"
                        value={formData.periodNumber}
                        onChange={(e) => setFormData({ ...formData, periodNumber: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      期間名 <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      value={formData.periodName}
                      onChange={(e) => setFormData({ ...formData, periodName: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="例: 2024年度第1期"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      開始日 <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="date"
                      value={formData.startDate}
                      onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      終了日 <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="date"
                      value={formData.endDate}
                      onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>
                </div>

                <div className="mt-6 flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    キャンセル
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    保存
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
}
