import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../utils/api';

export default function RecentJournals() {
  const [journals, setJournals] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRecentJournals();
  }, []);

  const fetchRecentJournals = async () => {
    try {
      // TODO: 実際のAPIエンドポイントに置き換え
      // const companyId = 1;
      // const response = await api.get(`/companies/${companyId}/journals?limit=10`);
      // setJournals(response.data.data);

      // ダミーデータ
      setJournals([
        { id: 1, journalNumber: 'J-2024-001', journalDate: '2024-01-15', description: '売上計上', totalAmount: 100000 },
        { id: 2, journalNumber: 'J-2024-002', journalDate: '2024-01-16', description: '経費支払', totalAmount: 50000 },
        { id: 3, journalNumber: 'J-2024-003', journalDate: '2024-01-17', description: '仕入計上', totalAmount: 80000 },
      ]);
    } catch (error) {
      console.error('Failed to fetch journals:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div>読み込み中...</div>;
  }

  return (
    <div className="bg-white shadow rounded-lg">
      <div className="px-4 py-5 sm:px-6 flex justify-between items-center">
        <h3 className="text-lg leading-6 font-medium text-gray-900">
          最近の仕訳
        </h3>
        <Link
          to="/transaction/journals"
          className="text-sm font-medium text-indigo-600 hover:text-indigo-500"
        >
          すべて表示
        </Link>
      </div>
      <div className="border-t border-gray-200">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                仕訳番号
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                日付
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                摘要
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                金額
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {journals.map((journal) => (
              <tr key={journal.id}>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {journal.journalNumber}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {journal.journalDate}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {journal.description}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">
                  ¥{journal.totalAmount.toLocaleString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
