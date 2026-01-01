<script setup>
import { ref, onMounted } from 'vue'
import { fetchAdminUsers, updateUserRole, deleteUser } from '../../api/admin'

const loading = ref(false)
const error = ref('')
const users = ref([])

const roles = [
  { value: 'reader', label: '读者' },
  { value: 'author', label: '作者' },
  { value: 'admin', label: '管理员' },
]

const loadUsers = async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchAdminUsers()
    if (data.code === 0) {
      users.value = data.data || []
    } else {
      error.value = data.message || '加载用户列表失败'
    }
  } catch (e) {
    error.value = '加载用户列表出错'
  } finally {
    loading.value = false
  }
}

const handleChangeRole = async (user, newRole) => {
  if (!user || user.role === newRole) return
  const ok = window.confirm(`确定要将用户「${user.username}」角色修改为「${newRole}」吗？`)
  if (!ok) return
  try {
    const { data } = await updateUserRole(user.id, newRole)
    if (data.code === 0) {
      user.role = newRole
    } else {
      error.value = data.message || '更新角色失败'
      await loadUsers()
    }
  } catch (e) {
    error.value = '更新角色出错'
    await loadUsers()
  }
}

const handleDelete = async (user) => {
  if (!user) return
  const ok = window.confirm(`确定要删除用户「${user.username}」吗？此操作不可恢复。`)
  if (!ok) return
  try {
    const { data } = await deleteUser(user.id)
    if (data.code === 0) {
      users.value = users.value.filter((u) => u.id !== user.id)
    } else {
      error.value = data.message || '删除用户失败'
    }
  } catch (e) {
    error.value = '删除用户出错'
  }
}

onMounted(() => {
  loadUsers()
})
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
      <h1 class="text-3xl font-bold text-gray-900">用户管理</h1>
    </div>

    <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-lg">
      {{ error }}
    </div>

    <div class="bg-white rounded-xl shadow-sm overflow-hidden border border-gray-100">
      <div class="p-6 border-b border-gray-100 flex items-center justify-between bg-gray-50/50">
        <div class="text-sm font-medium text-gray-500">
          共 <span class="text-gray-900">{{ users.length }}</span> 个用户
        </div>
        <div v-if="loading" class="text-xs text-gray-400 flex items-center gap-2">
          <div class="animate-spin rounded-full h-3 w-3 border-b-2 border-brand-red"></div>
          加载中...
        </div>
      </div>

      <div v-if="users.length === 0 && !loading" class="p-12 text-center text-gray-400">
        <div class="text-4xl mb-4">👥</div>
        <p>暂无用户数据</p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                用户名
              </th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                角色
              </th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                注册时间
              </th>
              <th class="px-6 py-4 text-right text-xs font-bold text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                {{ user.username }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                <select
                  :value="user.role"
                  @change="handleChangeRole(user, $event.target.value)"
                  class="px-3 py-1.5 border border-gray-200 rounded-lg text-sm bg-white focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-shadow cursor-pointer hover:border-gray-300"
                >
                  <option
                    v-for="r in roles"
                    :key="r.value"
                    :value="r.value"
                  >
                    {{ r.label }}
                  </option>
                </select>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ user.createdAt ? new Date(user.createdAt).toLocaleString() : '-' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm">
                <button
                  @click="handleDelete(user)"
                  class="text-gray-400 hover:text-red-600 transition-colors p-2 hover:bg-red-50 rounded-lg"
                  title="删除用户"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
