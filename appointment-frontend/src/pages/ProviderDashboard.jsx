import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Button } from '../components/ui/Button';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { format } from 'date-fns';
import { Calendar, User, Clock, ToggleLeft, ToggleRight, CheckCircle2 } from 'lucide-react';

export function ProviderDashboard() {
  const [activeTab, setActiveTab] = useState('appointments');
  const [appointments, setAppointments] = useState([]);
  const [slots, setSlots] = useState([]);

  useEffect(() => {
    if (activeTab === 'appointments') {
      fetchAppointments();
    } else {
      fetchSlots();
    }
  }, [activeTab]);

  const fetchAppointments = async () => {
    try {
      const response = await api.get('/provider/dashboard');
      setAppointments(response.data);
    } catch (err) {
      console.error('Failed to fetch dashboard', err);
    }
  };

  const fetchSlots = async () => {
    try {
      const response = await api.get('/provider/slots');
      setSlots(response.data);
    } catch (err) {
      console.error('Failed to fetch slots', err);
    }
  };

  const handleToggleSlot = async (id) => {
    try {
      await api.put(`/slots/${id}`);
      fetchSlots();
    } catch (err) {
      console.error('Failed to toggle slot', err);
      alert('Could not update slot status');
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'AVAILABLE': return <Badge variant="success">Available</Badge>;
      case 'BOOKED': return <Badge variant="danger">Booked</Badge>;
      case 'EXPIRED': return <Badge variant="default">Expired</Badge>;
      case 'UNAVAILABLE': return <Badge variant="warning">Unavailable</Badge>;
      default: return <Badge>{status}</Badge>;
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-gray-900">Appointment Booking</h1>
          <p className="text-gray-500">Manage your appointments and availability.</p>
        </div>
        <div className="flex bg-gray-100 p-1 rounded-xl w-fit">
          <button
            onClick={() => setActiveTab('appointments')}
            className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors flex items-center ${activeTab === 'appointments' ? 'bg-white text-[#6366f1] shadow-sm' : 'text-gray-600 hover:text-gray-900'}`}
          >
            <Calendar className="w-4 h-4 mr-2" />
            Appointments
          </button>
          <button
            onClick={() => setActiveTab('slots')}
            className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors flex items-center ${activeTab === 'slots' ? 'bg-white text-[#6366f1] shadow-sm' : 'text-gray-600 hover:text-gray-900'}`}
          >
            <Clock className="w-4 h-4 mr-2" />
            Manage Slots
          </button>
        </div>
      </div>

      {activeTab === 'appointments' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {appointments.map(apt => (
            <Card key={apt.id} className="border-gray-200 bg-white shadow-sm hover:shadow-md transition-shadow">
              <CardHeader className="pb-4">
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <Badge variant="primary" className="mb-2 bg-[#e0e7ff] text-[#6366f1]">Upcoming</Badge>
                    <CardTitle className="text-lg flex items-center">
                      <User className="w-5 h-5 mr-2 text-gray-400" />
                      {apt.providerName}
                    </CardTitle>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="flex items-center text-sm text-gray-700 bg-[#f8fafc] p-3 rounded-lg border border-gray-100">
                  <Clock className="w-4 h-4 mr-3 text-[#6366f1]" />
                  <span className="font-medium">{format(new Date(apt.slotDateTime), 'MMM d, yyyy - h:mm a')}</span>
                </div>
              </CardContent>
            </Card>
          ))}
          {appointments.length === 0 && (
            <div className="col-span-full py-16 text-center bg-gray-50 rounded-2xl border border-dashed border-gray-200">
              <CheckCircle2 className="w-12 h-12 text-gray-300 mx-auto mb-3" />
              <h3 className="text-lg font-medium text-gray-900">All caught up!</h3>
              <p className="text-gray-500 mt-1">You have no upcoming appointments.</p>
            </div>
          )}
        </div>
      )}

      {activeTab === 'slots' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {slots.map(slot => (
              <Card key={slot.id} className="border-gray-200 bg-white">
                <CardContent className="p-5 flex flex-col items-center justify-center text-center space-y-3">
                  {getStatusBadge(slot.status)}
                  <div className="mt-2">
                    <div className="font-semibold text-gray-900">{format(new Date(slot.dateTime), 'MMM d, yyyy')}</div>
                    <div className="text-sm text-gray-500 font-medium">{format(new Date(slot.dateTime), 'h:mm a')}</div>
                  </div>
                  
                  {(slot.status === 'AVAILABLE' || slot.status === 'UNAVAILABLE') && (
                    <Button 
                      size="sm" 
                      variant={slot.status === 'AVAILABLE' ? 'outline' : 'primary'}
                      className="w-full mt-2" 
                      onClick={() => handleToggleSlot(slot.id)}
                    >
                      {slot.status === 'AVAILABLE' ? (
                        <><ToggleRight className="w-4 h-4 mr-2 text-green-500" /> Mark Unavailable</>
                      ) : (
                        <><ToggleLeft className="w-4 h-4 mr-2" /> Mark Available</>
                      )}
                    </Button>
                  )}
                </CardContent>
              </Card>
            ))}
            {slots.length === 0 && (
              <div className="col-span-full py-12 text-center text-gray-500 bg-gray-50 rounded-xl border border-dashed border-gray-200">
                No slots configured yet.
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
