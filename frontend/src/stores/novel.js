import { defineStore } from 'pinia'
import { novelApi } from '../api/novel'

export const useNovelStore = defineStore('novel', {
  state: () => ({
    sessionId: '',
    title: '',
    author: '',
    totalChars: 0,
    chapters: [],
    selectedChapterIds: [],
    loading: false
  }),
  getters: {
    hasSession: (s) => !!s.sessionId,
    selectedCount: (s) => s.selectedChapterIds.length,
    canConvert: (s) => s.selectedChapterIds.length >= 3
  },
  actions: {
    async uploadFile(file, meta) {
      this.loading = true
      try {
        const data = await novelApi.uploadFile(file, meta)
        this.applySession(data)
        return data
      } finally {
        this.loading = false
      }
    },
    async pasteText(payload) {
      this.loading = true
      try {
        const data = await novelApi.pasteText(payload)
        this.applySession(data)
        return data
      } finally {
        this.loading = false
      }
    },
    async refresh(sessionId) {
      const data = await novelApi.getSession(sessionId)
      this.applySession(data)
    },
    applySession(data) {
      this.sessionId = data.sessionId
      this.title = data.title
      this.author = data.author
      this.totalChars = data.totalChars
      this.chapters = data.chapters || []
      this.selectedChapterIds = this.chapters
        .slice(0, Math.min(this.chapters.length, 3))
        .map(c => c.id)
    },
    toggleChapter(id) {
      const idx = this.selectedChapterIds.indexOf(id)
      if (idx >= 0) this.selectedChapterIds.splice(idx, 1)
      else this.selectedChapterIds.push(id)
    },
    selectAll() {
      this.selectedChapterIds = this.chapters.map(c => c.id)
    },
    clearSelection() {
      this.selectedChapterIds = []
    },
    reset() {
      this.sessionId = ''
      this.title = ''
      this.author = ''
      this.totalChars = 0
      this.chapters = []
      this.selectedChapterIds = []
    }
  }
})
