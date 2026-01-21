import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Users from './components/Users';
import Tasks from './components/Tasks';
import Withdrawals from './components/Withdrawals';
import Configuration from './components/Configuration';
import Header from './components/Header';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is authenticated
    const token = localStorage.getItem('adminToken');
    if (token) {
      setIsAuthenticated(true);
    }
    setLoading(false);
  }, []);

  const handleLogin = (token, adminData) => {
    localStorage.setItem('adminToken', token);
    localStorage.setItem('adminData', JSON.stringify(adminData));
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminData');
    setIsAuthenticated(false);
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <Router>
      <div className="App">
        {isAuthenticated && <Header onLogout={handleLogout} />}
        
        <Routes>
          <Route 
            path="/login" 
            element={
              isAuthenticated ? 
                <Navigate to="/dashboard" /> : 
                <Login onLogin={handleLogin} />
            } 
          />
          
          <Route 
            path="/dashboard" 
            element={
              isAuthenticated ? 
                <Dashboard /> : 
                <Navigate to="/login" />
            } 
          />
          
          <Route 
            path="/users" 
            element={
              isAuthenticated ? 
                <Users /> : 
                <Navigate to="/login" />
            } 
          />
          
          <Route 
            path="/tasks" 
            element={
              isAuthenticated ? 
                <Tasks /> : 
                <Navigate to="/login" />
            } 
          />
          
          <Route 
            path="/withdrawals" 
            element={
              isAuthenticated ? 
                <Withdrawals /> : 
                <Navigate to="/login" />
            } 
          />
          
          <Route 
            path="/configuration" 
            element={
              isAuthenticated ? 
                <Configuration /> : 
                <Navigate to="/login" />
            } 
          />
          
          <Route 
            path="/" 
            element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
