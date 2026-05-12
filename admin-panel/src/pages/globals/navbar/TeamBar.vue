<script setup lang="ts">
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import { SidebarMenuButton, SidebarMenuItem} from "@/components/ui/sidebar";
import {
  DropdownMenu,
  DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator, DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import { IconNotification, IconUserCircle} from "@tabler/icons-vue";
import {usersData} from "@/data/usersData.js";

</script>

<template>

  <div class=" bottom-4 flex-1 min-h-0 sm:max-h-60! md:max-h-80! lg:max-h-100! xl:max-h-150! 2xl:max-h-250! overflow-auto">
    <SidebarMenuItem
      v-for="user in usersData"
      :key="user.id"
      class="inline ">
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <SidebarMenuButton
            size="lg"
            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
          >
            <div class="relative">
              <Avatar class="size-8 rounded-lg ">
                <AvatarFallback class="rounded-full grayscale">
                  {{user.firstname.slice(0,1) + user.surname.slice(0,1)}}
                </AvatarFallback>
              </Avatar>
              <span
                class="absolute bottom-0 right-0 size-2 rounded-full  bg-green-badge"
                :class="{'bg-red-500' : !user.active}"
              ></span>
            </div>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-medium">{{ user.firstname + ' ' + user.surname }}</span>
              <span class="text-muted-foreground truncate text-xs">
                {{ user.email }}
              </span>
            </div>
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
              <Avatar class="size-9 rounded-lg grayscale">
                <AvatarFallback class="rounded-full">
                  {{user.firstname.slice(0,1) + user.surname.slice(0,1)}}
                </AvatarFallback>
              </Avatar>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-medium">{{ user.firstname + ' ' + user.surname }}</span>
                <span class="text-muted-foreground truncate text-xs">
                  {{ user.email }}
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
        </DropdownMenuContent>
      </DropdownMenu>
    </SidebarMenuItem>
  </div>
</template>
