import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const partnerService = {
  async getAll() {
    const response = await api.get(`/companies/${COMPANY_ID}/partners`);
    return response.data.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${COMPANY_ID}/partners/${id}`);
    return response.data.data;
  },

  async getByType(partnerType) {
    const response = await api.get(`/companies/${COMPANY_ID}/partners/by-type/${partnerType}`);
    return response.data.data;
  },

  async create(data) {
    const response = await api.post(`/companies/${COMPANY_ID}/partners`, data);
    return response.data.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${COMPANY_ID}/partners/${id}`, data);
    return response.data.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${COMPANY_ID}/partners/${id}`);
    return response.data.data;
  },
};

export default partnerService;
