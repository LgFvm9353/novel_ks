import http from './http'

export function login(username, password) {
  return http.post('/auth/login', { username, password })
}

export function register(username, password) {
  return http.post('/auth/register', { username, password })
}

export function getCurrentUser() {
  return http.get('/auth/me')
}

