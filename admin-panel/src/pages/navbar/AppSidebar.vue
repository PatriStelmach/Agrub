<script setup lang="ts">
import {
  Siren
} from "lucide-vue-next"

import {sidebarData} from '@/data/sidebarData.ts'
import NavDocuments from '@/pages/navbar/NavDocuments.vue'
import NavMain from '@/pages/navbar/NavMain.vue'
import NavUser from '@/pages/navbar/NavUser.vue'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
</script>

<template>
  <Sidebar class="bg-card z-100">
    <SidebarHeader class="bg-card">
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton
            as-child
            class="data-[slot=sidebar-menu-button]:p-1.5!"
          >
            <router-link class="items-start" to="/">
              <Siren class="size-5!" />
              <span class="text-base font-semibold">ALERT</span>
            </router-link>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarHeader>
    <SidebarContent class="bg-card">
      <NavMain :items="sidebarData.navMain" />
      <NavDocuments :items="sidebarData.documents" />
    </SidebarContent>
    <SidebarFooter class="bg-card">
      <NavUser :user="sidebarData.user" />
    </SidebarFooter>
  </Sidebar>
</template>
