<script setup>
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { fetchAuthorProfile, becomeAuthor, fetchMyNovels, deleteAuthorNovel } from '../../api/novels'

const authStore = useAuthStore()
const user = computed(() => authStore.user)

const author = ref(null)
const novels = ref([])
const loading = ref(false)
const error = ref('')
const pending = ref(false)

const becoming = ref(false)
const becomeName = ref('')
const becomeBio = ref('')
const becomeError = ref('')

const loadMyNovels = async () => {
  try {
    const { data } = await fetchMyNovels()
    if (data.code === 0) {
      novels.value = data.data || []
    }
  } catch (e) {}
}

const loadData = async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchAuthorProfile()
    if (data.code === 0) {
      author.value = data.data
      await loadMyNovels()
    } else {
      if (data.message === '还不是作者') {
        author.value = null
        pending.value = false
      } else if (data.message === '申请待审核') {
        author.value = null
        pending.value = true
      } else {
        error.value = data.message || '加载作者信息失败'
      }
    }
  } catch (e) {
    error.value = '加载作者信息出错'
  } finally {
    loading.value = false
  }
}

const submitBecomeAuthor = async () => {
  if (becoming.value) return
  becoming.value = true
  becomeError.value = ''
  try {
    const payload = {
      name: becomeName.value,
      bio: becomeBio.value,
    }
    const { data } = await becomeAuthor(payload)
    if (data.code === 0) {
      pending.value = true
    } else {
      becomeError.value = data.message || '操作失败'
    }
  } catch (e) {
    becomeError.value = '操作出错'
  } finally {
    becoming.value = false
  }
}

const handleDeleteNovel = async (novel) => {
  if (!novel || !novel.id) return
  const ok = window.confirm('确定要删除该作品吗？此操作不可恢复！')
  if (!ok) return
  try {
    const { data } = await deleteAuthorNovel(novel.id)
    if (data.code === 0) {
      await loadMyNovels()
    } else {
      error.value = data.message || '删除失败'
    }
  } catch (e) {
    error.value = '删除出错'
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-8 space-y-8">
    <div class="flex items-center justify-between">
      <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-2">
        <span class="text-3xl">👨‍💻</span>
        作者工作台
      </h1>
      <div v-if="user" class="text-sm text-gray-500 bg-white px-3 py-1.5 rounded-full shadow-sm border border-gray-100">
        {{ user.username }} <span class="px-2 py-0.5 bg-gray-100 rounded-full text-xs ml-2 text-gray-600">{{ user.role }}</span>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-brand-red"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-50 text-red-600 p-6 rounded-xl border border-red-100 text-center">
      {{ error }}
    </div>

    <!-- Not Author State -->
    <div v-else-if="!author && !pending" class="max-w-2xl mx-auto mt-12">
      <div class="bg-white rounded-2xl shadow-lg p-10 text-center space-y-8 border border-gray-100">
        <div class="w-24 h-24 bg-brand-red/10 text-brand-red rounded-full flex items-center justify-center mx-auto text-5xl">
          ✍️
        </div>
        <div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2">成为作者</h2>
          <p class="text-gray-600 max-w-md mx-auto leading-relaxed">
            加入我们，开启您的创作之旅。创建、管理并发布您的小说，与百万读者分享您的故事。
          </p>
        </div>
        
        <div class="space-y-5 text-left max-w-sm mx-auto pt-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">笔名</label>
            <input 
              v-model="becomeName"
              type="text" 
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all shadow-sm"
              placeholder="请输入您的笔名"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">个人简介</label>
            <textarea 
              v-model="becomeBio"
              rows="3"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all resize-none shadow-sm"
              placeholder="简单介绍一下自己..."
            ></textarea>
          </div>
          
          <div v-if="becomeError" class="text-sm text-red-500 text-center bg-red-50 py-2 rounded-lg">
            {{ becomeError }}
          </div>

          <button 
            @click="submitBecomeAuthor"
            :disabled="becoming"
            class="w-full py-3 bg-brand-red text-white rounded-lg hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-medium shadow-md hover:shadow-lg transform active:scale-[0.98]"
          >
            {{ becoming ? '提交中...' : '立即申请' }}
          </button>
        </div>
      </div>
    </div>

    <div v-else-if="pending" class="max-w-2xl mx-auto mt-12">
      <div class="bg-white rounded-2xl shadow-lg p-12 text-center space-y-6 border border-gray-100">
        <div class="w-24 h-24 bg-yellow-100 text-yellow-500 rounded-full flex items-center justify-center mx-auto text-5xl animate-pulse">
          ⏳
        </div>
        <h2 class="text-2xl font-bold text-gray-900">作者申请审核中</h2>
        <p class="text-gray-600">您的作者申请已提交，请等待管理员审核通过后即可创建小说。</p>
      </div>
    </div>

    <!-- Author Dashboard -->
    <div v-else class="space-y-8">
      <!-- Author Profile Card -->
      <div class="bg-white rounded-xl shadow-sm p-8 border border-gray-100">
        <div class="flex flex-col md:flex-row items-start justify-between gap-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-900 mb-2">{{ author.name }}</h2>
            <p class="text-gray-500 max-w-2xl leading-relaxed">{{ author.bio || '暂无简介' }}</p>
          </div>
          <div class="flex gap-4">
             <div class="text-center px-6 py-3 bg-red-50 rounded-xl border border-red-100">
                <div class="text-2xl font-bold text-brand-red">{{ novels.length }}</div>
                <div class="text-xs text-gray-600 font-medium mt-1">作品数</div>
             </div>
             <!-- Ideally we would have more stats here like total views, etc. -->
          </div>
        </div>
      </div>

      <!-- Novels List -->
      <div class="space-y-6">
        <div class="flex items-center justify-between">
          <h3 class="text-xl font-bold text-gray-900 flex items-center gap-2">
            <span class="w-1 h-6 bg-brand-red rounded-full"></span>
            我的作品
          </h3>
          <RouterLink 
            :to="{ name: 'author-novel-new' }"
            class="px-5 py-2.5 bg-brand-red text-white text-sm font-medium rounded-lg hover:bg-red-600 transition-all shadow-sm hover:shadow flex items-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
            </svg>
            创建新书
          </RouterLink>
        </div>

        <div v-if="novels.length === 0" class="bg-white rounded-xl shadow-sm p-16 text-center text-gray-400 border border-gray-100">
           <div class="text-6xl mb-4 opacity-30">📝</div>
           <p class="text-lg">暂无作品，快去创建第一本小说吧！</p>
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div 
            v-for="novel in novels" 
            :key="novel.id" 
            class="bg-white rounded-xl shadow-sm hover:shadow-md transition-all border border-gray-100 overflow-hidden group flex flex-col"
          >
            <div class="flex p-4 gap-4">
              <!-- Cover -->
              <div class="w-24 h-32 flex-shrink-0 bg-gray-200 rounded-lg overflow-hidden relative">
                <img 
                  v-if="novel.coverImage" 
                  :src="novel.coverImage" 
                  :alt="novel.title" 
                  class="w-full h-full object-cover transition-transform group-hover:scale-105 duration-300"
                />
                <div v-else class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-300 text-xs text-center p-2">
                  暂无封面
                </div>
                <div class="absolute top-0 right-0 bg-brand-red text-white text-xs px-1.5 py-0.5 rounded-bl-lg font-medium shadow-sm">
                   {{ novel.status }}
                </div>
              </div>
              
              <!-- Info -->
              <div class="flex-1 min-w-0 flex flex-col">
                <h4 class="font-bold text-gray-900 text-lg mb-1 truncate" :title="novel.title">{{ novel.title }}</h4>
                <div class="flex items-center gap-2 mb-2">
                   <span class="text-xs px-2 py-0.5 bg-gray-100 text-gray-600 rounded-full truncate max-w-[100px]">{{ novel.categoryName || '未分类' }}</span>
                </div>
                <p class="text-sm text-gray-500 line-clamp-2 mb-auto leading-relaxed">{{ novel.description || '暂无简介' }}</p>
              </div>
            </div>

            <!-- Actions -->
            <div class="border-t border-gray-50 p-3 flex items-center gap-2 bg-gray-50/50">
              <RouterLink 
                :to="{ name: 'author-novel-chapters', params: { id: novel.id } }"
                class="flex-1 text-center py-2 bg-white border border-gray-200 text-gray-700 text-sm font-medium rounded-lg hover:border-brand-red hover:text-brand-red transition-colors shadow-sm"
              >
                章节管理
              </RouterLink>
              <RouterLink 
                :to="{ name: 'author-novel-edit', params: { id: novel.id } }"
                class="flex-1 text-center py-2 bg-white border border-gray-200 text-gray-700 text-sm font-medium rounded-lg hover:border-brand-red hover:text-brand-red transition-colors shadow-sm"
              >
                编辑信息
              </RouterLink>
              <button 
                @click="handleDeleteNovel(novel)"
                class="px-3 py-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                title="删除作品"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
