import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Button } from '../components/ui/Button';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Users, Stethoscope, CalendarDays, CalendarCheck, MapPin, Trash2 } from 'lucide-react';

export function AdminDashboard() {
  const [stats, setStats] = useState({
    totalProviders: 0,
    totalUsers: 0,
    totalAppointments: 0,
    todaysBookings: 0
  });
  const [providers, setProviders] = useState([]);

  useEffect(() => {
    fetchStats();
    fetchProviders();
  }, []);

  const fetchStats = async () => {
    try {
      const response = await api.get('/admin/stats');
      setStats(response.data);
    } catch (err) {
      console.error('Failed to fetch stats', err);
    }
  };

  const fetchProviders = async () => {
    try {
      const response = await api.get('/admin/providers');
      setProviders(response.data);
    } catch (err) {
      console.error('Failed to fetch providers', err);
    }
  };

  const handleRemoveProvider = async (id) => {
    if (!window.confirm('Are you sure you want to remove this provider? All their data and appointments will be lost.')) return;
    try {
      await api.delete(`/admin/providers/${id}`);
      fetchProviders();
      fetchStats();
    } catch (err) {
      alert('Failed to remove provider');
    }
  };

  const statCards = [
    { title: 'Total Providers', value: stats.totalProviders, icon: Stethoscope, color: 'text-blue-600', bg: 'bg-blue-100' },
    { title: 'Total Users', value: stats.totalUsers, icon: Users, color: 'text-indigo-600', bg: 'bg-indigo-100' },
    { title: 'Total Appointments', value: stats.totalAppointments, icon: CalendarDays, color: 'text-purple-600', bg: 'bg-purple-100' },
    { title: "Today's Bookings", value: stats.todaysBookings, icon: CalendarCheck, color: 'text-green-600', bg: 'bg-green-100' }
  ];

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-gray-900">Platform Admin</h1>
          <p className="text-gray-500">Overview of platform metrics and provider management.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((stat, idx) => {
          const Icon = stat.icon;
          return (
            <Card key={idx} className="bg-white border-gray-200">
              <CardContent className="p-6 flex items-center space-x-4">
                <div className={`p-4 rounded-full ${stat.bg}`}>
                  <Icon className={`w-8 h-8 ${stat.color}`} />
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-500">{stat.title}</p>
                  <h3 className="text-3xl font-bold text-gray-900 mt-1">{stat.value}</h3>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="pt-6 border-t border-gray-200">
        <h2 className="text-xl font-bold text-gray-900 mb-6">Manage Providers</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {providers.map(provider => (
            <Card key={provider.id} className="flex flex-col border-gray-200 bg-white">
              <CardHeader className="pb-4 border-b border-gray-50">
                <div className="flex items-center justify-between">
                  <Badge variant="default" className="bg-gray-100 text-gray-700">
                    {provider.category.replace('_', ' ')}
                  </Badge>
                </div>
                <CardTitle className="mt-4 text-lg">{provider.name}</CardTitle>
                <div className="text-sm text-gray-500">{provider.email}</div>
              </CardHeader>
              <CardContent className="flex-1 space-y-3 pt-4">
                <div className="flex items-center text-sm text-gray-600">
                  <MapPin className="w-4 h-4 mr-2 text-gray-400" />
                  {provider.location}
                </div>
                <p className="text-sm text-gray-600 line-clamp-2">{provider.description}</p>
              </CardContent>
              <CardFooter className="pt-4 bg-gray-50 border-t border-gray-100 rounded-b-xl">
                <Button variant="danger" className="w-full text-red-600 bg-red-50 hover:bg-red-100 hover:text-red-700 border border-red-200" onClick={() => handleRemoveProvider(provider.id)}>
                  <Trash2 className="w-4 h-4 mr-2" />
                  Remove Provider
                </Button>
              </CardFooter>
            </Card>
          ))}
          {providers.length === 0 && (
            <div className="col-span-full py-12 text-center text-gray-500 bg-gray-50 rounded-xl border border-dashed border-gray-200">
              No providers registered on the platform.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
