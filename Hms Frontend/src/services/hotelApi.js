import api from './api';

const pageResponse = (response, rel) => {
  const body = response.data || {};
  const page = body.page || {};
  return {
    ...response,
    data: {
      data: {
        content: body._embedded?.[rel] || [],
        page: page.number || 0,
        size: page.size || 0,
        totalElements: page.totalElements || 0,
        totalPages: page.totalPages || 0,
      },
    },
  };
};

const itemResponse = (response) => ({
  ...response,
  data: {
    data: response.data,
  },
});

export const hotelService = {
  getAll: (page = 0, size = 12, search = '') => {
    const params = { page, size };
    const trimmedSearch = search.trim();
    if (trimmedSearch) {
      params.name = trimmedSearch;
      params.location = trimmedSearch;
      return api.get('/api/hotels/search/by-name-or-location', { params }).then((res) => pageResponse(res, 'hotels'));
    }
    return api.get('/api/hotels', { params }).then((res) => pageResponse(res, 'hotels'));
  },
  getById: (id) => api.get(`/api/hotels/${id}`).then(itemResponse),
  create: (data) => api.post('/api/hotels', data).then(itemResponse),
  update: (id, data) => api.put(`/api/hotels/${id}`, data).then(itemResponse),
  delete: (id) => api.delete(`/api/hotels/${id}`),
};

export const roomService = {
  getAll: (page = 0, size = 10) =>
    api.get('/api/rooms', { params: { page, size } }).then((res) => pageResponse(res, 'rooms')),
  getById: (id) => api.get(`/api/rooms/${id}`).then(itemResponse),
  getByHotel: (hotelId, page = 0, size = 100) =>
    api.get('/api/rooms/search/by-hotel', { params: { hotelId, page, size } }).then((res) => pageResponse(res, 'rooms')),
  getAvailableByType: (typeId, page = 0, size = 10) =>
    api.get('/api/rooms/search/available-by-room-type', { params: { roomTypeId: typeId, page, size } }).then((res) => pageResponse(res, 'rooms')),
  create: (data) => api.post('/api/room-management/rooms', data).then(itemResponse),
  update: (id, data) => api.put(`/api/room-management/rooms/${id}`, data).then(itemResponse),
  delete: (id) => api.delete(`/api/room-management/rooms/${id}`),
};

export const roomTypeService = {
  getAll: (page = 0, size = 50) =>
    api.get('/api/room-types', { params: { page, size } }).then((res) => pageResponse(res, 'roomTypes')),
  getById: (id) => api.get(`/api/room-types/${id}`).then(itemResponse),
  create: (data) => api.post('/api/room-types', data).then(itemResponse),
  update: (id, data) => api.put(`/api/room-types/${id}`, data).then(itemResponse),
  delete: (id) => api.delete(`/api/room-types/${id}`),
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
  getAll: (page = 0, size = 50) =>
    api.get('/api/amenities', { params: { page, size } }).then((res) => pageResponse(res, 'amenities')),
  getByHotel: (hotelId) =>
    api.get('/api/amenities/search/by-hotel', { params: { hotelId } }).then((res) => pageResponse(res, 'amenities')),
  getByRoom: (roomId) =>
    api.get('/api/amenities/search/by-room', { params: { roomId } }).then((res) => pageResponse(res, 'amenities')),
  create: (data) => api.post('/api/amenities', data).then(itemResponse),
  update: (id, data) => api.put(`/api/amenities/${id}`, data).then(itemResponse),
  delete: (id) => api.delete(`/api/amenities/${id}`),
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
