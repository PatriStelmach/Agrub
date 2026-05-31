<script setup lang="ts">
import type {User} from '@/types/types'
import { Card, CardDescription, CardHeader } from '@/components/ui/card'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import {bigNameLabel, gridCard, smallNameLabel} from '@/assets/cssFunctions'
import {
  IconUsersGroup, IconEdit, IconKey, IconTool,
} from "@tabler/icons-vue";
import EditUser from "@/pages/team/users/EditUser.vue";
import {ref, watch, watchEffect} from "vue";
import {useRoute} from "vue-router";
import router from "@/router";
import {useAuthStore} from "@/stores/authStore.ts";

const props = defineProps<{
  user: User
}>()
const route = useRoute()
const isDialogOpen = ref(false)
const authStore = useAuthStore()

watchEffect( () => {
  if(route.params.user === `${props.user.firstname} ${props.user.surname}` && route.params.id === `${props.user.id}`) {
    isDialogOpen.value = true
  }
})

watch(isDialogOpen, (newValue, oldValue) => {
  if (newValue === false && oldValue === true) {
    router.replace({ path: '/team_members' })
  }
})

</script>

<template>
  <Card :class="gridCard">
    <CardHeader class="px-3 flex space-x-1 items-center relative">
      <div class="relative">
        <Avatar class="size-9 rounded-lg">
          <AvatarFallback class="rounded-full grayscale">
            {{ user.firstname.slice(0, 1) + user.surname.slice(0, 1) }}
          </AvatarFallback>
        </Avatar>
        <span class="absolute bottom-0 right-0 size-2.5 rounded-full bg-green-badge" :class="{ 'bg-red-badge': !user.active }" />
      </div>
      <div class="grid flex-1 text-left text-sm mr-4">
        <span class="whitespace-break-spaces font-medium">{{ `${user.firstname} ${user.surname}` }}</span>
        <span class="text-muted-foreground truncate text-xs">{{ user.email }}</span>
      </div>
      <EditUser
        v-if="authStore.isAdmin"
        v-model:open="isDialogOpen"
        action-type="edit"
        :user="user"
      >
        <RouterLink :to=" authStore.currentUser?.id === user.id ? '/team_members/my_account' : `/team_members/${user.id}/${user.firstname} ${user.surname}`">
          <IconEdit
            class="absolute -top-2 right-2 text-green-badge hover:scale-105 cursor-pointer" />
        </RouterLink>
      </EditUser>

    </CardHeader>

    <CardDescription class="px-3">
      <div class="flex space-x-1">
        <component
          :class="{ 'rotate-90' : user.role === 'TECHNICIAN' }"
          :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" stroke="2" />
        <h1 :class="bigNameLabel">{{ user.role }}</h1>
      </div>
      <div class="flex flex-2 items-start whitespace-break-spaces">
        <div class="flex space-x-1 items-center mr-2 mt-1">
          <IconUsersGroup stroke="1.5" />
          <h1 :class="smallNameLabel">Groups:</h1>
        </div>
        <div>
          <RouterLink :to="`/groups/edit_group/${group.id}/${group.name}`" v-for="group in user.groups" :key="group.id">
            <Badge
              variant="tags">
              {{ group.name }}
            </Badge>

          </RouterLink>
        </div>
      </div>
    </CardDescription>
  </Card>
</template>
