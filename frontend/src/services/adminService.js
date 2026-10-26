import api from './api';

export const adminService = {
  getStats: async () => {
    return await api.get('/admin/stats');
  },

  getAllUsers: async () => {
    return await api.get('/admin/users');
  },

  getAllItems: async () => {
    return await api.get('/admin/items');
  },

  getPendingClaims: async () => {
    return await api.get('/admin/claims/pending');
  },

  getAllClaims: async () => {
    return await api.get('/admin/claims');
  },

  approveClaim: async (id, adminComment) => {
    return await api.put(`/admin/claims/${id}/approve`, { adminComment });
  },

  rejectClaim: async (id, adminComment) => {
    return await api.put(`/admin/claims/${id}/reject`, { adminComment });
  },
};
