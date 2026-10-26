import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ProtectedRoute from './components/ProtectedRoute';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import SearchPage from './pages/SearchPage';
import ItemDetailsPage from './pages/ItemDetailsPage';
import ReportItemPage from './pages/ReportItemPage';
import ProfilePage from './pages/ProfilePage';
import StudentDashboard from './pages/StudentDashboard';
import AdminDashboard from './pages/AdminDashboard';
import MyReportsPage from './pages/MyReportsPage';
import MyClaimsPage from './pages/MyClaimsPage';
import NotificationsPage from './pages/NotificationsPage';
import MatchesPage from './pages/MatchesPage';
import ErrorBoundary from './components/ErrorBoundary';

export default function App() {
  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      <main style={{ flex: 1 }}>
        <ErrorBoundary>
          <Routes>
          {/* Public routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/items/:id" element={<ItemDetailsPage />} />

          {/* Protected student routes */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <StudentDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/report/lost"
            element={
              <ProtectedRoute>
                <ReportItemPage type="LOST" />
              </ProtectedRoute>
            }
          />
          <Route
            path="/report/found"
            element={
              <ProtectedRoute>
                <ReportItemPage type="FOUND" />
              </ProtectedRoute>
            }
          />
          <Route
            path="/my-reports"
            element={
              <ProtectedRoute>
                <MyReportsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/my-claims"
            element={
              <ProtectedRoute>
                <MyClaimsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/notifications"
            element={
              <ProtectedRoute>
                <NotificationsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/matches"
            element={
              <ProtectedRoute>
                <MatchesPage />
              </ProtectedRoute>
            }
          />

          {/* Admin protected routes */}
          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute adminOnly={true}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
        </Routes>
        </ErrorBoundary>
      </main>
      <Footer />
    </div>
  );
}
