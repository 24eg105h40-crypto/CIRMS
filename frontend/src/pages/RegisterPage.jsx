import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { Shield, User, Lock, Mail, Phone, BookOpen, AlertCircle, CheckCircle } from 'lucide-react';

export default function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    fullName: '',
    studentId: '',
    email: '',
    phone: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState({});
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (validationErrors[e.target.name]) {
      setValidationErrors({ ...validationErrors, [e.target.name]: null });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setValidationErrors({});
    setLoading(true);

    try {
      const response = await authService.register(formData);
      if (response && response.token && response.user) {
        setSuccess(true);
        login(response.user, response.token);
        setTimeout(() => navigate('/dashboard'), 1000);
      } else {
        setSuccess(true);
        setTimeout(() => navigate('/login'), 1200);
      }
    } catch (err) {
      setError(err.message || 'Registration failed. Please check your details.');
      if (err.errors) {
        setValidationErrors(err.errors);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: 500, padding: '3rem 1rem' }}>
      <div className="cirms-card" style={{ padding: '2.5rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{
            width: 48,
            height: 48,
            background: 'var(--primary-light)',
            color: 'var(--primary-color)',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1rem'
          }}>
            <Shield size={26} />
          </div>
          <h2 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Create Account</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Join the CIRMS campus recovery network</p>
        </div>

        {error && (
          <div style={{
            background: '#fef2f2',
            border: '1px solid #fecaca',
            color: '#b91c1c',
            padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-sm)',
            marginBottom: '1.5rem',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            fontSize: '0.85rem'
          }}>
            <AlertCircle size={16} /> {error}
          </div>
        )}

        {success && (
          <div style={{
            background: '#ecfdf5',
            border: '1px solid #a7f3d0',
            color: '#065f46',
            padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-sm)',
            marginBottom: '1.5rem',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            fontSize: '0.85rem'
          }}>
            <CheckCircle size={16} /> Account created successfully! Logging you in...
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              Full Name *
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                name="fullName"
                required
                value={formData.fullName}
                onChange={handleChange}
                placeholder="Jane Doe"
                style={{
                  width: '100%',
                  padding: '0.7rem 1rem 0.7rem 2.5rem',
                  border: `1px solid ${validationErrors.fullName ? '#ef4444' : 'var(--border-color)'}`,
                  borderRadius: 'var(--radius-sm)',
                  fontSize: '0.9rem'
                }}
              />
              <User size={16} color="#94a3b8" style={{ position: 'absolute', left: '0.8rem', top: '50%', transform: 'translateY(-50%)' }} />
            </div>
            {validationErrors.fullName && (
              <span style={{ color: '#ef4444', fontSize: '0.75rem' }}>{validationErrors.fullName}</span>
            )}
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              Student / Staff ID *
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                name="studentId"
                required
                value={formData.studentId}
                onChange={handleChange}
                placeholder="BT21CSE042"
                style={{
                  width: '100%',
                  padding: '0.7rem 1rem 0.7rem 2.5rem',
                  border: `1px solid ${validationErrors.studentId ? '#ef4444' : 'var(--border-color)'}`,
                  borderRadius: 'var(--radius-sm)',
                  fontSize: '0.9rem'
                }}
              />
              <BookOpen size={16} color="#94a3b8" style={{ position: 'absolute', left: '0.8rem', top: '50%', transform: 'translateY(-50%)' }} />
            </div>
            {validationErrors.studentId && (
              <span style={{ color: '#ef4444', fontSize: '0.75rem' }}>{validationErrors.studentId}</span>
            )}
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              Campus Email *
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="email"
                name="email"
                required
                value={formData.email}
                onChange={handleChange}
                placeholder="jane.doe@campus.edu"
                style={{
                  width: '100%',
                  padding: '0.7rem 1rem 0.7rem 2.5rem',
                  border: `1px solid ${validationErrors.email ? '#ef4444' : 'var(--border-color)'}`,
                  borderRadius: 'var(--radius-sm)',
                  fontSize: '0.9rem'
                }}
              />
              <Mail size={16} color="#94a3b8" style={{ position: 'absolute', left: '0.8rem', top: '50%', transform: 'translateY(-50%)' }} />
            </div>
            {validationErrors.email && (
              <span style={{ color: '#ef4444', fontSize: '0.75rem' }}>{validationErrors.email}</span>
            )}
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              Phone Number *
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="tel"
                name="phone"
                required
                value={formData.phone}
                onChange={handleChange}
                placeholder="+91 98765 43210"
                style={{
                  width: '100%',
                  padding: '0.7rem 1rem 0.7rem 2.5rem',
                  border: `1px solid ${validationErrors.phone ? '#ef4444' : 'var(--border-color)'}`,
                  borderRadius: 'var(--radius-sm)',
                  fontSize: '0.9rem'
                }}
              />
              <Phone size={16} color="#94a3b8" style={{ position: 'absolute', left: '0.8rem', top: '50%', transform: 'translateY(-50%)' }} />
            </div>
            {validationErrors.phone && (
              <span style={{ color: '#ef4444', fontSize: '0.75rem' }}>{validationErrors.phone}</span>
            )}
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              Password *
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="password"
                name="password"
                required
                minLength={6}
                value={formData.password}
                onChange={handleChange}
                placeholder="At least 6 characters"
                style={{
                  width: '100%',
                  padding: '0.7rem 1rem 0.7rem 2.5rem',
                  border: `1px solid ${validationErrors.password ? '#ef4444' : 'var(--border-color)'}`,
                  borderRadius: 'var(--radius-sm)',
                  fontSize: '0.9rem'
                }}
              />
              <Lock size={16} color="#94a3b8" style={{ position: 'absolute', left: '0.8rem', top: '50%', transform: 'translateY(-50%)' }} />
            </div>
            {validationErrors.password && (
              <span style={{ color: '#ef4444', fontSize: '0.75rem' }}>{validationErrors.password}</span>
            )}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary"
            style={{ width: '100%', padding: '0.8rem', marginTop: '0.5rem' }}
          >
            {loading ? 'Creating Account...' : 'Register'}
          </button>
        </form>

        <p style={{ textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2rem' }}>
          Already have an account? <Link to="/login" style={{ fontWeight: 600 }}>Sign in</Link>
        </p>
      </div>
    </div>
  );
}
