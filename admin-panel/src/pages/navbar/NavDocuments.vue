<script setup lang="ts">
import type { Component } from "vue"

import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from '@/components/ui/sidebar'

interface Document {
  name: string
  url: string
  icon?: Component
}

defineProps<{
  items: Document[]
}>()

</script>

<template>
  <SidebarGroup class="group-data-[collapsible=icon]:hidden h-[25vh]">
    <SidebarGroupLabel>Plugins</SidebarGroupLabel>
    <SidebarMenu>
      <SidebarMenuItem v-for="item in items" :key="item.name">
        <SidebarMenuButton as-child>
          <router-link :to="item.url">
            <component :is="item.icon" />
            <span>{{ item.name }}</span>
          </router-link>
        </SidebarMenuButton>
      </SidebarMenuItem>
    </SidebarMenu>
  </SidebarGroup>
</template>
