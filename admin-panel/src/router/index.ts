import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/systems',
      name: 'systems',
      component: () => import('../pages/dashboard/MySystems.vue'),
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../pages/dashboard/LandingPage.vue'),
    },
    {
      path: '/add_system',
      name: 'add_system',
      component: () => import('../pages/dashboard/AddSystem.vue'),
    }
  ],
})

export default router
