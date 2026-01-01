<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { fetchNovelDetail, fetchChapters, fetchComments, addComment, deleteComment, voteNovel, fetchReadingHistory, checkVoteStatus } from '../api/novels'

const props = defineProps(['id'])
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const novel = ref(null)
const loading = ref(false)
const error = ref('')

const chapters = ref([])
const chaptersLoading = ref(false)
const chaptersError = ref('')

const comments = ref([])
const commentsLoading = ref(false)
const commentError = ref('')
const commentContent = ref('')

const voting = ref(false)
const voteMessage = ref('')
const voteMessageType = ref('success')
const hasVoted = ref(false)

const readingHistoryForNovel = ref(null)

const isLoggedIn = computed(() => !!authStore.token)
const isAuthor = computed(() => {
  if (!novel.value || !authStore.user) return false
  // Use authorUserId if available, otherwise fallback to authorId comparison (though less reliable if ID types differ)
  return novel.value.authorUserId === authStore.user.id
})

const loadNovel = async () => {
  const novelId = route.params.id
  if (!novelId) return
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchNovelDetail(novelId)
    if (data.code === 0) {
      novel.value = data.data
    } else {
      error.value = data.message || '加载失败'
    }
  } catch (e) {
    error.value = '请求出错'
  } finally {
    loading.value = false
  }
}

const loadChapters = async () => {
  const novelId = route.params.id
  if (!novelId) return
  chaptersLoading.value = true
  chaptersError.value = ''
  try {
    const { data } = await fetchChapters(novelId)
    if (data.code === 0) {
      chapters.value = data.data || []
    } else {
      chaptersError.value = data.message || '加载章节失败'
    }
  } catch (e) {
    chaptersError.value = '加载章节出错'
  } finally {
    chaptersLoading.value = false
  }
}

const loadComments = async () => {
  const novelId = route.params.id
  if (!novelId) return
  commentsLoading.value = true
  commentError.value = ''
  try {
    const { data } = await fetchComments(novelId)
    if (data.code === 0) {
      comments.value = data.data || []
    } else {
      commentError.value = data.message || '加载评论失败'
    }
  } catch (e) {
    commentError.value = '加载评论出错'
  } finally {
    commentsLoading.value = false
  }
}

const submitComment = async () => {
  const novelId = route.params.id
  if (!novelId) return
  if (!isLoggedIn.value) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  const content = commentContent.value.trim()
  if (!content) {
    commentError.value = '评论内容不能为空'
    return
  }
  commentsLoading.value = true
  commentError.value = ''
  try {
    const { data } = await addComment(novelId, content)
    if (data.code === 0) {
      commentContent.value = ''
      await loadComments()
    } else {
      commentError.value = data.message || '发表评论失败'
    }
  } catch (e) {
    commentError.value = '发表评论出错'
  } finally {
    commentsLoading.value = false
  }
}

const deleteCommentItem = async (commentId) => {
  const novelId = route.params.id
  if (!novelId) return
  commentsLoading.value = true
  commentError.value = ''
  try {
    const { data } = await deleteComment(novelId, commentId)
    if (data.code === 0) {
      comments.value = comments.value.filter((item) => item.id !== commentId)
    } else {
      commentError.value = data.message || '删除评论失败'
    }
  } catch (e) {
    commentError.value = '删除评论出错'
  } finally {
    commentsLoading.value = false
  }
}

const canDeleteComment = (comment) => {
  if (!authStore.user) return false
  return authStore.user.id && authStore.user.id === comment.userId
}

const checkUserVoteStatus = async () => {
  const novelId = route.params.id
  if (!novelId || !isLoggedIn.value) return
  try {
    const { data } = await checkVoteStatus(novelId)
    if (data.code === 0) {
      hasVoted.value = data.data
    }
  } catch (e) {
    console.error('Check vote status failed', e)
  }
}

const doVote = async () => {
  const novelId = route.params.id
  if (!novelId) return
  if (!isLoggedIn.value) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  voting.value = true
  voteMessage.value = ''
  voteMessageType.value = 'success'
  try {
    const { data } = await voteNovel(novelId)
    if (data.code === 0) {
      voteMessage.value = '投票成功'
      voteMessageType.value = 'success'
      hasVoted.value = true
      if (novel.value && typeof novel.value.voteCount === 'number') {
        novel.value.voteCount += 1
      }
      setTimeout(() => { voteMessage.value = '' }, 3000)
    } else {
      if (data.message === '已投过票') {
        hasVoted.value = true
        voteMessage.value = '您已投过票'
        voteMessageType.value = 'success' // Treat as success state for UI (show "Already Recommended")
        setTimeout(() => { voteMessage.value = '' }, 3000)
      } else {
        voteMessage.value = data.message || '投票失败'
        voteMessageType.value = 'error'
      }
    }
  } catch (e) {
    voteMessage.value = '投票出错'
    voteMessageType.value = 'error'
  } finally {
    voting.value = false
  }
}

const loadReadingHistoryForNovel = async () => {
  if (!authStore.token) {
    readingHistoryForNovel.value = null
    return
  }
  try {
    const { data } = await fetchReadingHistory()
    if (data.code === 0 && Array.isArray(data.data)) {
      const novelId = route.params.id
      const item = data.data.find((it) => it.novelId === novelId)
      readingHistoryForNovel.value = item || null
    }
  } catch (e) {
    readingHistoryForNovel.value = null
  }
}

const handleStartReading = () => {
  if (!novel.value) return
  
  // If has history, continue reading
  if (readingHistoryForNovel.value) {
    router.push({
      name: 'chapter-reader',
      params: {
        id: novel.value.id,
        chapterNumber: readingHistoryForNovel.value.chapterNumber,
      },
    })
    return
  }

  // Else start from chapter 1 (or the first available chapter)
  if (chapters.value && chapters.value.length > 0) {
    // Assuming chapters are sorted, pick the first one
    const firstChapter = chapters.value[0]
    router.push({
      name: 'chapter-reader',
      params: {
        id: novel.value.id,
        chapterNumber: firstChapter.chapterNumber,
      },
    })
  }
}

watch(isLoggedIn, (newVal) => {
  if (newVal) {
    checkUserVoteStatus()
    loadReadingHistoryForNovel()
  } else {
    hasVoted.value = false
    readingHistoryForNovel.value = null
  }
}, { immediate: true })

onMounted(async () => {
  await loadNovel()
  await Promise.all([loadChapters(), loadComments(), loadReadingHistoryForNovel(), checkUserVoteStatus()])
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-brand-red"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-50 text-red-600 p-8 rounded-xl text-center">
      {{ error }}
    </div>

    <!-- Not Found -->
    <div v-else-if="!novel" class="text-center py-20 text-gray-500">
      未找到该小说
    </div>

    <!-- Main Content -->
    <div v-else class="space-y-8">
      <!-- Novel Info Card -->
      <div class="bg-white rounded-2xl shadow-sm p-6 md:p-8 flex flex-col md:flex-row gap-8">
        <!-- Cover Image -->
        <div class="w-full md:w-64 flex-shrink-0">
           <div class="relative aspect-[3/4] bg-gray-200 rounded-lg overflow-hidden shadow-md">
             <img 
               v-if="novel.coverImage" 
               :src="novel.coverImage" 
               class="w-full h-full object-cover" 
               alt="novel cover"
             />
             <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
               <div class="text-center">
                 <div class="text-4xl mb-2">📖</div>
                 <div class="text-sm">暂无封面</div>
               </div>
             </div>
             <div v-if="novel.status" class="absolute top-0 right-0 bg-brand-red text-white text-xs px-2 py-1 rounded-bl-lg font-medium">
               {{ novel.status }}
             </div>
           </div>
        </div>
        
        <!-- Info -->
        <div class="flex-1 flex flex-col">
           <div class="mb-4">
             <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ novel.title }}</h1>
             <div class="flex flex-wrap gap-4 text-sm items-center text-gray-600">
                <span v-if="novel.categoryName" class="px-3 py-1 bg-gray-100 rounded-full font-medium">{{ novel.categoryName }}</span>
                <span class="text-gray-400">|</span>
                <span>{{ novel.status }}</span>
                <span class="text-gray-400">|</span>
                <span>更新于 {{ new Date(novel.updatedAt).toLocaleDateString() }}</span>
             </div>
           </div>
           
           <!-- Stats Grid -->
           <div class="flex gap-8 border-y border-gray-100 py-6 my-2">
              <div class="text-center">
                 <div class="text-2xl font-bold text-gray-900 font-mono">{{ novel.totalChapters || 0 }}</div>
                 <div class="text-xs text-gray-500 mt-1">总章节</div>
              </div>
              <div class="text-center px-8 border-l border-gray-100">
                 <div class="text-2xl font-bold text-gray-900 font-mono">{{ novel.voteCount || 0 }}</div>
                 <div class="text-xs text-gray-500 mt-1">推荐票</div>
              </div>
              <div class="text-center px-8 border-l border-gray-100 hidden sm:block">
                 <div class="text-2xl font-bold text-gray-900 font-mono">{{ novel.totalPages || 0 }}</div>
                 <div class="text-xs text-gray-500 mt-1">总页数</div>
              </div>
           </div>

           <div class="flex-1 py-4">
              <p class="text-gray-700 leading-relaxed line-clamp-6">{{ novel.description || '暂无简介' }}</p>
           </div>

           <!-- Actions -->
           <div class="flex flex-wrap gap-4 pt-4 mt-auto">
              <button 
                @click="handleStartReading" 
                :disabled="!chapters.length"
                class="bg-brand-red text-white px-8 py-3 rounded-full hover:bg-red-600 transition-colors shadow-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              >
                 <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z" />
                 </svg>
                 {{ readingHistoryForNovel ? `继续阅读 (第${readingHistoryForNovel.chapterNumber}章)` : '开始阅读' }}
              </button>
              
              <button 
                v-if="!isAuthor"
                @click="doVote" 
                :disabled="voting || hasVoted"
                class="px-8 py-3 border border-brand-red text-brand-red rounded-full hover:bg-red-50 transition-colors font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                :class="{'bg-red-50 text-red-400 border-red-200': hasVoted}"
              >
                 <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
                 </svg>
                 {{ voting ? '投票中...' : (hasVoted ? '已推荐' : '投推荐票') }}
              </button>
              
              <span v-if="voteMessage" class="flex items-center text-sm animate-fade-in-down" :class="voteMessageType === 'success' ? 'text-green-600' : 'text-red-600'">
                {{ voteMessage }}
              </span>
           </div>
        </div>
      </div>

      <!-- Chapter List -->
      <div class="bg-white rounded-2xl shadow-sm p-6 md:p-8">
         <div class="flex items-center justify-between mb-6">
            <h2 class="text-xl font-bold text-gray-900 border-l-4 border-brand-red pl-3">章节目录</h2>
            <span class="text-sm text-gray-500">共 {{ chapters.length }} 章</span>
         </div>
         
         <div v-if="chaptersLoading" class="py-8 text-center text-gray-500">章节加载中...</div>
         <div v-else-if="chaptersError" class="py-8 text-center text-red-500">{{ chaptersError }}</div>
         <div v-else-if="chapters.length === 0" class="py-8 text-center text-gray-500">暂无章节</div>
         
         <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            <RouterLink
              v-for="item in chapters" 
              :key="item.chapterNumber"
              :to="{ name: 'chapter-reader', params: { id: novel.id, chapterNumber: item.chapterNumber } }"
              class="group flex items-center p-3 rounded-lg hover:bg-gray-50 border border-transparent hover:border-gray-200 transition-all truncate"
            >
              <span class="text-gray-400 mr-2 text-sm group-hover:text-brand-red w-12 text-right flex-shrink-0">{{ item.chapterNumber }}章</span>
              <span class="text-gray-700 text-sm truncate group-hover:text-gray-900">{{ item.title }}</span>
            </RouterLink>
         </div>
      </div>

      <!-- Comments -->
       <div class="bg-white rounded-2xl shadow-sm p-6 md:p-8">
         <h2 class="text-xl font-bold text-gray-900 border-l-4 border-brand-red pl-3 mb-6">书友评论</h2>
         
         <!-- Comment Form -->
         <div class="mb-8 bg-gray-50 p-4 rounded-xl">
            <div v-if="!isLoggedIn" class="text-center py-4 text-gray-500">
               <RouterLink :to="{ name: 'login', query: { redirect: route.fullPath } }" class="text-brand-red hover:underline">登录</RouterLink> 后参与评论
            </div>
            <div v-else>
               <textarea
                 v-model="commentContent"
                 rows="3"
                 class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all resize-none bg-white"
                 placeholder="写下你的评论..."
               ></textarea>
               <div class="flex justify-between items-center mt-3">
                  <span class="text-xs text-gray-400">请文明发言</span>
                  <button
                    @click="submitComment"
                    :disabled="commentsLoading"
                    class="px-6 py-2 bg-brand-red text-white rounded-full text-sm hover:bg-red-600 disabled:opacity-50 transition-colors"
                  >
                    {{ commentsLoading ? '发送中...' : '发表评论' }}
                  </button>
               </div>
               <div v-if="commentError" class="text-sm text-red-500 mt-2">{{ commentError }}</div>
            </div>
         </div>

         <!-- Comment List -->
         <div v-if="commentsLoading && comments.length === 0" class="text-center py-8 text-gray-500">评论加载中...</div>
         <div v-else>
            <div v-if="comments.length === 0" class="text-center py-8 text-gray-500">暂无评论，快来抢沙发吧~</div>
            <div v-else class="space-y-6">
               <div v-for="item in comments" :key="item.id" class="flex gap-4 border-b border-gray-100 pb-6 last:border-0">
                  <div class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center text-gray-500 flex-shrink-0">
                     👤
                  </div>
                  <div class="flex-1">
                     <div class="flex items-center justify-between mb-1">
                        <span class="font-medium text-gray-900 text-sm">{{ item.username || '用户' + item.userId.substring(0,6) }}</span>
                        <span class="text-xs text-gray-400">{{ new Date(item.createdAt).toLocaleString() }}</span>
                     </div>
                     <p class="text-gray-700 text-sm leading-relaxed">{{ item.content }}</p>
                     
                     <div v-if="canDeleteComment(item)" class="mt-2 text-right">
                        <button 
                           @click="deleteCommentItem(item.id)"
                           class="text-xs text-red-400 hover:text-red-600 hover:underline"
                        >
                           删除
                        </button>
                     </div>
                  </div>
               </div>
            </div>
         </div>
       </div>
    </div>
  </div>
</template>

<style scoped>
/* Additional specific styles can go here */
</style>
