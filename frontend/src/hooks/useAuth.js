import { useReducer, useCallback } from 'react';
import api from '../api/apiClient';
import { ACTIONS, authReducer, initialState } from '../types/authTypes';

export const useAuth = () => {
  const [state, dispatch] = useReducer(authReducer, initialState);
  const { isLogin, formData, loading, message } = state;

  const handleChange = useCallback((e) => {
    dispatch({
      type: ACTIONS.UPDATE_FIELD,
      payload: {
        field: e.target.name,
        value: e.target.value
      }
    });

    if (message.text) {
      dispatch({ type: ACTIONS.CLEAR_MESSAGE });
    }
  }, [message.text]);

  const toggleMode = useCallback(() => {
    dispatch({ type: ACTIONS.TOGGLE_MODE });
  }, []);

  const clearMessage = useCallback(() => {
    dispatch({ type: ACTIONS.CLEAR_MESSAGE });
  }, []);

  const setMessage = useCallback((type, text) => {
    dispatch({
      type: ACTIONS.SET_MESSAGE,
      payload: { type, text }
    });
  }, []);

  const handleLogin = useCallback(async () => {
    const { emailOrUsername, password } = formData;

    if (!emailOrUsername || !password) {
      setMessage('error', 'Please fill in all fields');
      return false;
    }

    try {
      const response = await api.post('/auth/login', {
        usernameOrEmail: emailOrUsername,
        password
      });

      localStorage.setItem('accessToken', response.data.accessToken);
      setMessage('success', `Logged in as ${response.data.user.username}`);
      
      setTimeout(() => {
        dispatch({ type: ACTIONS.CLEAR_MESSAGE });
      }, 5000);

      return true;
    } catch (err) {
      if (err.response) {
        setMessage('error', err.response.data.message || err.response.data.error);
      } else {
        setMessage('error', 'Network error');
      }
      return false;
    }
  }, [formData, setMessage]);

  const handleRegister = useCallback(async () => {
    const { name, email, password, confirmPassword } = formData;

    if (!name || !email || !password || !confirmPassword) {
      setMessage('error', 'Please fill in all fields');
      return false;
    }

    if (password !== confirmPassword) {
      setMessage('error', "Passwords don't match!");
      return false;
    }

    if (password.length < 6) {
      setMessage('error', 'Password must be at least 6 characters long');
      return false;
    }

    try {
      await api.post('/auth/register', {
        username: name,
        email,
        password
      });

      setMessage('success', `Welcome ${name}! Registration successful.`);

      setTimeout(() => {
        dispatch({ type: ACTIONS.CLEAR_MESSAGE });
        dispatch({ type: ACTIONS.TOGGLE_MODE });
      }, 3000);

      return true;
    } catch (err) {
      if (err.response) {
        setMessage('error', err.response.data.message || err.response.data.error);
      } else {
        setMessage('error', 'Network error');
      }
      return false;
    }
  }, [formData, setMessage]);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    
    dispatch({ type: ACTIONS.SET_LOADING, payload: true });

    let success = false;
    if (isLogin) {
      success = await handleLogin();
    } else {
      success = await handleRegister();
    }

    dispatch({ type: ACTIONS.SET_LOADING, payload: false });
    return success;
  }, [isLogin, handleLogin, handleRegister]);

  return {
    isLogin,
    formData,
    loading,
    message,
    
    handleChange,
    handleSubmit,
    toggleMode,
    clearMessage
  };
};