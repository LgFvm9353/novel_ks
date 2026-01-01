<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { fetchReadingHistory, deleteReadingHistory, clearReadingHistory } from '../api/novels'

const router = useRouter()
const authStore = useAuthStore()
const user = computed(() => authStore.user)

const historyList = ref([])
const loading = ref(false)
const error = ref('')

const loadHistory = async () => {
  if (!authStore.token) {
    historyList.value = []
    return
  }
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchReadingHistory()
    if (data.code === 0) {
      historyList.value = data.data || []
    } else {
      error.value = data.message || '加载阅读历史失败'
    }
  } catch (e) {
    error.value = '加载阅读历史出错'
  } finally {
    loading.value = false
  }
}

const goToNovel = (item) => {
  if (!item || !item.novelId) return
  router.push({ name: 'novel-detail', params: { id: item.novelId } })
}

const goToChapter = (item) => {
  if (!item || !item.novelId || !item.chapterNumber) return
  router.push({
    name: 'chapter-reader',
    params: { id: item.novelId, chapterNumber: item.chapterNumber },
  })
}

const removeHistory = async (id) => {
  if (!confirm('确定要删除这条阅读记录吗？')) return
  try {
    const { data } = await deleteReadingHistory(id)
    if (data.code === 0) {
      historyList.value = historyList.value.filter((item) => item.id !== id)
    }
  } catch (e) {}
}

const clearAllHistory = async () => {
  if (!confirm('确定要清空所有阅读记录吗？')) return
  try {
    const { data } = await clearReadingHistory()
    if (data.code === 0) {
      historyList.value = []
    }
  } catch (e) {}
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-8 space-y-8">
    <!-- User Info Card -->
    <div class="bg-white rounded-xl shadow-sm p-8 flex flex-col md:flex-row items-center gap-6 border border-gray-100">
      <div class="w-20 h-20 bg-gradient-to-br from-brand-red to-red-600 rounded-full flex items-center justify-center text-white text-3xl font-bold shadow-md">
        {{ user?.username?.charAt(0).toUpperCase() || 'U' }}
      </div>
      <div class="text-center md:text-left flex-1">
        <h1 class="text-2xl font-bold text-gray-900 mb-2">{{ user?.username }}</h1>
        <div class="flex flex-wrap justify-center md:justify-start gap-2">
           <span class="px-3 py-1 bg-gray-100 text-gray-600 text-xs font-medium rounded-full">ID: {{ user?.id }}</span>
           <span class="px-3 py-1 bg-blue-50 text-blue-600 text-xs font-medium rounded-full border border-blue-100">{{ user?.role === 'author' ? '签约作者' : '普通读者' }}</span>
        </div>
      </div>
      <div class="flex gap-4" v-if="user?.role === 'author'">
        <RouterLink to="/author/dashboard" class="px-6 py-2 bg-gray-900 text-white text-sm font-medium rounded-lg hover:bg-gray-800 transition-colors shadow-sm">
          进入作者后台
        </RouterLink>
      </div>
    </div>

    <!-- Reading History -->
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-xl font-bold text-gray-900 flex items-center gap-2">
          <span class="w-1 h-6 bg-brand-red rounded-full"></span>
          阅读历史
        </h2>
        <button 
          v-if="historyList.length > 0" 
          @click="clearAllHistory"
          class="text-sm text-gray-500 hover:text-brand-red transition-colors flex items-center gap-1"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
          清空历史
        </button>
      </div>

      <div v-if="loading" class="flex justify-center py-20">
         <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-brand-red"></div>
      </div>
      
      <div v-else-if="error" class="bg-red-50 text-red-600 p-6 rounded-xl text-center">
        {{ error }}
      </div>

      <div v-else-if="historyList.length === 0" class="bg-white rounded-xl shadow-sm p-16 text-center text-gray-400 border border-gray-100">
         <div class="text-6xl mb-4 opacity-30">📚</div>
         <p class="text-lg mb-6">书架空空如也，快去挑本书看看吧~</p>
         <RouterLink to="/" class="px-6 py-2 bg-brand-red text-white rounded-full hover:bg-red-600 transition-colors inline-block font-medium">
           去书城逛逛
         </RouterLink>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="item in historyList" 
          :key="item.id" 
          class="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow p-4 border border-gray-100 group relative flex gap-4"
        >
           <!-- Cover Image -->
           <div 
             class="w-24 h-32 flex-shrink-0 bg-gray-200 rounded-lg overflow-hidden cursor-pointer relative"
             @click="goToNovel(item)"
           >
             <img 
               v-if="item.coverImage" 
               :src="item.coverImage" 
               :alt="item.novelTitle" 
               class="w-full h-full object-cover transition-transform group-hover:scale-105 duration-300"
             />
             <div v-else class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-300 text-xs text-center p-2">
               暂无封面
             </div>
           </div>

           <!-- Content -->
           <div class="flex-1 min-w-0 flex flex-col justify-between py-1">
             <div>
               <h3 
                 @click="goToNovel(item)"
                 class="font-bold text-gray-900 mb-1 hover:text-brand-red cursor-pointer truncate transition-colors"
                 :title="item.novelTitle || `小说 ID: ${item.novelId}`"
               >
                 {{ item.novelTitle || `小说 ID: ${item.novelId}` }}
               </h3>
               <p class="text-xs text-gray-500 mb-3">
                 上次阅读：{{ new Date(item.lastReadTime || item.updatedAt).toLocaleDateString() }}
               </p>
             </div>

             <div class="space-y-2">
               <div class="text-sm text-gray-600 truncate">
                 <span class="text-xs bg-gray-100 px-1.5 py-0.5 rounded text-gray-500 mr-1">读至</span>
                 {{ item.chapterTitle || `第 ${item.chapterNumber} 章` }}
               </div>
               
               <button 
                 @click="goToChapter(item)"
                 class="w-full py-1.5 bg-red-50 text-brand-red text-sm font-medium rounded-lg hover:bg-brand-red hover:text-white transition-all text-center"
               >
                 继续阅读
               </button>
             </div>
           </div>

           <!-- Delete Button -->
           <button 
             @click.stop="removeHistory(item.id)" 
             class="absolute top-2 right-2 p-1.5 text-gray-300 hover:text-red-500 hover:bg-red-50 rounded-full transition-all opacity-0 group-hover:opacity-100"
             title="删除记录"
           >
             <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
               <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
             </svg>
           </button>
        </div>
      </div>
    </div>
  </div>
</template>
