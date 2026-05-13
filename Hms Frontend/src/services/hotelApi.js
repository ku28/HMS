import api from './api';

export const hotelService = {
  getAll: (page = 0, size = 12, search = '') =>
    api.get(`/api/hotels/all?page=${page}&size=${size}&search=${search}`),
  getById: (id) => api.get(`/api/hotels/${id}`),
  create: (data) => api.post('/api/hotels/post', data),
  update: (id, data) => api.put(`/api/hotels/update/${id}`, data),
  delete: (id) => api.delete(`/api/hotels/delete/${id}`),
};

export const roomService = {
  getAll: (page = 0, size = 10) => api.get(`/api/room/all?page=${page}&size=${size}`),
  getById: (id) => api.get(`/api/room/${id}`),
  getByHotel: (hotelId, page = 0, size = 100) => api.get(`/api/room/hotel/${hotelId}?page=${page}&size=${size}`),
  getAvailableByType: (typeId, page = 0, size = 10) =>
    api.get(`/api/rooms/available/${typeId}?page=${page}&size=${size}`),
  create: (data) => api.post('/api/rooms/post', data),
  update: (id, data) => api.put(`/api/room/update/${id}`, data),
  delete: (id) => api.delete(`/api/room/delete/${id}`),
};

export const roomTypeService = {
  getAll: (page = 0, size = 50) => api.get(`/api/RoomType/all?page=${page}&size=${size}`),
  getById: (id) => api.get(`/api/RoomType/${id}`),
  create: (data) => api.post('/api/RoomType/post', data),
  update: (id, data) => api.put(`/api/RoomType/update/${id}`, data),
  delete: (id) => api.delete(`/api/RoomType/delete/${id}`),
};

export const reservationService = {
  getAll: (page = 0, size = 10) => api.get(`/api/reservation/all?page=${page}&size=${size}`),
  getMyReservations: (email, page = 0, size = 10) =>
    api.get(`/api/reservation/my?email=${encodeURIComponent(email)}&page=${page}&size=${size}`),
  getById: (id) => api.get(`/api/reservation/${id}`),
  create: (data) => api.post('/api/reservation/post', data),
  update: (id, data) => api.put(`/api/reservation/update/${id}`, data),
  delete: (id) => api.delete(`/api/reservation/${id}`),
};

export const paymentService = {
  getAll: (page = 0, size = 10) => api.get(`/api/payment/all?page=${page}&size=${size}`),
  create: (data) => api.post('/api/payment/post', data),
  getTotalRevenue: () => api.get('/api/payments/total-revenue'),
};

export const reviewService = {
  getAll: (page = 0, size = 10) => api.get(`/api/review/all?page=${page}&size=${size}`),
  getByRating: (rating, page = 0, size = 10) =>
    api.get(`/api/reviews/rating/${rating}?page=${page}&size=${size}`),
  getRecent: () => api.get('/api/reviews/recent'),
  create: (data) => api.post('/api/review/post', data),
  delete: (id) => api.delete(`/api/review/delete/${id}`),
};

export const amenityService = {
  getAll: (page = 0, size = 50) => api.get(`/api/amenity/all?page=${page}&size=${size}`),
  getByHotel: (hotelId) => api.get(`/api/amenity/hotel/${hotelId}`),
  getByRoom: (roomId) => api.get(`/api/amenity/room/${roomId}`),
  create: (data) => api.post('/api/amenity/post', data),
  update: (id, data) => api.put(`/api/amenity/update/${id}`, data),
  delete: (id) => api.delete(`/api/amenity/${id}`),
};

export const hotelAmenityService = {
  addToHotel: (data) => api.post('/api/hotelamenity/post', data),
};

export const roomAmenityService = {
  addToRoom: (data) => api.post('/api/roomAmenity/post', data),
};

export const authService = {
  login: (data) => api.post('/api/auth/login', data),
  register: (data) => api.post('/api/auth/register', data),
};

export const adminService = {
  getDashboard: () => api.get('/api/admin/dashboard'),
};
