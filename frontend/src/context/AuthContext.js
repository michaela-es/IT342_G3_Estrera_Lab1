import { createContext, useState, useEffect } from 'react';
import api from '../../api/ApiClient';
import { AUTH_URLS } from '../../api/constants';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);

  const login = async (email, password) => {
    const { data } = await api.post(AUTH_URLS.LOGIN, { email, password });
    localStorage.setItem('accessToken', data.accessToken);
    setUser(data.user);
  };

  const register = async (formData) => {
    await api.post(AUTH_URLS.REGISTER, formData);
  };

  const logout = async () => {
    await api.post(AUTH_URLS.LOGOUT);
    localStorage.removeItem('accessToken');
    setUser(null);
  };

  const fetchCurrentUser = async () => {
    try {
      const { data } = await api.get(USER_URLS.ME);
      setUser(data);
    } catch (err) {
      setUser(null);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) fetchCurrentUser();
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
