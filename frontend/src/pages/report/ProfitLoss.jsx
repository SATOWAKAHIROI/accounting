import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import reportService from '../../services/reportService';

export default function ProfitLoss() {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // デフォルト日付設定（当月）
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    setStartDate(firstDay.toISOString().split('T')[0]);
    setEndDate(today.toISOString().split('T')[0]);
  }, []);

  const handleGenerate = async (e) => {
    e.preventDefault();
    if (!startDate || !endDate) {
      alert('期間を選択してください');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await reportService.getProfitLoss(startDate, endDate);
      setReport(response);
    } catch (err) {
      setError('損益計算書の生成に失敗しました: ' + (err.response?.data?.message || err.message));
      setReport(null);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }).format(amount);
  };

  return (
    <MainLayout>
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-6">損益計算書（P/L）</h1>

        {/* 検索フォーム */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <form onSubmit={handleGenerate} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                開始日 <span className="text-red-500">*</span>
              </label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
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
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <div className="flex items-end">
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-400"
              >
                {loading ? '生成中...' : '生成'}
              </button>
            </div>
          </form>
        </div>

        {/* エラー表示 */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
            {error}
          </div>
        )}

        {/* レポート表示 */}
        {report && (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="p-6 border-b border-gray-200">
              <h2 className="text-xl font-semibold">損益計算書</h2>
              <p className="text-sm text-gray-600 mt-1">
                期間: {startDate} ～ {endDate}
              </p>
            </div>

            <div className="p-6">
              <div className="space-y-6">
                {/* 収益の部 */}
                <div>
                  <h3 className="text-lg font-semibold mb-3 text-blue-900">収益の部</h3>
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex justify-between items-center py-2">
                      <span className="font-medium">売上高</span>
                      <span className="text-lg font-semibold">{formatCurrency(report.revenue || 0)}</span>
                    </div>
                  </div>
                </div>

                {/* 費用の部 */}
                <div>
                  <h3 className="text-lg font-semibold mb-3 text-red-900">費用の部</h3>
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex justify-between items-center py-2">
                      <span className="font-medium">売上原価</span>
                      <span className="text-lg">{formatCurrency(report.costOfSales || 0)}</span>
                    </div>
                    <div className="flex justify-between items-center py-2 border-t border-gray-200 mt-2 pt-2">
                      <span className="font-medium">販売費及び一般管理費</span>
                      <span className="text-lg">{formatCurrency(report.operatingExpenses || 0)}</span>
                    </div>
                    <div className="flex justify-between items-center py-2 border-t border-gray-200 mt-2 pt-2">
                      <span className="font-medium">その他費用</span>
                      <span className="text-lg">{formatCurrency(report.otherExpenses || 0)}</span>
                    </div>
                    <div className="flex justify-between items-center py-2 border-t-2 border-gray-400 mt-3 pt-3">
                      <span className="font-semibold">費用合計</span>
                      <span className="text-lg font-semibold">{formatCurrency(report.expense || 0)}</span>
                    </div>
                  </div>
                </div>

                {/* 当期純利益 */}
                <div className="bg-gradient-to-r from-blue-50 to-green-50 rounded-lg p-6 border-2 border-blue-300">
                  <div className="flex justify-between items-center">
                    <span className="text-xl font-bold text-gray-900">当期純利益</span>
                    <span className={`text-2xl font-bold ${report.netIncome >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                      {formatCurrency(report.netIncome || 0)}
                    </span>
                  </div>
                  {report.netIncome < 0 && (
                    <p className="text-sm text-red-600 mt-2">※ マイナスは損失を示します</p>
                  )}
                </div>

                {/* 計算式の説明 */}
                <div className="bg-gray-100 rounded-lg p-4 text-sm text-gray-700">
                  <h4 className="font-semibold mb-2">計算式:</h4>
                  <p>当期純利益 = 収益 - 費用</p>
                  <p className="mt-1">
                    = {formatCurrency(report.revenue || 0)} - {formatCurrency(report.expense || 0)}
                  </p>
                  <p className="mt-1">
                    = {formatCurrency(report.netIncome || 0)}
                  </p>
                </div>
              </div>

              <div className="mt-6 flex justify-end space-x-4">
                <button
                  type="button"
                  className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onClick={() => alert('PDF出力機能は今後実装予定です')}
                >
                  PDF出力
                </button>
                <button
                  type="button"
                  className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onClick={() => alert('Excel出力機能は今後実装予定です')}
                >
                  Excel出力
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
}
