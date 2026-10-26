import api from './api';

export const matchingService = {
  /**
   * Get matches for a specific item
   * @param {number} itemId
   * @param {number} maxResults
   */
  getMatchesForItem: async (itemId, maxResults = 10) => {
    return await api.get(`/matches/item/${itemId}`, { params: { maxResults } });
  },

  /**
   * Get aggregated matches across all the current user's active reports
   */
  getMyMatches: async () => {
    return await api.get('/matches/my');
  },
};
