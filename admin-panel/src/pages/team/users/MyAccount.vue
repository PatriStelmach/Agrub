<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue"
import {useForm} from 'vee-validate'
import { useUserStore } from "@/stores/userStore.js"

import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import {
  IconKey, IconTool, IconDeviceFloppy, IconLabel, IconLock,
  IconLogout, IconMail, IconCalendarEvent, IconLoader,
  IconEdit, IconUsersGroup, IconEditOff, IconUserKey
} from '@tabler/icons-vue'
import MyTagInput from "@/helpers_components/MyTagInput.vue"
import {toast} from "vue-sonner";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {
  editCurrentUserSchema,
} from "@/helpers_functions/formSchemas.ts";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {bigNameLabel, hoverListRow} from "@/assets/cssFunctions.ts";
import {dateParser} from "@/composables/dateParser.ts";
import ChangePasswordDialog from "@/pages/team/users/ChangePasswordDialog.vue";
import {useAuthStore} from "@/stores/authStore.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {Badge} from "@/components/ui/badge";
import ShowRuleDiv from "@/pages/team/groups/ShowRuleDiv.vue";
import EditRuleDiv from "@/pages/team/groups/EditRuleDiv.vue";
import {getUserActions} from "@/helpers_functions/requests.ts";
import type {ActionResponse, Actions} from "@/types/types.ts";
import ActionsList from "@/pages/alerts/ActionsList.vue";

const authStore = useAuthStore();
const user = computed(() => authStore.currentUser!)
const userStore = useUserStore()

onMounted(async () => {
  try {
    await userStore.getAllGroupsRequest()
    actions.value = await getUserActions(user.value.id!)
  }
  catch (error) {
    toast.error(`${error}`)
  }
  finally {
    isActionsLoading.value = false
  }

})

const isSubmitLoading = ref(false)
const isActionsLoading = ref(true)
const actions = ref<ActionResponse[]>([])
const isEditMode = ref<boolean>(false)
const tagsForEdit = ref<string[] | undefined>(
  user.value.groups?.map(g => g.name)
)

const { handleSubmit, resetForm, setFieldValue } = useForm({
  validationSchema:  editCurrentUserSchema,
  initialValues: {
    id: user.value.id,
    firstname: user.value.firstname,
    surname: user.value.surname,
    email: user.value.email,
    autoLogoutMinutes: user.value.autoLogoutMinutes,
    groups: user.value.groups,
  }
})

const onSubmit = handleSubmit(async (data) => {
  isSubmitLoading.value = true
  await userStore.editUserRequest(data)
    .then((res) => toast.success(`User ${res} updated successfully.`))
    .catch((error) => toast.error(`Error updating ${data.email}: ${error}`))
  await authStore.refreshToken()
    .then(() => {
      isSubmitLoading.value = false
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
  <div class="w-full">
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
          Submit <IconDeviceFloppy v-if="!isSubmitLoading" />
          <IconLoader v-else class="animate-spin" />
        </Button>
      </ButtonGroup>
    </TopH1Div>
    <div class="w-full flex space-x-20 max-h-[80vh]  overflow-y-auto">
      <div class="w-3/10 ml-4">
        <form v-if="isEditMode" id="edit-user-form" @submit="onSubmit" class=" space-y-4 max-h-[70vh]  w-full overflow-auto">
          <div class="grid space-y-3 w-9/10">
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
            <FormInput autocomplete="email" type="email" name="email" label="E-mail" orientation="vertical">
              <IconMail class="size-4"/>
            </FormInput>
            <FormNumberInput :min="0"  name="autoLogoutMinutes" label="Auto logout (in minutes)" orientation="vertical" autocomplete="auto-logout">
              <IconLogout class="size-4"/>
            </FormNumberInput>

            <div class="space-x-2 flex items-center" >
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
        <div v-else class=" w-full space-y-4">
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
                <IconLogout class="size-4" />
                <h1 :class="bigNameLabel">Auto logout: </h1>
              </div>
              <span class="text-comment">{{ user.autoLogoutMinutes ?? '—' }} min</span>
            </div>
            <div>
              <div>
                <IconUserKey class="size-4"/>
                <h1 :class="bigNameLabel">User role: </h1>
              </div>
              <span class="text-comment">
            <component :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" class="size-5" :class="{'rotate-90' : user.role === 'TECHNICIAN'}" />
            <span>{{ user.role }}</span>
          </span>
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
      <div class="w-7/10 mr-4">
        <h1 class="pb-1 mb-2 border-b-4  text-center">User interactions with alerts: {{ actions.length }}</h1>
        <div class=" pb-2 border-b-4 ">
          <ul>
            <ActionsList :max-h="70" :actions="actions" />
          </ul>
        </div>
      </div>
    </div>
  </div>

</template>
