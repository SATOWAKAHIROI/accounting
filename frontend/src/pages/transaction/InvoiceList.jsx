import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import invoiceService from '../../services/invoiceService';
import partnerService from '../../services/partnerService';
import itemService from '../../services/itemService';

export default function InvoiceList() {
  const [invoices, setInvoices] = useState([]);
  const [partners, setPartners] = useState([]);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingInvoice, setEditingInvoice] = useState(null);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [formData, setFormData] = useState({
    invoiceNumber: '',
    invoiceDate: '',
    partnerId: '',
    status: 'DRAFT',
    details: [
      { lineNumber: 1, itemId: '', quantity: '', unitPrice: '', amount: '' },
    ],
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [invoicesRes, partnersRes, itemsRes] = await Promise.all([
        invoiceService.getAll(),
        partnerService.getAll(),
        itemService.getAll(),
      ]);
      setInvoices(invoicesRes.data || []);
      setPartners(partnersRes.data || []);
      setItems(itemsRes.data || []);
      setError(null);
    } catch (err) {
      setError('データの取得に失敗しました: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingInvoice(null);
    setFormData({
      invoiceNumber: '',
      invoiceDate: new Date().toISOString().split('T')[0],
      partnerId: '',
      status: 'DRAFT',
      details: [{ lineNumber: 1, itemId: '', quantity: '', unitPrice: '', amount: '' }],
    });
    setShowModal(true);
  };

  const handleEdit = (invoice) => {
    setEditingInvoice(invoice);
    setFormData({
      invoiceNumber: invoice.invoiceNumber,
      invoiceDate: invoice.invoiceDate,
      partnerId: invoice.partner?.id || '',
      status: invoice.status,
      details: invoice.details?.map(d => ({
        lineNumber: d.lineNumber,
        itemId: d.item?.id || '',
        quantity: d.quantity,
        unitPrice: d.unitPrice,
        amount: d.amount,
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
        { lineNumber: newLineNumber, itemId: '', quantity: '', unitPrice: '', amount: '' },
      ],
    });
  };

  const handleRemoveDetail = (index) => {
    if (formData.details.length <= 1) {
      alert('最低1行必要です');
      return;
    }
    setFormData({ ...formData, details: formData.details.filter((_, i) => i !== index) });
  };

  const handleDetailChange = (index, field, value) => {
    const newDetails = [...formData.details];
    newDetails[index] = { ...newDetails[index], [field]: value };

    if (field === 'itemId' && value) {
      const item = items.find(i => i.id == value);
      if (item) {
        newDetails[index].unitPrice = item.unitPrice;
      }
    }

    if (field === 'quantity' || field === 'unitPrice') {
      const quantity = parseFloat(newDetails[index].quantity) || 0;
      const unitPrice = parseFloat(newDetails[index].unitPrice) || 0;
      newDetails[index].amount = (quantity * unitPrice).toFixed(2);
    }

    setFormData({ ...formData, details: newDetails });
  };

  const calculateTotal = () => {
    return formData.details.reduce((sum, d) => sum + (parseFloat(d.amount) || 0), 0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingInvoice) {
        await invoiceService.update(editingInvoice.id, formData);
      } else {
        await invoiceService.create(formData);
      }
      setShowModal(false);
      fetchData();
    } catch (err) {
      alert('保存に失敗しました: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleStatusChange = async (invoice, newStatus) => {
    try {
      await invoiceService.updateStatus(invoice.id, newStatus);
      fetchData();
    } catch (err) {
      alert('ステータス変更に失敗しました: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleDelete = async (invoice) => {
    if (window.confirm(`請求書 ${invoice.invoiceNumber} を削除しますか？`)) {
      try {
        await invoiceService.delete(invoice.id);
        fetchData();
      } catch (err) {
        alert('削除に失敗しました: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const statusLabels = {
    DRAFT: '下書き',
    ISSUED: '発行済み',
    PAID: '支払済み',
    CANCELED: 'キャンセル',
  };

  const statusColors = {
    DRAFT: 'bg-gray-100 text-gray-800',
    ISSUED: 'bg-blue-100 text-blue-800',
    PAID: 'bg-green-100 text-green-800',
    CANCELED: 'bg-red-100 text-red-800',
  };

  const filteredInvoices = filterStatus === 'ALL'
    ? invoices
    : invoices.filter(inv => inv.status === filterStatus);

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }).format(value);
  };

  const total = calculateTotal();

  return (
    <MainLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-900">請求書管理</h2>
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
          <label className="text-sm font-medium text-gray-700 mr-2">ステータス:</label>
          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="border border-gray-300 rounded-md shadow-sm py-1 px-3"
          >
            <option value="ALL">すべて</option>
            <option value="DRAFT">下書き</option>
            <option value="ISSUED">発行済み</option>
            <option value="PAID">支払済み</option>
            <option value="CANCELED">キャンセル</option>
          </select>
        </div>

        <div className="bg-white shadow rounded-lg overflow-hidden">
          {loading ? (
            <div className="p-6 text-center">読み込み中...</div>
          ) : (
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">請求書番号</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">日付</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">取引先</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ステータス</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">金額</th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">操作</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredInvoices.map((invoice) => {
                  const totalAmount = invoice.details?.reduce((sum, d) => sum + parseFloat(d.amount || 0), 0) || 0;
                  return (
                    <tr key={invoice.id}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {invoice.invoiceNumber}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {invoice.invoiceDate}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {invoice.partner?.name}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusColors[invoice.status]}`}>
                          {statusLabels[invoice.status]}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {formatCurrency(totalAmount)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        {invoice.status === 'DRAFT' && (
                          <button
                            onClick={() => handleStatusChange(invoice, 'ISSUED')}
                            className="text-blue-600 hover:text-blue-900 mr-2"
                          >
                            発行
                          </button>
                        )}
                        {invoice.status === 'ISSUED' && (
                          <button
                            onClick={() => handleStatusChange(invoice, 'PAID')}
                            className="text-green-600 hover:text-green-900 mr-2"
                          >
                            支払済み
                          </button>
                        )}
                        <button
                          onClick={() => handleEdit(invoice)}
                          className="text-indigo-600 hover:text-indigo-900 mr-2"
                        >
                          編集
                        </button>
                        <button
                          onClick={() => handleDelete(invoice)}
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
          <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20">
            <div className="fixed inset-0 bg-gray-500 bg-opacity-75"></div>
            <div className="inline-block bg-white rounded-lg shadow-xl transform transition-all sm:max-w-4xl sm:w-full">
              <form onSubmit={handleSubmit}>
                <div className="bg-white px-4 pt-5 pb-4 sm:p-6">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">
                    {editingInvoice ? '請求書編集' : '請求書新規作成'}
                  </h3>
                  <div className="space-y-4">
                    <div className="grid grid-cols-3 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">請求書番号</label>
                        <input
                          type="text"
                          required
                          value={formData.invoiceNumber}
                          onChange={(e) => setFormData({ ...formData, invoiceNumber: e.target.value })}
                          className="mt-1 block w-full border border-gray-300 rounded-md py-2 px-3"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">日付</label>
                        <input
                          type="date"
                          required
                          value={formData.invoiceDate}
                          onChange={(e) => setFormData({ ...formData, invoiceDate: e.target.value })}
                          className="mt-1 block w-full border border-gray-300 rounded-md py-2 px-3"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">取引先</label>
                        <select
                          required
                          value={formData.partnerId}
                          onChange={(e) => setFormData({ ...formData, partnerId: e.target.value })}
                          className="mt-1 block w-full border border-gray-300 rounded-md py-2 px-3"
                        >
                          <option value="">選択してください</option>
                          {partners.map(p => (
                            <option key={p.id} value={p.id}>{p.name}</option>
                          ))}
                        </select>
                      </div>
                    </div>

                    <div className="border-t pt-4">
                      <div className="flex justify-between items-center mb-2">
                        <h4 className="font-medium text-gray-900">明細行</h4>
                        <button
                          type="button"
                          onClick={handleAddDetail}
                          className="text-sm bg-green-600 text-white px-3 py-1 rounded-md"
                        >
                          行追加
                        </button>
                      </div>
                      <div className="space-y-2">
                        {formData.details.map((detail, index) => (
                          <div key={index} className="grid grid-cols-12 gap-2 items-center bg-gray-50 p-2 rounded">
                            <div className="col-span-5">
                              <select
                                required
                                value={detail.itemId}
                                onChange={(e) => handleDetailChange(index, 'itemId', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              >
                                <option value="">品目選択</option>
                                {items.map(item => (
                                  <option key={item.id} value={item.id}>{item.name}</option>
                                ))}
                              </select>
                            </div>
                            <div className="col-span-2">
                              <input
                                type="number"
                                step="0.01"
                                required
                                placeholder="数量"
                                value={detail.quantity}
                                onChange={(e) => handleDetailChange(index, 'quantity', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              />
                            </div>
                            <div className="col-span-2">
                              <input
                                type="number"
                                step="0.01"
                                required
                                placeholder="単価"
                                value={detail.unitPrice}
                                onChange={(e) => handleDetailChange(index, 'unitPrice', e.target.value)}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm"
                              />
                            </div>
                            <div className="col-span-2">
                              <input
                                type="number"
                                readOnly
                                value={detail.amount}
                                className="block w-full border border-gray-300 rounded-md py-1 px-2 text-sm bg-gray-100"
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
                      <div className="mt-4 flex justify-end bg-gray-100 p-3 rounded">
                        <span className="font-medium">合計: {formatCurrency(total)}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse">
                  <button
                    type="submit"
                    className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 sm:ml-3"
                  >
                    {editingInvoice ? '更新' : '作成'}
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="mt-3 bg-white text-gray-700 px-4 py-2 rounded-md border sm:mt-0"
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
