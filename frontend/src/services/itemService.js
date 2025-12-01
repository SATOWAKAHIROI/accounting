import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const itemService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/items`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/items/${id}`);
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/items`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/items/${id}`, data);
    return response.data.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${COMPANY_ID}/items/${id}`);
    return response.data.data;
  },
};

export default itemService;
