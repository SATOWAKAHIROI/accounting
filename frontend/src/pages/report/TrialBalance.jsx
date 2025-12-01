import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import reportService from '../../services/reportService';

export default function TrialBalance() {
  const [asOfDate, setAsOfDate] = useState('');
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // デフォルト日付設定（当日）
    const today = new Date();
    setAsOfDate(today.toISOString().split('T')[0]);
  }, []);

  const handleGenerate = async (e) => {
    e.preventDefault();
    if (!asOfDate) {
      alert('基準日を選択してください');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await reportService.getTrialBalance(asOfDate);
      setReport(response);
    } catch (err) {
      setError('試算表の生成に失敗しました: ' + (err.response?.data?.message || err.message));
      setReport(null);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }).format(amount);
  };

  const accountTypeLabels = {
    ASSET: '資産',
    LIABILITY: '負債',
    EQUITY: '純資産',
    REVENUE: '収益',
    EXPENSE: '費用',
  };

  return (
    <MainLayout>
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-6">試算表</h1>

        {/* 検索フォーム */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <form onSubmit={handleGenerate} className="flex items-end space-x-4">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                基準日 <span className="text-red-500">*</span>
              </label>
              <input
                type="date"
                value={asOfDate}
                onChange={(e) => setAsOfDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-400"
            >
              {loading ? '生成中...' : '生成'}
            </button>
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
              <h2 className="text-xl font-semibold">試算表</h2>
              <p className="text-sm text-gray-600 mt-1">基準日: {asOfDate}</p>
            </div>

            <div className="p-6">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        勘定科目コード
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        勘定科目名
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        タイプ
                      </th>
                      <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        借方残高
                      </th>
                      <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        貸方残高
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {report.entries && report.entries.length > 0 ? (
                      <>
                        {report.entries.map((entry, index) => (
                          <tr key={index} className="hover:bg-gray-50">
                            <td className="px-4 py-3 whitespace-nowrap text-sm font-mono text-gray-900">
                              {entry.accountCode}
                            </td>
                            <td className="px-4 py-3 text-sm text-gray-900">
                              {entry.accountName}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-600">
                              {accountTypeLabels[entry.accountType] || entry.accountType}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm text-right text-gray-900">
                              {entry.debitBalance > 0 ? formatCurrency(entry.debitBalance) : '-'}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm text-right text-gray-900">
                              {entry.creditBalance > 0 ? formatCurrency(entry.creditBalance) : '-'}
                            </td>
                          </tr>
                        ))}
                        {/* 合計行 */}
                        <tr className="bg-gray-100 font-bold">
                          <td colSpan="3" className="px-4 py-3 text-sm text-gray-900">
                            合計
                          </td>
                          <td className="px-4 py-3 whitespace-nowrap text-sm text-right text-gray-900">
                            {formatCurrency(report.totalDebit)}
                          </td>
                          <td className="px-4 py-3 whitespace-nowrap text-sm text-right text-gray-900">
                            {formatCurrency(report.totalCredit)}
                          </td>
                        </tr>
                      </>
                    ) : (
                      <tr>
                        <td colSpan="5" className="px-4 py-8 text-center text-gray-500">
                          データがありません
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>

              {/* バランスチェック */}
              {report.entries && report.entries.length > 0 && (
                <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded">
                  <div className="flex items-center justify-between">
                    <span className="font-medium text-gray-700">貸借バランス:</span>
                    <div className="flex items-center space-x-2">
                      {Math.abs(report.difference) < 0.01 ? (
                        <>
                          <span className="text-green-600 font-semibold">✓ バランスOK</span>
                          <span className="text-gray-600">（差額: {formatCurrency(report.difference)}）</span>
                        </>
                      ) : (
                        <>
                          <span className="text-red-600 font-semibold">✗ 不一致</span>
                          <span className="text-red-600">（差額: {formatCurrency(report.difference)}）</span>
                        </>
                      )}
                    </div>
                  </div>
                </div>
              )}

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
