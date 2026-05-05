import React, { useState } from 'react';
import { authService } from '../services/api';

interface LoginPageProps {
  onLoginSuccess: (user: any) => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ onLoginSuccess }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      const response = await authService.login(email, password);
      const { data } = response.data;

      // Store tokens and user info
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);
      localStorage.setItem('user', JSON.stringify(data));

      onLoginSuccess(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.title}>🍔 FoodApp</h1>
        <h2 style={styles.subtitle}>Login</h2>

        {error && <div style={styles.error}>{error}</div>}

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@foodapp.com"
              required
              style={styles.input}
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="admin123"
              required
              style={styles.input}
            />
          </div>

          <button
            type="submit"
            disabled={isLoading}
            style={{
              ...styles.button,
              opacity: isLoading ? 0.6 : 1,
            }}
          >
            {isLoading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div style={styles.testAccounts}>
          <h3>Test Accounts:</h3>
          <p>👨‍💼 Admin: admin@foodapp.com / admin123</p>
          <p>👤 Customer: rahul@gmail.com / password</p>
          <p>🏪 Vendor: rajesh@vendor.com / vendor123</p>
          <p>🚚 Delivery: vikram@delivery.com / delivery123</p>
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    backgroundColor: '#f5f5f5',
  } as React.CSSProperties,
  card: {
    backgroundColor: 'white',
    padding: '40px',
    borderRadius: '10px',
    boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
    width: '100%',
    maxWidth: '400px',
  } as React.CSSProperties,
  title: {
    textAlign: 'center' as const,
    fontSize: '32px',
    marginBottom: '10px',
  } as React.CSSProperties,
  subtitle: {
    textAlign: 'center' as const,
    fontSize: '24px',
    color: '#666',
    marginBottom: '30px',
  } as React.CSSProperties,
  form: {
    marginBottom: '30px',
  } as React.CSSProperties,
  formGroup: {
    marginBottom: '20px',
  } as React.CSSProperties,
  label: {
    display: 'block',
    marginBottom: '8px',
    fontWeight: 'bold',
    color: '#333',
  } as React.CSSProperties,
  input: {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    fontSize: '14px',
  } as React.CSSProperties,
  button: {
    width: '100%',
    padding: '12px',
    backgroundColor: '#ff6b6b',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    fontSize: '16px',
    fontWeight: 'bold',
    cursor: 'pointer',
  } as React.CSSProperties,
  error: {
    backgroundColor: '#ffebee',
    color: '#c62828',
    padding: '12px',
    borderRadius: '5px',
    marginBottom: '20px',
  } as React.CSSProperties,
  testAccounts: {
    backgroundColor: '#f9f9f9',
    padding: '15px',
    borderRadius: '5px',
    fontSize: '12px',
  } as React.CSSProperties,
};

export default LoginPage;
