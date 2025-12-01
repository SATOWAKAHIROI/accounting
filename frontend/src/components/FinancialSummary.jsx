import React, { useState, useEffect } from 'react';
import api from '../utils/api';

export default function FinancialSummary() {
  const [summary, setSummary] = useState({
    revenue: 0,
    expense: 0,
    netProfit: 0,
    pendingInvoices: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchSummary();
  }, []);

  const fetchSummary = async () => {
    try {
      // TODO: 実際のAPIエンドポイントに置き換え
      // const companyId = 1; // 実際は認証から取得
      // const response = await api.get(`/companies/${companyId}/dashboard/summary`);
      // setSummary(response.data.data);

      // ダミーデータ
      setSummary({
        revenue: 5000000,
        expense: 3000000,
        netProfit: 2000000,
        pendingInvoices: 5,
      });
    } catch (error) {
      console.error('Failed to fetch summary:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div>読み込み中...</div>;
  }

  return (
    <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <dt className="text-sm font-medium text-gray-500 truncate">
            当月売上
          </dt>
          <dd className="mt-1 text-3xl font-semibold text-gray-900">
            ¥{summary.revenue.toLocaleString()}
          </dd>
        </div>
      </div>

      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <dt className="text-sm font-medium text-gray-500 truncate">
            当月費用
          </dt>
          <dd className="mt-1 text-3xl font-semibold text-gray-900">
            ¥{summary.expense.toLocaleString()}
          </dd>
        </div>
      </div>

      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <dt className="text-sm font-medium text-gray-500 truncate">
            当月利益
          </dt>
          <dd className="mt-1 text-3xl font-semibold text-green-600">
            ¥{summary.netProfit.toLocaleString()}
          </dd>
        </div>
      </div>

      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <dt className="text-sm font-medium text-gray-500 truncate">
            未処理請求書
          </dt>
          <dd className="mt-1 text-3xl font-semibold text-orange-600">
            {summary.pendingInvoices}件
          </dd>
        </div>
      </div>
    </div>
  );
}
