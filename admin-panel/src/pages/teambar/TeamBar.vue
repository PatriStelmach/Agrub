<script setup lang="ts">
import { ScrollArea } from '@/components/ui/scroll-area'
import type {User} from "@/types/types.ts";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {SidebarMenu, SidebarMenuButton, SidebarMenuItem} from "@/components/ui/sidebar";
import {
  DropdownMenu,
  DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator, DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {IconUser , IconNotification, IconUserCircle} from "@tabler/icons-vue";
import {sidebarData} from "@/data/sidebarData.ts";
import {usersData} from "@/data/usersData.ts";
import NavUser from "@/pages/teambar/NavUser.vue";

</script>

<template>

  <NavUser :user="sidebarData.loggedUser"/>
  <ScrollArea class=" border-t-4 pt-4">
      <SidebarMenu>
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
                <Avatar class="size-9 rounded-lg relative ">
                  <AvatarFallback class="rounded-full grayscale">
                    {{user.firstname.slice(0,1) + user.surname.slice(0,1)}}
                  </AvatarFallback>
                  <span
                    class="absolute bottom-1 right-1 size-1.5 rounded-full  bg-green-500"
                    :class="{'bg-destructive' : !user.active}"
                  ></span>
                </Avatar>
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
      </SidebarMenu>
  </ScrollArea>
</template>
