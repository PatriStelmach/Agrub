<script setup lang="ts">
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useUserStore } from "@/stores/userStore.ts";
import type { User } from "@/types/types.ts";
import {IconTool, IconKey, IconUserPlus, IconLoader} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import {assignUserToGroupRequest} from "@/helpers_functions/requests/groupsRequests.ts";
import {computed, ref} from "vue";
import {toast} from "vue-sonner";

const props = defineProps<{
  currentUsers: User[]
  groupId: number
}>()
const emit = defineEmits<{
  'update:add-user': [User]
}>()

const userStore = useUserStore()
const loadingButtons = ref<number[]>([]);
if(userStore.allUsers.length === 0){
  userStore.getAllUsersRequest()
}

const availableUsers = computed(() =>
  userStore.allUsers.filter(u => !props.currentUsers.map(cu => cu.id).includes(u.id)))

const assignUser = async (user: User) => {
  if(user.id) {
    loadingButtons.value.push(user.id)
    await assignUserToGroupRequest(user.id, props.groupId)
      .then((res: string) => {
        toast.success(`Successfully assigned user: ${res} to group!`)
        emit('update:add-user', user)
      })
      .catch((err) => {
        toast.error(err.message)
      })
      .finally(() => loadingButtons.value.pop())
  }
}

</script>

<template>
  <Popover>
    <PopoverTrigger as-child>
      <slot/>
    </PopoverTrigger>
    <PopoverContent class="w-96 p-0" align="center">
      <Command>
        <CommandInput placeholder="Search users..." />
        <CommandList>
          <CommandEmpty>No users found.</CommandEmpty>
          <div v-if="availableUsers.length === 0" class="py-6 text-center text-sm text-muted-foreground">
            All users already in group.
          </div>
          <CommandGroup>
            <CommandItem
              v-for="user in availableUsers"
              :key="user.id"
              :value="userStore.fullName(user)"
              class="flex items-center gap-2 hover:bg-blue-badge/10 odd:bg-accent/50"
            >
              <Avatar class="size-8 rounded-full">
                <AvatarFallback class="text-xs rounded-full grayscale">
                  {{ userStore.avFallback(user) }}
                </AvatarFallback>
              </Avatar>

              <div class="grid">
                <span class="text-sm flex space-x-2"><span>{{ userStore.fullName(user) }}</span>
                  <component :class="{'rotate-90' : user.role === 'TECHNICIAN'}" :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool"/></span>
                <span class="text-xs text-muted-foreground">{{ user.email }}</span>
              </div>

              <Button
                @click="assignUser(user)"
                class="ml-auto duration-0!"
                variant="blue_outline"
                size="icon-sm"
              ><IconLoader class="animate-spin" v-if="loadingButtons.some(u => u === user.id)"/>
                <IconUserPlus v-else/>
              </Button>
            </CommandItem>
          </CommandGroup>
        </CommandList>
      </Command>
    </PopoverContent>
  </Popover>
</template>
