<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'

const username = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const handleSubmit = async () => {
  error.value = ''
  try {
    await authStore.login(username.value, password.value)
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    error.value = '登录失败'
  }
}
</script>

<template>
  <div class="min-h-[80vh] flex items-center justify-center bg-brand-gray px-4">
    <div class="max-w-md w-full bg-white rounded-xl shadow-lg p-8 space-y-6">
      <div class="text-center">
        <h1 class="text-3xl font-bold text-gray-900">欢迎回来</h1>
        <p class="mt-2 text-sm text-gray-600">请登录您的账号</p>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input 
              v-model="username" 
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all"
              placeholder="请输入用户名"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input 
              v-model="password" 
              type="password" 
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all"
              placeholder="请输入密码"
            />
          </div>
        </div>

        <div v-if="error" class="bg-red-50 text-red-500 text-sm px-4 py-2 rounded-lg">
          {{ error }}
        </div>

        <button 
          type="submit" 
          :disabled="authStore.loading"
          class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-brand-red hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-red disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <span v-if="authStore.loading" class="mr-2">
            <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </span>
          {{ authStore.loading ? '登录中...' : '登录' }}
        </button>

        <div class="text-center text-sm">
           <span class="text-gray-600">还没有账号？</span>
           <RouterLink to="/register" class="font-medium text-brand-red hover:text-red-500">立即注册</RouterLink>
        </div>
      </form>
    </div>
  </div>
</template>

