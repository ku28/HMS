import { Outlet, Link, useNavigate } from 'react-router-dom';
import useAuthStore from '../store/authStore';

export default function PublicLayout() {
  const { isAuthenticated, user, logout, isAdmin } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-gray-50 font-[Inter,system-ui,sans-serif]">
      <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <Link to="/" className="text-xl font-bold tracking-tight text-gray-900">
              HMS<span className="text-blue-600">.</span>
            </Link>
            <div className="hidden md:flex items-center gap-8">
              <Link to="/hotels" className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors">Hotels</Link>
              {isAuthenticated && (
                <Link to="/reservations" className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors">My Reservations</Link>
              )}
              {isAuthenticated && isAdmin() && (
                <Link to="/admin" className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors">Dashboard</Link>
              )}
            </div>
            <div className="flex items-center gap-4">
              {isAuthenticated ? (
                <div className="flex items-center gap-3">
                  <span className="text-sm text-gray-600">{user?.fullName}</span>
                  <button
                    onClick={handleLogout}
                    className="text-sm font-medium text-gray-600 hover:text-gray-900 cursor-pointer transition-colors"
                  >
                    Logout
                  </button>
                </div>
              ) : (
                <div className="flex items-center gap-3">
                  <Link to="/login" className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors">Login</Link>
                  <Link
                    to="/signup"
                    className="text-sm font-medium bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                  >
                    Sign Up
                  </Link>
                </div>
              )}
            </div>
          </div>
        </div>
      </nav>
      <main>
        <Outlet />
      </main>
      <footer className="bg-white border-t border-gray-200 py-8 mt-16">
        <div className="max-w-7xl mx-auto px-4 text-center text-sm text-gray-500">
          © 2024 Hotel Management System. All rights reserved.
        </div>
      </footer>
    </div>
  );
}
