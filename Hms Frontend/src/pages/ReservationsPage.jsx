import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { reservationService } from '../services/hotelApi';
import useAuthStore from '../store/authStore';

export default function ReservationsPage() {
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    if (!isAuthenticated) { navigate('/login'); return; }
    reservationService.getAll(page, 10).then((res) => {
      setReservations(res.data?.data?.content || []);
      setTotalPages(res.data?.data?.totalPages || 0);
    }).catch(console.error).finally(() => setLoading(false));
  }, [page, isAuthenticated]);

  if (loading) {
    return <div className="flex items-center justify-center min-h-[60vh]"><div className="w-8 h-8 border-2 border-gray-300 border-t-blue-600 rounded-full animate-spin" /></div>;
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-12">
      <h1 className="text-2xl font-bold text-gray-900 mb-2">My Reservations</h1>
      <p className="text-gray-500 mb-8">View and manage your bookings</p>

      {reservations.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-gray-100">
          <p className="text-gray-500">No reservations found</p>
        </div>
      ) : (
        <div className="space-y-4">
          {reservations.map((r) => (
            <div key={r.reservationId} className="bg-white rounded-xl border border-gray-100 p-6 hover:border-gray-200 transition-all">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                  <h3 className="font-semibold text-gray-900">Reservation #{r.reservationId}</h3>
                  <p className="text-sm text-gray-500 mt-1">Room {r.roomNumber} — {r.roomTypeName}</p>
                  <p className="text-sm text-gray-400 mt-1">{r.guestName} · {r.guestEmail}</p>
                </div>
                <div className="text-right">
                  <div className="flex items-center gap-2 text-sm">
                    <span className="text-gray-500">{r.checkInDate}</span>
                    <span className="text-gray-300">→</span>
                    <span className="text-gray-500">{r.checkOutDate}</span>
                  </div>
                  <span className="inline-block mt-2 px-3 py-1 bg-green-50 text-green-700 text-xs rounded-full font-medium">
                    Confirmed
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="mt-8 flex justify-center gap-2">
          <button onClick={() => setPage(Math.max(0, page - 1))} disabled={page === 0}
            className="px-4 py-2 text-sm bg-white border border-gray-200 rounded-lg hover:bg-gray-50 disabled:opacity-50 cursor-pointer transition-colors">
            Previous
          </button>
          <span className="px-4 py-2 text-sm text-gray-500">Page {page + 1} of {totalPages}</span>
          <button onClick={() => setPage(Math.min(totalPages - 1, page + 1))} disabled={page >= totalPages - 1}
            className="px-4 py-2 text-sm bg-white border border-gray-200 rounded-lg hover:bg-gray-50 disabled:opacity-50 cursor-pointer transition-colors">
            Next
          </button>
        </div>
      )}
    </div>
  );
}
