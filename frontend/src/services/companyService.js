import api from '../utils/api';

const companyService = {
  async getAll() {
    const response = await api.get('/companies');
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/companies/${id}`);
    return response.data;
  },

  async create(data) {
    const response = await api.post('/companies', data);
    return response.data;
  },

  async update(id, data) {
    const response = await api.put(`/companies/${id}`, data);
    return response.data;
  },

  async delete(id) {
    const response = await api.delete(`/companies/${id}`);
    return response.data;
  },
};

export default companyService;
