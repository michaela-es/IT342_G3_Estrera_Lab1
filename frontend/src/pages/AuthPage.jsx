import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';
import '../AuthPage.css';

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    emailOrUsername: '',
    password: '',
    confirmPassword: ''
  });
  
  const { login, register, loading, error, clearError } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    if (error) clearError();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    let result;
    if (isLogin) {
      result = await login(formData.emailOrUsername, formData.password);
      if (result.success) {
        navigate('/profile');
      }
    } else {
      if (formData.password !== formData.confirmPassword) {
        return;
      }
      result = await register(formData);
      if (result.success) {
        setIsLogin(true);
        setFormData({
          name: '',
          email: '',
          emailOrUsername: '',
          password: '',
          confirmPassword: ''
        });
      }
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-header">
          <h1 className="auth-title">
            {isLogin ? 'Sign In' : 'Create Account'}
          </h1>
        </div>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          {isLogin ? (
            <LoginForm 
              formData={formData}
              handleChange={handleChange}
              loading={loading}
            />
          ) : (
            <RegisterForm 
              formData={formData}
              handleChange={handleChange}
              loading={loading}
            />
          )}

          <button 
            type="submit" 
            className="submit-btn"
            disabled={loading}
          >
            {loading ? 'Please wait...' : isLogin ? 'Sign In' : 'Sign Up'}
          </button>
        </form>

        <div className="auth-footer">
          <p className="toggle-text">
            {isLogin ? "Don't have an account?" : "Already have an account?"}
            <button
              type="button"
              className="toggle-btn"
              onClick={() => {
                setIsLogin(!isLogin);
                clearError();
              }}
              disabled={loading}
            >
              {isLogin ? ' Sign up' : ' Sign in'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;