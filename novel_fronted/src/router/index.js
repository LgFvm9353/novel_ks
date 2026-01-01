import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import NovelDetail from '../views/NovelDetail.vue'
import ChapterReader from '../views/ChapterReader.vue'
import ReaderProfile from '../views/ReaderProfile.vue'
import Ranking from '../views/Ranking.vue'
import AuthorDashboard from '../views/author/AuthorDashboard.vue'
import AuthorNovelEdit from '../views/author/AuthorNovelEdit.vue'
import AuthorNovelChapters from '../views/author/AuthorNovelChapters.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AdminUsers from '../views/admin/AdminUsers.vue'
import AdminAuthors from '../views/admin/AdminAuthors.vue'
import { useAuthStore } from '../store/auth'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/login', name: 'login', component: Login },
  { path: '/register', name: 'register', component: Register },
  { path: '/novel/:id', name: 'novel-detail', component: NovelDetail, props: true },
  { path: '/ranking', name: 'ranking', component: Ranking },
  {
    path: '/novel/:id/chapter/:chapterNumber',
    name: 'chapter-reader',
    component: ChapterReader,
    props: true,
  },
  {
    path: '/reader/profile',
    name: 'reader-profile',
    component: ReaderProfile,
    meta: { requiresAuth: true, roles: ['reader', 'author', 'admin'] },
  },
  {
    path: '/author/dashboard',
    name: 'author-dashboard',
    component: AuthorDashboard,
    meta: { requiresAuth: true, roles: ['reader', 'author'] },
  },
  {
    path: '/author/novel/new',
    name: 'author-novel-new',
    component: AuthorNovelEdit,
    meta: { requiresAuth: true, roles: ['author'] },
  },
  {
    path: '/author/novel/:id/edit',
    name: 'author-novel-edit',
    component: AuthorNovelEdit,
    props: true,
    meta: { requiresAuth: true, roles: ['author'] },
  },
  {
    path: '/author/novel/:id/chapters',
    name: 'author-novel-chapters',
    component: AuthorNovelChapters,
    props: true,
    meta: { requiresAuth: true, roles: ['author'] },
  },
  {
    path: '/admin/dashboard',
    name: 'admin-dashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, roles: ['admin'] },
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: AdminUsers,
    meta: { requiresAuth: true, roles: ['admin'] },
  },
  {
    path: '/admin/authors',
    name: 'admin-authors',
    component: AdminAuthors,
    meta: { requiresAuth: true, roles: ['admin'] },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  if (!authStore.initialized) {
    await authStore.initFromStorage()
  }

  if (to.meta.requiresAuth) {
    if (!authStore.token) {
      return next({ name: 'login', query: { redirect: to.fullPath } })
    }
    if (!authStore.user) {
      await authStore.fetchCurrentUser()
    }
    const allowedRoles = to.meta.roles
    if (Array.isArray(allowedRoles) && authStore.user && !allowedRoles.includes(authStore.user.role)) {
      return next({ name: 'home' })
    }
  }

  next()
})

export default router

