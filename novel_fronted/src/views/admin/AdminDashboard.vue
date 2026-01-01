<script setup>
import { ref, onMounted } from 'vue'
import { fetchAdminStats } from '../../api/admin'

const loading = ref(false)
const error = ref('')
const stats = ref({
  totalUsers: 0,
  totalAuthors: 0,
  totalNovels: 0,
})

const loadStats = async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchAdminStats()
    if (data.code === 0 && data.data) {
      stats.value = data.data
    } else {
      error.value = data.message || '加载统计数据失败'
    }
  } catch (e) {
    error.value = '加载统计数据出错'
  } finally {
    loading.value = false
  }
}

onMounted(loadStats)
</script>

<template>
  <div class="max-w-6xl mx-auto space-y-8">
    <h1 class="text-3xl font-bold text-gray-900">管理员后台</h1>
    
    <!-- Stats Section -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <h3 class="text-gray-500 text-sm font-medium">总用户数</h3>
        <p class="text-3xl font-bold text-gray-900 mt-2">
          <span v-if="!loading">{{ stats.totalUsers }}</span>
          <span v-else>...</span>
        </p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <h3 class="text-gray-500 text-sm font-medium">总作者数</h3>
        <p class="text-3xl font-bold text-gray-900 mt-2">
          <span v-if="!loading">{{ stats.totalAuthors }}</span>
          <span v-else>...</span>
        </p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <h3 class="text-gray-500 text-sm font-medium">总小说数</h3>
        <p class="text-3xl font-bold text-gray-900 mt-2">
          <span v-if="!loading">{{ stats.totalNovels }}</span>
          <span v-else>...</span>
        </p>
      </div>
    </div>

    <!-- Management Modules -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <RouterLink 
        to="/admin/users"
        class="bg-white rounded-xl shadow-sm p-8 border border-gray-100 hover:shadow-md transition-all group flex items-center gap-6"
      >
        <div class="w-16 h-16 bg-blue-50 text-blue-500 rounded-2xl flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">
          👥
        </div>
        <div>
          <h2 class="text-xl font-bold text-gray-900 mb-1 group-hover:text-blue-600 transition-colors">用户管理</h2>
          <p class="text-gray-500">查看、编辑用户信息，管理用户权限与状态</p>
        </div>
      </RouterLink>

      <RouterLink 
        to="/admin/authors"
        class="bg-white rounded-xl shadow-sm p-8 border border-gray-100 hover:shadow-md transition-all group flex items-center gap-6"
      >
        <div class="w-16 h-16 bg-green-50 text-green-500 rounded-2xl flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">
          ✒️
        </div>
        <div>
          <h2 class="text-xl font-bold text-gray-900 mb-1 group-hover:text-green-600 transition-colors">作者管理</h2>
          <p class="text-gray-500">审核作者申请，管理现有作者及其作品</p>
        </div>
      </RouterLink>
    </div>

    <div class="bg-white rounded-xl shadow-sm p-12 text-center border border-gray-100 opacity-50">
      <div v-if="error" class="text-red-500">
        {{ error }}
      </div>
      <div v-else class="text-gray-400">
        <div class="text-4xl mb-4">🚧</div>
        <p>更多管理功能开发中...</p>
      </div>
    </div>
  </div>
</template>
