<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchRanking } from '../api/novels'

const novels = ref([])
const loading = ref(false)
const error = ref('')

const loadData = async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchRanking(20) // Fetch top 20
    if (data.code === 0) {
      novels.value = data.data
    } else {
      error.value = data.message || '加载失败'
    }
  } catch (e) {
    error.value = '请求出错'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const getRankClass = (index) => {
    if (index === 0) return 'bg-yellow-500 text-white'
    if (index === 1) return 'bg-gray-400 text-white'
    if (index === 2) return 'bg-orange-500 text-white'
    return 'bg-gray-100 text-gray-600'
}
</script>

<template>
  <div class="max-w-4xl mx-auto space-y-8">
    <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <span class="text-3xl">🏆</span>
          推荐榜单
        </h2>
    </div>

     <div v-if="loading" class="space-y-4">
        <div v-for="i in 5" :key="i" class="animate-pulse bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex gap-4">
          <div class="w-16 h-20 bg-gray-200 rounded-lg shrink-0"></div>
          <div class="flex-1 space-y-2 py-1">
             <div class="h-4 bg-gray-200 rounded w-1/4"></div>
             <div class="h-3 bg-gray-200 rounded w-1/2"></div>
          </div>
        </div>
      </div>

      <div v-else-if="error" class="text-center py-12 bg-white rounded-xl shadow-sm border border-gray-100">
        <div class="text-red-500 mb-2">{{ error }}</div>
        <button 
          @click="loadData" 
          class="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg text-sm text-gray-600 transition-colors"
        >
          重试
        </button>
      </div>

       <div v-else-if="novels.length === 0" class="text-center py-12 text-gray-400 bg-white rounded-xl shadow-sm border border-gray-100">
        暂无数据
      </div>

    <div v-else class="space-y-4">
        <RouterLink
          v-for="(novel, index) in novels"
          :key="novel.id"
          :to="{ name: 'novel-detail', params: { id: novel.id } }"
          class="block bg-white p-4 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow group"
        >
            <div class="flex gap-4 items-center">
                <div 
                    class="w-8 h-8 flex items-center justify-center rounded-full font-bold text-sm shrink-0"
                    :class="getRankClass(index)"
                >
                    {{ index + 1 }}
                </div>
                
                 <div class="w-16 h-20 bg-gray-100 rounded-lg overflow-hidden shrink-0">
                    <img
                      v-if="novel.coverImage"
                      :src="novel.coverImage"
                      class="w-full h-full object-cover"
                      alt="cover"
                    />
                    <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                        📚
                    </div>
                </div>

                <div class="flex-1 min-w-0">
                    <h3 class="font-bold text-gray-900 group-hover:text-brand-red transition-colors truncate">
                        {{ novel.title }}
                    </h3>
                     <p class="text-sm text-gray-500 mt-1 line-clamp-1">
                        {{ novel.description || '暂无简介' }}
                    </p>
                    <div class="flex items-center gap-4 mt-2 text-xs text-gray-400">
                         <span>{{ novel.categoryName || '未分类' }}</span>
                         <span>{{ novel.status || '连载中' }}</span>
                    </div>
                </div>

                <div class="text-right shrink-0">
                    <div class="text-brand-red font-bold text-lg">
                        {{ novel.voteCount || 0 }}
                    </div>
                    <div class="text-xs text-gray-400">推荐票</div>
                </div>
            </div>
        </RouterLink>
    </div>
  </div>
</template>