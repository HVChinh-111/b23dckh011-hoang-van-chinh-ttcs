const BASE_URL = ''

// ── Date formatter ────────────────────────────────────────────────────────────

function formatDate(isoString) {
  if (!isoString) return ''
  const d = new Date(isoString)
  return d.toLocaleDateString('vi-VN', { day: 'numeric', month: 'long', year: 'numeric' })
}

// ── Shape adapters ─────────────────────────────────────────────────────────────

function adaptPost(apiPost) {
  return {
    id: apiPost.id,
    title: apiPost.title,
    slug: apiPost.slug,
    excerpt: apiPost.summary ?? '',
    date: formatDate(apiPost.publishedAt),
    topics: (apiPost.topics ?? []).map((t) => ({ name: t.name, slug: t.slug })),
    imageUrl: null,
    imageAlt: apiPost.title,
    status: apiPost.status ?? null,
  }
}

function adaptProfile(apiProfile) {
  return {
    name: apiProfile.fullName,
    bio: apiProfile.shortBio ?? '',
    avatarUrl: apiProfile.avatar ? BASE_URL + apiProfile.avatar.publicPath : null,
    githubUrl: apiProfile.githubUrl ?? '#',
    linkedinUrl: apiProfile.linkedInUrl ?? '#',
    facebookUrl: apiProfile.facebookUrl ?? '#',
    contactEmail: apiProfile.contactEmail ?? '',
  }
}

function adaptToc(tocItems) {
  if (!tocItems) return []
  return tocItems
    .filter((item) => item.level === 2 || item.level === 3)
    .map((item) => ({ id: item.id, label: item.title, level: item.level }))
}

function adaptSeriesNav(seriesInfo, seriesItems) {
  if (!seriesInfo) return null
  return {
    name: seriesInfo.name,
    slug: seriesInfo.slug,
    items: (seriesItems ?? []).map((item) => ({
      order: item.sequenceNumber,
      title: item.title,
      slug: item.slug,
      isCurrent: item.current,
    })),
  }
}

// ── Core fetch function ────────────────────────────────────────────────────────

async function apiFetch(path, options = {}, retry = true) {
  const url = BASE_URL + path

  const headers = { ...(options.headers ?? {}) }
  const token = localStorage.getItem('adminToken')
  if (token) headers['Authorization'] = `Bearer ${token}`

  const isFormData = options.body instanceof FormData
  if (!isFormData && options.body && typeof options.body === 'string') {
    headers['Content-Type'] = 'application/json'
  }

  const res = await fetch(url, { ...options, headers })

  if (res.status === 204) return null

  // Auth endpoints (login/refresh/logout) phải tự bộc lộ lỗi của mình —
  // không kích hoạt luồng làm mới token / chuyển hướng khi hết phiên.
  const isAuthEndpoint = path.startsWith('/api/auth/')

  if (res.status === 401 && retry && !isAuthEndpoint) {
    const refreshToken = localStorage.getItem('refreshToken')
    if (!refreshToken) {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('refreshToken')
      window.location.replace('/admin/login')
      return
    }
    try {
      const refreshRes = await fetch(BASE_URL + '/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      })
      if (!refreshRes.ok) throw new Error('Refresh failed')
      const refreshJson = await refreshRes.json()
      const newAccessToken = refreshJson.data.accessToken
      const newRefreshToken = refreshJson.data.refreshToken
      localStorage.setItem('adminToken', newAccessToken)
      localStorage.setItem('refreshToken', newRefreshToken)
      return apiFetch(path, options, false)
    } catch {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('refreshToken')
      window.location.replace('/admin/login')
      return
    }
  }

  const json = await res.json().catch(() => ({}))

  if (!res.ok) {
    const err = new Error(json.message ?? 'Đã xảy ra lỗi')
    err.code = json.code ?? res.status
    throw err
  }

  return json.data
}

// ── Auth ───────────────────────────────────────────────────────────────────────

export async function apiLogin(email, password) {
  return apiFetch('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export async function apiRefresh(refreshToken) {
  return apiFetch('/api/auth/refresh', {
    method: 'POST',
    body: JSON.stringify({ refreshToken }),
  })
}

export async function apiLogout(refreshToken) {
  return apiFetch('/api/auth/logout', {
    method: 'POST',
    body: JSON.stringify({ refreshToken }),
  })
}

// ── Profile ────────────────────────────────────────────────────────────────────

export async function fetchProfile() {
  const data = await apiFetch('/api/profile')
  return adaptProfile(data)
}

export async function updateAdminProfile(formData) {
  const data = await apiFetch('/api/profile', { method: 'PUT', body: formData })
  return adaptProfile(data)
}

// ── Posts — Public ─────────────────────────────────────────────────────────────

export async function fetchPosts(page = 1, size = 10) {
  const data = await apiFetch(`/api/posts?page=${page}&size=${size}`)
  return {
    posts: (data.posts ?? []).map(adaptPost),
    pagination: data.pagination,
  }
}

export async function searchPosts(keyword, page = 1, size = 10) {
  const data = await apiFetch(
    `/api/posts/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`
  )
  return {
    posts: (data.posts ?? []).map(adaptPost),
    pagination: data.pagination,
    totalResults: data.totalResults,
  }
}

export async function fetchPostBySlug(slug) {
  const data = await apiFetch(`/api/posts/${encodeURIComponent(slug)}`)
  return {
    ...data,
    toc: adaptToc(data.tocItems),
    series: adaptSeriesNav(data.seriesInfo, data.seriesItems),
  }
}

// ── Topics — Public ────────────────────────────────────────────────────────────

export async function fetchTopics() {
  return apiFetch('/api/topics')
}

export async function fetchTopicPosts(slug, page = 1, size = 10) {
  const data = await apiFetch(`/api/topics/${encodeURIComponent(slug)}/posts?page=${page}&size=${size}`)
  return {
    topicInfo: data.topicInfo,
    posts: (data.posts ?? []).map(adaptPost),
    pagination: data.pagination,
  }
}

// ── Series — Public ────────────────────────────────────────────────────────────

export async function fetchSeriesList() {
  return apiFetch('/api/series')
}

export async function fetchSeriesPosts(slug, page = 1, size = 10) {
  const data = await apiFetch(`/api/series/${encodeURIComponent(slug)}/posts?page=${page}&size=${size}`)
  return {
    seriesInfo: data.seriesInfo,
    items: data.items ?? [],
    pagination: data.pagination,
  }
}

// ── Posts — Admin ──────────────────────────────────────────────────────────────

export async function fetchAdminPosts(page = 1, size = 10) {
  return apiFetch(`/api/admin/posts?page=${page}&size=${size}`)
}

export async function fetchAdminPostFormOptions() {
  return apiFetch('/api/admin/posts/form-options')
}

export async function fetchAdminPostById(id) {
  return apiFetch(`/api/admin/posts/${id}`)
}

export async function createAdminPost(body) {
  return apiFetch('/api/admin/posts', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export async function updateAdminPost(id, body) {
  return apiFetch(`/api/admin/posts/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

export async function deleteAdminPost(id) {
  return apiFetch(`/api/admin/posts/${id}`, { method: 'DELETE' })
}

// ── Topics — Admin ─────────────────────────────────────────────────────────────

export async function createTopic(body) {
  return apiFetch('/api/topics', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export async function updateTopic(id, body) {
  return apiFetch(`/api/topics/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

export async function deleteTopic(id) {
  return apiFetch(`/api/topics/${id}`, { method: 'DELETE' })
}

// ── Series — Admin ─────────────────────────────────────────────────────────────

export async function createSeries(body) {
  return apiFetch('/api/series', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export async function updateSeries(id, body) {
  return apiFetch(`/api/series/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

export async function deleteSeries(id) {
  return apiFetch(`/api/series/${id}`, { method: 'DELETE' })
}

// ── Media ──────────────────────────────────────────────────────────────────────

export async function uploadPostImage(file, postId) {
  const formData = new FormData()
  formData.append('file', file)
  if (postId) formData.append('postId', postId)
  return apiFetch('/api/media/images', { method: 'POST', body: formData })
}

export async function fetchPostImages(postId) {
  return apiFetch(`/api/media/images?postId=${encodeURIComponent(postId)}`)
}

export async function deletePostImage(mediaId) {
  return apiFetch(`/api/media/images/${mediaId}`, { method: 'DELETE' })
}
