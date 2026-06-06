import http from '../utils/http'

export const novelApi = {
  uploadFile(file, { title, author } = {}) {
    const form = new FormData()
    form.append('file', file)
    if (title) form.append('title', title)
    if (author) form.append('author', author)
    return http.post('/novel/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  pasteText(payload) {
    return http.post('/novel/paste', payload)
  },
  getSession(sessionId) {
    return http.get(`/novel/session/${sessionId}`)
  },
  getChapter(sessionId, chapterId) {
    return http.get(`/novel/session/${sessionId}/chapter/${chapterId}`)
  }
}
