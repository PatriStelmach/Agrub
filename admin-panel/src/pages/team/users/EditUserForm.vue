<script setup lang="ts">
import {ref, watch } from "vue"
import {useForm} from 'vee-validate'
import { useUserStore } from "@/stores/userStore.js"
import { type User} from "@/types/types"

import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { IconKey, IconTool, IconDeviceFloppy, IconLabel, IconLock, IconMail, IconX, IconLoader } from '@tabler/icons-vue'
import { SheetFooter, SheetClose } from '@/components/ui/sheet'
import MyTagInput from "@/helpers_components/MyTagInput.vue"
import {toast} from "vue-sonner";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {
  createUserSchema,
  editUserSchema
} from "@/helpers_functions/formSchemas.ts";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
const props = defineProps<{
  user: User
  actionType: "create" | "edit"
}>()

const isOpen = defineModel<boolean>('isOpen')
const userStore = useUserStore()
const updatedUser = ref<User>({ ...props.user })
const isLoading = ref(false)

const tagsForEdit = ref<string[]>(
  updatedUser.value.groups?.map(g => g.name) ?? []
)

const { handleSubmit, values } = useForm({
  validationSchema: props.actionType === 'create' ? createUserSchema : editUserSchema,
  initialValues: updatedUser.value
})

const onSubmit = handleSubmit(async () => {
  isLoading.value = true
  if(props.actionType === "edit") {
    await userStore.editUserRequest(updatedUser.value)
      .then((res) => {
        toast.success(`User ${res} updated successfully.`)
        isOpen.value = false
      })
      .catch((error) => toast.error(`Error updating ${updatedUser.value.email}: ${error.message}`))
      .finally(() => isLoading.value = false)
  }
  else {
    await userStore.createUserRequest(updatedUser.value)
      .then((res) => {
        toast.success(`User ${res} created successfully.`)
        isOpen.value = false
      })
      .catch((error) => toast.error(`Error creating ${updatedUser.value.email}: ${error.message}`))
      .finally(() => isLoading.value = false)
  }
})

watch(tagsForEdit, (val) => {
  updatedUser.value.groups = userStore.allGroups.filter(g => val.includes(g.name))
}, { deep: true })

watch(values, (newValues) => {
  Object.assign(updatedUser.value, newValues)
}, { deep: true })



</script>

<template>
  <form id="edit-user-form" @submit="onSubmit" class="px-4 space-y-4 max-h-[75vh] overflow-auto">
    <div class="grid space-y-3 w-3/4">
      <Avatar class="size-9 rounded-lg">
        <AvatarFallback class="rounded-full text-md grayscale">
          {{ userStore.avFallback(values as User)}}
        </AvatarFallback>
      </Avatar>
      <FormInput autocomplete="name" name="firstname" label="Firstname" orientation="vertical">
        <IconLabel class="size-4"/>
      </FormInput>
      <FormInput name="surname" label="Surname" orientation="vertical">
        <IconLabel class="size-4"/>
      </FormInput>
      <FormInput autocomplete="email" type="email" name="email" label="E-mail" orientation="vertical">
        <IconMail class="size-4"/>
      </FormInput>
      <FormInput v-if="actionType === 'create'" autocomplete="password" type="password" name="password" label="Password" orientation="vertical">
        <IconLock class="size-4"/>
      </FormInput>
      <FormInput v-if="actionType === 'create'" autocomplete="new-password" type="password" name="confirmPassword" label="Confirm password" orientation="vertical">
        <IconLock class="size-4"/>
      </FormInput>

      <div class="grid mt-4 space-y-6">
        <div>
          <MyFieldLabel text="Role" for="role" class="mb-2" />
          <div  class="grid **:text-md **:text-label">
            <div class="flex items-center space-x-2">
              <input type="radio" v-model="updatedUser.role" id="radio-administrator" value="ADMINISTRATOR" />
              <IconKey class="mx-2" /><Label class="cursor-pointer" for="radio-administrator">ADMINISTRATOR</Label>
            </div>
            <div class="flex items-center space-x-2">
              <input type="radio" v-model="updatedUser.role" id="radio-technician" value="TECHNICIAN" />
              <IconTool  class="rotate-90 mx-2" /><Label class="cursor-pointer" for="radio-technician">TECHNICIAN</Label>
            </div>
          </div>
        </div>
        <MyTagInput
          v-model:tags="tagsForEdit"
          :all-tags="userStore.allGroups.map(g => g.name)"
          :can-add-new="false"
          tags-label="Groups"
          input-id="user-groups"/>
      </div>
    </div>
  </form>

  <SheetFooter>
      <Button  type="submit" form="edit-user-form" variant="green_outline" class="flex items-center">
        Save <IconDeviceFloppy v-if="!isLoading" />
        <IconLoader v-else class="animate-spin" />
      </Button>
    <SheetClose as-child>
      <Button class="flex items-center" variant="red_outline">Cancel<IconX/></Button>
    </SheetClose>
  </SheetFooter>
</template>
