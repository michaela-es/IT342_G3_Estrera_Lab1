import React from 'react';

const ActionButton = ({ loading, isLogin }) => {
  const getButtonText = () => {
    if (loading) return 'Please wait...';
    return isLogin ? 'Sign In' : 'Sign Up';
  };

  return (
    <button 
      type="submit" 
      className="submit-btn" 
      disabled={loading}
    >
      {getButtonText()}
    </button>
  );
};

export default ActionButton;