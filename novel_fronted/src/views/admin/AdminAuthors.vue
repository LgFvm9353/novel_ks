<script setup>
import { ref, onMounted } from 'vue'
import { fetchAdminAuthors, approveAuthor, rejectAuthor } from '../../api/admin'

const loading = ref(false)
const error = ref('')
const authors = ref([])

const loadAuthors = async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchAdminAuthors()
    if (data.code === 0 && Array.isArray(data.data)) {
      authors.value = data.data
    } else {
      error.value = data.message || '加载作者列表失败'
    }
  } catch (e) {
    error.value = '加载作者列表出错'
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id) => {
  try {
    const { data } = await approveAuthor(id)
    if (data.code === 0) {
      await loadAuthors()
    }
  } catch (e) {}
}

const handleReject = async (id) => {
  try {
    const { data } = await rejectAuthor(id)
    if (data.code === 0) {
      await loadAuthors()
    }
  } catch (e) {}
}

onMounted(loadAuthors)
</script>

<template>
  <div class="max-w-6xl mx-auto space-y-8">
    <div class="flex items-center gap-4">
      <RouterLink 
        to="/admin/dashboard"
        class="w-10 h-10 flex items-center justify-center rounded-full bg-white border border-gray-200 text-gray-500 hover:text-brand-red hover:border-brand-red transition-colors shadow-sm"
        title="返回仪表盘"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
      </RouterLink>
      <h1 class="text-3xl font-bold text-gray-900">作者管理</h1>
    </div>

    <div class="bg-white rounded-xl shadow-sm overflow-hidden border border-gray-100">
      <div class="p-6 border-b border-gray-100 flex items-center justify-between bg-gray-50/50">
        <h2 class="text-lg font-bold text-gray-900">作者申请与列表</h2>
        <button
          class="text-sm text-gray-600 hover:text-brand-red flex items-center gap-1 transition-colors"
          @click="loadAuthors"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          刷新
        </button>
      </div>

      <div v-if="loading" class="p-12 text-center text-gray-400 flex flex-col items-center">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-brand-red mb-4"></div>
        正在加载...
      </div>
      <div v-else-if="error" class="p-12 text-center text-red-500 bg-red-50">
        {{ error }}
      </div>
      <div v-else-if="authors.length === 0" class="p-12 text-center text-gray-400">
        暂无作者申请
      </div>
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-100">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">用户名</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">当前角色</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">笔名</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">简介</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">状态</th>
              <th class="px-6 py-4 text-right text-xs font-bold text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-100">
            <tr v-for="item in authors" :key="item.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ item.username }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span class="px-2 py-1 rounded-full text-xs bg-gray-100 text-gray-600">{{ item.role }}</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ item.name }}</td>
              <td class="px-6 py-4 text-sm text-gray-500 max-w-xs">
                <p class="truncate" :title="item.bio">{{ item.bio || '暂无简介' }}</p>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <span
                  v-if="item.status === 'approved'"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800"
                >
                  <span class="w-1.5 h-1.5 bg-green-500 rounded-full mr-1.5"></span>
                  已通过
                </span>
                <span
                  v-else
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800"
                >
                  <span class="w-1.5 h-1.5 bg-yellow-500 rounded-full mr-1.5"></span>
                  待审核
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-right space-x-3">
                <button
                  v-if="item.status === 'pending'"
                  class="text-green-600 hover:text-green-900 font-medium transition-colors"
                  @click="handleApprove(item.id)"
                >
                  通过
                </button>
                <button
                  v-if="item.status === 'pending'"
                  class="text-red-600 hover:text-red-900 font-medium transition-colors"
                  @click="handleReject(item.id)"
                >
                  驳回
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
