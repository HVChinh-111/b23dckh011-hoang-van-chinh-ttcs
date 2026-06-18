import { apiLogin, apiLogout } from '../lib/fetchModelData.js'

export const getAccessToken = () => localStorage.getItem('adminToken')
export const getRefreshToken = () => localStorage.getItem('refreshToken')
export const isAuthenticated = () => Boolean(getAccessToken())

export const saveTokens = ({ accessToken, refreshToken }) => {
  localStorage.setItem('adminToken', accessToken)
  localStorage.setItem('refreshToken', refreshToken)
}

export const clearTokens = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('refreshToken')
}

export const login = async (email, password) => {
  const result = await apiLogin(email, password)
  saveTokens(result)
}

export const logout = async () => {
  const token = getRefreshToken()
  try {
    if (token) await apiLogout(token)
  } catch {
    // swallow — tokens cleared regardless
  }
  clearTokens()
}
