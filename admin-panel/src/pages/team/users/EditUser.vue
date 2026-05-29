<script setup lang="ts">
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
  SheetDescription,
} from '@/components/ui/sheet'
import { IconLoader } from '@tabler/icons-vue'
import type { User } from "@/types/types"
import EditUserForm from './EditUserForm.vue'
import {useAuthStore} from "@/stores/authStore.ts";
import {computed} from "vue";

const props = defineProps<{
  user: User
  actionType: "create" | "edit"
}>()
const authStore = useAuthStore()
const isMe = computed(() => (`${props.user.firstname} ${props.user.surname}`) === authStore.fullName)
const isOpen = defineModel<boolean>('open', { default: false })

</script>

<template>
  <Sheet v-model:open="isOpen">
    <SheetTrigger as-child>
      <slot />
    </SheetTrigger>

    <SheetContent side="left">
      <SheetHeader>
        <SheetTitle>{{ actionType === 'create' ? 'New user' : isMe ? 'Edit your account' : 'Edit user' }}</SheetTitle>
        <SheetDescription>{{ actionType === "create" ? 'Create new user account' : 'Change privileges and user data' }}</SheetDescription>
      </SheetHeader>
      <EditUserForm
        :is-me="isMe"
        :action-type="props.actionType"
        v-if="isOpen"
        :user="user" />
    </SheetContent>
  </Sheet>
</template>
