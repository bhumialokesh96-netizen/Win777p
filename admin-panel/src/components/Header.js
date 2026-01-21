import React from 'react';
import { Link, useLocation } from 'react-router-dom';

function Header({ onLogout }) {
  const location = useLocation();
  const adminData = JSON.parse(localStorage.getItem('adminData') || '{}');

  return (
    <div className="header">
      <div>
        <h2 style={{ margin: 0 }}>WIN777 Admin Panel</h2>
      </div>
      <nav className="nav">
        <Link 
          to="/dashboard" 
          className={`nav-link ${location.pathname === '/dashboard' ? 'active' : ''}`}
        >
          Dashboard
        </Link>
        <Link 
          to="/users" 
          className={`nav-link ${location.pathname === '/users' ? 'active' : ''}`}
        >
          Users
        </Link>
        <Link 
          to="/tasks" 
          className={`nav-link ${location.pathname === '/tasks' ? 'active' : ''}`}
        >
          Tasks
        </Link>
        <Link 
          to="/withdrawals" 
          className={`nav-link ${location.pathname === '/withdrawals' ? 'active' : ''}`}
        >
          Withdrawals
        </Link>
        <Link 
          to="/configuration" 
          className={`nav-link ${location.pathname === '/configuration' ? 'active' : ''}`}
        >
          Configuration
        </Link>
        <div style={{ marginLeft: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span>{adminData.username || 'Admin'}</span>
          <button onClick={onLogout} className="btn btn-danger" style={{ padding: '5px 15px' }}>
            Logout
          </button>
        </div>
      </nav>
    </div>
  );
}

export default Header;
