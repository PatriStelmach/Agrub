<script setup lang="ts">
import {
  IconAlertTriangle, IconDatabase,
  IconHistory, IconPhoneRinging,
  IconScript, IconSitemap, IconDeviceDesktopAnalytics,
  IconUpload, IconUsers, IconUsersGroup, IconSettings, IconKey, IconPassword, IconLockCode
} from "@tabler/icons-vue";
import {
  Sidebar, SidebarContent,
  SidebarFooter, SidebarGroup, SidebarGroupContent, SidebarGroupLabel, SidebarHeader, SidebarMenu,
  SidebarMenuButton, SidebarMenuItem, SidebarRail
} from "@/components/ui/sidebar";


import NavUser from "@/pages/globals/navbar/NavUser.vue";
import {useRoute} from "vue-router";

const route = useRoute()


const navItems = [
  {
    label: 'Alerts',
    links: [
      { label: 'Active', to: 'active_alerts', icon: IconAlertTriangle },
      { label: 'History', to: 'alerts_history', icon: IconHistory },
    ]
  },
  {
    label: 'Plugins',
    links: [
      { label: 'My plugins', to: 'my_plugins', icon: IconScript },
      { label: 'Library', to: 'plugins_library', icon: IconDatabase },
    ]
  },
  {
    label: 'Team',
    links: [
      { label: 'Team members', to: 'team_members', icon: IconUsers },
      { label: 'Groups', to: 'groups', icon: IconUsersGroup },
    ],
  },
  {
    label: 'Settings',
    links: [
      { label: 'Systems', to: 'settings/systems', icon: IconDeviceDesktopAnalytics },
      { label: 'Configuration', to: 'settings/configuration', icon: IconSettings },
      { label: 'API keys', to:  'settings/api_keys', icon: IconLockCode}
    ]
  }

]
</script>

<template>

  <Sidebar>
    <SidebarHeader>
      <div class="text-xl space-x-2 flex pt-4"><IconPhoneRinging/>
        <span>Alert</span>
      </div>
    </SidebarHeader>
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupContent class="w-fit">
          <SidebarMenu>
            <SidebarMenuItem  v-for="item in navItems" :key="item.label" :value="item.label">
              <SidebarMenuButton as-child>
                <SidebarGroupLabel class="hover:bg-transparent pl-1 flex items-center text-center" >
                  {{ item.label }}
                </SidebarGroupLabel>
                  <RouterLink
                    :class="{'bg-blue-badge/50 border-blue-badge' : route.name?.toString().includes(link.to.toString())}"
                    class="flex xl:text-lg ml-3 border-l-4 w-full hover:bg-input rounded-[0_0.5rem_0.5rem_0]"
                    v-for="link in item.links" :key="link.to" :to="{ name: link.to}">
                    <div class="flex items-center gap-x-2 p-2 ml-1 ">
                      <component class="size-5  xl:size-6" :is="link.icon"/> {{link.label}}
                    </div>
                  </RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
    <SidebarFooter>
      <NavUser/>
    </SidebarFooter>
    <SidebarRail/>
  </Sidebar>
</template>
