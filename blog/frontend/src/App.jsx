import { Routes, Route, Navigate } from 'react-router-dom'
import HomePage from './pages/HomePage.jsx'
import PostDetailPage from './pages/PostDetailPage.jsx'
import PostListPage from './pages/PostListPage.jsx'
import SearchPage from './pages/SearchPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'
import AdminLayout from './layouts/AdminLayout.jsx'
import AdminLoginPage from './pages/admin/AdminLoginPage.jsx'
import AdminPostsPage from './pages/admin/AdminPostsPage.jsx'
import AdminAddEditPostPage from './pages/admin/AdminAddEditPostPage.jsx'
import AdminTopicsSeriesPage from './pages/admin/AdminTopicsSeriesPage.jsx'
import AdminProfilePage from './pages/admin/AdminProfilePage.jsx'

const App = () => (
  <Routes>
    <Route path="/" element={<HomePage />} />
    <Route path="/posts/:slug" element={<PostDetailPage />} />
    <Route path="/topics/:slug" element={<PostListPage type="topic" />} />
    <Route path="/series/:slug" element={<PostListPage type="series" />} />
    <Route path="/search" element={<SearchPage />} />

    <Route path="/admin/login" element={<AdminLoginPage />} />
    <Route path="/admin" element={<AdminLayout />}>
      <Route index element={<Navigate to="posts" replace />} />
      <Route path="posts" element={<AdminPostsPage />} />
      <Route path="posts/new" element={<AdminAddEditPostPage />} />
      <Route path="posts/:id/edit" element={<AdminAddEditPostPage />} />
      <Route path="topics-series" element={<AdminTopicsSeriesPage />} />
      <Route path="profile" element={<AdminProfilePage />} />
    </Route>

    <Route path="*" element={<NotFoundPage />} />
  </Routes>
)

export default App
