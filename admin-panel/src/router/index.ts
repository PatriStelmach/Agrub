import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/my_systems',
      name: 'systems',
      component: () => import('../pages/systems/MySystems.vue'),
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../pages/dashboard/LandingPage.vue'),
    },
    {
      path: '/systems_library',
      name: 'systems_library',
      component: () => import('../pages/systems/SystemsLibrary.vue'),
    },
    {
      path: '/plugins_library',
      name: 'plugins_library',
      component: () => import('../pages/plugins/PluginLibrary.vue'),
    },
    {
      path: '/my_plugins',
      name: 'my_plugins',
      component: () => import('../pages/plugins/MyPlugins.vue'),
    }

  ],
})

export default router
