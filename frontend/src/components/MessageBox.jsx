import React from 'react';

const MessageBox = ({ type, text, onClose }) => {
  if (!text) return null;

  const getIcon = () => {
    switch (type) {
      case 'success':
        return '✓';
      case 'error':
        return '✗';
      case 'info':
        return 'ℹ';
      default:
        return '';
    }
  };

  return (
    <div className={`message message-${type}`}>
      <span className="message-icon">{getIcon()}</span>
      <span className="message-text">{text}</span>
      {onClose && (
        <button 
          type="button"
          className="message-close" 
          onClick={onClose}
          aria-label="Close message"
        >
          ×
        </button>
      )}
    </div>
  );
};

export default MessageBox;