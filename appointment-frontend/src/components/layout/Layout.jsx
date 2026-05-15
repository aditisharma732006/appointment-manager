import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate, Outlet } from 'react-router-dom';
import { LogOut, Calendar, User } from 'lucide-react';
import { Button } from '../ui/Button';

export function Layout() {
  const { role, name, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen flex flex-col bg-white">
      <header className="border-b border-gray-100 bg-[#f9fafb] px-6 py-4 flex items-center justify-between sticky top-0 z-10 shadow-sm">
        <div className="flex items-center space-x-2 text-[#6366f1]">
          <Calendar className="h-6 w-6" />
          <span className="text-xl font-bold tracking-tight text-gray-900">
            Appointment Booking
          </span>
        </div>

        <div className="flex items-center space-x-3">
          <div className="inline-flex items-center gap-2 text-sm text-gray-700 bg-gray-100 px-3 py-2 rounded-full">
            <User className="h-4 w-4 text-[#6366f1]" />
            <span>{name || role || 'Guest'}</span>
          </div>
          <Button variant="ghost" size="sm" onClick={handleLogout} className="text-gray-600 hover:text-red-600 hover:bg-red-50">
            <LogOut className="h-4 w-4 mr-2" />
            Logout
          </Button>
        </div>
      </header>

      <main className="flex-1 p-6 md:p-8 max-w-7xl mx-auto w-full">
        <Outlet />
      </main>
    </div>
  );
}
