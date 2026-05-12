import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { roomService, reservationService, paymentService } from '../services/hotelApi';
import useAuthStore from '../store/authStore';

export default function BookingPage() {
  const { roomId } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuthStore();
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({
    guestName: '',
    guestEmail: '',
    guestPhone: '',
    checkInDate: '',
    checkOutDate: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated) { navigate('/login'); return; }
    roomService.getById(roomId).then((res) => {
      setRoom(res.data?.data);
      setForm(f => ({ ...f, guestName: user?.fullName || '', guestEmail: user?.email || '' }));
    }).catch(() => setError('Room not found')).finally(() => setLoading(false));
  }, [roomId, isAuthenticated]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const res = await reservationService.create({ ...form, roomId: parseInt(roomId) });
      const reservation = res.data?.data;
      if (reservation && room?.roomType) {
        const nights = Math.ceil((new Date(form.checkOutDate) - new Date(form.checkInDate)) / (1000 * 60 * 60 * 24));
        await paymentService.create({
          reservationId: reservation.reservationId,
          amount: room.roomType.pricePerNight * nights,
          paymentDate: new Date().toISOString().split('T')[0],
          paymentStatus: 'Paid',
        });
      }
      navigate('/reservations');
    } catch (err) {
      setError(err.response?.data?.message || 'Booking failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[60vh]"><div className="w-8 h-8 border-2 border-gray-300 border-t-blue-600 rounded-full animate-spin" /></div>;
  }

  return (
    <div className="max-w-2xl mx-auto px-4 py-12">
      <h1 className="text-2xl font-bold text-gray-900 mb-2">Book Your Stay</h1>
      {room && (
        <p className="text-gray-500 mb-8">Room {room.roomNumber} — {room.roomType?.typeName} — ${room.roomType?.pricePerNight}/night</p>
      )}

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">{error}</div>
      )}

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1.5">Full Name</label>
          <input type="text" required value={form.guestName} onChange={e => setForm({...form, guestName: e.target.value})}
            className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1.5">Email</label>
          <input type="email" required value={form.guestEmail} onChange={e => setForm({...form, guestEmail: e.target.value})}
            className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1.5">Phone</label>
          <input type="tel" required value={form.guestPhone} onChange={e => setForm({...form, guestPhone: e.target.value})}
            className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Check-in</label>
            <input type="date" required value={form.checkInDate} onChange={e => setForm({...form, checkInDate: e.target.value})}
              className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Check-out</label>
            <input type="date" required value={form.checkOutDate} onChange={e => setForm({...form, checkOutDate: e.target.value})}
              className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
        </div>
        <button type="submit" disabled={submitting}
          className="w-full py-3.5 bg-gray-900 text-white font-semibold rounded-xl hover:bg-gray-800 disabled:opacity-50 cursor-pointer transition-colors">
          {submitting ? 'Processing...' : 'Confirm Booking'}
        </button>
      </form>
    </div>
  );
}
