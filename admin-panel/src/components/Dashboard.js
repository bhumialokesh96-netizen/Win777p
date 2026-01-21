import React from 'react';

function Dashboard() {
  const adminData = JSON.parse(localStorage.getItem('adminData') || '{}');

  return (
    <div className="container">
      <h1>Dashboard</h1>
      <div className="card">
        <h3>Welcome, {adminData.username}!</h3>
        <p>Email: {adminData.email}</p>
        <p>Role: {adminData.role}</p>
      </div>
      <div className="card">
        <h3>Phase 2 Features Implemented</h3>
        <ul>
          <li>✅ Fraud Prevention System (Device mapping, SMS rate limiting, emulator detection)</li>
          <li>✅ User Management (Ban/Unban, Search)</li>
          <li>✅ Task Management (CRUD operations)</li>
          <li>✅ Withdrawal Approval System</li>
          <li>✅ Configuration Management (Theme, Banners, Maintenance mode)</li>
          <li>✅ Admin Audit Logging</li>
        </ul>
      </div>
    </div>
  );
}

export default Dashboard;
