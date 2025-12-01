import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const journalService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/journals`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/journals/${id}`);
    return response.data.data;
  },

  async getByDateRange(startDate, endDate) {
    const response = await api.get(`/companies/${COMPANY_ID}/journals`, {
      params: { startDate, endDate }
    });
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/journals`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/journals/${id}`, data);
    return response.data.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${COMPANY_ID}/journals/${id}`);
    return response.data.data;
  },
};

export default journalService;
