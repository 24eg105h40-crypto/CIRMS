import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { itemService } from '../services/itemService';
import { MapPin, Calendar, Tag, Trash2, Eye, Package, PlusCircle, AlertCircle, CheckCircle, Clock } from 'lucide-react';

const STATUS_STYLES = {
  LOST:           { color: '#ef4444', bg: '#fef2f2', label: 'LOST' },
  FOUND:          { color: '#10b981', bg: '#ecfdf5', label: 'FOUND' },
  CLAIM_PENDING:  { color: '#f59e0b', bg: '#fffbeb', label: 'CLAIM PENDING' },
  CLAIM_APPROVED: { color: '#2563eb', bg: '#eff6ff', label: 'RECOVERED' },
  CLAIM_REJECTED: { color: '#6b7280', bg: '#f9fafb', label: 'REJECTED' },
};

export default function MyReportsPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    fetchMyReports();
  }, []);

  const fetchMyReports = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await itemService.getMyReports();
      setItems(data.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load your reports.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    setDeleting(true);
    try {
      await itemService.deleteItem(id);
      setItems(prev => prev.filter(i => i.id !== id));
      setDeleteConfirm(null);
    } catch (err) {
      alert(err.message || 'Failed to delete item.');
    } finally {
      setDeleting(false);
    }
  };

  const filtered = filter === 'ALL' ? items : items.filter(i => i.itemType === filter);

  return (
    <div className="container" style={{ padding: '3rem 1rem' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>My Reports</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            Manage your lost and found item reports
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <Link to="/report/lost" className="btn btn-outline" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <PlusCircle size={15} /> Report Lost
          </Link>
          <Link to="/report/found" className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <PlusCircle size={15} /> Report Found
          </Link>
        </div>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem' }}>
        {['ALL', 'LOST', 'FOUND'].map(tab => (
          <button
            key={tab}
            onClick={() => setFilter(tab)}
            style={{
              padding: '6px 16px',
              borderRadius: '999px',
              border: '1.5px solid',
              borderColor: filter === tab ? 'var(--primary-color)' : 'var(--border-color)',
              background: filter === tab ? 'var(--primary-color)' : 'transparent',
              color: filter === tab ? '#fff' : 'var(--text-muted)',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer',
              transition: 'all 0.2s',
            }}
          >
            {tab === 'ALL' ? `All (${items.length})` : `${tab} (${items.filter(i => i.itemType === tab).length})`}
          </button>
        ))}
      </div>

      {/* Error */}
      {error && (
        <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 'var(--radius-md)', padding: '1rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '8px', color: '#dc2626' }}>
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {/* Loading */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
          <div style={{ width: 40, height: 40, border: '3px solid var(--border-color)', borderTopColor: 'var(--primary-color)', borderRadius: '50%', animation: 'spin 0.8s linear infinite', margin: '0 auto 1rem' }} />
          Loading your reports...
        </div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '5rem 2rem', color: 'var(--text-muted)' }}>
          <Package size={64} color="#cbd5e1" style={{ marginBottom: '1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>No {filter !== 'ALL' ? filter.toLowerCase() : ''} reports yet</h3>
          <p style={{ marginBottom: '1.5rem', fontSize: '0.9rem' }}>
            Start by reporting a lost or found item on campus.
          </p>
          <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center' }}>
            <Link to="/report/lost" className="btn btn-outline">Report Lost Item</Link>
            <Link to="/report/found" className="btn btn-primary">Report Found Item</Link>
          </div>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {filtered.map(item => {
            const s = STATUS_STYLES[item.status] || STATUS_STYLES.LOST;
            const isLost = item.itemType === 'LOST';
            return (
              <div key={item.id} className="cirms-card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', padding: '1.25rem', position: 'relative' }}>
                {/* Image */}
                <div style={{
                  width: 80, height: 80, borderRadius: 'var(--radius-md)',
                  background: '#f1f5f9', flexShrink: 0,
                  overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center'
                }}>
                  {item.imageUrl ? (
                    <img src={item.imageUrl} alt={item.itemName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  ) : (
                    <Package size={32} color="#94a3b8" />
                  )}
                </div>

                {/* Details */}
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px', flexWrap: 'wrap' }}>
                    <span style={{
                      fontSize: '0.72rem', fontWeight: 700, padding: '2px 8px', borderRadius: '999px',
                      background: isLost ? '#fef2f2' : '#ecfdf5',
                      color: isLost ? '#dc2626' : '#059669'
                    }}>{item.itemType}</span>
                    <span style={{
                      fontSize: '0.72rem', fontWeight: 700, padding: '2px 8px', borderRadius: '999px',
                      background: s.bg, color: s.color
                    }}>{s.label}</span>
                  </div>
                  <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '4px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {item.itemName}
                  </h3>
                  <div style={{ display: 'flex', gap: '1rem', color: 'var(--text-muted)', fontSize: '0.8rem', flexWrap: 'wrap' }}>
                    {item.location && (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <MapPin size={12} /> {item.location}
                      </span>
                    )}
                    {item.dateLostOrFound && (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <Calendar size={12} /> {item.dateLostOrFound}
                      </span>
                    )}
                    {item.categoryName && (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <Tag size={12} /> {item.categoryName}
                      </span>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div style={{ display: 'flex', gap: '0.5rem', flexShrink: 0 }}>
                  <Link
                    to={`/items/${item.id}`}
                    className="btn btn-outline"
                    style={{ padding: '6px 12px', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                  >
                    <Eye size={14} /> View
                  </Link>
                  <button
                    onClick={() => setDeleteConfirm(item)}
                    style={{
                      padding: '6px 12px', borderRadius: 'var(--radius-sm)',
                      border: '1.5px solid #fecaca', background: '#fef2f2',
                      color: '#dc2626', cursor: 'pointer', fontSize: '0.8rem',
                      display: 'flex', alignItems: 'center', gap: '4px',
                    }}
                  >
                    <Trash2 size={14} /> Delete
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Delete Confirm Modal */}
      {deleteConfirm && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '1rem'
        }}>
          <div className="cirms-card" style={{ maxWidth: 420, width: '100%', padding: '2rem' }}>
            <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
              <div style={{ width: 56, height: 56, background: '#fef2f2', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem' }}>
                <Trash2 size={24} color="#dc2626" />
              </div>
              <h3 style={{ marginBottom: '0.5rem' }}>Delete Report?</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                Are you sure you want to delete <strong>"{deleteConfirm.itemName}"</strong>? This action cannot be undone.
              </p>
            </div>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <button className="btn btn-outline" style={{ flex: 1 }} onClick={() => setDeleteConfirm(null)}>
                Cancel
              </button>
              <button
                className="btn btn-primary"
                style={{ flex: 1, background: '#dc2626', borderColor: '#dc2626' }}
                disabled={deleting}
                onClick={() => handleDelete(deleteConfirm.id)}
              >
                {deleting ? 'Deleting...' : 'Yes, Delete'}
              </button>
            </div>
          </div>
        </div>
      )}

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
}
