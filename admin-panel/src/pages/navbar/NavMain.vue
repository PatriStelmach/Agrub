<script setup lang="ts">
import type { Component } from "vue"
import { IconCirclePlusFilled } from "@tabler/icons-vue"

import {
  SidebarGroup,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import {Button} from "@/components/ui/button";

interface NavItem {
  title: string
  url: string
  icon?: Component
}

defineProps<{
  items: NavItem[]
}>()
</script>

<template>
  <SidebarGroup>
    <SidebarGroupContent class="flex flex-col gap-2">
      <SidebarMenu>
        <SidebarMenuItem class="flex items-center gap-2">
          <SidebarMenuButton
            tooltip="Send notificatons">

            <Button variant="destructive" class="w-full"><IconCirclePlusFilled />Send notificaton</Button>
          </SidebarMenuButton>

        </SidebarMenuItem>
      </SidebarMenu>
      <SidebarMenu>
        <SidebarMenuItem v-for="item in items" :key="item.title">
          <SidebarMenuButton :tooltip="item.title">
            <component :is="item.icon" v-if="item.icon" />
            <RouterLink :to=item.url><span>{{ item.title }}</span></RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarGroupContent>
  </SidebarGroup>
</template>
