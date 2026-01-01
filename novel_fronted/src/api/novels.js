import http from './http'

export function fetchNovels(params) {
  return http.get('/novels', { params })
}

export function fetchRanking(limit = 10) {
  return http.get('/novels/ranking', { params: { limit } })
}

export function fetchNovelDetail(id) {
  return http.get(`/novels/${id}`)
}

export function fetchChapters(novelId) {
  return http.get(`/novels/${novelId}/chapters`)
}

export function fetchChapterDetail(novelId, chapterNumber) {
  return http.get(`/novels/${novelId}/chapters/${chapterNumber}`)
}

export function fetchComments(novelId) {
  return http.get(`/novels/${novelId}/comments`)
}

export function addComment(novelId, content) {
  return http.post(`/novels/${novelId}/comments`, { content })
}

export function deleteComment(novelId, commentId) {
  return http.delete(`/novels/${novelId}/comments/${commentId}`)
}

export function voteNovel(novelId) {
  return http.post(`/novels/${novelId}/vote`)
}

export function checkVoteStatus(novelId) {
  return http.get(`/novels/${novelId}/vote/status`)
}

export function fetchReadingHistory() {
  return http.get('/reader/history')
}

export function saveReadingHistory(payload) {
  return http.post('/reader/history', payload)
}

export function deleteReadingHistory(id) {
  return http.delete(`/reader/history/${id}`)
}

export function clearReadingHistory() {
  return http.delete('/reader/history')
}

export function fetchCategories() {
  return http.get('/categories')
}

export function fetchAuthorProfile() {
  return http.get('/author/me')
}

export function becomeAuthor(payload) {
  return http.post('/author/become', payload)
}

export function fetchMyNovels() {
  return http.get('/author/novels')
}

export function createNovel(payload) {
  return http.post('/author/novels', payload)
}

export function updateNovel(id, payload) {
  return http.put(`/author/novels/${id}`, payload)
}

export function deleteAuthorNovel(id) {
  return http.delete(`/author/novels/${id}`)
}

export function fetchAuthorChapters(novelId) {
  return http.get(`/author/novels/${novelId}/chapters`)
}

export function createAuthorChapter(novelId, payload) {
  return http.post(`/author/novels/${novelId}/chapters`, payload)
}

export function updateAuthorChapter(novelId, chapterNumber, payload) {
  return http.put(`/author/novels/${novelId}/chapters/${chapterNumber}`, payload)
}

export function deleteAuthorChapter(novelId, chapterNumber) {
  return http.delete(`/author/novels/${novelId}/chapters/${chapterNumber}`)
}
