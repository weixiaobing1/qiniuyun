import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'upload', component: () => import('../pages/UploadPage.vue') },
  { path: '/preview', name: 'preview', component: () => import('../pages/PreviewPage.vue') },
  { path: '/history', name: 'history', component: () => import('../pages/HistoryPage.vue') }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
