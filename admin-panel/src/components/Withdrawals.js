import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

function Withdrawals() {
  const [withdrawals, setWithdrawals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchWithdrawals();
  }, []);

  const fetchWithdrawals = async () => {
    try {
      const response = await api.getPendingWithdrawals();
      setWithdrawals(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch withdrawals');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (withdrawalId) => {
    if (!window.confirm('Approve this withdrawal?')) return;
    
    try {
      await api.approveWithdrawal(withdrawalId);
      alert('Withdrawal approved successfully');
      fetchWithdrawals();
    } catch (err) {
      alert('Failed to approve withdrawal');
    }
  };

  const handleReject = async (withdrawalId) => {
    const reason = prompt('Enter reason for rejection:');
    if (!reason) return;
    
    try {
      await api.rejectWithdrawal(withdrawalId, reason);
      alert('Withdrawal rejected successfully');
      fetchWithdrawals();
    } catch (err) {
      alert('Failed to reject withdrawal');
    }
  };

  if (loading) return <div className="container"><div className="loading">Loading withdrawals...</div></div>;

  return (
    <div className="container">
      <h1>Withdrawal Management</h1>
      
      {error && <div className="error">{error}</div>}

      <div className="card">
        <h3>Pending Withdrawals</h3>
        {withdrawals.length === 0 ? (
          <p>No pending withdrawals</p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>User ID</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Requested At</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {withdrawals.map(withdrawal => (
                <tr key={withdrawal.id}>
                  <td>{withdrawal.id}</td>
                  <td>{withdrawal.userId}</td>
                  <td>₹{withdrawal.amount}</td>
                  <td>
                    <span className="badge badge-warning">{withdrawal.status}</span>
                  </td>
                  <td>{new Date(withdrawal.createdAt).toLocaleString()}</td>
                  <td>
                    <button 
                      className="btn btn-success" 
                      style={{ padding: '5px 10px', fontSize: '12px', marginRight: '5px' }}
                      onClick={() => handleApprove(withdrawal.id)}
                    >
                      Approve
                    </button>
                    <button 
                      className="btn btn-danger" 
                      style={{ padding: '5px 10px', fontSize: '12px' }}
                      onClick={() => handleReject(withdrawal.id)}
                    >
                      Reject
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default Withdrawals;
