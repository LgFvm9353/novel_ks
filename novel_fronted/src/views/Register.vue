<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const router = useRouter()
const authStore = useAuthStore()

const handleSubmit = async () => {
  error.value = ''
  success.value = ''
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  if (password.value !== confirmPassword.value) {
    error.value = '两次输入的密码不一致'
    return
  }
  try {
    await authStore.register(username.value, password.value)
    success.value = '注册成功，请登录'
    setTimeout(() => router.push({ name: 'login' }), 800)
  } catch (e) {
    error.value = '注册失败'
  }
}
</script>

<template>
  <div class="min-h-[80vh] flex items-center justify-center bg-brand-gray px-4">
    <div class="max-w-md w-full bg-white rounded-xl shadow-lg p-8 space-y-6">
      <div class="text-center">
        <h1 class="text-3xl font-bold text-gray-900">创建账号</h1>
        <p class="mt-2 text-sm text-gray-600">开启您的阅读之旅</p>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input 
              v-model="username" 
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all"
              placeholder="设置用户名"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input 
              v-model="password" 
              type="password" 
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all"
              placeholder="设置密码"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
            <input 
              v-model="confirmPassword" 
              type="password" 
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all"
              placeholder="再次输入密码"
            />
          </div>
        </div>

        <div v-if="error" class="bg-red-50 text-red-500 text-sm px-4 py-2 rounded-lg">
          {{ error }}
        </div>
        <div v-if="success" class="bg-green-50 text-green-500 text-sm px-4 py-2 rounded-lg">
          {{ success }}
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
          {{ authStore.loading ? '注册中...' : '立即注册' }}
        </button>

        <div class="text-center text-sm">
           <span class="text-gray-600">已有账号？</span>
           <RouterLink to="/login" class="font-medium text-brand-red hover:text-red-500">去登录</RouterLink>
        </div>
      </form>
    </div>
  </div>
</template>

