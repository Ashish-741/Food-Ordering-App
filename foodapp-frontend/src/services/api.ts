import axios, { AxiosInstance } from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: (email: string, password: string) =>
    api.post('/auth/login', { email, password }),
  
  register: (email: string, name: string, password: string, phone: string) =>
    api.post('/auth/register', { email, name, password, phone }),
  
  refreshToken: (refreshToken: string) =>
    api.post('/auth/refresh-token', { refreshToken }),
};

export const restaurantService = {
  getAll: () => api.get('/restaurants'),
  getById: (id: string) => api.get(`/restaurants/${id}`),
  getMenuItems: (restaurantId: string) =>
    api.get(`/restaurants/${restaurantId}/menu`),
};

export const cartService = {
  getCart: () => api.get('/cart'),
  addItem: (itemId: string, quantity: number) =>
    api.post('/cart/items', { itemId, quantity }),
  removeItem: (itemId: string) =>
    api.delete(`/cart/items/${itemId}`),
  clearCart: () => api.delete('/cart'),
};

export const orderService = {
  createOrder: (cartId: string, deliveryAddress: string) =>
    api.post('/orders', { cartId, deliveryAddress }),
  
  getOrders: () => api.get('/orders'),
  getOrderById: (id: string) => api.get(`/orders/${id}`),
};

export default api;
