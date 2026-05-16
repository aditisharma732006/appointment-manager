import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Button } from '../components/ui/Button';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { format } from 'date-fns';
import { Calendar, MapPin, User, Stethoscope, Briefcase, BookOpen, UserCheck, XCircle, CheckCircle2 } from 'lucide-react';

const CATEGORIES = ['MEDICAL', 'PERSONAL_CARE', 'EDUCATION', 'CONSULTING'];

const CategoryIcon = ({ category, className }) => {
  switch (category) {
    case 'MEDICAL': return <Stethoscope className={className} />;
    case 'PERSONAL_CARE': return <UserCheck className={className} />;
    case 'EDUCATION': return <BookOpen className={className} />;
    case 'CONSULTING': return <Briefcase className={className} />;
    default: return <User className={className} />;
  }
};

export function UserDashboard() {
  const [activeTab, setActiveTab] = useState('browse');
  const [category, setCategory] = useState(CATEGORIES[0]);
  const [providers, setProviders] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [selectedProvider, setSelectedProvider] = useState(null);
  const [slots, setSlots] = useState([]);

  useEffect(() => {
    if (activeTab === 'browse') {
      fetchProviders();
    } else if (activeTab === 'appointments') {
      fetchAppointments();
    }
  }, [activeTab, category]);

  const fetchProviders = async () => {
    try {
      const response = await api.get(`/providers?category=${category}`);
      setProviders(response.data);
    } catch (err) {
      console.error('Failed to fetch providers', err);
    }
  };

  const fetchAppointments = async () => {
    try {
      const response = await api.get('/appointments');
      setAppointments(response.data);
    } catch (err) {
      console.error('Failed to fetch appointments', err);
    }
  };

  const handleViewSlots = async (provider) => {
    setSelectedProvider(provider);
    try {
      const response = await api.get(`/providers/${provider.id}/slots`);
      setSlots(response.data);
    } catch (err) {
      console.error('Failed to fetch slots', err);
    }
  };

  const handleBookSlot = async (slotId) => {
    try {
      await api.post(`/appointments/book/${slotId}`);
      alert('Appointment booked successfully!');
      setSelectedProvider(null);
      setActiveTab('appointments');
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to book slot');
    }
  };

  const handleCancelAppointment = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) return;
    try {
      await api.delete(`/appointments/${id}`);
      fetchAppointments();
    } catch (err) {
      alert('Failed to cancel appointment');
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-gray-900">Appointment Booking</h1>
          <p className="text-gray-500">Manage your bookings and discover services.</p>
        </div>
        <div className="flex bg-gray-100 p-1 rounded-xl w-fit">
          <button
            onClick={() => { setActiveTab('browse'); setSelectedProvider(null); }}
            className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors ${activeTab === 'browse' ? 'bg-white text-[#6366f1] shadow-sm' : 'text-gray-600 hover:text-gray-900'}`}
          >
            Browse Providers
          </button>
          <button
            onClick={() => setActiveTab('appointments')}
            className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors ${activeTab === 'appointments' ? 'bg-white text-[#6366f1] shadow-sm' : 'text-gray-600 hover:text-gray-900'}`}
          >
            My Appointments
          </button>
        </div>
      </div>

      {activeTab === 'browse' && !selectedProvider && (
        <div className="space-y-6">
          <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
            {CATEGORIES.map(cat => (
              <Button
                key={cat}
                variant={category === cat ? 'primary' : 'outline'}
                onClick={() => setCategory(cat)}
                className={`whitespace-nowrap ${category !== cat ? 'border-gray-200 text-gray-700 bg-white hover:bg-gray-50' : ''}`}
              >
                {cat.replace('_', ' ')}
              </Button>
            ))}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {providers.map(provider => (
              <Card key={provider.id} className="flex flex-col hover:border-[#6366f1] transition-colors border-gray-200">
                <CardHeader className="pb-4">
                  <div className="flex items-center justify-between">
                    <Badge variant="primary" className="bg-[#e0e7ff] text-[#6366f1]">
                      {provider.category?.replace('_', ' ')}
                    </Badge>
                  </div>
                  <CardTitle className="mt-4 text-xl">{provider.name}</CardTitle>
                </CardHeader>
                <CardContent className="flex-1 space-y-3">
                  <div className="flex items-center text-sm text-gray-500">
                    <MapPin className="w-4 h-4 mr-2 text-gray-400" />
                    {provider.location}
                  </div>
                  <p className="text-sm text-gray-600 line-clamp-3">{provider.description}</p>
                </CardContent>
                <CardFooter className="pt-4 border-t border-gray-100">
                  <Button className="w-full" onClick={() => handleViewSlots(provider)}>
                    View Slots
                  </Button>
                </CardFooter>
              </Card>
            ))}
            {providers.length === 0 && (
              <div className="col-span-full py-12 text-center text-gray-500 bg-gray-50 rounded-xl border border-dashed border-gray-200">
                No providers found in this category.
              </div>
            )}
          </div>
        </div>
      )}

      {activeTab === 'browse' && selectedProvider && (() => {
        const availableSlots = slots.filter(s => s.status === 'AVAILABLE');
        const groupedSlots = availableSlots.reduce((acc, slot) => {
          const dateStr = format(new Date(slot.dateTime), 'MMMM d, yyyy');
          if (!acc[dateStr]) acc[dateStr] = [];
          acc[dateStr].push(slot);
          return acc;
        }, {});
        const sortedDates = Object.keys(groupedSlots).sort((a, b) => new Date(a) - new Date(b));

        return (
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <button 
                  onClick={() => setSelectedProvider(null)}
                  className="text-sm font-medium text-[#6366f1] hover:underline mb-2 block"
                >
                  &larr; Back to Providers
                </button>
                <h2 className="text-xl font-bold text-gray-900">Available Slots</h2>
                <p className="text-gray-500">Booking with {selectedProvider.name}</p>
              </div>
            </div>

            <div className="space-y-8">
              {sortedDates.length > 0 ? (
                sortedDates.map(date => (
                  <div key={date} className="space-y-4">
                    <h3 className="text-lg font-bold text-gray-900 border-b pb-2">{date}</h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                      {groupedSlots[date].map(slot => (
                        <Card key={slot.id} className="border-green-100 hover:border-green-300 transition-colors bg-white">
                          <CardContent className="p-4 flex flex-col items-center justify-center text-center space-y-3">
                            <Calendar className="w-6 h-6 text-green-500 mb-1" />
                            <div>
                              <div className="font-semibold text-gray-900">{format(new Date(slot.dateTime), 'MMM d, yyyy')}</div>
                              <div className="text-sm text-gray-500">{format(new Date(slot.dateTime), 'h:mm a')}</div>
                            </div>
                            <Button size="sm" variant="outline" className="w-full border-green-500 text-green-600 hover:bg-green-50" onClick={() => handleBookSlot(slot.id)}>
                              Book Time
                            </Button>
                          </CardContent>
                        </Card>
                      ))}
                    </div>
                  </div>
                ))
              ) : (
                <div className="col-span-full py-12 text-center text-gray-500 bg-gray-50 rounded-xl border border-dashed border-gray-200">
                  No available slots at the moment.
                </div>
              )}
            </div>
          </div>
        );
      })()}

      {activeTab === 'appointments' && (() => {
        const now = new Date();
        const upcomingAppointments = appointments.filter(apt => new Date(apt.slotDateTime) > now);
        const pastAppointments = appointments.filter(apt => new Date(apt.slotDateTime) <= now);

        return (
          <div className="space-y-8">
            {/* Upcoming Appointments Section */}
            <div className="space-y-4">
              <h2 className="text-xl font-bold tracking-tight text-gray-900">
                Upcoming Appointments ({upcomingAppointments.length})
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {upcomingAppointments.length > 0 ? (
                  upcomingAppointments.map(apt => (
                    <Card key={apt.id} className="border-gray-200 transition-opacity bg-white">
                      <CardHeader className="pb-4">
                        <div className="flex items-start justify-between">
                          <div className="space-y-1">
                            <Badge variant="success" className="mb-2">Booked</Badge>
                            <CardTitle className="text-lg">{apt.providerName}</CardTitle>
                            <p className="text-xs text-gray-500 font-medium">{apt.category?.replace('_', ' ')}</p>
                          </div>
                          <div className="bg-green-50 text-green-600 p-2 rounded-lg">
                            <CheckCircle2 className="w-5 h-5" />
                          </div>
                        </div>
                      </CardHeader>
                      <CardContent className="space-y-3">
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between bg-gray-50 p-3 rounded-lg border border-gray-100 gap-2">
                          <div className="flex items-center text-sm text-gray-700">
                            <Calendar className="w-4 h-4 mr-3 text-[#6366f1]" />
                            <span className="font-medium">{format(new Date(apt.slotDateTime), 'MMM d, yyyy - h:mm a')}</span>
                          </div>
                          <Badge variant="success" className="bg-green-100 text-green-700">
                            Upcoming
                          </Badge>
                        </div>
                        <div className="flex items-center text-sm text-gray-500 px-1">
                          <MapPin className="w-4 h-4 mr-2 text-gray-400" />
                          {apt.location}
                        </div>
                        <div className="text-xs text-gray-400 px-1 mt-4">
                          Booked on {format(new Date(apt.bookedAt), 'MMM d, yyyy')}
                        </div>
                      </CardContent>
                      <CardFooter className="pt-4 border-t border-gray-50">
                        <Button variant="danger" className="w-full bg-red-50 text-red-600 hover:bg-red-100 hover:text-red-700 border-0" onClick={() => handleCancelAppointment(apt.id)}>
                          <XCircle className="w-4 h-4 mr-2" />
                          Cancel Appointment
                        </Button>
                      </CardFooter>
                    </Card>
                  ))
                ) : appointments.length === 0 ? (
                  <div className="col-span-full py-16 text-center bg-gray-50 rounded-2xl border border-dashed border-gray-200">
                    <Calendar className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                    <h3 className="text-lg font-medium text-gray-900">No appointments yet</h3>
                    <p className="text-gray-500 mt-1">When you book an appointment, it will appear here.</p>
                    <Button className="mt-4" onClick={() => setActiveTab('browse')}>Find a Provider</Button>
                  </div>
                ) : (
                  <div className="col-span-full py-16 text-center bg-gray-50 rounded-2xl border border-dashed border-gray-200">
                    <CheckCircle2 className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                    <h3 className="text-lg font-medium text-gray-900">All caught up!</h3>
                    <p className="text-gray-500 mt-1">You have no upcoming appointments.</p>
                  </div>
                )}
              </div>
            </div>

            {/* Past Appointments Section */}
            {pastAppointments.length > 0 && (
              <div className="space-y-4 pt-4 border-t border-gray-100">
                <h2 className="text-lg font-medium text-gray-500">
                  Past Appointments ({pastAppointments.length})
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {pastAppointments.map(apt => (
                    <Card key={apt.id} className="border-gray-200 transition-opacity bg-gray-50 opacity-75">
                      <CardHeader className="pb-4">
                        <div className="flex items-start justify-between">
                          <div className="space-y-1">
                            <Badge variant="success" className="mb-2">Booked</Badge>
                            <CardTitle className="text-lg">{apt.providerName}</CardTitle>
                            <p className="text-xs text-gray-500 font-medium">{apt.category?.replace('_', ' ')}</p>
                          </div>
                          <div className="bg-gray-100 text-gray-400 p-2 rounded-lg">
                            <CheckCircle2 className="w-5 h-5" />
                          </div>
                        </div>
                      </CardHeader>
                      <CardContent className="space-y-3">
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between bg-gray-50 p-3 rounded-lg border border-gray-100 gap-2">
                          <div className="flex items-center text-sm text-gray-700">
                            <Calendar className="w-4 h-4 mr-3 text-[#6366f1]" />
                            <span className="font-medium">{format(new Date(apt.slotDateTime), 'MMM d, yyyy - h:mm a')}</span>
                          </div>
                          <Badge variant="secondary" className="bg-gray-200 text-gray-600">
                            Completed
                          </Badge>
                        </div>
                        <div className="flex items-center text-sm text-gray-500 px-1">
                          <MapPin className="w-4 h-4 mr-2 text-gray-400" />
                          {apt.location}
                        </div>
                        <div className="text-xs text-gray-400 px-1 mt-4">
                          Booked on {format(new Date(apt.bookedAt), 'MMM d, yyyy')}
                        </div>
                      </CardContent>
                      <CardFooter className="pt-4 border-t border-gray-50">
                        <Button disabled variant="secondary" className="w-full bg-gray-100 text-gray-400 hover:bg-gray-100 cursor-not-allowed border-0">
                          <XCircle className="w-4 h-4 mr-2" />
                          Cannot Cancel
                        </Button>
                      </CardFooter>
                    </Card>
                  ))}
                </div>
              </div>
            )}
          </div>
        );
      })()}
    </div>
  );
}
