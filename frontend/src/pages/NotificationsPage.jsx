import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { notificationService } from '../services/notificationService';
import { Bell, CheckCheck, Info, ShieldCheck, Package, MessageSquare, AlertCircle } from 'lucide-react';

const TYPE_ICONS = {
  CLAIM_APPROVED: ShieldCheck,
  CLAIM_REJECTED: AlertCircle,
  CLAIM_SUBMITTED: MessageSquare,
  ITEM_MATCH: Package,
  INFO: Info,
};

const TYPE_COLORS = {
  CLAIM_APPROVED: '#10b981',
  CLAIM_REJECTED: '#ef4444',
  CLAIM_SUBMITTED: '#f59e0b',
  ITEM_MATCH: '#2563eb',
  INFO: '#6b7280',
};

export default function NotificationsPage() {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [markingAll, setMarkingAll] = useState(false);

  useEffect(() => {
    if (user?.id) fetchNotifications();
  }, [user]);

  const fetchNotifications = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await notificationService.getUserNotifications(user.id);
      setNotifications(data.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load notifications.');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkRead = async (notifId) => {
    try {
      await notificationService.markAsRead(notifId, user.id);
      setNotifications(prev =>
        prev.map(n => n.id === notifId ? { ...n, isRead: true, read: true } : n)
      );
    } catch (err) {
      console.error('Failed to mark as read', err);
    }
  };

  const handleMarkAllRead = async () => {
    setMarkingAll(true);
    try {
      await notificationService.markAllAsRead(user.id);
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true, read: true })));
    } catch (err) {
      console.error('Failed to mark all as read', err);
    } finally {
      setMarkingAll(false);
    }
  };

  const unreadCount = notifications.filter(n => !n.isRead && !n.read).length;

  return (
    <div className="container" style={{ padding: '3rem 1rem', maxWidth: 720 }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.25rem', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <Bell size={28} color="var(--primary-color)" />
            Notifications
            {unreadCount > 0 && (
              <span style={{
                background: '#ef4444', color: '#fff', fontSize: '0.75rem',
                fontWeight: 700, padding: '2px 8px', borderRadius: '999px'
              }}>{unreadCount}</span>
            )}
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Stay updated on claim decisions and item matches
          </p>
        </div>
        {unreadCount > 0 && (
          <button
            className="btn btn-outline"
            style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem' }}
            onClick={handleMarkAllRead}
            disabled={markingAll}
          >
            <CheckCheck size={16} /> {markingAll ? 'Marking...' : 'Mark all as read'}
          </button>
        )}
      </div>

      {/* Error */}
      {error && (
        <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 'var(--radius-md)', padding: '1rem', marginBottom: '1.5rem', color: '#dc2626', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {/* Loading */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
          <div style={{ width: 40, height: 40, border: '3px solid var(--border-color)', borderTopColor: 'var(--primary-color)', borderRadius: '50%', animation: 'spin 0.8s linear infinite', margin: '0 auto 1rem' }} />
          Loading notifications...
        </div>
      ) : notifications.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '5rem 2rem', color: 'var(--text-muted)' }}>
          <Bell size={64} color="#cbd5e1" style={{ marginBottom: '1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>No notifications yet</h3>
          <p style={{ fontSize: '0.9rem' }}>
            You'll receive updates when your claims are reviewed or items match your reports.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          {notifications.map(notif => {
            const isUnread = !notif.isRead && !notif.read;
            const iconType = notif.type || 'INFO';
            const IconComp = TYPE_ICONS[iconType] || Info;
            const iconColor = TYPE_COLORS[iconType] || '#6b7280';

            return (
              <div
                key={notif.id}
                onClick={() => isUnread && handleMarkRead(notif.id)}
                style={{
                  display: 'flex', gap: '1rem', alignItems: 'flex-start',
                  padding: '1.25rem',
                  borderRadius: 'var(--radius-md)',
                  border: '1.5px solid',
                  borderColor: isUnread ? iconColor + '44' : 'var(--border-color)',
                  background: isUnread ? iconColor + '08' : 'var(--card-bg)',
                  cursor: isUnread ? 'pointer' : 'default',
                  transition: 'all 0.2s',
                }}
              >
                {/* Icon */}
                <div style={{
                  width: 40, height: 40, borderRadius: '10px', flexShrink: 0,
                  background: iconColor + '18', display: 'flex', alignItems: 'center', justifyContent: 'center'
                }}>
                  <IconComp size={20} color={iconColor} />
                </div>

                {/* Content */}
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '0.5rem', flexWrap: 'wrap' }}>
                    <h4 style={{ fontSize: '0.95rem', fontWeight: isUnread ? 700 : 600, marginBottom: '4px' }}>
                      {notif.title}
                    </h4>
                    {isUnread && (
                      <span style={{
                        width: 8, height: 8, borderRadius: '50%',
                        background: '#2563eb', flexShrink: 0, marginTop: 6
                      }} />
                    )}
                  </div>
                  <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', lineHeight: 1.5, marginBottom: '6px' }}>
                    {notif.message}
                  </p>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    {notif.createdAt ? new Date(notif.createdAt).toLocaleString('en-IN', {
                      day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
                    }) : ''}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      )}

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
