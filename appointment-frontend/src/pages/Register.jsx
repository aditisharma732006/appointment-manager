import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '../components/ui/Card';
import { Calendar } from 'lucide-react';

export function Register() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'USER',
    category: 'MEDICAL',
    description: '',
    location: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const payload = {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        role: formData.role
      };
      
      if (formData.role === 'PROVIDER') {
        payload.category = formData.category;
        payload.description = formData.description;
        payload.location = formData.location;
      }
      
      await api.post('/auth/register', payload);
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-white p-4 py-12">
      <Card className="w-full max-w-lg shadow-lg border-gray-100">
        <CardHeader className="space-y-4 text-center pb-6 pt-8">
          <div className="mx-auto bg-[#e0e7ff] w-16 h-16 rounded-full flex items-center justify-center text-[#6366f1] shadow-sm mb-2">
             <Calendar className="w-8 h-8" />
          </div>
          <CardTitle className="text-3xl font-bold tracking-tight text-gray-900">Create Account</CardTitle>
          <p className="text-sm text-gray-500">Join our platform to manage appointments easily</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && <div className="p-3 bg-red-50 text-red-600 text-sm rounded-xl border border-red-100">{error}</div>}
            
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2 col-span-2">
                <label className="text-sm font-medium text-gray-700">Full Name</label>
                <Input
                  name="name"
                  placeholder="John Doe"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  className="bg-[#f9fafb]"
                />
              </div>
              <div className="space-y-2 col-span-2 sm:col-span-1">
                <label className="text-sm font-medium text-gray-700">Email</label>
                <Input
                  type="email"
                  name="email"
                  placeholder="m@example.com"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  className="bg-[#f9fafb]"
                />
              </div>
              <div className="space-y-2 col-span-2 sm:col-span-1">
                <label className="text-sm font-medium text-gray-700">Password</label>
                <Input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  required
                  className="bg-[#f9fafb]"
                />
              </div>
              <div className="space-y-2 col-span-2">
                <label className="text-sm font-medium text-gray-700">I am a</label>
                <Select name="role" value={formData.role} onChange={handleChange} className="bg-[#f9fafb]">
                  <option value="USER">Patient / User</option>
                  <option value="PROVIDER">Service Provider</option>
                </Select>
              </div>
            </div>

            {formData.role === 'PROVIDER' && (
              <div className="space-y-4 pt-4 border-t border-gray-100 mt-4">
                <h4 className="text-sm font-semibold text-[#6366f1] uppercase tracking-wider">Provider Details</h4>
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">Category</label>
                  <Select name="category" value={formData.category} onChange={handleChange} className="bg-[#f9fafb]">
                    <option value="MEDICAL">Medical</option>
                    <option value="PERSONAL_CARE">Personal Care</option>
                    <option value="EDUCATION">Education</option>
                    <option value="CONSULTING">Consulting</option>
                  </Select>
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">Location</label>
                  <Input
                    name="location"
                    placeholder="Clinic/Office Address"
                    value={formData.location}
                    onChange={handleChange}
                    required={formData.role === 'PROVIDER'}
                    className="bg-[#f9fafb]"
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">Description</label>
                  <textarea
                    name="description"
                    rows="3"
                    className="flex w-full rounded-xl border border-gray-300 bg-[#f9fafb] px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-[#6366f1] focus:border-transparent resize-none"
                    placeholder="Briefly describe your services..."
                    value={formData.description}
                    onChange={handleChange}
                    required={formData.role === 'PROVIDER'}
                  />
                </div>
              </div>
            )}

            <Button type="submit" className="w-full mt-6 h-12 text-md">
              Create Account
            </Button>
          </form>
        </CardContent>
        <CardFooter className="flex justify-center border-t border-gray-100 pt-6 pb-6">
          <p className="text-sm text-gray-600">
            Already have an account?{' '}
            <Link to="/login" className="text-[#6366f1] font-semibold hover:underline">
              Sign in
            </Link>
          </p>
        </CardFooter>
      </Card>
    </div>
  );
}
