import api from './api';

export const notificationService = {
  getUserNotifications: async (userId) => {
    return await api.get(`/notifications/user/${userId}`);
  },

  getUnreadCount: async (userId) => {
    return await api.get(`/notifications/user/${userId}/unread-count`);
  },

  markAsRead: async (id, userId) => {
    return await api.put(`/notifications/${id}/read`, null, { params: { userId } });
  },

  markAllAsRead: async (userId) => {
    return await api.put(`/notifications/user/${userId}/read-all`);
  },
};
