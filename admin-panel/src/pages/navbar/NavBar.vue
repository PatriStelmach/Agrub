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
import TeamBar from "@/pages/teambar/TeamBar.vue";
import {
  Sidebar, SidebarHeader,
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
import NavUser from "@/pages/teambar/NavUser.vue";

const navItems = [
  {
    label: 'Alerts',
    links: [
      { label: 'Active alerts', to: 'alerts', icon: IconAlertTriangle },
      { label: 'History', to: 'alerts_history', icon: IconHistory },
    ]
  },
  {
    label: 'Logs',
    links: [
      { label: 'Active logs', to: 'logs', icon: IconLogs },
      { label: 'History', to: 'logs_history', icon: IconHistory },
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

  <SidebarProvider class="h-full  border-none px-4 w-60 ">
    <SidebarInset class=" h-full flex flex-col w-40 xl:w-50 bg-card">
      <SidebarHeader>
        <div class="text-xl space-x-2 flex pl-2 pt-4"><IconPhoneRinging/>
          <span>Alert</span>
        </div>
      </SidebarHeader>
      <Accordion class="mt-2" type="single" collapsible>
        <AccordionItem class="" v-for="item in navItems" :key="item.label" :value="item.label">
          <AccordionTrigger class=" cursor-pointer lg:text-lg xl:text-xl">
            <div class="flex items-center text-center space-x-2 xl:space-x-4">
              {{ item.label }}
            </div>
          </AccordionTrigger>
          <AccordionContent class="grid gap-y-2 ">
            <RouterLink class="hover:bg-input py-2 px-1 rounded-md xl:text-lg" v-for="link in item.links" :key="link.to" :to="link.to">
              <div class="flex items-center gap-x-2"><component class="size-4  xl:size-6" :is="link.icon"/> {{link.label}} </div></RouterLink>
          </AccordionContent>
        </AccordionItem>
      </Accordion>
      <div class="mb-4  mt-auto bg-card">

        <SidebarMenu class=" mt-auto mb-0">
          <SidebarMenuItem class="border-y-4">
            <SidebarMenuButton class="flex items-center text-center justify-center my-2 text-lg">
              <IconUsers class="xl:size-5! 2xl:size-6!"/>
              <RouterLink to="/team">
                Team
              </RouterLink>
            </SidebarMenuButton>
          </SidebarMenuItem>
          <TeamBar  />
          <NavUser  :user="sidebarData.loggedUser"/>
        </SidebarMenu>
      </div>
    </SidebarInset>
</SidebarProvider>

</template>
