import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const accountService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/accounts`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/accounts/${id}`);
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/accounts`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/accounts/${id}`, data);
    return response.data.data;
  },

  async delete(id) {
    await api.delete(`/companies/${COMPANY_ID}/accounts/${id}`);
  },
};

export default accountService;
