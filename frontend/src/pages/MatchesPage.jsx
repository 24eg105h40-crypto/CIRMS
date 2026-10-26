import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { matchingService } from '../services/matchingService';
import {
  Zap, Package, MapPin, Calendar, Tag, ChevronRight,
  AlertCircle, RefreshCw, TrendingUp, Loader2
} from 'lucide-react';

// Score thresholds → badge colors
const getScoreBadge = (score) => {
  if (score >= 70) return { bg: '#ecfdf5', color: '#059669', label: 'High Match' };
  if (score >= 50) return { bg: '#eff6ff', color: '#2563eb', label: 'Good Match' };
  if (score >= 35) return { bg: '#fffbeb', color: '#d97706', label: 'Possible Match' };
  return { bg: '#f9fafb', color: '#6b7280', label: 'Weak Match' };
};

// Circular score gauge
function ScoreGauge({ score }) {
  const { bg, color } = getScoreBadge(score);
  const radius = 26;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;

  return (
    <div style={{ position: 'relative', width: 72, height: 72, flexShrink: 0 }}>
      <svg width="72" height="72" style={{ transform: 'rotate(-90deg)' }}>
        <circle cx="36" cy="36" r={radius} fill="none" stroke="#f1f5f9" strokeWidth="6" />
        <circle
          cx="36" cy="36" r={radius}
          fill="none" stroke={color} strokeWidth="6"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          style={{ transition: 'stroke-dashoffset 0.6s ease' }}
        />
      </svg>
      <div style={{
        position: 'absolute', inset: 0,
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
      }}>
        <span style={{ fontSize: '1rem', fontWeight: 800, color, lineHeight: 1 }}>{score}</span>
        <span style={{ fontSize: '0.55rem', color: '#94a3b8', fontWeight: 600 }}>/ 100</span>
      </div>
    </div>
  );
}

// Attribute score pill
function ScorePill({ label, value, max }) {
  if (!value) return null;
  const pct = Math.round((value / max) * 100);
  const filled = pct >= 100;
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: '4px',
      fontSize: '0.72rem', fontWeight: 600, padding: '2px 8px', borderRadius: '999px',
      background: filled ? '#ecfdf5' : '#f1f5f9',
      color: filled ? '#059669' : '#64748b',
    }}>
      {filled ? '✓' : '~'} {label}
    </span>
  );
}

export default function MatchesPage() {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMatches();
  }, []);

  const fetchMatches = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await matchingService.getMyMatches();
      setMatches(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load matches. Make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '3rem 1rem', maxWidth: 900 }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '0.4rem' }}>
            <div style={{ background: 'linear-gradient(135deg, #2563eb, #7c3aed)', padding: '8px', borderRadius: '10px', color: '#fff' }}>
              <Zap size={20} />
            </div>
            <h1 style={{ fontSize: '2rem', margin: 0 }}>Smart Matches</h1>
          </div>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            AI-powered matching across your lost &amp; found reports — scored by category, name, color, brand, location, and date.
          </p>
        </div>
        <button
          className="btn btn-outline"
          style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem' }}
          onClick={fetchMatches}
          disabled={loading}
        >
          <RefreshCw size={15} /> Refresh
        </button>
      </div>

      {/* Score legend */}
      <div style={{ display: 'flex', gap: '0.6rem', marginBottom: '1.75rem', flexWrap: 'wrap' }}>
        {[
          { min: 70, label: 'High Match (70+)', bg: '#ecfdf5', color: '#059669' },
          { min: 50, label: 'Good Match (50–69)', bg: '#eff6ff', color: '#2563eb' },
          { min: 35, label: 'Possible Match (35–49)', bg: '#fffbeb', color: '#d97706' },
        ].map(item => (
          <span key={item.min} style={{
            fontSize: '0.78rem', fontWeight: 600, padding: '4px 12px',
            borderRadius: '999px', background: item.bg, color: item.color
          }}>
            {item.label}
          </span>
        ))}
      </div>

      {/* Error */}
      {error && (
        <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 'var(--radius-md)', padding: '1rem', marginBottom: '1.5rem', color: '#dc2626', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {/* Loading */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '5rem', color: 'var(--text-muted)' }}>
          <Loader2 size={40} color="var(--primary-color)" style={{ animation: 'spin 1s linear infinite', marginBottom: '1rem' }} />
          <p>Running matching algorithm across your reports...</p>
        </div>
      ) : matches.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '5rem 2rem', color: 'var(--text-muted)' }}>
          <TrendingUp size={64} color="#cbd5e1" style={{ marginBottom: '1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>No potential matches yet</h3>
          <p style={{ fontSize: '0.9rem', maxWidth: 400, margin: '0 auto 1.5rem' }}>
            Matches appear when a found item closely resembles your lost report (or vice versa) based on category, name, color, brand, location, and date.
          </p>
          <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center' }}>
            <Link to="/report/lost" className="btn btn-outline">Report Lost Item</Link>
            <Link to="/search" className="btn btn-primary">Browse Found Items</Link>
          </div>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {matches.map(match => {
            const badge = getScoreBadge(match.score);
            return (
              <div key={match.matchedItemId} className="cirms-card" style={{ padding: '1.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: '1.25rem', flexWrap: 'wrap' }}>

                  {/* Score Gauge */}
                  <ScoreGauge score={match.score} />

                  {/* Item Thumbnail */}
                  <div style={{
                    width: 72, height: 72, borderRadius: 'var(--radius-md)',
                    background: '#f1f5f9', flexShrink: 0,
                    overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center'
                  }}>
                    {match.matchedItemImageUrl ? (
                      <img src={match.matchedItemImageUrl} alt={match.matchedItemName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    ) : (
                      <Package size={28} color="#94a3b8" />
                    )}
                  </div>

                  {/* Details */}
                  <div style={{ flex: 1, minWidth: 200 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px', flexWrap: 'wrap' }}>
                      <h3 style={{ fontSize: '1rem', fontWeight: 700, margin: 0 }}>
                        {match.matchedItemName}
                      </h3>
                      <span style={{
                        fontSize: '0.7rem', fontWeight: 700, padding: '2px 8px', borderRadius: '999px',
                        background: match.matchedItemType === 'FOUND' ? '#ecfdf5' : '#fef2f2',
                        color: match.matchedItemType === 'FOUND' ? '#059669' : '#dc2626',
                      }}>
                        {match.matchedItemType}
                      </span>
                      <span style={{
                        fontSize: '0.7rem', fontWeight: 700, padding: '2px 8px', borderRadius: '999px',
                        background: badge.bg, color: badge.color,
                      }}>
                        {badge.label}
                      </span>
                    </div>

                    <div style={{ display: 'flex', gap: '1rem', color: 'var(--text-muted)', fontSize: '0.82rem', flexWrap: 'wrap', marginBottom: '0.75rem' }}>
                      {match.matchedItemCategory && (
                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                          <Tag size={12} /> {match.matchedItemCategory}
                        </span>
                      )}
                      {match.matchedItemLocation && (
                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                          <MapPin size={12} /> {match.matchedItemLocation}
                        </span>
                      )}
                      {match.matchedItemDate && (
                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                          <Calendar size={12} /> {match.matchedItemDate}
                        </span>
                      )}
                    </div>

                    {/* Attribute score pills */}
                    <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap', marginBottom: '0.75rem' }}>
                      <ScorePill label="Category" value={match.categoryScore} max={30} />
                      <ScorePill label="Name"     value={match.nameScore}     max={25} />
                      <ScorePill label="Color"    value={match.colorScore}    max={15} />
                      <ScorePill label="Brand"    value={match.brandScore}    max={15} />
                      <ScorePill label="Location" value={match.locationScore} max={10} />
                      <ScorePill label="Date"     value={match.dateScore}     max={5}  />
                    </div>

                    <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>
                      {match.matchSummary}
                    </p>
                  </div>

                  {/* View Action */}
                  <Link
                    to={`/items/${match.matchedItemId}`}
                    className="btn btn-primary"
                    style={{ alignSelf: 'center', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem', whiteSpace: 'nowrap' }}
                  >
                    View Item <ChevronRight size={14} />
                  </Link>
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
