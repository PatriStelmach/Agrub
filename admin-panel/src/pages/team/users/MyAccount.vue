<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue"
import {useForm} from 'vee-validate'
import { useUserStore } from "@/stores/userStore.js"

import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Button } from '@/components/ui/button'
import {
  IconKey, IconTool, IconDeviceFloppy, IconLabel, IconLock,
  IconLogout, IconMail, IconCalendarEvent, IconLoader,
  IconEdit, IconUsersGroup, IconEditOff
} from '@tabler/icons-vue'
import MyTagInput from "@/helpers_components/MyTagInput.vue"
import DialogLabel from "@/helpers_components/DialogLabel.vue"
import {toast} from "vue-sonner";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {
  editCurrentUserSchema,
} from "@/helpers_functions/formSchemas.ts";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {dateParser} from "@/composables/dateParser.ts";
import ChangePasswordDialog from "@/pages/team/users/ChangePasswordDialog.vue";
import {useAuthStore} from "@/stores/authStore.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {Badge} from "@/components/ui/badge";

const authStore = useAuthStore();
const user = computed(() => authStore.currentUser!)
const userStore = useUserStore()

onMounted(() => {
  userStore.getAllGroupsRequest()
})

const isLoading = ref(false)
const isEditMode = ref<boolean>(false)
const tagsForEdit = ref<string[] | undefined>(
  user.value.groups?.map(g => g.name)
)

const { handleSubmit, values, resetForm, setFieldValue } = useForm({
  validationSchema:  editCurrentUserSchema,
  initialValues: {
    id: user.value.id,
    firstname: user.value.firstname,
    surname: user.value.surname,
    email: user.value.email,
    role: user.value.role,
    autoLogoutMinutes: user.value.autoLogoutMinutes,
    groups: user.value.groups,
  }
})

const onSubmit = handleSubmit(async (data) => {
  isLoading.value = true
  await userStore.editUserRequest(data)
    .then((res) => toast.success(`User ${res} updated successfully.`))
    .catch((error) => toast.error(`Error updating ${data.email}: ${error}`))
  await authStore.refreshToken()
    .then(() => {
      isLoading.value = false
      isEditMode.value = false
    })
})

const reset = () => {
  resetForm()
  isEditMode.value = !isEditMode.value
  tagsForEdit.value = user.value.groups?.map(g => g.name)
}

watch(tagsForEdit, (val) => {
  if(val){
    setFieldValue('groups', userStore.allGroups.filter(g => val.includes(g.name)))
  }
}, { deep: true })

</script>

<template>
  <div>
    <TopH1Div h1="Account details">
      <ButtonGroup>
        <ChangePasswordDialog>
          <Button
            type="button"
            variant="orange_outline"
          >
            Change password
            <IconLock class="size-4"/>
          </Button>
        </ChangePasswordDialog>
        <Button
          :variant=" isEditMode ? 'red_outline' : 'blue_outline'"
          type="button"
          @click="reset"
          class="border-l-2! flex items-center">
          {{ isEditMode ? 'Cancel' : 'Edit' }}
          <component :is="isEditMode ? IconEditOff : IconEdit" class="pointer-events-none"/>
        </Button>
        <Button v-if="isEditMode" type="submit" form="edit-user-form" variant="green_outline" class="border-l-2! flex items-center">
          Submit <IconDeviceFloppy v-if="!isLoading" />
          <IconLoader v-else class="animate-spin" />
        </Button>
      </ButtonGroup>
    </TopH1Div>
    <form v-if="isEditMode" id="edit-user-form" @submit="onSubmit" class="px-4 space-y-4 mx-auto max-h-[85vh] overflow-auto">
      <div class="grid space-y-3 mx-auto w-9/10">
        <Avatar class="size-14 rounded-full">
          <AvatarFallback class="rounded-full text-2xl grayscale">
            {{ authStore.avFallback }}
          </AvatarFallback>
        </Avatar>

        <FormInput autocomplete="name" name="firstname" label="Firstname" orientation="vertical">
          <IconLabel class="size-4"/>
        </FormInput>
        <FormInput name="surname" label="Surname" orientation="vertical">
          <IconLabel class="size-4"/>
        </FormInput>
        <FormInput autocomplete="email" type="email" name="email" label="e-mail" orientation="vertical">
          <IconMail class="size-4"/>
        </FormInput>
        <FormNumberInput :min="0"  name="autoLogoutMinutes" label="Auto logout (in minutes)" orientation="vertical" autocomplete="auto-logout">
          <IconLogout class="size-4"/>
        </FormNumberInput>

        <div class="space-y-2" >
          <h1 :class="bigNameLabel">Last password change</h1>
          <div class="text-sm text-comment  space-x-2">{{user.lastPasswordChangeDate ? dateParser(user.lastPasswordChangeDate).fullDate : '--------' }}</div>
        </div>

        <div class="grid mt-2 space-y-6">
            <DialogLabel text="Role" for="role" class="mb-2" />
            <RadioGroup id="role" v-model="user.role" class="grid **:text-md **:text-label">
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
    <div v-else class="px-4 space-y-4 w-1/2">
        <Avatar class="size-14 rounded-full">
          <AvatarFallback class="rounded-full text-2xl grayscale">
            {{ authStore.avFallback }}
          </AvatarFallback>
        </Avatar>


      <div class="grid space-y-3 **:flex **:items-center **:gap-2">
        <div>
          <div class="flex items-center">
            <IconLabel class="size-4" />
            <h1 :class="bigNameLabel">Firstname: </h1>
          </div>
          <span class="text-comment">{{ user.firstname }}</span>
        </div>
        <div>
          <div class="flex items-center">
            <IconLabel class="size-4" />
            <h1 :class="bigNameLabel">Surname: </h1>
          </div>
          <span class="text-comment">{{ user.surname }}</span>
        </div>
        <div>
          <div>
            <IconMail class="size-4" />
            <h1 :class="bigNameLabel">Email: </h1>
          </div>
          <span class="text-comment">{{ user.email }}</span>
        </div>
        <div>
          <div>
            <component :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" class="size-4" :class="{'rotate-90' : user.role === 'TECHNICIAN'}" />
            <h1 :class="bigNameLabel">User role: </h1>
          </div>
          <span class="text-comment">{{ user.role }}</span>
        </div>
        <div>
          <div>
            <IconLogout class="size-4" />
            <h1 :class="bigNameLabel">Auto logout: </h1>
          </div>
          <span class="text-comment">{{ user.autoLogoutMinutes ?? '—' }} min</span>
        </div>
        <div>
            <div>
              <IconCalendarEvent class="size-4" />
              <h1 :class="bigNameLabel"> Last password change: </h1>
            </div>
            <span class="text-comment">{{ user.lastPasswordChangeDate ? dateParser(user.lastPasswordChangeDate).fullDate : '--------' }}</span>
        </div>
        <div>
          <div>
            <IconUsersGroup class="size-4" />
            <h1 :class="bigNameLabel">User groups:</h1>
          </div>
          <RouterLink v-for="g in user.groups" :key="g.id" :to="`/groups/edit_group/${g.id}/${g.name}`">
            <Badge class="mt-0!" variant="tags" >
              {{ g.name }}
            </Badge>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>

</template>
