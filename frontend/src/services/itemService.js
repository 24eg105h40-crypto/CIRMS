import api from './api';

export const itemService = {
  getAllItems: async () => {
    return await api.get('/items');
  },

  getItemById: async (id) => {
    return await api.get(`/items/${id}`);
  },

  searchItems: async (params) => {
    return await api.get('/items/search', { params });
  },

  getMyReports: async () => {
    return await api.get('/items/my');
  },

  getMyStats: async () => {
    return await api.get('/items/my-stats');
  },

  createLostItem: async (data) => {
    return await api.post('/items/lost', data);
  },

  createFoundItem: async (data) => {
    return await api.post('/items/found', data);
  },

  updateItem: async (id, data) => {
    return await api.put(`/items/${id}`, data);
  },

  deleteItem: async (id) => {
    return await api.delete(`/items/${id}`);
  },

  getCategories: async () => {
    return await api.get('/categories');
  },
};
