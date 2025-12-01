import React, { useState, useEffect } from "react";
import MainLayout from "../../components/layout/MainLayout";
import reportService from "../../services/reportService";

export default function BalanceSheet() {
  const [asOfDate, setAsOfDate] = useState("");
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // デフォルト日付設定（当日）
    const today = new Date();
    setAsOfDate(today.toISOString().split("T")[0]);
  }, []);

  const handleGenerate = async (e) => {
    e.preventDefault();
    if (!asOfDate) {
      alert("基準日を選択してください");
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await reportService.getBalanceSheet(asOfDate);
      setReport(response);
    } catch (err) {
      setError(
        "貸借対照表の生成に失敗しました: " +
          (err.response?.data?.message || err.message)
      );
      setReport(null);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ja-JP", {
      style: "currency",
      currency: "JPY",
    }).format(amount);
  };

  return (
    <MainLayout>
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-6">貸借対照表（B/S）</h1>

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
              {loading ? "生成中..." : "生成"}
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
              <h2 className="text-xl font-semibold">貸借対照表</h2>
              <p className="text-sm text-gray-600 mt-1">基準日: {asOfDate}</p>
            </div>

            <div className="p-6">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* 左側：資産の部 */}
                <div>
                  <h3 className="text-lg font-semibold mb-4 text-blue-900 border-b-2 border-blue-900 pb-2">
                    資産の部
                  </h3>
                  <div className="space-y-3">
                    <div className="bg-blue-50 rounded-lg p-4">
                      <div className="flex justify-between items-center">
                        <span className="font-medium">流動資産</span>
                        <span className="text-lg font-semibold">
                          {formatCurrency(report.currentAssets || 0)}
                        </span>
                      </div>
                    </div>
                    <div className="bg-blue-50 rounded-lg p-4">
                      <div className="flex justify-between items-center">
                        <span className="font-medium">固定資産</span>
                        <span className="text-lg font-semibold">
                          {formatCurrency(report.fixedAssets || 0)}
                        </span>
                      </div>
                    </div>
                    <div className="bg-blue-100 rounded-lg p-4 border-2 border-blue-900">
                      <div className="flex justify-between items-center">
                        <span className="font-bold text-lg">資産合計</span>
                        <span className="text-xl font-bold text-blue-900">
                          {formatCurrency(report.assets || 0)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                {/* 右側：負債・純資産の部 */}
                <div>
                  <h3 className="text-lg font-semibold mb-4 text-red-900 border-b-2 border-red-900 pb-2">
                    負債・純資産の部
                  </h3>
                  <div className="space-y-3">
                    {/* 負債 */}
                    <div className="bg-red-50 rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <span className="font-medium text-gray-700">
                          流動負債
                        </span>
                        <span className="text-lg font-semibold">
                          {formatCurrency(report.currentLiabilities || 0)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center pt-2 border-t border-red-200">
                        <span className="font-medium text-gray-700">
                          固定負債
                        </span>
                        <span className="text-lg font-semibold">
                          {formatCurrency(report.fixedLiabilities || 0)}
                        </span>
                      </div>
                      <div className="flex justify-between items-center pt-2 mt-2 border-t-2 border-red-300">
                        <span className="font-semibold">負債合計</span>
                        <span className="text-lg font-bold">
                          {formatCurrency(report.liabilities || 0)}
                        </span>
                      </div>
                    </div>

                    {/* 純資産 */}
                    <div className="bg-green-50 rounded-lg p-4">
                      <div className="flex justify-between items-center">
                        <span className="font-semibold">純資産合計</span>
                        <span className="text-lg font-bold text-green-900">
                          {formatCurrency(report.equity || 0)}
                        </span>
                      </div>
                    </div>

                    {/* 当期純利益 */}
                    <div className="bg-blue-50 rounded-lg p-4">
                      <div className="flex justify-between items-center">
                        <span className="font-semibold">当期純利益</span>
                        <span
                          className={`text-lg font-bold ${
                            (report.netProfit || 0) >= 0
                              ? "text-blue-900"
                              : "text-red-600"
                          }`}
                        >
                          {formatCurrency(report.netProfit || 0)}
                        </span>
                      </div>
                      {(report.netProfit || 0) < 0 && (
                        <p className="text-xs text-red-600 mt-1">
                          ※ マイナスは損失を示します
                        </p>
                      )}
                    </div>

                    {/* 負債・純資産合計 */}
                    <div className="bg-red-100 rounded-lg p-4 border-2 border-red-900">
                      <div className="flex justify-between items-center">
                        <span className="font-bold text-lg">
                          負債・純資産合計
                        </span>
                        <span className="text-xl font-bold text-red-900">
                          {formatCurrency(
                            report.totalLiabilitiesAndEquity || 0
                          )}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* バランスチェック */}
              <div className="mt-8 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                <div className="flex items-center justify-between">
                  <span className="font-medium text-gray-700">
                    貸借バランス:
                  </span>
                  <div className="flex items-center space-x-2">
                    {Math.abs(report.difference || 0) < 0.01 ? (
                      <>
                        <span className="text-green-600 font-semibold text-lg">
                          ✓ バランスOK
                        </span>
                        <span className="text-gray-600">
                          （資産 = 負債 + 純資産 + 当期純利益）
                        </span>
                      </>
                    ) : (
                      <>
                        <span className="text-red-600 font-semibold text-lg">
                          ✗ 不一致
                        </span>
                        <span className="text-red-600">
                          （差額: {formatCurrency(report.difference || 0)}）
                        </span>
                      </>
                    )}
                  </div>
                </div>
              </div>

              {/* 計算式の説明 */}
              <div className="mt-4 bg-gray-100 rounded-lg p-4 text-sm text-gray-700">
                <h4 className="font-semibold mb-2">計算式:</h4>
                <p>資産合計 = 負債合計 + 純資産合計 + 当期純利益</p>
                <p className="mt-1">
                  {formatCurrency(report.assets || 0)} ={" "}
                  {formatCurrency(report.liabilities || 0)} +{" "}
                  {formatCurrency(report.equity || 0)} +{" "}
                  {formatCurrency(report.netProfit || 0)}
                </p>
                <p className="mt-1">
                  {formatCurrency(report.assets || 0)} ={" "}
                  {formatCurrency(report.totalLiabilitiesAndEquity || 0)}
                </p>
                <p className="mt-1 text-xs text-gray-600">
                  ※
                  当期純利益がマイナスの場合は損失を意味し、負債・純資産合計から差し引かれます
                </p>
              </div>

              <div className="mt-6 flex justify-end space-x-4">
                <button
                  type="button"
                  className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onClick={() => alert("PDF出力機能は今後実装予定です")}
                >
                  PDF出力
                </button>
                <button
                  type="button"
                  className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onClick={() => alert("Excel出力機能は今後実装予定です")}
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
