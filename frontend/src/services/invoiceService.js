import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const invoiceService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/invoices`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/invoices/${id}`);
    return response.data.data;
  },

  async getByStatus(status) {
    const response = await api.get(`/companies/${COMPANY_ID}/invoices/by-status/${status}`);
    return response.data.data;
  },

  async getByPartner(partnerId) {
    const response = await api.get(`/companies/${COMPANY_ID}/invoices/by-partner/${partnerId}`);
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/invoices`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/invoices/${id}`, data);
    return response.data.data;
  },

  async updateStatus(id, status) {
    const response = await api.put(`/companies/${COMPANY_ID}/invoices/${id}/status`, null, {
      params: { status }
    });
    return response.data.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${COMPANY_ID}/invoices/${id}`);
    return response.data.data;
  },
};

export default invoiceService;
