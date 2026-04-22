import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/my_systems',
      name: 'my_systems',
      component: () => import('../pages/systems/MySystems.vue'),
    },
    {
      path: '/all_systems',
      name: 'all_systems',
      component: () => import('../pages/systems/AllSystems.vue'),
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/pages/alerts/AlertsPage.vue'),
    },
    {
      path: '/alerts',
      name: 'alerts',
      component: () => import('@/pages/alerts/AlertsPage.vue'),
    },
    {
      path: '/plugins_library',
      name: 'plugins_library',
      component: () => import('../pages/plugins/PluginsLibrary.vue'),
    },
    {
      path: '/my_plugins',
      name: 'my_plugins',
      component: () => import('../pages/plugins/MyPlugins.vue'),
    },
    {
      path: '/import_plugins',
      name: 'import_plugins',
      component: () => import('../pages/plugins/ImportPlugin.vue')
    },
    {
      path: '/team',
      name: 'team',
      component: () => import('../pages/users/TeamPage.vue')
    },
    {
      path: '/logs',
      name: 'logs',
      component: () => import('../pages/logs/LogsPage.vue')
    }

  ],
})

export default router
