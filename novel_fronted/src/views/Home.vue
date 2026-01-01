<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchNovels } from '../api/novels'

const novels = ref([])
const loading = ref(false)
const error = ref('')

const loadData = async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchNovels({ page: 1, size: 20 })
    if (data.code === 0) {
      novels.value = data.data.items
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
</script>

<template>
  <div class="space-y-8">
    <!-- Hero Section -->
    <div class="bg-gradient-to-r from-brand-red to-red-600 rounded-2xl p-8 md:p-12 text-white shadow-lg relative overflow-hidden">
      <div class="relative z-10 max-w-2xl">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">探索精彩世界</h1>
        <p class="text-red-100 text-lg mb-8">海量热门小说，随时随地畅读。发现你的下一个最爱。</p>
        <button class="bg-white text-brand-red px-6 py-3 rounded-lg font-bold hover:bg-red-50 transition-colors shadow-md">
          开始阅读
        </button>
      </div>
      <div class="absolute right-0 top-0 h-full w-1/3 bg-white/10 skew-x-12 transform translate-x-1/2"></div>
      <div class="absolute -bottom-10 -right-10 w-64 h-64 bg-white/10 rounded-full blur-3xl"></div>
    </div>

    <!-- Novel List -->
    <div>
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <span class="w-1 h-6 bg-brand-red rounded-full"></span>
          热门推荐
        </h2>
        <!-- <RouterLink to="/category" class="text-sm text-gray-500 hover:text-brand-red flex items-center gap-1">
          查看全部
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
          </svg>
        </RouterLink> -->
      </div>

      <div v-if="loading" class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">
        <div v-for="i in 10" :key="i" class="animate-pulse space-y-3">
          <div class="bg-gray-200 h-64 rounded-xl"></div>
          <div class="h-4 bg-gray-200 rounded w-3/4"></div>
          <div class="h-3 bg-gray-200 rounded w-1/2"></div>
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

      <div
        v-else
        class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6"
      >
        <RouterLink
          v-for="novel in novels"
          :key="novel.id"
          :to="{ name: 'novel-detail', params: { id: novel.id } }"
          class="group space-y-3"
        >
          <div class="aspect-[3/4] bg-gray-100 rounded-xl overflow-hidden relative shadow-sm group-hover:shadow-md transition-all duration-300">
            <img
              v-if="novel.coverImage"
              :src="novel.coverImage"
              class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
              alt="cover"
            />
            <div
              v-else
              class="w-full h-full flex items-center justify-center text-gray-400 bg-gray-100"
            >
              <div class="text-center">
                <span class="block text-2xl mb-1">📚</span>
                <span class="text-xs">暂无封面</span>
              </div>
            </div>
            
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-end p-3">
               <p class="text-white text-xs line-clamp-2">{{ novel.description || '暂无简介' }}</p>
            </div>

            <div
              v-if="novel.status"
              class="absolute top-2 right-2 bg-black/60 backdrop-blur-sm text-white text-[10px] px-2 py-0.5 rounded-full"
            >
              {{ novel.status }}
            </div>
          </div>
          
          <div>
            <h3 class="font-bold text-gray-900 truncate group-hover:text-brand-red transition-colors" :title="novel.title">
              {{ novel.title }}
            </h3>
            <div class="flex items-center justify-between text-xs text-gray-500 mt-1">
              <span class="truncate max-w-[60%]">{{ novel.categoryName || '未分类' }}</span>
              <span class="bg-gray-100 px-1.5 py-0.5 rounded text-gray-400 scale-90 origin-right">
                {{ novel.voteCount || 0 }} 票
              </span>
            </div>
          </div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>
