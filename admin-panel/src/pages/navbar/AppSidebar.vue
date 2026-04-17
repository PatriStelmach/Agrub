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
import TeamBar from "@/pages/navbar/TeamBar.vue";
import { usersData } from "@/data/usersData.ts";
</script>

<template>
  <Sidebar class="bg-card z-100">
    <SidebarHeader class="bg-card h-[5vh]">
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
    <SidebarContent class="bg-card max-h-[40vh] oveflow-auto  ">
      <NavMain :items="sidebarData.navMain" />
      <NavDocuments :items="sidebarData.documents" />
    </SidebarContent>
    <SidebarFooter class="bg-card max-h-[40vh]">
      <NavUser :user="sidebarData.loggedUser" class="1/7" />
      <TeamBar :data="usersData" class="h-6/7"/>
    </SidebarFooter>
  </Sidebar>
</template>
