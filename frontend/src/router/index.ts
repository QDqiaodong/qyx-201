import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/Home.vue')
  },
  {
    path: '/kits',
    name: 'KitManagement',
    component: () => import('@/pages/KitManagement.vue')
  },
  {
    path: '/staff',
    name: 'StaffManagement',
    component: () => import('@/pages/StaffManagement.vue')
  },
  {
    path: '/change-logs',
    name: 'ChangeLogs',
    component: () => import('@/pages/ChangeLogs.vue')
  },
  {
    path: '/monthly-summary',
    name: 'MonthlySummary',
    component: () => import('@/pages/MonthlySummary.vue')
  },
  {
    path: '/staff-kits',
    name: 'StaffKits',
    component: () => import('@/pages/StaffKits.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
