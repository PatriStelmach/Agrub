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
      component: () => import('@/pages/alerts/AlertsPage.vue'),
    },
    {
      path: '/systems_library',
      name: 'systems_library',
      component: () => import('../pages/systems/SystemsLibrary.vue'),
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
      path: '/add_new_system',
      name: 'add_new_system',
      component: () => import('../pages/systems/AddNew.vue'),
    },
    {
      path: '/import_plugins',
      name: 'import_plugins',
      component: () => import('../pages/plugins/ImportPlugin.vue')
    },
    {
      path: '/team',
      name: 'team',
      component: () => import('../pages/users/TeamMembers.vue')
    },
    {
      path: '/logs',
      name: 'logs',
      component: () => import('../pages/logs/LogsPage.vue')
    }

  ],
})

export default router
