import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import journalService from '../../services/journalService';
import accountService from '../../services/accountService';
import subAccountService from '../../services/subAccountService';

export default function JournalList() {
  const [journals, setJournals] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [subAccounts, setSubAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingJournal, setEditingJournal] = useState(null);
  const [filterStartDate, setFilterStartDate] = useState('');
  const [filterEndDate, setFilterEndDate] = useState('');
  const [formData, setFormData] = useState({
    journalDate: '',
    description: '',
    details: [
      { lineNumber: 1, entryType: 'DEBIT', accountId: '', subAccountId: '', amount: '', description: '' },
      { lineNumber: 2, entryType: 'CREDIT', accountId: '', subAccountId: '', amount: '', description: '' },
    ],
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [journalsRes, accountsRes, subAccountsRes] = await Promise.all([
        journalService.getAll(),
        accountService.getAll(),
        subAccountService.getAll(),
      ]);
      setJournals(journalsRes || []);
      setAccounts(accountsRes || []);
      setSubAccounts(subAccountsRes || []);
      setError(null);
    } catch (err) {
      setError('データの取得に失敗しました: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingJournal(null);
    setFormData({
      journalDate: new Date().toISOString().split('T')[0],
      description: '',
      details: [
        { lineNumber: 1, entryType: 'DEBIT', accountId: '', subAccountId: '', amount: '', description: '' },
        { lineNumber: 2, entryType: 'CREDIT', accountId: '', subAccountId: '', amount: '', description: '' },
      ],
    });
    setShowModal(true);
  };

  const handleEdit = (journal) => {
    setEditingJournal(journal);
    setFormData({
      journalDate: journal.journalDate,
      description: journal.description || '',
      details: journal.details?.map(d => ({
        lineNumber: d.lineNumber,
        entryType: d.entryType,
        accountId: d.account?.id || '',
        subAccountId: d.subAccount?.id || '',
        amount: d.amount,
        description: d.description || '',
      })) || [],
    });
    setShowModal(true);
  };

  const handleAddDetail = () => {
    const newLineNumber = formData.details.length + 1;
    setFormData({
      ...formData,
      details: [
        ...formData.details,
        { lineNumber: newLineNumber, entryType: 'DEBIT', accountId: '', subAccountId: '', amount: '', description: '' },
      ],
    });
  };

  const handleRemoveDetail = (index) => {
    if (formData.details.length <= 2) {
      alert('最低2行必要です');
      return;
    }
    const newDetails = formData.details.filter((_, i) => i !== index);
    setFormData({ ...formData, details: newDetails });
  };

  const handleDetailChange = (index, field, value) => {
    const newDetails = [...formData.details];
    newDetails[index] = { ...newDetails[index], [field]: value };
    setFormData({ ...formData, details: newDetails });
  };

  const calculateTotals = () => {
    const debitTotal = formData.details
      .filter(d => d.entryType === 'DEBIT')
      .reduce((sum, d) => sum + (parseFloat(d.amount) || 0), 0);
    const creditTotal = formData.details
      .filter(d => d.entryType === 'CREDIT')
      .reduce((sum, d) => sum + (parseFloat(d.amount) || 0), 0);
    return { debitTotal, creditTotal };
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { debitTotal, creditTotal } = calculateTotals();
    if (Math.abs(debitTotal - creditTotal) > 0.01) {
      alert(`借方合計(${debitTotal})と貸方合計(${creditTotal})が一致しません`);
      return;
    }
    try {
      // 仕訳番号を自動生成（日付ベース）
      const journalNumber = editingJournal
        ? editingJournal.journalNumber
        : `J-${formData.journalDate.replace(/-/g, '')}-${Date.now().toString().slice(-6)}`;

      const payload = {
        ...formData,
        journalNumber,
        details: formData.details.map(d => ({
          ...d,
          subAccountId: d.subAccountId || null,
        })),
      };
      if (editingJournal) {
        await journalService.update(editingJournal.id, payload);
      } else {
        await journalService.create(payload);
      }
      setShowModal(false);
      fetchData();
    } catch (err) {
      alert('保存に失敗しました: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleDelete = async (journal) => {
    if (window.confirm(`${journal.journalDate}の仕訳を削除しますか？`)) {
      try {
        await journalService.delete(journal.id);
        fetchData();
      } catch (err) {
        alert('削除に失敗しました: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleFilter = async () => {
    if (!filterStartDate || !filterEndDate) {
      alert('開始日と終了日を両方入力してください');
      return;
    }
    try {
      setLoading(true);
      const response = await journalService.getByDateRange(filterStartDate, filterEndDate);
      setJournals(response || []);
    } catch (err) {
      setError('フィルタに失敗しました: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleClearFilter = () => {
    setFilterStartDate('');
    setFilterEndDate('');
    fetchData();
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }).format(value);
  };

  const { debitTotal, creditTotal } = calculateTotals();

  return (
    <MainLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-900">仕訳管理</h2>
          <button
            onClick={handleCreate}
            className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
          >
            新規作成
          </button>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        <div className="bg-white p-4 rounded-lg shadow">
          <div className="flex items-center gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">開始日</label>
              <input
                type="date"
                value={filterStartDate}
                onChange={(e) => setFilterStartDate(e.target.value)}
                className="mt-1 border border-gray-300 rounded-md shadow-sm py-1 px-3"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">終了日</label>
              <input
                type="date"
                value={filterEndDate}
                onChange={(e) => setFilterEndDate(e.target.value)}
                className="mt-1 border border-gray-300 rounded-md shadow-sm py-1 px-3"
              />
            </div>
            <div className="flex gap-2 mt-6">
              <button
                onClick={handleFilter}
                className="bg-gray-600 text-white px-4 py-1 rounded-md hover:bg-gray-700"
              >
                絞り込み
              </button>
              <button
                onClick={handleClearFilter}
                className="bg-gray-300 text-gray-700 px-4 py-1 rounded-md hover:bg-gray-400"
              >
                クリア
              </button>
            </div>
          </div>
        </div>

        <div className="bg-white shadow rounded-lg overflow-hidden">
          {loading ? (
            <div className="p-6 text-center">読み込み中...</div>
          ) : (
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    日付
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    摘要
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    明細数
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    金額
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {journals.map((journal) => {
                  const totalAmount = journal.details
                    ?.filter(d => d.entryType === 'DEBIT')
                    .reduce((sum, d) => sum + parseFloat(d.amount || 0), 0) || 0;
                  return (
                    <tr key={journal.id}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {journal.journalDate}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {journal.description}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {journal.details?.length || 0}行
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {formatCurrency(totalAmount)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <button
                          onClick={() => handleEdit(journal)}
                          className="text-indigo-600 hover:text-indigo-900 mr-4"
                        >
                          編集
                        </button>
                        <button
                          onClick={() => handleDelete(journal)}
                          className="text-red-600 hover:text-red-900"
                        >
                          削除
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {showModal && (
        <div className="fixed z-10 inset-0 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:p-0">
            <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>
            <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full">
              <form onSubmit={handleSubmit}>
                <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">
                    {editingJournal ? '仕訳編集' : '仕訳新規作成'}
                  </h3>
                  <div className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">日付</label>
                        <input
                          type="date"
                          required
                          value={formData.journalDate}
                          onChange={(e) => setFormData({ ...formData, journalDate: e.target.value })}
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">摘要</label>
                        <input
                          type="text"
                          value={formData.description}
                          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3"
                        />
                      </div>
                    </div>

                    <div className="border-t pt-4">
                      <div className="flex justify-between items-center mb-2">
                        <h4 className="font-medium text-gray-900">明細行</h4>
                        <button
                          type="button"
                          onClick={handleAddDetail}
                          className="text-sm bg-green-600 text-white px-3 py-1 rounded-md hover:bg-green-700"
                        >
                          行追加
                        </button>
                      </div>
                      <div className="space-y-2 max-h-96 overflow-y-auto">
                        {formData.details.map((detail, index) => (
                          <div key={index} className="grid grid-cols-12 gap-2 items-center bg-gray-50 p-2 rounded">
                            <div className="col-span-1">
                              <select
                                value={detail.entryType}
                                onChange={(e) => handleDetailChange(index, 'entryType', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              >
                                <option value="DEBIT">借方</option>
                                <option value="CREDIT">貸方</option>
                              </select>
                            </div>
                            <div className="col-span-3">
                              <select
                                required
                                value={detail.accountId}
                                onChange={(e) => handleDetailChange(index, 'accountId', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              >
                                <option value="">勘定科目選択</option>
                                {accounts.map(a => (
                                  <option key={a.id} value={a.id}>{a.code} - {a.name}</option>
                                ))}
                              </select>
                            </div>
                            <div className="col-span-2">
                              <select
                                value={detail.subAccountId}
                                onChange={(e) => handleDetailChange(index, 'subAccountId', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              >
                                <option value="">補助科目</option>
                                {subAccounts.filter(sa => sa.account?.id == detail.accountId).map(sa => (
                                  <option key={sa.id} value={sa.id}>{sa.name}</option>
                                ))}
                              </select>
                            </div>
                            <div className="col-span-2">
                              <input
                                type="number"
                                step="0.01"
                                required
                                placeholder="金額"
                                value={detail.amount}
                                onChange={(e) => handleDetailChange(index, 'amount', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              />
                            </div>
                            <div className="col-span-3">
                              <input
                                type="text"
                                placeholder="摘要"
                                value={detail.description}
                                onChange={(e) => handleDetailChange(index, 'description', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              />
                            </div>
                            <div className="col-span-1">
                              <button
                                type="button"
                                onClick={() => handleRemoveDetail(index)}
                                className="text-red-600 hover:text-red-900 text-sm"
                              >
                                削除
                              </button>
                            </div>
                          </div>
                        ))}
                      </div>
                      <div className="mt-4 flex justify-between bg-gray-100 p-3 rounded">
                        <div>
                          <span className="font-medium">借方合計: </span>
                          <span className={debitTotal !== creditTotal ? 'text-red-600 font-bold' : ''}>
                            {formatCurrency(debitTotal)}
                          </span>
                        </div>
                        <div>
                          <span className="font-medium">貸方合計: </span>
                          <span className={debitTotal !== creditTotal ? 'text-red-600 font-bold' : ''}>
                            {formatCurrency(creditTotal)}
                          </span>
                        </div>
                        <div>
                          <span className="font-medium">差額: </span>
                          <span className={Math.abs(debitTotal - creditTotal) > 0.01 ? 'text-red-600 font-bold' : 'text-green-600'}>
                            {formatCurrency(Math.abs(debitTotal - creditTotal))}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                  <button
                    type="submit"
                    className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 sm:ml-3 sm:w-auto sm:text-sm"
                  >
                    {editingJournal ? '更新' : '作成'}
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                  >
                    キャンセル
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </MainLayout>
  );
}
