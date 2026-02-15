import React from 'react';

const LoginForm = ({ formData, handleChange, loading }) => {
  return (
    <>
      <div className="form-group">
        <label htmlFor="emailOrUsername">Username or Email</label>
        <input
          type="text"
          id="emailOrUsername"
          name="emailOrUsername"
          value={formData.emailOrUsername}
          onChange={handleChange}
          placeholder="Enter your username or email"
          disabled={loading}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="password">Password</label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          placeholder="Enter your password"
          disabled={loading}
          required
          minLength="6"
        />
      </div>
    </>
  );
};

export default LoginForm;