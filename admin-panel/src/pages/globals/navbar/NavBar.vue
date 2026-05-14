<script setup lang="ts">
import {
  IconAlertTriangle, IconDatabase,
  IconHistory, IconPhoneRinging,
  IconScript, IconSettings, IconSitemap, IconSitemapOff,
  IconUpload, IconUsers, IconUsersGroup
} from "@tabler/icons-vue";
import TeamBar from "@/pages/globals/navbar/TeamBar.vue";
import {
  Sidebar, SidebarContent, SidebarGroup, SidebarGroupContent, SidebarGroupLabel, SidebarHeader, SidebarMenu,
  SidebarMenuButton, SidebarMenuItem
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
      { label: 'Import', to: 'import_plugins', icon: IconUpload },
    ]
  },
  {
    label: 'Systems',
    links: [
      { label: 'Connected', to: 'my_systems', icon: IconSitemap },
      { label: 'All systems', to: 'all_systems', icon: IconSitemapOff },
    ]
  },
  {
    label: 'Team',
    links: [
      { label: 'Team members', to: 'team_members', icon: IconUsers },
      { label: 'Groups', to: 'groups', icon: IconUsersGroup },
    ]
  },

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
                    class="flex xl:text-lg ml-3 border-l-3 w-full hover:bg-input rounded-[0_0.5rem_0.5rem_0]"
                    v-for="link in item.links" :key="link.to" :to="{ name: link.to}">
                    <div class="flex items-center gap-x-2 p-2 ml-1 ">
                      <component class="size-4  xl:size-6" :is="link.icon"/> {{link.label}}
                    </div>
                  </RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
    <NavUser class="py-4"/>
  </Sidebar>
</template>
