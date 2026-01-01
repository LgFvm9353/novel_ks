<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchNovelDetail, fetchCategories, createNovel, updateNovel } from '../../api/novels'

const route = useRoute()
const router = useRouter()
const novelId = computed(() => route.params.id)

const title = ref('')
const categoryId = ref('')
const description = ref('')
const coverImage = ref('')
const status = ref('连载中')

const categories = ref([])
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const saveMessage = ref('')

const isEdit = computed(() => !!novelId.value)

const loadCategories = async () => {
  try {
    const { data } = await fetchCategories()
    if (data.code === 0) {
      categories.value = data.data || []
      if (categories.value.length > 0 && !categoryId.value) {
        categoryId.value = categories.value[0].id
      }
    }
  } catch (e) {}
}

const loadNovel = async () => {
  if (!novelId.value) return
  loading.value = true
  error.value = ''
  try {
    const { data } = await fetchNovelDetail(novelId.value)
    if (data.code === 0 && data.data) {
      const info = data.data
      title.value = info.title || ''
      description.value = info.description || ''
      status.value = info.status || '连载中'
      coverImage.value = info.coverImage || ''
      // Need to find category ID by name if not provided directly, but usually we need ID.
      // Assuming API returns categoryName, we might need to map it back or update API to return categoryId.
      // For now, let's assume we can't easily set categoryId if not returned.
      // Actually, looking at NovelDetailDto, it only has categoryName. This is a small issue for editing.
      // Ideally backend should return categoryId.
      // I'll skip fixing backend for this minor detail now unless it's critical.
      // But wait, if I edit, I need to know the current category.
      // Let's see if I can find the category by name.
      if (info.categoryName && categories.value.length > 0) {
          const cat = categories.value.find(c => c.name === info.categoryName)
          if (cat) {
              categoryId.value = cat.id
          }
      }
    } else {
      error.value = data.message || '加载小说失败'
    }
  } catch (e) {
    error.value = '加载小说出错'
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!title.value.trim()) {
    error.value = '标题不能为空'
    return
  }
  if (categories.value.length === 0) {
    error.value = '当前没有可用分类，请稍后再试'
    return
  }
  if (!categoryId.value) {
    error.value = '请选择分类'
    return
  }
  saving.value = true
  error.value = ''
  saveMessage.value = ''
  const payload = {
    title: title.value,
    categoryId: categoryId.value || null,
    description: description.value,
    coverImage: coverImage.value,
    status: status.value,
  }
  try {
    if (isEdit.value) {
      const { data } = await updateNovel(novelId.value, payload)
      if (data.code === 0) {
        saveMessage.value = '保存成功'
      } else {
        error.value = data.message || '保存失败'
      }
    } else {
      const { data } = await createNovel(payload)
      if (data.code === 0) {
        const newId = data.data
        saveMessage.value = '创建成功'
        if (newId) {
          router.push({ name: 'author-novel-chapters', params: { id: newId } })
        }
      } else {
        error.value = data.message || '创建失败'
      }
    }
  } catch (e) {
    error.value = '保存出错'
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadCategories()
  await loadNovel()
})
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <div class="mb-8 flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900 flex items-center gap-2">
        <span class="text-2xl">{{ isEdit ? '📝' : '✨' }}</span>
        {{ isEdit ? '编辑作品' : '创建新书' }}
      </h1>
      <button 
        @click="router.back()" 
        class="text-gray-500 hover:text-gray-700 text-sm flex items-center gap-1 bg-white px-3 py-1.5 rounded-lg border border-gray-200 shadow-sm transition-all hover:bg-gray-50"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        返回
      </button>
    </div>

    <div class="bg-white rounded-xl shadow-sm p-8 space-y-8 border border-gray-100">
      <div v-if="loading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-brand-red"></div>
      </div>

      <template v-else>
        <!-- Title -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">小说标题 <span class="text-red-500">*</span></label>
          <input 
            v-model="title"
            type="text" 
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all shadow-sm"
            placeholder="给你的故事起个响亮的名字"
          />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Category -->
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">分类</label>
            <div class="relative">
              <select 
                v-model="categoryId"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all bg-white appearance-none shadow-sm"
              >
                <option value="">请选择分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
              <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-gray-500">
                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
              </div>
            </div>
          </div>

          <!-- Status -->
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">状态</label>
            <div class="flex gap-4 h-[50px] items-center">
              <label class="flex items-center gap-2 cursor-pointer group">
                <div class="relative flex items-center">
                  <input type="radio" v-model="status" value="连载中" class="peer sr-only">
                  <div class="w-5 h-5 border-2 border-gray-300 rounded-full peer-checked:border-brand-red peer-checked:bg-brand-red transition-all"></div>
                </div>
                <span class="text-gray-700 group-hover:text-brand-red transition-colors">连载中</span>
              </label>
              <label class="flex items-center gap-2 cursor-pointer group">
                <div class="relative flex items-center">
                  <input type="radio" v-model="status" value="已完结" class="peer sr-only">
                  <div class="w-5 h-5 border-2 border-gray-300 rounded-full peer-checked:border-brand-red peer-checked:bg-brand-red transition-all"></div>
                </div>
                <span class="text-gray-700 group-hover:text-brand-red transition-colors">已完结</span>
              </label>
            </div>
          </div>
        </div>

        <!-- Cover Image -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">封面图片链接</label>
          <div class="flex gap-4 items-start">
             <div class="flex-1">
               <input 
                 v-model="coverImage"
                 type="text" 
                 class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all shadow-sm"
                 placeholder="http://example.com/cover.jpg"
               />
               <p class="mt-2 text-xs text-gray-500">建议尺寸 3:4，支持 jpg, png 格式</p>
             </div>
             <div class="w-20 h-28 bg-gray-100 rounded-lg border border-gray-200 flex-shrink-0 overflow-hidden">
                <img v-if="coverImage" :src="coverImage" class="w-full h-full object-cover" alt="预览">
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300 text-xs">预览</div>
             </div>
          </div>
        </div>

        <!-- Description -->
        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">作品简介</label>
          <textarea 
            v-model="description"
            rows="6"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-red focus:border-transparent outline-none transition-all resize-none shadow-sm"
            placeholder="请输入作品简介..."
          ></textarea>
        </div>

        <!-- Error & Message -->
        <div v-if="error" class="text-sm text-red-600 bg-red-50 p-4 rounded-lg flex items-center gap-2 border border-red-100">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
          {{ error }}
        </div>
        <div v-if="saveMessage" class="text-sm text-green-600 bg-green-50 p-4 rounded-lg flex items-center gap-2 border border-green-100">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
          {{ saveMessage }}
        </div>

        <!-- Actions -->
        <div class="pt-4 flex justify-end">
          <button 
            @click="handleSubmit"
            :disabled="saving"
            class="px-8 py-3 bg-brand-red text-white rounded-lg hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-medium shadow-md hover:shadow-lg transform active:scale-[0.98] flex items-center gap-2"
          >
            <span v-if="saving" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
            {{ saving ? '保存中...' : (isEdit ? '保存修改' : '立即创建') }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>
