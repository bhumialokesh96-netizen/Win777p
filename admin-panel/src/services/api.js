import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const getAuthHeader = () => {
  const token = localStorage.getItem('adminToken');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const api = {
  // Auth
  login: (username, password) => 
    axios.post(`${API_URL}/admin/login`, { username, password }),

  // Users
  getUsers: (page = 0, size = 10) => 
    axios.get(`${API_URL}/admin/users?page=${page}&size=${size}`, { 
      headers: getAuthHeader() 
    }),

  searchUsers: (mobile) => 
    axios.get(`${API_URL}/admin/users/search?mobile=${mobile}`, { 
      headers: getAuthHeader() 
    }),

  banUser: (userId, reason) => 
    axios.post(`${API_URL}/admin/users/${userId}/ban`, { reason }, { 
      headers: getAuthHeader() 
    }),

  unbanUser: (userId) => 
    axios.post(`${API_URL}/admin/users/${userId}/unban`, {}, { 
      headers: getAuthHeader() 
    }),

  adjustBalance: (userId, amount, reason) => 
    axios.post(`${API_URL}/admin/users/${userId}/adjust-balance`, 
      { amount, reason }, 
      { headers: getAuthHeader() }
    ),

  // Tasks
  getTasks: () => 
    axios.get(`${API_URL}/tasks`, { headers: getAuthHeader() }),

  saveTask: (task) => 
    axios.post(`${API_URL}/admin/tasks`, task, { headers: getAuthHeader() }),

  deleteTask: (taskId) => 
    axios.delete(`${API_URL}/admin/tasks/${taskId}`, { headers: getAuthHeader() }),

  // Withdrawals
  getPendingWithdrawals: () => 
    axios.get(`${API_URL}/admin/withdrawals/pending`, { headers: getAuthHeader() }),

  approveWithdrawal: (withdrawalId) => 
    axios.post(`${API_URL}/admin/withdrawals/${withdrawalId}/approve`, {}, { 
      headers: getAuthHeader() 
    }),

  rejectWithdrawal: (withdrawalId, reason) => 
    axios.post(`${API_URL}/admin/withdrawals/${withdrawalId}/reject`, 
      { reason }, 
      { headers: getAuthHeader() }
    ),

  // Configuration
  getConfigs: () => 
    axios.get(`${API_URL}/config`, { headers: getAuthHeader() }),

  setConfig: (configKey, configValue, configType, description) => 
    axios.post(`${API_URL}/config`, 
      { configKey, configValue, configType, description }, 
      { headers: getAuthHeader() }
    ),

  setMaintenanceMode: (enabled) => 
    axios.post(`${API_URL}/config/maintenance-mode`, enabled, { 
      headers: { ...getAuthHeader(), 'Content-Type': 'application/json' }
    }),

  setThemeColor: (color) => 
    axios.post(`${API_URL}/config/theme-color`, color, { 
      headers: { ...getAuthHeader(), 'Content-Type': 'application/json' }
    }),

  getBanners: () => 
    axios.get(`${API_URL}/config/banners`),

  saveBanner: (banner) => 
    axios.post(`${API_URL}/config/banners`, banner, { headers: getAuthHeader() }),

  deleteBanner: (bannerId) => 
    axios.delete(`${API_URL}/config/banners/${bannerId}`, { headers: getAuthHeader() }),
};
