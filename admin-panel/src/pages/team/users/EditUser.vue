<script setup lang="ts">
import { ref } from 'vue'
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

const props = defineProps<{
  user: User
  actionType: "create" | "edit"
}>()

const isOpen = ref(false)
</script>

<template>
  <Sheet v-model:open="isOpen">
    <SheetTrigger as-child>
      <slot />
    </SheetTrigger>

    <SheetContent side="left">
      <SheetHeader>
        <SheetTitle>Edit user</SheetTitle>
        <SheetDescription>Change privileges and user data</SheetDescription>
      </SheetHeader>
      <EditUserForm
        :action-type="props.actionType"
        v-if="isOpen"
        :user="user" />
      <div v-else class="flex justify-center p-10">
        <IconLoader class="animate-spin" />
      </div>
    </SheetContent>
  </Sheet>
</template>
