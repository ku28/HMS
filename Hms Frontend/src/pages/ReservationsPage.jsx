import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { reservationService } from '../services/hotelApi';
import useAuthStore from '../store/authStore';

export default function ReservationsPage() {
  const { isAuthenticated, user } = useAuthStore();
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    if (!isAuthenticated) { navigate('/login'); return; }
    const email = user?.email;
    if (!email) return;
    reservationService.getMyReservations(email, page, 10).then((res) => {
      setReservations(res.data?.data?.content || []);
      setTotalPages(res.data?.data?.totalPages || 0);
    }).catch(console.error).finally(() => setLoading(false));
  }, [page, isAuthenticated]);

  if (loading) return <div className="flex items-center justify-center min-h-[60vh]"><div className="w-8 h-8 border-2 border-gray-600 border-t-blue-500 rounded-full animate-spin" /></div>;

  return (
    <div className="max-w-5xl mx-auto px-4 py-12">
      <h1 className="text-2xl font-bold text-gray-100 mb-2">My Reservations</h1>
      <p className="text-gray-500 mb-8">View your bookings</p>
      {reservations.length === 0 ? (
        <div className="text-center py-16 bg-gray-900 rounded-2xl border border-gray-800">
          <svg className="w-16 h-16 mx-auto text-gray-700 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" /></svg>
          <p className="text-gray-500">No reservations found</p>
          <p className="text-sm text-gray-600 mt-1">Book a room to see your reservations here</p>
        </div>
      ) : (
        <div className="space-y-4">
          {reservations.map((r) => (
            <div key={r.reservationId} className="bg-gray-900 rounded-xl border border-gray-800 p-6 hover:border-gray-700 transition-all">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                  <h3 className="font-semibold text-gray-100">Reservation #{r.reservationId}</h3>
                  <p className="text-sm text-gray-400 mt-1">Room {r.roomNumber} — {r.roomTypeName}</p>
                  <p className="text-sm text-gray-500 mt-1">{r.guestName} · {r.guestEmail}</p>
                </div>
                <div className="text-right">
                  <div className="flex items-center gap-2 text-sm">
                    <span className="text-gray-400">{r.checkInDate}</span>
                    <span className="text-gray-600">→</span>
                    <span className="text-gray-400">{r.checkOutDate}</span>
                  </div>
                  <span className="inline-block mt-2 px-3 py-1 bg-green-500/15 text-green-400 text-xs rounded-full font-medium">Confirmed</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
      {totalPages > 1 && (
        <div className="mt-8 flex justify-center gap-2">
          <button onClick={() => setPage(Math.max(0, page - 1))} disabled={page === 0} className="px-4 py-2 text-sm bg-gray-800 border border-gray-700 text-gray-300 rounded-lg disabled:opacity-50 cursor-pointer">Previous</button>
          <span className="px-4 py-2 text-sm text-gray-500">Page {page + 1} of {totalPages}</span>
          <button onClick={() => setPage(Math.min(totalPages - 1, page + 1))} disabled={page >= totalPages - 1} className="px-4 py-2 text-sm bg-gray-800 border border-gray-700 text-gray-300 rounded-lg disabled:opacity-50 cursor-pointer">Next</button>
        </div>
      )}
    </div>
  );
}
