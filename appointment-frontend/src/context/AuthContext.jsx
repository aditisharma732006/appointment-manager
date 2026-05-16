import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

const parseJwt = (token) => {
  if (!token) return null;
  try {
    const base64Url = token.split('.')[1];
    if (!base64Url) return null;
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const decodedPayload = decodeURIComponent(
      Array.prototype.map
        .call(atob(base64), (c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(decodedPayload);
  } catch {
    return null;
  }
};

const getNameFromToken = (token) => {
  const payload = parseJwt(token);
  return payload?.name || payload?.fullName || payload?.username || payload?.sub || null;
};

export const AuthProvider = ({ children }) => {
  const initialToken = localStorage.getItem('token');
  const initialRole = localStorage.getItem('role');
  const initialName = localStorage.getItem('name') || getNameFromToken(initialToken);

  const [token, setToken] = useState(initialToken || null);
  const [role, setRole] = useState(initialRole || null);
  const [name, setName] = useState(initialName || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // We could validate token here if we had an endpoint
    setLoading(false);
  }, []);

  const login = (newToken, newRole, providedName) => {
    const resolvedName = providedName || getNameFromToken(newToken);
    setToken(newToken);
    setRole(newRole);
    setName(resolvedName);
    localStorage.setItem('token', newToken);
    localStorage.setItem('role', newRole);
    if (resolvedName) {
      localStorage.setItem('name', resolvedName);
    } else {
      localStorage.removeItem('name');
    }
  };

  const logout = () => {
    setToken(null);
    setRole(null);
    setName(null);
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('name');
  };

  return (
    <AuthContext.Provider value={{ token, role, name, login, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
