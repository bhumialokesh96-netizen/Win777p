import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

function Tasks() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    try {
      const response = await api.getTasks();
      setTasks(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch tasks');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="container"><div className="loading">Loading tasks...</div></div>;

  return (
    <div className="container">
      <h1>Task Management</h1>
      
      {error && <div className="error">{error}</div>}

      <div className="card">
        <h3>Available Tasks</h3>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Type</th>
              <th>Reward</th>
              <th>Daily Limit</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {tasks.map(task => (
              <tr key={task.id}>
                <td>{task.id}</td>
                <td>{task.title}</td>
                <td>{task.taskType}</td>
                <td>₹{task.rewardAmount}</td>
                <td>{task.dailyLimit || 10}</td>
                <td>
                  <span className={`badge badge-${task.status === 'ACTIVE' ? 'success' : 'danger'}`}>
                    {task.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Tasks;
