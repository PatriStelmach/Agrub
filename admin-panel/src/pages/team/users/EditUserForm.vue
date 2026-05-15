<script setup lang="ts">
import {computed, ref, watch, watchEffect} from "vue"
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from "@vee-validate/zod"
import z from "zod"
import { useUserStore } from "@/stores/userStore.js"
import type { User } from "@/types/types"

import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Button } from '@/components/ui/button'
import { IconKey, IconTool, IconDeviceFloppy, IconLoader } from '@tabler/icons-vue'
import { Field, FieldError, FieldGroup } from "@/components/ui/field"
import { SheetFooter, SheetClose } from '@/components/ui/sheet'
import MyTagInput from "@/helpers_components/MyTagInput.vue"
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue"
import DialogLabel from "@/helpers_components/DialogLabel.vue"
import { inputText, nameLabel } from '@/assets/cssFunctions'
import {toast} from "vue-sonner";

const props = defineProps<{
  user: User
}>()

const userStore = useUserStore()
const updatedUser = ref<User>({ ...props.user })
const isLoading = ref(false)
const tagsForEdit = ref<string[]>(
  updatedUser.value.groups?.map(g => g.name) ?? []
)

watch(tagsForEdit, (val) => {
  updatedUser.value.groups = userStore.allGroups.filter(g => val.includes(g.name))
  console.log(updatedUser.value)
}, { deep: true })
// watch(() => updatedUser.value.groups, (val) => {
//   tagsForEdit.value = val?.map(g => g.name) ?? []
// }, { immediate: true })

const formSchema = toTypedSchema(
  z.object({
    email: z.string().email('Invalid email address'),
    firstname: z.string().min(2, 'Firstname must be at least 2 characters.'),
    surname: z.string().min(2, 'Surname must be at least 2 characters.')
  })
)

const { handleSubmit } = useForm({ validationSchema: formSchema })

const { value: firstname, errors: firstnameErrors } = useField<string>('firstname', undefined,
  { initialValue: updatedUser.value.firstname })
const { value: surname, errors: surnameErrors } = useField<string>('surname', undefined,
  { initialValue: updatedUser.value.surname })
const { value: email, errors: emailErrors } = useField<string>('email', undefined,
  { initialValue: updatedUser.value.email })

const onSubmit = handleSubmit(async () => {
  isLoading.value = true
  updatedUser.value.firstname = firstname.value
  updatedUser.value.surname = surname.value
  updatedUser.value.email = email.value
  await userStore.editUserRequest(updatedUser.value)
    .then((res) => toast.success(`User ${res} updated successfully.`))
    .catch((err) => toast.error(`${err.message}`))
    .finally(() => isLoading.value = false)
})
</script>

<template>
  <form id="edit-user-form" @submit="onSubmit" class="px-4 space-y-4">
    <div class="grid space-y-2 w-3/4">
      <Avatar class="size-12 rounded-lg">
        <AvatarFallback class="rounded-full text-xl grayscale">
          {{ userStore.avFallback(updatedUser)}}
        </AvatarFallback>
      </Avatar>

      <FieldGroup class="space-x-2">
        <Field :data-invalid="!!firstnameErrors.length" class="w-fit">
          <MyFieldLabel text="Firstname" :class="nameLabel" for="firstname" />
          <Input v-model="firstname" placeholder="Firstname..." id="firstname" :class="inputText" />
          <FieldError v-if="firstnameErrors.length" :errors="firstnameErrors" />
        </Field>

        <Field :data-invalid="!!surnameErrors.length" class="w-fit">
          <MyFieldLabel text="Surname" :class="nameLabel" for="surname" />
          <Input v-model="surname" placeholder="Surname..." id="surname" :class="inputText" />
          <FieldError v-if="surnameErrors.length" :errors="surnameErrors" />
        </Field>

        <Field :data-invalid="!!emailErrors.length" class="w-fit">
          <MyFieldLabel text="e-mail" :class="nameLabel" for="email" />
          <Input v-model="email" placeholder="user@domain.com" id="email" :class="inputText" />
          <FieldError v-if="emailErrors.length" :errors="emailErrors" />
        </Field>
      </FieldGroup>

      <div class="grid mt-4 space-y-6">
        <div>
          <DialogLabel text="Role" for="role" class="mb-2" />
          <RadioGroup id="role" v-model="updatedUser.role" class="grid **:text-md **:text-label">
            <div class="flex items-center space-x-2">
              <RadioGroupItem id="radio-administrator" value="ADMINISTRATOR" />
              <IconKey /><Label class="cursor-pointer" for="radio-administrator">ADMINISTRATOR</Label>
            </div>
            <div class="flex items-center space-x-2">
              <RadioGroupItem id="radio-technician" value="TECHNICIAN" />
              <IconTool class="rotate-90" /><Label class="cursor-pointer" for="radio-technician">TECHNICIAN</Label>
            </div>
          </RadioGroup>
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

  <SheetFooter class="mt-6">
      <Button v-if="!isLoading" type="submit" form="edit-user-form" variant="green_outline" class="flex items-center">
        Save <IconDeviceFloppy />
      </Button>
      <IconLoader v-else class="animate-spin" />
    <SheetClose as-child>
      <Button variant="red_outline">Cancel</Button>
    </SheetClose>
  </SheetFooter>
</template>
