import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { User, Mail, Phone, BookOpen, Shield, Calendar } from 'lucide-react';

export default function ProfilePage() {
  const { user: authUser } = useAuth();
  const [profile, setProfile] = useState(authUser);
  const [loading, setLoading] = useState(!authUser);

  useEffect(() => {
    authService.getCurrentUser()
      .then((data) => {
        setProfile(data);
      })
      .catch((err) => {
        console.error('Failed to load profile:', err);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="container" style={{ padding: '4rem 1rem', textAlign: 'center' }}>
        <p style={{ color: 'var(--text-muted)' }}>Loading profile details...</p>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: 640, padding: '3rem 1rem' }}>
      <div className="cirms-card" style={{ padding: '2.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', marginBottom: '2rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1.5rem' }}>
          <div style={{
            width: 64,
            height: 64,
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #2563eb, #1e40af)',
            color: '#fff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '1.75rem',
            fontWeight: 700
          }}>
            {profile?.fullName ? profile.fullName.charAt(0).toUpperCase() : 'U'}
          </div>

          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <h1 style={{ fontSize: '1.5rem', marginBottom: '0.2rem' }}>{profile?.fullName || 'User Profile'}</h1>
              <span className={`badge ${profile?.role === 'ADMIN' ? 'badge-lost' : 'badge-approved'}`}>
                {profile?.role || 'STUDENT'}
              </span>
            </div>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>
              Verified Campus User
            </p>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem', background: '#f8fafc', borderRadius: 'var(--radius-sm)' }}>
            <Mail size={18} color="var(--primary-color)" />
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>Email Address</div>
              <div style={{ fontSize: '0.9rem', fontWeight: 500 }}>{profile?.email}</div>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem', background: '#f8fafc', borderRadius: 'var(--radius-sm)' }}>
            <BookOpen size={18} color="var(--primary-color)" />
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>Student / Staff ID</div>
              <div style={{ fontSize: '0.9rem', fontWeight: 500 }}>{profile?.studentId || 'N/A'}</div>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem', background: '#f8fafc', borderRadius: 'var(--radius-sm)' }}>
            <Phone size={18} color="var(--primary-color)" />
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>Phone Number</div>
              <div style={{ fontSize: '0.9rem', fontWeight: 500 }}>{profile?.phone || 'N/A'}</div>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem', background: '#f8fafc', borderRadius: 'var(--radius-sm)' }}>
            <Shield size={18} color="var(--primary-color)" />
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>Role Authorization</div>
              <div style={{ fontSize: '0.9rem', fontWeight: 500 }}>{profile?.role}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
