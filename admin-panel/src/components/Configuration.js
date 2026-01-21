import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

function Configuration() {
  const [maintenanceMode, setMaintenanceMode] = useState(false);
  const [themeColor, setThemeColor] = useState('#007bff');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  useEffect(() => {
    // In a real app, fetch current configuration
    setLoading(false);
  }, []);

  const handleMaintenanceMode = async () => {
    try {
      await api.setMaintenanceMode(!maintenanceMode);
      setMaintenanceMode(!maintenanceMode);
      setMessage('Maintenance mode updated successfully');
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setMessage('Failed to update maintenance mode');
    }
  };

  const handleThemeColor = async (e) => {
    e.preventDefault();
    try {
      await api.setThemeColor(themeColor);
      setMessage('Theme color updated successfully');
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setMessage('Failed to update theme color');
    }
  };

  if (loading) return <div className="container"><div className="loading">Loading configuration...</div></div>;

  return (
    <div className="container">
      <h1>Configuration Management</h1>
      
      {message && <div className={message.includes('success') ? 'success' : 'error'}>{message}</div>}

      <div className="card">
        <h3>Maintenance Mode</h3>
        <p>Enable or disable system maintenance mode</p>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span>Status: <strong>{maintenanceMode ? 'ON' : 'OFF'}</strong></span>
          <button 
            className={`btn ${maintenanceMode ? 'btn-danger' : 'btn-success'}`}
            onClick={handleMaintenanceMode}
          >
            {maintenanceMode ? 'Disable' : 'Enable'} Maintenance Mode
          </button>
        </div>
      </div>

      <div className="card">
        <h3>Theme Color</h3>
        <form onSubmit={handleThemeColor}>
          <div className="form-group">
            <label>Select Theme Color</label>
            <div style={{ display: 'flex', gap: '10px' }}>
              <input
                type="color"
                value={themeColor}
                onChange={(e) => setThemeColor(e.target.value)}
              />
              <input
                type="text"
                className="form-control"
                value={themeColor}
                onChange={(e) => setThemeColor(e.target.value)}
                placeholder="#007bff"
              />
              <button type="submit" className="btn btn-primary">Update</button>
            </div>
          </div>
        </form>
      </div>

      <div className="card">
        <h3>System Information</h3>
        <ul>
          <li><strong>Backend URL:</strong> {process.env.REACT_APP_API_URL || 'http://localhost:8080'}</li>
          <li><strong>Phase:</strong> Phase 2 - Complete</li>
          <li><strong>Version:</strong> 1.0.0-SNAPSHOT</li>
        </ul>
      </div>
    </div>
  );
}

export default Configuration;
