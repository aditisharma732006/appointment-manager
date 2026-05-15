import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate, Outlet } from 'react-router-dom';
import { LogOut, Calendar } from 'lucide-react';
import { Button } from '../ui/Button';

export function Layout() {
  const { role, logout } = useAuth();
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
            BookIt<span className="text-[#6366f1]">.</span>
          </span>
        </div>
        
        <div className="flex items-center space-x-4">
          <span className="text-sm font-medium text-gray-500 uppercase tracking-wider bg-gray-200 px-2.5 py-1 rounded-md">
            {role}
          </span>
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
