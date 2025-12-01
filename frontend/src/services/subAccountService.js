import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const subAccountService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/sub-accounts`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/sub-accounts/${id}`);
    return response.data.data;
  },

  async getByAccount(accountId) {
    const response = await api.get(`/companies/${COMPANY_ID}/sub-accounts/by-account/${accountId}`);
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/sub-accounts`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/sub-accounts/${id}`, data);
    return response.data.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${COMPANY_ID}/sub-accounts/${id}`);
    return response.data.data;
  },
};

export default subAccountService;
