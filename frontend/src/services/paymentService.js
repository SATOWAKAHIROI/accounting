import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const paymentService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/payments`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/payments/${id}`);
    return response.data.data;
  },

  async getByInvoice(invoiceId) {
    const response = await api.get(`/companies/${COMPANY_ID}/payments/by-invoice/${invoiceId}`);
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/payments`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/payments/${id}`, data);
    return response.data.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${COMPANY_ID}/payments/${id}`);
    return response.data.data;
  },
};

export default paymentService;
