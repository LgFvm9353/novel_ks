<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  fetchAuthorChapters,
  createAuthorChapter,
  updateAuthorChapter,
  deleteAuthorChapter,
  fetchChapterDetail,
} from '../../api/novels'

const route = useRoute()
const router = useRouter()
const novelId = route.params.id

const chapters = ref([])
const loading = ref(false)
const error = ref('')

const formVisible = ref(false)
const formTitle = ref('')
const formContent = ref('')
const formError = ref('')
const saving = ref(false)
const saveMessage = ref('')
const editingChapterNumber = ref(null)

const loadChapters = async () => {
  if (!novelId) return
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchAuthorChapters(novelId)
    if (data.code === 0) {
      chapters.value = data.data || []
    } else {
      error.value = data.message || '加载章节失败'
    }
  } catch (e) {
    error.value = '加载章节出错'
  } finally {
    loading.value = false
  }
}

const openCreateForm = () => {
  editingChapterNumber.value = null
  formTitle.value = ''
  formContent.value = ''
  formError.value = ''
  saveMessage.value = ''
  formVisible.value = true
}

const openEditForm = async (chapter) => {
  if (!novelId) return
  editingChapterNumber.value = chapter.chapterNumber
  formTitle.value = chapter.title || ''
  formContent.value = ''
  formError.value = ''
  saveMessage.value = ''
  formVisible.value = true
  try {
    const { data } = await fetchChapterDetail(novelId, chapter.chapterNumber)
    if (data.code === 0 && data.data) {
      formContent.value = data.data.content || ''
    }
  } catch (e) {}
}

const handleSubmit = async () => {
  if (!novelId) return
  if (saving.value) return
  const title = formTitle.value.trim()
  if (!title) {
    formError.value = '章节标题不能为空'
    return
  }
  saving.value = true
  formError.value = ''
  saveMessage.value = ''
  try {
    if (editingChapterNumber.value) {
      const { data } = await updateAuthorChapter(novelId, editingChapterNumber.value, {
        title,
        content: formContent.value,
      })
      if (data.code === 0) {
        saveMessage.value = '保存成功'
        await loadChapters()
      } else {
        formError.value = data.message || '保存失败'
      }
    } else {
      const { data } = await createAuthorChapter(novelId, {
        title,
        content: formContent.value,
      })
      if (data.code === 0) {
        saveMessage.value = '创建成功'
        formTitle.value = ''
        formContent.value = ''
        await loadChapters()
      } else {
        formError.value = data.message || '创建失败'
      }
    }
  } catch (e) {
    formError.value = '提交出错'
  } finally {
    saving.value = false
  }
}

const handleDelete = async (chapter) => {
  if (!novelId) return
  const ok = window.confirm('确定要删除该章节吗？')
  if (!ok) return
  try {
    const { data } = await deleteAuthorChapter(novelId, chapter.chapterNumber)
    if (data.code === 0) {
      await loadChapters()
      if (editingChapterNumber.value === chapter.chapterNumber) {
          formVisible.value = false
          editingChapterNumber.value = null
      }
    } else {
      error.value = data.message || '删除失败'
    }
  } catch (e) {
    error.value = '删除出错'
  }
}

const goBack = () => {
  router.push({ name: 'author-dashboard' })
}

onMounted(() => {
  loadChapters()
})
</script>

<template>
  <div class="h-[calc(100vh-80px)] max-w-[1600px] mx-auto p-4 flex flex-col">
    <!-- Header -->
    <div class="flex items-center justify-between mb-4">
        <h1 class="text-xl font-bold text-gray-900 flex items-center gap-2">
            <span class="text-xl">📚</span>
            章节管理
        </h1>
        <button 
            @click="goBack" 
            class="text-gray-500 hover:text-gray-700 text-sm flex items-center gap-1 bg-white px-3 py-1.5 rounded-lg border border-gray-200 shadow-sm transition-all hover:bg-gray-50"
        >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            返回工作台
        </button>
    </div>

    <div class="flex-1 flex flex-col md:flex-row gap-6 min-h-0">
        <!-- Left Sidebar: Chapter List -->
        <div class="w-full md:w-80 flex flex-col bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden flex-shrink-0">
        <div class="p-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
            <h2 class="font-bold text-gray-900">章节列表</h2>
            <button 
            @click="openCreateForm"
            class="text-xs px-3 py-1.5 bg-brand-red text-white rounded-lg hover:bg-red-600 transition-colors shadow-sm flex items-center gap-1"
            >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
            </svg>
            新建章节
            </button>
        </div>
        
        <div class="flex-1 overflow-y-auto p-2 space-y-1 custom-scrollbar">
            <div v-if="loading" class="text-center py-8 text-gray-400 text-sm">
            <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-brand-red mx-auto mb-2"></div>
            加载中...
            </div>
            <div v-else-if="chapters.length === 0" class="text-center py-8 text-gray-400 text-sm">
            暂无章节，点击右上角新建
            </div>
            <div 
                v-for="chapter in chapters"
                :key="chapter.id"
                class="group relative"
            >
                <button
                    @click="openEditForm(chapter)"
                    class="w-full text-left px-3 py-2.5 rounded-lg text-sm transition-all flex justify-between items-center border border-transparent"
                    :class="editingChapterNumber === chapter.chapterNumber ? 'bg-red-50 text-brand-red font-medium border-red-100' : 'hover:bg-gray-50 text-gray-700'"
                >
                    <span class="truncate pr-8">
                        {{ chapter.chapterNumber }}. {{ chapter.title }}
                    </span>
                    <span class="text-xs text-gray-400 group-hover:text-gray-500 font-normal">
                        {{ new Date(chapter.createdAt).toLocaleDateString() }}
                    </span>
                </button>
                <button 
                    @click.stop="handleDelete(chapter)"
                    class="absolute right-2 top-1/2 -translate-y-1/2 p-1.5 text-gray-300 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-all"
                    title="删除章节"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                </button>
            </div>
        </div>
        </div>

        <!-- Right Area: Editor -->
        <div class="flex-1 flex flex-col bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div v-if="!formVisible" class="flex-1 flex flex-col items-center justify-center text-gray-400 bg-gray-50/30">
            <div class="text-6xl mb-4 opacity-20">📝</div>
            <p class="text-lg font-medium text-gray-500">选择章节进行编辑</p>
            <p class="text-sm mt-2">或者点击左侧“新建章节”开始创作</p>
        </div>

        <div v-else class="flex flex-col h-full">
            <!-- Header -->
            <div class="p-4 border-b border-gray-100 flex items-center justify-between bg-gray-50">
                <div class="flex items-center gap-4 flex-1">
                    <span class="font-bold text-gray-900 whitespace-nowrap">
                        {{ editingChapterNumber ? `编辑第 ${editingChapterNumber} 章` : '新建章节' }}
                    </span>
                    <div class="h-4 w-px bg-gray-300"></div>
                    <input 
                        v-model="formTitle"
                        type="text"
                        class="flex-1 bg-transparent border-none focus:ring-0 text-sm font-medium placeholder-gray-400 p-0"
                        placeholder="在此输入章节标题..."
                    />
                </div>
                <div class="flex items-center gap-3">
                    <span v-if="saveMessage" class="text-xs text-green-600 bg-green-50 px-2 py-1 rounded flex items-center gap-1">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                        </svg>
                        {{ saveMessage }}
                    </span>
                    <button 
                        @click="handleSubmit"
                        :disabled="saving"
                        class="px-4 py-1.5 bg-brand-red text-white text-sm rounded-lg hover:bg-red-600 disabled:opacity-50 transition-colors shadow-sm font-medium"
                    >
                        {{ saving ? '保存中...' : '保存章节' }}
                    </button>
                    <button 
                        @click="formVisible = false"
                        class="text-gray-400 hover:text-gray-600 p-1.5 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>
            </div>

            <!-- Editor -->
            <div class="flex-1 relative">
                <textarea 
                    v-model="formContent"
                    class="absolute inset-0 w-full h-full p-6 resize-none outline-none text-gray-800 leading-relaxed text-lg font-serif"
                    placeholder="开始你的创作..."
                ></textarea>
            </div>
            
            <!-- Footer -->
            <div class="p-2 border-t border-gray-100 bg-gray-50 text-xs text-gray-400 flex justify-between px-4">
                <span>{{ formContent.length }} 字</span>
                <span v-if="formError" class="text-red-500">{{ formError }}</span>
            </div>
        </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #e5e7eb;
  border-radius: 20px;
}
</style>
