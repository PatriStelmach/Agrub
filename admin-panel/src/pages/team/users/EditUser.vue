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
import {useRoute} from "vue-router";
import {computed} from "vue";

const props = defineProps<{
  user: User
  actionType: "create" | "edit"
}>()
const authStore = useAuthStore()
const route = useRoute()
const isMe = computed(() => route.params.user === authStore.userEmail)

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
      <div v-else class="flex justify-center p-10">
        <IconLoader class="animate-spin" />
      </div>
    </SheetContent>
  </Sheet>
</template>
