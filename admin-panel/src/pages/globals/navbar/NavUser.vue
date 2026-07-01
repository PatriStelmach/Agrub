<script setup lang="ts">
import {
  IconDotsVertical,
  IconLogout,
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
import {useAuthStore} from "@/stores/authStore.ts";

const authStore = useAuthStore()
</script>

<template>
  <SidebarMenu>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <SidebarMenuButton
            id="my_profile_button"
            size="lg"
            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
          >
            <Avatar class="size-9 rounded-lg relative ">
              <AvatarFallback class="rounded-full grayscale">
                {{ authStore.avFallback}}
              </AvatarFallback>
            </Avatar>
            <div class="grid flex-1 text-left text-sm ">
              <span class="truncate font-medium">
                {{ authStore.fullName }}
              </span>
              <span class="text-muted-foreground truncate text-xs">
                {{ authStore.currentUser?.email }}
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
              <div class="grid flex-1 text-left text-sm ">
                <span class="truncate font-medium">
                  {{ authStore.fullName }}
                </span>
                <span class="text-muted-foreground truncate text-xs">
                  {{ authStore.currentUser?.email }}
                </span>
              </div>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <RouterLink to="/team_members/my_account">
              <DropdownMenuItem>
                <IconUserCircle />
                Account
              </DropdownMenuItem>
            </RouterLink>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            id="logout_button"
            @click="authStore.logout">
            <IconLogout/>
            Log out
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
  </SidebarMenu>
</template>
