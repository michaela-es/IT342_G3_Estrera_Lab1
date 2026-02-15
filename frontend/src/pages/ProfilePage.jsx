import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import '../ProfilePage.css';

const ProfilePage = () => {
  const { user, loading, initializing, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (initializing || loading) {
    return (
      <div className="profile-page">
        <div className="loading-container">Loading...</div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="profile-page">
      <div className="profile-container">
        <div className="profile-card">
          <div className="profile-avatar">
            {user.username?.charAt(0).toUpperCase()}
          </div>
          <h1 className="profile-name">{user.username}</h1>
          <p className="profile-email">{user.email}</p>
          
          <div className="profile-status">
            <span className={`status-badge ${user.enabled ? 'active' : 'inactive'}`}>
              {user.enabled ? 'Email Verified' : 'Email Not Verified'}
            </span>
          </div>

          <button onClick={handleLogout} className="logout-btn">
            Sign Out
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;