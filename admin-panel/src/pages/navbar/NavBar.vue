<script setup lang="ts">
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import {
  IconAlertTriangle, IconDatabase,
  IconHistory,
  IconLogs, IconPhoneRinging,
  IconScript, IconSitemap, IconSitemapOff,
  IconUpload, IconUserCircle, IconUsers
} from "@tabler/icons-vue";
import TeamBar from "@/pages/navbar/TeamBar.vue";
import {
  Sidebar, SidebarContent, SidebarGroup, SidebarGroupContent, SidebarGroupLabel, SidebarHeader,
  SidebarInput,
  SidebarInset, SidebarMenu,
  SidebarMenuButton, SidebarMenuItem,
  SidebarProvider
} from "@/components/ui/sidebar";
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuGroup,
  DropdownMenuItem, DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import {usersData} from "@/data/usersData.ts";
import {sidebarData} from "@/data/sidebarData.ts";
import NavUser from "@/pages/navbar/NavUser.vue";
import {useRoute} from "vue-router";

const route = useRoute()


const navItems = [
  {
    label: 'Alerts',
    links: [
      { label: 'Active alerts', to: 'alerts', icon: IconAlertTriangle },
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
      { label: 'My systems', to: 'my_systems', icon: IconSitemap },
      { label: 'All systems', to: 'all_systems', icon: IconSitemapOff },
    ]
  },
]
</script>

<template>

  <Sidebar class="">
          <SidebarHeader>
            <div class="text-xl space-x-2 flex pl-2 pt-4"><IconPhoneRinging/>
              <span>Alert</span>
            </div>
          </SidebarHeader>
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem  v-for="item in navItems" :key="item.label" :value="item.label">
              <SidebarMenuButton as-child>
                <SidebarGroupLabel class="hover:bg-transparent pl-1 flex items-center text-center" >
                  {{ item.label }}
                </SidebarGroupLabel>

                  <RouterLink
                    :class="{'bg-badge/50' : route.name === link.to}"
                    class="flex xl:text-lg ml-3 border-l-3 w-full hover:bg-input rounded-[0_1rem_1rem_0]"
                    v-for="link in item.links" :key="link.to" :to="link.to">
                    <div class="flex items-center gap-x-2 p-2 ml-1 ">
                      <component class="size-4  xl:size-6" :is="link.icon"/> {{link.label}}
                    </div>
                  </RouterLink>

              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
      <SidebarGroupContent class="mt-auto mb-3">
        <SidebarMenu>

            <SidebarMenuItem class="border-y-4">
              <SidebarMenuButton class="flex items-center text-center justify-center my-2 text-lg">
                <IconUsers class="xl:size-5! 2xl:size-6!"/>
                <RouterLink to="/team">
                  Team
                </RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem >
            <TeamBar  />
            <NavUser class="border-t-4 pt-2" :user="sidebarData.loggedUser"/>


        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarContent>
  </Sidebar>
</template>
