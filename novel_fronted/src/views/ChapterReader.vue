<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../api/http'
import { useAuthStore } from '../store/auth'
import { saveReadingHistory } from '../api/novels'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const chapter = ref(null)
const loading = ref(false)
const error = ref('')

const isLastChapter = ref(false)

const loadChapter = async () => {
  const novelId = route.params.id
  const chapterNumber = route.params.chapterNumber
  if (!novelId || !chapterNumber) return
  loading.value = true
  error.value = ''
  isLastChapter.value = false
  try {
    const { data } = await http.get(`/novels/${novelId}/chapters/${chapterNumber}`)
    if (data.code === 0) {
      chapter.value = data.data
      if (authStore.token && chapter.value) {
        saveReadingHistory({
          novelId: chapter.value.novelId,
          chapterNumber: chapter.value.chapterNumber,
          chapterTitle: chapter.value.title,
        })
      }
      // Scroll to top
      window.scrollTo(0, 0)
    } else {
      if (data.message === '章节不存在') {
         isLastChapter.value = true
      }
      error.value = data.message || '加载失败'
    }
  } catch (e) {
    error.value = '请求出错'
  } finally {
    loading.value = false
  }
}

const goPrev = () => {
  const novelId = route.params.id
  const current = Number(route.params.chapterNumber)
  if (current > 1) {
    router.push({
      name: 'chapter-reader',
      params: { id: novelId, chapterNumber: current - 1 },
    })
  }
}

const goNext = () => {
  const novelId = route.params.id
  const current = Number(route.params.chapterNumber)
  router.push({
    name: 'chapter-reader',
    params: { id: novelId, chapterNumber: current + 1 },
  })
}

const goToc = () => {
  const novelId = route.params.id
  router.push({ name: 'novel-detail', params: { id: novelId } })
}

onMounted(loadChapter)

watch(
  () => route.fullPath,
  () => {
    loadChapter()
  }
)
</script>

<template>
  <div class="min-h-screen bg-[#f0f0f0] pb-20">
    <!-- Header -->
    <header class="bg-white shadow-sm sticky top-0 z-30 h-14">
      <div class="max-w-7xl mx-auto px-4 h-full flex items-center justify-between">
        <div class="flex items-center gap-4 overflow-hidden">
          <button @click="goToc" class="text-gray-500 hover:text-brand-red flex items-center gap-1 flex-shrink-0 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
            </svg>
            <span class="hidden sm:inline">返回目录</span>
          </button>
          <div class="h-4 w-px bg-gray-300 mx-2 hidden sm:block"></div>
          <h1 class="text-sm sm:text-base font-medium text-gray-900 truncate" v-if="chapter">
            {{ chapter.title }}
          </h1>
        </div>
        <div class="flex items-center gap-4 text-sm">
           <RouterLink to="/" class="text-gray-500 hover:text-brand-red transition-colors">首页</RouterLink>
           <RouterLink v-if="authStore.user" to="/reader/profile" class="text-gray-500 hover:text-brand-red transition-colors">书架</RouterLink>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-3xl mx-auto px-4 py-6 sm:py-8">
      <!-- Loading -->
      <div v-if="loading" class="flex justify-center py-40">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-brand-red"></div>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="flex flex-col items-center justify-center py-40 text-center">
        <template v-if="isLastChapter">
          <div class="text-2xl font-bold text-gray-800 mb-4">已经是最后一章了</div>
          <div class="text-gray-500 mb-8">作者正在努力更新中...</div>
          <div class="flex gap-4">
             <button 
              @click="goPrev" 
              class="px-6 py-2 bg-white border border-gray-300 rounded-full hover:bg-gray-50 transition-colors text-sm"
            >
              返回上一章
            </button>
            <button 
              @click="goToc" 
              class="px-6 py-2 bg-brand-red text-white rounded-full hover:bg-red-600 transition-colors text-sm shadow-sm"
            >
              返回目录
            </button>
          </div>
        </template>
        <template v-else>
          <div class="text-red-500 mb-4">{{ error }}</div>
          <button 
            @click="loadChapter" 
            class="px-6 py-2 bg-white border border-gray-300 rounded-full hover:bg-gray-50 transition-colors text-sm"
          >
            重试
          </button>
        </template>
      </div>

      <!-- Reader Card -->
      <article 
        v-else-if="chapter" 
        class="bg-[#faf9f6] text-[#2c2c2c] shadow-sm rounded-lg overflow-hidden min-h-[80vh]"
      >
        <div class="px-6 py-12 md:px-12 md:py-16 lg:px-16">
          <h2 class="text-2xl md:text-3xl font-bold mb-12 text-center font-serif text-gray-900 leading-tight">
            {{ chapter.title }}
          </h2>
          
          <div class="prose prose-lg md:prose-xl max-w-none font-serif leading-loose tracking-wide whitespace-pre-wrap text-justify selection:bg-brand-red/20">
            {{ chapter.content }}
          </div>
        </div>
      </article>
    </main>

    <!-- Bottom Navigation -->
    <div class="fixed bottom-0 left-0 right-0 bg-white/95 backdrop-blur border-t border-gray-200 z-30 shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.05)]">
      <div class="max-w-3xl mx-auto px-4 py-3">
        <div class="flex justify-between items-center gap-4">
          <button 
            @click="goPrev"
            :disabled="Number(route.params.chapterNumber) <= 1"
            class="flex-1 px-4 py-2.5 rounded-lg border border-gray-200 text-gray-700 hover:bg-gray-50 hover:border-gray-300 disabled:opacity-40 disabled:cursor-not-allowed transition-all text-sm font-medium"
          >
            上一章
          </button>
          
          <button 
            @click="goToc"
            class="px-4 py-2.5 text-gray-500 hover:text-brand-red transition-colors"
            title="目录"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>

          <button 
            @click="goNext"
            class="flex-1 px-4 py-2.5 rounded-lg bg-brand-red text-white hover:bg-red-600 shadow-sm hover:shadow transition-all text-sm font-medium"
          >
            下一章
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Ensure the reading background is pleasant */
.prose {
  color: #2c2c2c;
}
</style>
