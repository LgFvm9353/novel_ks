<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { useAuthStore } from './store/auth'

const authStore = useAuthStore()
const user = computed(() => authStore.user)

const handleLogout = () => {
  authStore.logout()
}
</script>

<template>
  <div class="min-h-screen flex flex-col bg-brand-gray font-sans">
    <!-- Top Navbar -->
    <header class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <!-- Logo & Primary Nav -->
          <div class="flex items-center gap-8">
            <RouterLink to="/" class="flex items-center gap-2">
               <!-- Placeholder Logo Icon -->
               <div class="w-8 h-8 bg-brand-red rounded-lg flex items-center justify-center text-white font-bold text-lg">番</div>
               <span class="text-xl font-bold text-gray-900 tracking-tight">小说网站</span>
            </RouterLink>
            
            <nav class="hidden md:flex space-x-6">
              <RouterLink to="/" class="text-gray-600 hover:text-brand-red font-medium transition-colors">首页</RouterLink>
              <RouterLink to="/ranking" class="text-gray-600 hover:text-brand-red font-medium transition-colors">排行榜</RouterLink>
            </nav>
          </div>

          <!-- Search Bar (Placeholder) -->
          <div class="flex-1 max-w-lg mx-8 hidden lg:block">
            <div class="relative">
              <input 
                type="text" 
                placeholder="搜索书名、作者..." 
                class="w-full bg-gray-100 border-none rounded-full py-2 px-4 pl-10 focus:ring-2 focus:ring-brand-red focus:bg-white transition-all text-sm"
              >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <!-- Right Side: User & Actions -->
          <div class="flex items-center gap-4">
             <template v-if="user">
                <RouterLink v-if="['reader', 'author'].includes(user.role)" to="/author/dashboard" class="text-sm text-gray-500 hover:text-gray-900 hidden md:block">作者后台</RouterLink>
                <RouterLink v-if="user.role === 'admin'" to="/admin/dashboard" class="text-sm text-gray-500 hover:text-gray-900 hidden md:block">管理后台</RouterLink>
             </template>

             <div class="h-6 w-px bg-gray-200 hidden md:block"></div>

            <template v-if="user">
               <div class="flex items-center gap-3">
                  <RouterLink to="/reader/profile" class="text-gray-700 hover:text-brand-red font-medium text-sm flex items-center gap-2">
                    <div class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center text-xs overflow-hidden">
                       {{ user.username.charAt(0).toUpperCase() }}
                    </div>
                    <span class="hidden sm:inline">{{ user.username }}</span>
                  </RouterLink>
                  <button @click="handleLogout" class="text-gray-400 hover:text-gray-600 text-sm">退出</button>
               </div>
            </template>
            <template v-else>
              <div class="flex items-center gap-3">
                <RouterLink to="/login" class="text-gray-600 hover:text-brand-red font-medium transition-colors">登录</RouterLink>
                <RouterLink to="/register" class="bg-brand-red text-white px-4 py-1.5 rounded-full text-sm font-medium hover:bg-red-600 transition-colors shadow-sm">注册</RouterLink>
              </div>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-grow">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <RouterView />
      </div>
    </main>
    
    <!-- Footer -->
    <footer class="bg-white border-t border-gray-200 py-8 mt-12">
        <div class="max-w-7xl mx-auto px-4 text-center text-gray-500 text-sm">
            <p>&copy; 2025 小说阅读网站. All rights reserved.</p>
        </div>
    </footer>
  </div>
</template>

<style scoped>
/* Scoped styles can be minimal now thanks to Tailwind */
</style>
