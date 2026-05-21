import { createRouter, createWebHistory } from 'vue-router'

import MySystems from '../pages/systems/MySystems.vue'
import ActiveAlertsPage from '../pages/alerts/active/ActiveAlertsPage.vue'
import AlertsHistory from '../pages/alerts/history/AlertsHistory.vue'
import PluginsLibrary from '../pages/plugins/library/PluginsLibrary.vue'
import MyPlugins from '../pages/plugins/my/MyPlugins.vue'
import TeamPage from '../pages/team/users/TeamPage.vue'
import Groups from '../pages/team/groups/Groups.vue'
import ImportPlugin from '../pages/plugins/my/ImportPlugin.vue'
import GroupCardDetails from '../pages/team/groups/GroupCardDetails.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/my_systems/:system?',      name: 'my_systems',      component: MySystems },
    { path: '/',                name: 'home',            component: ActiveAlertsPage },
    { path: '/active_alerts',   name: 'active_alerts',   component: ActiveAlertsPage },
    { path: '/alerts_history',  name: 'alerts_history',  component: AlertsHistory },
    { path: '/plugins_library', name: 'plugins_library', component: PluginsLibrary },
    { path: '/my_plugins/:plugin?',      name: 'my_plugins',      component: MyPlugins },
    { path: '/team_members',    name: 'team_members',    component: TeamPage },
    { path: '/groups',          name: 'groups',          component: Groups },
    { path: '/import_plugins',  name: 'import_plugins',  component: ImportPlugin },
    { path: '/groups/edit_group/:id/:name',  name: 'groups/edit_group',      component: GroupCardDetails },
    { path: '/groups/create_group',    name: 'groups/create_group',    component: GroupCardDetails },
  ],
})



export default router
