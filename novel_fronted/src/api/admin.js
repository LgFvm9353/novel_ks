import http from './http'

export function fetchAdminStats() {
  return http.get('/admin/stats')
}

export function fetchAdminAuthors() {
  return http.get('/admin/authors')
}

export function fetchAdminUsers() {
  return http.get('/admin/users')
}

export function approveAuthor(id) {
  return http.post(`/admin/authors/${id}/approve`)
}

export function rejectAuthor(id) {
  return http.post(`/admin/authors/${id}/reject`)
}

export function updateUserRole(id, role) {
  return http.post(`/admin/users/${id}/role`, null, { params: { role } })
}

export function deleteUser(id) {
  return http.delete(`/admin/users/${id}`)
}
