<script setup lang="ts">

import {
  Dialog,
  DialogContent,
  DialogDescription, DialogHeader,
  DialogTitle,
  DialogTrigger
} from "@/components/ui/dialog";
import type {User} from "@/types/types.ts";
import {IconKey, IconLoader, IconTool, IconTrash} from "@tabler/icons-vue";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import {Button} from "@/components/ui/button";
import {useUserStore} from "@/stores/userStore.ts";
import {toast} from "vue-sonner";
import {ref} from "vue";

const props = defineProps<{
  user: User
}>()

const userStore = useUserStore()
const isLoading = ref(false)

const deleteUser = async () => {
  isLoading.value = true
    await userStore.deleteUserRequest(props.user)
      .then((res) => toast.success(`Successfully deleted user: ${res}`))
      .catch((err) => toast.error(`Error deleting user: ${err.message}`))
      .finally(() => isLoading.value = false)
}

</script>

<template>
  <Dialog>
    <DialogTrigger as-child>
      <slot/>
    </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            Delete user
          </DialogTitle>
          <DialogDescription>
            Are you sure you want to delete this user?
          </DialogDescription>
        </DialogHeader>
        <div
          v-if="user.id"
          class="space-x-2  flex items-center py-2 px-2 pr-6 relative">
          <Avatar class="size-9 rounded-lg">
            <AvatarFallback class="text-sm rounded-full grayscale">
              {{ userStore.avFallback(user)}}
            </AvatarFallback>
          </Avatar>
          <div class="grid mr-5">
            <span class="whitespace-break-spaces">{{ userStore.fullName(user) }}</span>
            <span class="text-comment text-sm whitespace-break-spaces">{{ user.email }}</span>
          </div>
          <div class="flex items-center gap-1">
            <component
              :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" stroke="2"
              :class="{ 'rotate-90' : user.role === 'TECHNICIAN' }"
            />
            <span class="text-comment">{{ user.role === 'TECHNICIAN' ? 'TECHNICIAN' : 'ADMINISTRATOR'  }}</span>

          </div>
        </div>
        <Button
          @click="deleteUser"
          variant="red_outline" class="" >
          Delete
          <IconLoader v-if="isLoading" class="animate-spin "/>
          <IconTrash v-else />
        </Button>
      </DialogContent>
  </Dialog>
</template>
