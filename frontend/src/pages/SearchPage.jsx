import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { itemService } from '../services/itemService';
import { Search, Filter, MapPin, Calendar, Tag, ArrowRight, Package, ChevronLeft, ChevronRight, X } from 'lucide-react';

const STATUS_BADGE = {
  LOST:           { label: 'LOST',           bg: '#fee2e2', color: '#b91c1c' },
  FOUND:          { label: 'FOUND',          bg: '#dcfce7', color: '#15803d' },
  CLAIM_PENDING:  { label: 'CLAIM PENDING',  bg: '#fef3c7', color: '#b45309' },
  CLAIM_APPROVED: { label: 'CLAIM APPROVED', bg: '#e0e7ff', color: '#4338ca' },
  RECOVERED:      { label: 'RECOVERED',      bg: '#d1fae5', color: '#047857' },
};

function ItemSkeleton() {
  return (
    <div className="cirms-card" style={{ display: 'flex', flexDirection: 'column' }}>
      <div className="skeleton skeleton-image" style={{ height: 180, marginBottom: '1rem' }} />
      <div className="skeleton skeleton-text" style={{ width: '60%', marginBottom: '8px' }} />
      <div className="skeleton skeleton-title" style={{ width: '80%', marginBottom: '12px' }} />
      <div className="skeleton skeleton-text" style={{ width: '90%', marginBottom: '6px' }} />
      <div className="skeleton skeleton-text" style={{ width: '50%' }} />
    </div>
  );
}

export default function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [items, setItems]       = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading]   = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [filtersOpen, setFiltersOpen] = useState(false);

  const [filters, setFilters] = useState({
    query:      searchParams.get('query')    || '',
    type:       searchParams.get('type')     || '',
    status:     searchParams.get('status')   || '',
    categoryId: searchParams.get('category') || '',
    location:   searchParams.get('location') || '',
    color:      searchParams.get('color')    || '',
    brand:      searchParams.get('brand')    || '',
    page: 0,
    size: 9,
  });

  useEffect(() => {
    itemService.getCategories()
      .then((res) => setCategories(res.data || []))
      .catch(() => {});
  }, []);

  useEffect(() => { fetchItems(); }, [filters]);

  const fetchItems = async () => {
    setLoading(true);
    try {
      const cleanParams = Object.fromEntries(
        Object.entries(filters).filter(([_, v]) => v !== '' && v !== null)
      );
      const res = await itemService.searchItems(cleanParams);
      if (res.data) {
        setItems(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const setFilter = (key, value) => setFilters(f => ({ ...f, [key]: value, page: 0 }));

  const clearFilters = () => setFilters(f => ({
    ...f, query: '', type: '', status: '', categoryId: '', location: '', color: '', brand: '', page: 0
  }));

  const hasActiveFilters = [filters.query, filters.type, filters.status, filters.categoryId, filters.location, filters.color, filters.brand].some(v => v);

  return (
    <div className="container" style={{ padding: '2.5rem 1rem 4rem' }}>

      {/* Page header */}
      <div className="page-header animate-fade-in">
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '0.35rem' }}>
          <div style={{ background: 'linear-gradient(135deg, var(--primary-color), var(--purple-color))', padding: '8px', borderRadius: '10px', color: '#fff' }}>
            <Search size={18} />
          </div>
          <h1 style={{ margin: 0 }}>Campus Item Search</h1>
        </div>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.92rem' }}>
          Search and filter reported lost & found items across campus
          {totalElements > 0 && !loading && <span style={{ marginLeft: '8px', fontWeight: 600, color: 'var(--text-secondary)' }}>— {totalElements} item{totalElements !== 1 ? 's' : ''} found</span>}
        </p>
      </div>

      {/* Search bar + filter toggle */}
      <div className="cirms-card animate-fade-in" style={{ marginBottom: '1.5rem', padding: '1.25rem 1.5rem' }}>
        <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: 200, position: 'relative' }}>
            <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', pointerEvents: 'none' }} />
            <input
              type="text"
              className="form-input"
              value={filters.query}
              onChange={e => setFilter('query', e.target.value)}
              placeholder="Search items — laptop, wallet, AirPods..."
              style={{ paddingLeft: '2.5rem' }}
            />
          </div>
          <select
            className="form-select"
            value={filters.type}
            onChange={e => setFilter('type', e.target.value)}
            style={{ width: 140 }}
          >
            <option value="">All Types</option>
            <option value="LOST">Lost Items</option>
            <option value="FOUND">Found Items</option>
          </select>
          <button
            className={`btn ${filtersOpen ? 'btn-primary' : 'btn-outline'}`}
            style={{ display: 'flex', alignItems: 'center', gap: '6px', whiteSpace: 'nowrap' }}
            onClick={() => setFiltersOpen(v => !v)}
          >
            <Filter size={15} /> Filters
            {hasActiveFilters && <span style={{ background: 'rgba(255,255,255,0.25)', borderRadius: '9999px', width: 18, height: 18, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.7rem', fontWeight: 700 }}>●</span>}
          </button>
          {hasActiveFilters && (
            <button
              className="btn btn-ghost btn-sm"
              onClick={clearFilters}
              style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--danger-color)' }}
            >
              <X size={14} /> Clear
            </button>
          )}
        </div>

        {/* Advanced filters */}
        {filtersOpen && (
          <div style={{
            marginTop: '1.25rem',
            paddingTop: '1.25rem',
            borderTop: '1px solid var(--border-color)',
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
            gap: '1rem',
            animation: 'fadeIn 0.2s ease',
          }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Category</label>
              <select className="form-select" value={filters.categoryId} onChange={e => setFilter('categoryId', e.target.value)}>
                <option value="">All Categories</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Location</label>
              <input
                type="text"
                className="form-input"
                value={filters.location}
                onChange={e => setFilter('location', e.target.value)}
                placeholder="Library, Canteen..."
              />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Color</label>
              <input
                type="text"
                className="form-input"
                value={filters.color}
                onChange={e => setFilter('color', e.target.value)}
                placeholder="Black, Blue..."
              />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Brand</label>
              <input
                type="text"
                className="form-input"
                value={filters.brand}
                onChange={e => setFilter('brand', e.target.value)}
                placeholder="Apple, Samsung..."
              />
            </div>
          </div>
        )}
      </div>

      {/* Active filter chips */}
      {hasActiveFilters && !filtersOpen && (
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '1.25rem', animation: 'fadeIn 0.2s ease' }}>
          {[
            { key: 'query', label: `"${filters.query}"` },
            { key: 'type', label: filters.type },
            { key: 'location', label: `📍 ${filters.location}` },
            { key: 'color', label: `🎨 ${filters.color}` },
            { key: 'brand', label: `🏷 ${filters.brand}` },
          ].filter(f => filters[f.key]).map(f => (
            <span key={f.key} style={{
              display: 'inline-flex', alignItems: 'center', gap: '5px',
              background: 'var(--primary-light)', color: 'var(--primary-color)',
              padding: '3px 10px', borderRadius: '9999px', fontSize: '0.78rem', fontWeight: 600,
            }}>
              {f.label}
              <button onClick={() => setFilter(f.key, '')} style={{ background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', color: 'var(--primary-color)', padding: 0 }}>
                <X size={12} />
              </button>
            </span>
          ))}
        </div>
      )}

      {/* Results */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(290px, 1fr))', gap: '1.5rem' }}>
          {[...Array(6)].map((_, i) => <ItemSkeleton key={i} />)}
        </div>
      ) : items.length === 0 ? (
        <div className="empty-state cirms-card">
          <Package size={56} color="#cbd5e1" />
          <h3>No Items Found</h3>
          <p>Try adjusting your search filters or {' '}
            <Link to="/report/lost" style={{ fontWeight: 600 }}>report a lost item</Link>.
          </p>
          {hasActiveFilters && (
            <button className="btn btn-outline" onClick={clearFilters} style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
              <X size={14} /> Clear all filters
            </button>
          )}
        </div>
      ) : (
        <>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(290px, 1fr))',
            gap: '1.25rem',
            marginBottom: '2.5rem',
          }}>
            {items.map((item, i) => {
              const statusStyle = STATUS_BADGE[item.status] || { label: item.status, bg: '#f1f5f9', color: '#475569' };
              return (
                <div
                  key={item.id}
                  className="cirms-card animate-fade-in-up"
                  style={{ display: 'flex', flexDirection: 'column', animationDelay: `${i * 50}ms` }}
                >
                  {/* Image */}
                  <div style={{
                    height: 188,
                    background: '#f1f5f9',
                    borderRadius: 'var(--radius-sm)',
                    marginBottom: '1rem',
                    overflow: 'hidden',
                    position: 'relative',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    {item.imageUrl
                      ? <img src={item.imageUrl} alt={item.itemName} style={{ width: '100%', height: '100%', objectFit: 'cover', transition: 'transform 0.3s ease' }}
                          onMouseEnter={e => e.target.style.transform = 'scale(1.04)'}
                          onMouseLeave={e => e.target.style.transform = 'scale(1)'}
                        />
                      : <Package size={48} color="#cbd5e1" />
                    }
                    <div style={{ position: 'absolute', top: 8, right: 8 }}>
                      <span style={{ fontSize: '0.68rem', fontWeight: 700, padding: '3px 8px', borderRadius: '9999px', background: statusStyle.bg, color: statusStyle.color }}>
                        {statusStyle.label}
                      </span>
                    </div>
                    {item.itemType && (
                      <div style={{ position: 'absolute', top: 8, left: 8 }}>
                        <span style={{ fontSize: '0.68rem', fontWeight: 700, padding: '3px 8px', borderRadius: '9999px', background: item.itemType === 'LOST' ? 'rgba(239,68,68,0.9)' : 'rgba(16,185,129,0.9)', color: '#fff' }}>
                          {item.itemType}
                        </span>
                      </div>
                    )}
                  </div>

                  {/* Content */}
                  <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                    {item.categoryName && (
                      <div style={{ display: 'flex', alignItems: 'center', gap: '5px', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.3rem', fontWeight: 500 }}>
                        <Tag size={12} /> {item.categoryName}
                      </div>
                    )}
                    <h3 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '0.4rem', color: '#0f172a', lineHeight: 1.3 }}>{item.itemName}</h3>
                    <p style={{
                      fontSize: '0.82rem', color: 'var(--text-muted)', marginBottom: '1rem',
                      display: '-webkit-box', WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical', overflow: 'hidden', lineHeight: 1.55,
                    }}>
                      {item.description || <em>No description provided</em>}
                    </p>

                    <div style={{ marginTop: 'auto', paddingTop: '0.875rem', borderTop: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', display: 'flex', flexDirection: 'column', gap: '2px' }}>
                        {item.location && (
                          <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}><MapPin size={12} /> {item.location}</span>
                        )}
                        {item.dateLostOrFound && (
                          <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}><Calendar size={12} /> {item.dateLostOrFound}</span>
                        )}
                      </div>
                      <Link
                        to={`/items/${item.id}`}
                        className="btn btn-outline btn-sm"
                        style={{ display: 'flex', alignItems: 'center', gap: '4px' }}
                      >
                        View <ArrowRight size={13} />
                      </Link>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
              <button
                className="btn btn-outline btn-sm"
                disabled={filters.page === 0}
                onClick={() => setFilters(f => ({ ...f, page: f.page - 1 }))}
                style={{ display: 'flex', alignItems: 'center', gap: '4px' }}
              >
                <ChevronLeft size={15} /> Prev
              </button>
              {[...Array(totalPages)].map((_, i) => (
                <button
                  key={i}
                  className={`btn btn-sm ${filters.page === i ? 'btn-primary' : 'btn-outline'}`}
                  onClick={() => setFilters(f => ({ ...f, page: i }))}
                  style={{ minWidth: 36 }}
                >
                  {i + 1}
                </button>
              ))}
              <button
                className="btn btn-outline btn-sm"
                disabled={filters.page >= totalPages - 1}
                onClick={() => setFilters(f => ({ ...f, page: f.page + 1 }))}
                style={{ display: 'flex', alignItems: 'center', gap: '4px' }}
              >
                Next <ChevronRight size={15} />
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
