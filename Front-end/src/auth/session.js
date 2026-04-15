const AUTH_KEY = 'v2x_admin_logged_in'

export function setAuthenticated(value) {
  if (value) {
    localStorage.setItem(AUTH_KEY, '1')
    return
  }
  localStorage.removeItem(AUTH_KEY)
}

export function isAuthenticated() {
  return localStorage.getItem(AUTH_KEY) === '1'
}

export function clearAuthenticated() {
  localStorage.removeItem(AUTH_KEY)
}
