import { createRouter, createWebHistory } from 'vue-router'

import ExternalSystems from '@/pages/settings/systems/ExternalSystems.vue'
import ActiveAlertsPage from '../pages/alerts/active/ActiveAlertsPage.vue'
import AlertsHistory from '../pages/alerts/history/AlertsHistory.vue'
import PluginsLibrary from '../pages/plugins/library/PluginsLibrary.vue'
import MyPlugins from '../pages/plugins/my/MyPlugins.vue'
import TeamPage from '../pages/team/users/TeamPage.vue'
import Groups from '../pages/team/groups/Groups.vue'
import GroupCardDetails from '../pages/team/groups/GroupCardDetails.vue'
import MyAccount from "@/pages/team/users/MyAccount.vue";
import ConfigPage from "@/pages/settings/Configuration/ConfigPage.vue"
import ApiKeysPage from "@/pages/settings/api_keys/ApiKeysPage.vue";
import ChartsPage from "@/pages/charts/ChartsPage.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/',                name: 'home',            component: ActiveAlertsPage },
    { path: '/active_alerts/:alert?',   name: 'active_alerts',   component: ActiveAlertsPage },
    { path: '/alerts_history/:alert?',  name: 'alerts_history',  component: AlertsHistory },
    { path: '/plugins_library', name: 'plugins_library', component: PluginsLibrary },
    { path: '/my_plugins/:plugin?',      name: 'my_plugins',      component: MyPlugins },
    { path: '/team_members/:id?/:user?',    name: 'team_members',    component: TeamPage },
    { path: '/team_members/my_account', name: 'my_account', component: MyAccount},
    { path: '/groups',          name: 'groups',          component: Groups },
    { path: '/groups/edit_group/:id/:name',  name: 'groups/edit_group',      component: GroupCardDetails },
    { path: '/groups/create_group',    name: 'groups/create_group',    component: GroupCardDetails },
    { path: '/settings/api_keys',      name: 'settings/api_keys',      component: ApiKeysPage },
    { path: '/settings/configuration',      name: 'settings/configuration',      component: ConfigPage },
    { path: '/settings/systems/:system?',      name: 'settings/systems',      component: ExternalSystems },
    { path: '/charts' , name: 'charts', component: ChartsPage},
    { path: '/help' , name: 'help', component: Groups}
  ],
})

export default router
