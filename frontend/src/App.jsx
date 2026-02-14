import { useState } from 'react';
import api from './api/apiClient'; 
import './App.css';

function App() {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    emailOrUsername: '', 
    password: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (isLogin) {
        // Login with username or email
        const response = await api.post('/auth/login', {
          usernameOrEmail: formData.emailOrUsername,
          password: formData.password
        });

        localStorage.setItem('accessToken', response.data.accessToken);
        alert(`Logged in as ${response.data.user.username}`);
      } else {
        // Registration
        if (formData.password !== formData.confirmPassword) {
          alert("Passwords don't match!");
          setLoading(false);
          return;
        }

        await api.post('/auth/register', {
          username: formData.name,
          email: formData.email,
          password: formData.password
        });

        alert(`Welcome ${formData.name}! Registration successful.`);
        setIsLogin(true); 
      }
    } catch (err) {
      if (err.response) {
        alert(err.response.data.message || err.response.data.error);
      } else {
        alert('Network error');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container">
      <div className="auth-container">

        {/* Header */}
        <div className="auth-header">
          <h1 className="auth-title">
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h1>
          <p className="auth-subtitle">
            {isLogin ? 'Sign in to your account' : 'Sign up to get started'}
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="auth-form">

          {/* Username or Email for login */}
          {isLogin && (
            <div className="form-group">
              <label htmlFor="emailOrUsername">Username or Email</label>
              <input
                type="text"
                id="emailOrUsername"
                name="emailOrUsername"
                value={formData.emailOrUsername}
                onChange={handleChange}
                placeholder="Enter your username or email"
                required
              />
            </div>
          )}

          {/* Username (registration only) */}
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="name">Username</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Enter your username"
                required
              />
            </div>
          )}

          {/* Email (registration only) */}
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter your email"
                required
              />
            </div>
          )}

          {/* Password */}
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
              minLength="6"
            />
          </div>

          {/* Confirm Password (registration only) */}
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="confirmPassword">Confirm Password</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Confirm your password"
                required
                minLength="6"
              />
            </div>
          )}

          {/* Submit button */}
          <button type="submit" className="submit-btn" disabled={loading}>
            {loading ? 'Please wait...' : isLogin ? 'Sign In' : 'Sign Up'}
          </button>
        </form>

        {/* Footer toggle */}
        <div className="auth-footer">
          <p className="toggle-text">
            {isLogin ? "Don't have an account?" : "Already have an account?"}
            <button
              type="button"
              className="toggle-btn"
              onClick={() => setIsLogin(!isLogin)}
            >
              {isLogin ? ' Sign up' : ' Sign in'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default App;
