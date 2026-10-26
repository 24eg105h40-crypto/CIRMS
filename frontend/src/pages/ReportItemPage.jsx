import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { itemService } from '../services/itemService';
import { useAuth } from '../context/AuthContext';
import {
  PlusCircle, AlertCircle, CheckCircle, Loader2,
  Tag, MapPin, Calendar, Palette, ShoppingBag, FileText,
  ArrowLeft, Info
} from 'lucide-react';

const ACCENT = { LOST: '#ef4444', FOUND: '#10b981' };
const GRADIENT = {
  LOST:  'linear-gradient(135deg, #ef4444, #dc2626)',
  FOUND: 'linear-gradient(135deg, #10b981, #059669)',
};

export default function ReportItemPage({ type = 'LOST' }) {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const [formData, setFormData] = useState({
    categoryId:            '',
    itemName:              '',
    description:           '',
    color:                 '',
    brand:                 '',
    location:              '',
    dateLostOrFound:       new Date().toISOString().split('T')[0],
    timeLostOrFound:       '',
    itemType:              type,
    imageUrl:              '',
    additionalInformation: '',
  });

  useEffect(() => {
    itemService.getCategories()
      .then((res) => {
        setCategories(res.data || []);
        if (res.data?.length > 0) {
          setFormData((prev) => ({ ...prev, categoryId: res.data[0].id }));
        }
      })
      .catch(() => {});
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) { navigate('/login'); return; }
    setError('');
    setLoading(true);
    try {
      const apiCall = type === 'LOST' ? itemService.createLostItem : itemService.createFoundItem;
      const res = await apiCall(formData);
      if (res.success) {
        setSuccess(true);
        setTimeout(() => navigate('/dashboard'), 1800);
      }
    } catch (err) {
      setError(err.message || 'Failed to submit report. Please check all required fields.');
    } finally {
      setLoading(false);
    }
  };

  const accent = ACCENT[type];
  const gradient = GRADIENT[type];

  /* ── Success state ───────────────────────────── */
  if (success) {
    return (
      <div className="container" style={{ maxWidth: 560, padding: '4rem 1rem', textAlign: 'center' }}>
        <div className="cirms-card animate-scale-in" style={{ padding: '3rem 2rem' }}>
          <div style={{
            width: 72, height: 72, borderRadius: '50%', background: '#ecfdf5',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            margin: '0 auto 1.5rem',
          }}>
            <CheckCircle size={36} color="#10b981" />
          </div>
          <h2 style={{ marginBottom: '0.75rem' }}>Report Submitted!</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.92rem', marginBottom: '1.5rem' }}>
            Your {type.toLowerCase()} item report has been submitted. You'll receive a notification if a smart match is found.
          </p>
          <div className="spinner spinner-sm" style={{ margin: '0 auto' }} />
          <p style={{ fontSize: '0.8rem', color: 'var(--text-faint)', marginTop: '0.75rem' }}>Redirecting to dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: 720, padding: '3rem 1rem 4rem' }}>
      {/* Back link */}
      <Link to="/dashboard" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
        <ArrowLeft size={15} /> Back to Dashboard
      </Link>

      <div className="cirms-card animate-fade-in" style={{ padding: 0, overflow: 'hidden' }}>
        {/* Header bar */}
        <div style={{
          background: gradient,
          padding: '2rem 2.5rem',
          color: '#fff',
          position: 'relative',
          overflow: 'hidden',
        }}>
          <div style={{ position: 'absolute', top: -30, right: -30, width: 180, height: 180, borderRadius: '50%', background: 'rgba(255,255,255,0.08)', pointerEvents: 'none' }} />
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', position: 'relative' }}>
            <div style={{ background: 'rgba(255,255,255,0.2)', padding: '10px', borderRadius: '12px', display: 'flex' }}>
              <PlusCircle size={22} />
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', fontWeight: 700, opacity: 0.8, letterSpacing: '0.06em', textTransform: 'uppercase', marginBottom: '2px' }}>
                {type} ITEM REPORT
              </div>
              <h1 style={{ color: '#fff', fontSize: '1.6rem', margin: 0 }}>
                Report a {type === 'LOST' ? 'Lost' : 'Found'} Item
              </h1>
            </div>
          </div>
          <p style={{ color: 'rgba(255,255,255,0.8)', fontSize: '0.875rem', marginTop: '0.5rem', position: 'relative' }}>
            Accurate details improve the chance of recovery through our smart matching algorithm.
          </p>
        </div>

        {/* Form body */}
        <div style={{ padding: '2rem 2.5rem' }}>
          {/* Smart matching tip */}
          <div className="alert alert-info" style={{ marginBottom: '1.75rem' }}>
            <Info size={16} style={{ flexShrink: 0, marginTop: 1 }} />
            <span>
              <strong>Smart Matching Tip:</strong> Fill in <strong>Category</strong>, <strong>Color</strong>, and <strong>Brand</strong> to improve match accuracy — the algorithm uses these for scoring.
            </span>
          </div>

          {error && (
            <div className="alert alert-error" style={{ marginBottom: '1.5rem' }}>
              <AlertCircle size={16} style={{ flexShrink: 0 }} /> {error}
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.35rem' }}>
            {/* Item Name */}
            <div className="form-group">
              <label className="form-label">
                <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                  <FileText size={14} color={accent} />
                  Item Name <span className="form-required">*</span>
                </span>
              </label>
              <input
                type="text"
                name="itemName"
                required
                className="form-input"
                value={formData.itemName}
                onChange={handleChange}
                placeholder="e.g. Dell XPS 15 Laptop, Blue Casio Watch..."
              />
            </div>

            {/* Category + Location */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <Tag size={14} color={accent} />
                    Category <span className="form-required">*</span>
                  </span>
                </label>
                <select
                  name="categoryId"
                  required
                  className="form-select"
                  value={formData.categoryId}
                  onChange={handleChange}
                >
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <MapPin size={14} color={accent} />
                    Location {type === 'LOST' ? 'Lost' : 'Found'} <span className="form-required">*</span>
                  </span>
                </label>
                <input
                  type="text"
                  name="location"
                  required
                  className="form-input"
                  value={formData.location}
                  onChange={handleChange}
                  placeholder="e.g. Central Library, 2nd Floor"
                />
              </div>
            </div>

            {/* Date + Time */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <Calendar size={14} color={accent} />
                    Date {type === 'LOST' ? 'Lost' : 'Found'} <span className="form-required">*</span>
                  </span>
                </label>
                <input
                  type="date"
                  name="dateLostOrFound"
                  required
                  className="form-input"
                  value={formData.dateLostOrFound}
                  onChange={handleChange}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Time (Approximate)</label>
                <input
                  type="text"
                  name="timeLostOrFound"
                  className="form-input"
                  value={formData.timeLostOrFound}
                  onChange={handleChange}
                  placeholder="e.g. 2:30 PM"
                />
              </div>
            </div>

            {/* Color + Brand */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <Palette size={14} color={accent} />
                    Color
                  </span>
                </label>
                <input
                  type="text"
                  name="color"
                  className="form-input"
                  value={formData.color}
                  onChange={handleChange}
                  placeholder="e.g. Matte Black, Silver"
                />
                <span className="form-hint">Improves smart matching accuracy</span>
              </div>
              <div className="form-group">
                <label className="form-label">
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <ShoppingBag size={14} color={accent} />
                    Brand / Manufacturer
                  </span>
                </label>
                <input
                  type="text"
                  name="brand"
                  className="form-input"
                  value={formData.brand}
                  onChange={handleChange}
                  placeholder="e.g. Dell, Apple, Casio"
                />
                <span className="form-hint">Improves smart matching accuracy</span>
              </div>
            </div>

            {/* Description */}
            <div className="form-group">
              <label className="form-label">
                Description <span className="form-required">*</span>
              </label>
              <textarea
                name="description"
                required
                rows={4}
                className="form-textarea"
                value={formData.description}
                onChange={handleChange}
                placeholder="Detailed description — unique marks, scratches, stickers, or distinctive features..."
              />
            </div>

            {/* Additional Info */}
            <div className="form-group">
              <label className="form-label">Additional Information</label>
              <textarea
                name="additionalInformation"
                rows={2}
                className="form-textarea"
                value={formData.additionalInformation}
                onChange={handleChange}
                placeholder="Any other helpful context or notes..."
              />
            </div>

            <hr className="divider" />

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary btn-lg"
              style={{
                width: '100%',
                justifyContent: 'center',
                background: gradient,
                boxShadow: type === 'LOST' ? '0 4px 14px rgba(239,68,68,0.3)' : '0 4px 14px rgba(16,185,129,0.3)',
              }}
            >
              {loading
                ? <><Loader2 size={18} style={{ animation: 'spin 1s linear infinite' }} /> Submitting Report...</>
                : <><PlusCircle size={18} /> Submit {type === 'LOST' ? 'Lost' : 'Found'} Report</>
              }
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
