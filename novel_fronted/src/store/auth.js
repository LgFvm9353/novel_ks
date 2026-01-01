import { defineStore } from 'pinia'
import { login, register, getCurrentUser } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    user: null,
    initialized: false,
    loading: false,
  }),
  actions: {
    async initFromStorage() {
      const storedToken = localStorage.getItem('token')
      if (storedToken) {
        this.token = storedToken
        try {
          const res = await getCurrentUser()
          this.user = res.data.data
        } catch (e) {
          this.token = null
          localStorage.removeItem('token')
        }
      }
      this.initialized = true
    },
    async login(username, password) {
      this.loading = true
      try {
        const res = await login(username, password)
        this.token = res.data.data.token
        this.user = res.data.data.user
        localStorage.setItem('token', this.token)
      } finally {
        this.loading = false
      }
    },
    async register(username, password) {
      this.loading = true
      try {
        await register(username, password)
      } finally {
        this.loading = false
      }
    },
    async fetchCurrentUser() {
      if (!this.token) {
        this.user = null
        return
      }
      const res = await getCurrentUser()
      this.user = res.data.data
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
    },
  },
})

