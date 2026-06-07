<script setup lang="ts">

import MyTagInput from "@/helpers_components/MyTagInput.vue";
import {
  IconCalendarEvent, IconKey,
  IconLabel,
  IconLogout,
  IconMail, IconTool,
  IconUsersGroup
} from "@tabler/icons-vue";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import FormInput from "@/helpers_components/form/FormInput.vue";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {ref, watch} from "vue";
import {useForm} from "vee-validate";
import {editCurrentUserSchema} from "@/helpers_functions/formSchemas.ts";
import {toast} from "vue-sonner";
import type {User} from "@/types/types.ts";
import {useUserStore} from "@/stores/userStore.ts";
import {useAuthStore} from "@/stores/authStore.ts";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {dateParser} from "@/helpers_functions/dateParser.js";

const props = defineProps<{
  user: User,

}>()
const userStore = useUserStore()
const authStore = useAuthStore()

const isEditMode = defineModel<boolean>('isEditMode')
const isSubmitLoading = defineModel<boolean>('isSubmitLoading')

const tagsForEdit = ref<string[] | undefined>(
  props.user.groups?.map(g => g.name)
)

const { handleSubmit, resetForm, setFieldValue } = useForm({
  validationSchema:  editCurrentUserSchema,
  initialValues: {
    id: props.user.id,
    firstname: props.user.firstname,
    surname: props.user.surname,
    email: props.user.email,
    autoLogoutMinutes: props.user.autoLogoutMinutes,
    groups: props.user.groups,
  }
})

const onSubmit = handleSubmit(async (data) => {
  isSubmitLoading.value = true
  await userStore.editUserRequest({...data, role: props.user.role})
    .then((res) => toast.success(`User ${res} updated successfully.`))
    .catch((error) => toast.error(`Error updating ${data.email}: ${error}`))
    .finally( async () =>{
      await authStore.refreshToken()
        .then(() => {
          isSubmitLoading.value = false
          isEditMode.value = false
        })
    })
})

watch(isEditMode,() => {
  resetForm()
  tagsForEdit.value = props.user.groups?.map(g => g.name)
})

watch(tagsForEdit, (val) => {
  if(val){
    setFieldValue('groups', userStore.allGroups.filter(g => val.includes(g.name)))
  }
}, { deep: true })
</script>

<template>
  <form id="edit-user-form" @submit="onSubmit" class=" space-y-4 max-h-[70vh]  w-full overflow-auto">
    <div class="grid space-y-3 w-9/10">
      <Avatar class="size-14 rounded-full">
        <AvatarFallback class="rounded-full text-2xl grayscale">
          {{ authStore.avFallback }}
        </AvatarFallback>
      </Avatar>

      <FormInput autocomplete="name" name="firstname" label="Firstname" orientation="vertical">
        <IconLabel class="size-5 mb-1"/>
      </FormInput>
      <FormInput name="surname" label="Surname" orientation="vertical">
        <IconLabel class="size-5 mb-1"/>
      </FormInput>
      <FormInput autocomplete="email" type="email" name="email" label="E-mail" orientation="vertical">
        <IconMail class="size-5 mb-1"/>
      </FormInput>
      <FormNumberInput :min="0"  name="autoLogoutMinutes" label="Auto logout (in minutes)" orientation="vertical" autocomplete="auto-logout">
        <IconLogout class="size-5 mb-1"/>
      </FormNumberInput>

      <div class="space-x-2 flex items-center" >
        <IconCalendarEvent class="size-5 mb-1" />
        <h1 :class="bigNameLabel">Last password change:</h1>
        <div class="text-sm text-comment  space-x-2">{{user.lastPasswordChangeDate ? dateParser(user.lastPasswordChangeDate).fullDate : '--------' }}</div>
      </div>

      <div class="space-x-2 flex items-center">
        <h1 :class="bigNameLabel">User role:</h1>
        <span class="flex space-x-2 text-comment">
            <component :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" class="size-5" :class="{ 'rotate-90' : user.role === 'TECHNICIAN' }"/>
            <span>
            {{ user.role }}
          </span>

          </span>

      </div>
      <div class="flex space-x-2">
        <IconUsersGroup class="size-5 mt-1"/>
        <MyTagInput
          v-model:tags="tagsForEdit"
          :all-tags="userStore.allGroups.map(g => g.name)"
          :can-add-new="false"
          tags-label="Groups"
          input-id="groups"/>
      </div>
    </div>
  </form>
</template>

<style scoped>

</style>
