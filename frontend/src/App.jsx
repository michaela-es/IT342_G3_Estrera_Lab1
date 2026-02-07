import { useState } from 'react'
import './App.css'

function App() {
  const [isLogin, setIsLogin] = useState(true)
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: ''
  })

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    
    if (isLogin) {
      console.log('Login attempt:', {
        email: formData.email,
        password: formData.password
      })
      alert(`Logging in with ${formData.email}`)
    } else {
      if (formData.password !== formData.confirmPassword) {
        alert("Passwords don't match!")
        return
      }
      console.log('Registration data:', formData)
      alert(`Welcome ${formData.name}! Registration successful.`)
    }
  }

  return (
    <div className="app-container">
      <div className="auth-container">
        <div className="auth-header">
          <h1 className="auth-title">{isLogin ? 'Welcome Back' : 'Create Account'}</h1>
          <p className="auth-subtitle">
            {isLogin ? 'Sign in to your account' : 'Sign up to get started'}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="name">Username</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Enter your name"
                required={!isLogin}
              />
            </div>
          )}

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
                required={!isLogin}
                minLength="6"
              />
            </div>
          )}

          {isLogin && (
            <div className="form-options">
              <label className="checkbox-label">
                {/* <input type="checkbox" /> Remember me */}
              </label>
            </div>
          )}

          <button type="submit" className="submit-btn">
            {isLogin ? 'Sign In' : 'Sign Up'}
          </button>
        </form>

        <div className="auth-footer">
          <p className="toggle-text">
            {isLogin ? "Don't have an account?" : "Already have an account?"}
            <button 
              className="toggle-btn"
              onClick={() => setIsLogin(!isLogin)}
            >
              {isLogin ? ' Sign up' : ' Sign in'}
            </button>
          </p>
          
    
        </div>
      </div>
    </div>
  )
}

export default App