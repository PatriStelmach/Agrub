<script setup lang="ts">
import {
  IconDotsVertical,
  IconLogout,
  IconNotification,
  IconUserCircle,
} from "@tabler/icons-vue"

import {
  Avatar,
  AvatarFallback,
} from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  SidebarMenu,
  SidebarMenuButton,
} from '@/components/ui/sidebar'
import type {User} from "@/types/types.js";
import {useAuthStore} from "@/stores/authStore.ts";

const authStore = useAuthStore()
const user = authStore.user
//const avFallback = `${user.firstname.slice(0, 1).toUpperCase()} ${user.surname.slice(0,1).toUpperCase()}`
//const fullName = `${user.firstname} ${user.surname}`
</script>

<template>
  <SidebarMenu>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <SidebarMenuButton
            size="lg"
            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
          >
            <Avatar class="size-9 rounded-lg relative ">
              <AvatarFallback class="rounded-full grayscale">
<!--                {{user.firstname.slice(0,1) + user.surname.slice(0,1)}}-->
                AP
              </AvatarFallback>
            </Avatar>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-medium">
<!--                {{ user.firstname + ' ' + user.surname }}-->
                Administrator Pjatk
              </span>
              <span class="text-muted-foreground truncate text-xs">
                {{ user }}
              </span>
            </div>
            <IconDotsVertical class="ml-auto size-4" />
          </SidebarMenuButton>
        </DropdownMenuTrigger>
        <DropdownMenuContent
          class="w-(--reka-dropdown-menu-trigger-width) min-w-56 rounded-lg z-120"
          :side="'right'"
          :side-offset="4"
          align="end"
        >
          <DropdownMenuLabel class="p-0 font-normal">
            <div class="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
              <Avatar class="h-8 w-8 rounded-lg">
                <AvatarFallback class="rounded-lg">
                  AP
                </AvatarFallback>
              </Avatar>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-medium">Administrator Pjatk</span>
                <span class="text-muted-foreground truncate text-xs">
                  {{ user }}
                </span>
              </div>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem>
              <IconUserCircle />
              Account
            </DropdownMenuItem>
            <DropdownMenuItem>
              <IconNotification />
              Notifications
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            @click="authStore.logout">
            <IconLogout/>
            Log out
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
  </SidebarMenu>
</template>
