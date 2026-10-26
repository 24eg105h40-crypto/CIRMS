import api from './api';

export const claimService = {
  getMyClaims: async () => {
    return await api.get('/claims/my');
  },

  getAllClaims: async () => {
    return await api.get('/claims');
  },

  getClaimById: async (id) => {
    return await api.get(`/claims/${id}`);
  },

  submitClaim: async (claimData) => {
    return await api.post('/claims', claimData);
  },

  approveClaim: async (id, comment) => {
    return await api.put(`/admin/claims/${id}/approve`, { adminComment: comment });
  },

  rejectClaim: async (id, comment) => {
    return await api.put(`/admin/claims/${id}/reject`, { adminComment: comment });
  },
};
