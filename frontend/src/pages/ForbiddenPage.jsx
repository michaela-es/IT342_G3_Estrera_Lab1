import React from 'react';
import { Link } from 'react-router-dom';
import '../ForbiddenPage.css';

const ForbiddenPage = () => {
  return (
    <div className="forbidden-page">
      <div className="forbidden-container">
        <div className="forbidden-code">403</div>
        <h1 className="forbidden-title">Access Forbidden</h1>
        <p className="forbidden-message">
          You don't have permission to access this page.
          Please log in with appropriate credentials.
        </p>
        <div className="forbidden-actions">
          <Link to="/login" className="forbidden-btn primary">
            Go to Login
          </Link>
          <Link to="/" className="forbidden-btn secondary">
            Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ForbiddenPage;