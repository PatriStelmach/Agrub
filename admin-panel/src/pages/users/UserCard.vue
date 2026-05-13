<script setup lang="ts">
import type { User } from '@/types/types'
import { Card, CardDescription, CardHeader } from '@/components/ui/card'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { nameLabel } from '@/assets/cssFunctions'
import {
  IconUsersGroup, IconEdit, IconKey, IconTool,
} from "@tabler/icons-vue";
import EditUser from "@/pages/users/EditUser.vue";

defineProps<{
  user: User
}>()

</script>

<template>
  <Card class="border-2 my-2 user-box flex hover:shadow-md hover:border-blue-badge/50 hover:shadow-blue-badge/50 transition-all duration-200 hover:translate-y-2">
    <CardHeader class="px-3 flex space-x-1 items-center relative">
      <div class="relative">
        <Avatar class="size-9 rounded-lg">
          <AvatarFallback class="rounded-full grayscale">
            {{ user.firstname.slice(0, 1) + user.surname.slice(0, 1) }}
          </AvatarFallback>
        </Avatar>
        <span class="absolute bottom-0 right-0 size-2.5 rounded-full bg-green-badge" :class="{ 'bg-red-badge': !user.active }" />
      </div>
      <div class="grid flex-1 text-left text-sm leading-tight">
        <span class="truncate font-medium">{{ `${user.firstname} ${user.surname}` }}</span>
        <span class="text-muted-foreground truncate text-xs">{{ user.email }}</span>
      </div>
      <EditUser
        action-type="edit"
        :user="user"
      >
        <IconEdit
          class="absolute -top-2 right-2 text-green-badge hover:scale-105 cursor-pointer" />
      </EditUser>

    </CardHeader>

    <CardDescription class="px-3">
      <div class="flex space-x-1">
        <component :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" stroke="2" />
        <h1 class="font-bold mb-2 text-lg xl:text-xl 2xl:text-2xl">{{ user.role }}</h1>
      </div>
      <div class="flex flex-2 items-start whitespace-break-spaces">
        <div class="flex space-x-1 items-center mr-2 mt-1">
          <IconUsersGroup stroke="1.5" />
          <h1 :class="nameLabel">Groups:</h1>
        </div>
        <div>
          <Badge variant="tags" v-for="(group, index) in user.groups" :key="index">{{ group }}</Badge>
        </div>
      </div>
    </CardDescription>
  </Card>
</template>
