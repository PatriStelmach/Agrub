<script setup lang="ts">
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
  SheetDescription,
} from '@/components/ui/sheet'
import type { User } from "@/types/types"
import EditUserForm from './EditUserForm.vue'

const props = defineProps<{
  user: User
  actionType: "create" | "edit"
}>()
const isOpen = defineModel<boolean>('open', { default: false })

</script>

<template>
  <Sheet v-model:open="isOpen">
    <SheetTrigger as-child>
      <slot />
    </SheetTrigger>

    <SheetContent side="left">
      <SheetHeader>
        <SheetTitle>{{ actionType === 'create' ? 'New user' : 'Edit user' }}</SheetTitle>
        <SheetDescription>{{ actionType === "create" ? 'Create new user account' : 'Change privileges and user data' }}</SheetDescription>
      </SheetHeader>
      <EditUserForm
        :action-type="props.actionType"
        v-if="isOpen"
        :user="user" />
    </SheetContent>
  </Sheet>
</template>
