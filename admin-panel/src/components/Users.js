import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchMobile, setSearchMobile] = useState('');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await api.getUsers(0, 20);
      setUsers(response.data.content || response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch users');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchMobile) {
      fetchUsers();
      return;
    }
    try {
      const response = await api.searchUsers(searchMobile);
      setUsers(response.data);
      setError('');
    } catch (err) {
      setError('Search failed');
    }
  };

  const handleBanUser = async (userId) => {
    const reason = prompt('Enter reason for banning:');
    if (!reason) return;
    
    try {
      await api.banUser(userId, reason);
      alert('User banned successfully');
      fetchUsers();
    } catch (err) {
      alert('Failed to ban user');
    }
  };

  const handleUnbanUser = async (userId) => {
    try {
      await api.unbanUser(userId);
      alert('User unbanned successfully');
      fetchUsers();
    } catch (err) {
      alert('Failed to unban user');
    }
  };

  if (loading) return <div className="container"><div className="loading">Loading users...</div></div>;

  return (
    <div className="container">
      <h1>User Management</h1>
      
      <div className="card">
        <form onSubmit={handleSearch}>
          <div className="form-group" style={{ display: 'flex', gap: '10px' }}>
            <input
              type="text"
              className="form-control"
              placeholder="Search by mobile number"
              value={searchMobile}
              onChange={(e) => setSearchMobile(e.target.value)}
            />
            <button type="submit" className="btn btn-primary">Search</button>
            <button type="button" className="btn btn-warning" onClick={() => { setSearchMobile(''); fetchUsers(); }}>
              Clear
            </button>
          </div>
        </form>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Mobile</th>
              <th>Status</th>
              <th>Banned</th>
              <th>Device Count</th>
              <th>Created At</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.mobile}</td>
                <td>
                  <span className={`badge badge-${user.status === 'ACTIVE' ? 'success' : 'danger'}`}>
                    {user.status}
                  </span>
                </td>
                <td>
                  <span className={`badge badge-${user.isBanned ? 'danger' : 'success'}`}>
                    {user.isBanned ? 'Yes' : 'No'}
                  </span>
                </td>
                <td>{user.deviceCount || 1}</td>
                <td>{new Date(user.createdAt).toLocaleDateString()}</td>
                <td>
                  {user.isBanned ? (
                    <button 
                      className="btn btn-success" 
                      style={{ padding: '5px 10px', fontSize: '12px' }}
                      onClick={() => handleUnbanUser(user.id)}
                    >
                      Unban
                    </button>
                  ) : (
                    <button 
                      className="btn btn-danger" 
                      style={{ padding: '5px 10px', fontSize: '12px' }}
                      onClick={() => handleBanUser(user.id)}
                    >
                      Ban
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Users;
