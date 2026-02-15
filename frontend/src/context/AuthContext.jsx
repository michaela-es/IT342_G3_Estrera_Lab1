import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/apiClient';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [initializing, setInitializing] = useState(true);

  useEffect(() => {
    const loadUser = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const savedUser = localStorage.getItem('user');
        
        console.log("AuthProvider: Checking saved data...");
        
        if (token && savedUser) {
          const parsedUser = JSON.parse(savedUser);
          setUser(parsedUser);
          console.log("AuthProvider: User restored from localStorage:", parsedUser);
        } else {
          console.log("AuthProvider: No saved user found");
        }
      } catch (err) {
        console.error('Error loading user:', err);
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
      } finally {
        setInitializing(false);
      }
    };

    loadUser();
  }, []);

  useEffect(() => {
    const handleStorageChange = (e) => {
      if (e.key === 'user') {
        if (e.newValue) {
          setUser(JSON.parse(e.newValue));
        } else {
          setUser(null);
        }
      }
      if (e.key === 'accessToken' && !e.newValue) {
        setUser(null);
      }
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  const login = async (emailOrUsername, password) => {
    try {
      setLoading(true);
      setError(null);
      
      const response = await api.post('/auth/login', {
        usernameOrEmail: emailOrUsername,
        password
      });

      const { accessToken, user: userData } = response.data;
      
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('user', JSON.stringify(userData));
      
      setUser(userData);
      
      console.log("AuthProvider: Login successful, user saved:", userData);
      return { success: true };
    } catch (err) {
      const message = err.response?.data?.message || err.response?.data?.error || 'Login failed';
      setError(message);
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    try {
      setLoading(true);
      setError(null);
      
      await api.post('/auth/register', {
        username: userData.name,
        email: userData.email,
        password: userData.password
      });
      
      return { success: true };
    } catch (err) {
      const message = err.response?.data?.message || err.response?.data?.error || 'Registration failed';
      setError(message);
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
    setUser(null);
    setError(null);
    
    api.post('/auth/logout').catch(console.error);
  };

  const updateUser = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const value = {
    user,                   
    loading,
    error,
    initializing,
    isAuthenticated: !!user,
    login,
    register,
    logout,
    updateUser,
    clearError: () => setError(null)
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};