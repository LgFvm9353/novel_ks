import axios from 'axios'
import { useAuthStore } from '../store/auth'

const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

export default http

