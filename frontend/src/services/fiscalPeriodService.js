import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const fiscalPeriodService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/fiscal-periods`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/fiscal-periods/${id}`);
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/fiscal-periods`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/fiscal-periods/${id}`, data);
    return response.data.data;
  },

  async close(id) {
    const response = await api.put(`/companies/${COMPANY_ID}/fiscal-periods/${id}/close`);
    return response.data.data;
  },

  async reopen(id) {
    const response = await api.put(`/companies/${COMPANY_ID}/fiscal-periods/${id}/reopen`);
    return response.data.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${COMPANY_ID}/fiscal-periods/${id}`);
    return response.data.data;
  },
};

export default fiscalPeriodService;
