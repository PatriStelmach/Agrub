<script setup lang="ts">
import wazuh_logo_black from "/icons/wazuh_logo_black.svg"
import wazuh_logo_white from "/icons/wazuh_logo_white.svg"
import {ref} from "vue"
import { useColorMode } from "@vueuse/core"
import {
  bigNameLabel,
  gridSystemCard,
  gridSystemCardUnwrapped
} from "@/assets/cssFunctions.ts"
import { IconDeviceFloppy, IconLock, IconPower, IconShieldExclamation, IconUser, IconLink, IconCheck, IconEdit, IconX } from "@tabler/icons-vue"
import { Card, CardDescription, CardFooter, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import FormInput from "@/helpers_components/form/FormInput.vue"
import type { MonitoringSystemsConfig} from "@/types/types.ts";
import {useForm} from "vee-validate";
import {wazuhSchema} from "@/helpers_functions/formSchemas.ts";
import {useSettingStore} from "@/stores/settingStore.ts";
import {toast} from "vue-sonner";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";

const props = defineProps<{
  system: MonitoringSystemsConfig
  isUnwrapped: boolean
}>()

const emit = defineEmits<{
  'edit': [string]
  'cancel': []
  'save': []
}>()

const changePassword = ref(false)
const settingStore = useSettingStore()
const isLoading = ref(false)
const colorMode = useColorMode()

const { handleSubmit, setValues } = useForm({
  validationSchema: wazuhSchema,
  initialValues: {
    wazuh_url: props.system.url,
    wazuh_user: props.system.user,
    wazuh_password_SECRET: '',
  },
})

const onSubmit = handleSubmit(async (values) => {
  isLoading.value = true
  const changedValues: Record<string, string> = {}

  if (values.wazuh_url !== props.system.url) {
    changedValues.wazuh_url = values.wazuh_url
  }
  if (values.wazuh_user && values.wazuh_user !== props.system.user) {
    changedValues.wazuh_user = values.wazuh_user
  }
  if (values.wazuh_min_active_level !== props.system.min_active_level) {
    changedValues.wazuh_min_active_level = String(values.wazuh_min_active_level)
  }
  if (!!values.wazuh_password_SECRET) {
    changedValues.wazuh_password_SECRET = values.wazuh_password_SECRET
  }
  if (Object.keys(changedValues).length === 0) {
    toast.info("No changes detected.")
    isLoading.value = false
    return
  }

  await settingStore.updateSystemFullSettingsRequest(changedValues)
    .then(() => emit('save'))
    .catch((error) => toast.error(`Error updating system configuration: ${error.message}`))
    .finally(() => isLoading.value = false)
})

const onEdit = () => {
  setValues({
    wazuh_url: props.system.url,
    wazuh_user: props.system.user,
    wazuh_min_active_level: props.system.min_active_level,
    wazuh_password_SECRET: '',
  })
  emit('edit', props.system.name)
}

const onCancel = () => {
  changePassword.value = false
  emit('cancel')
}

</script>

<template>
  <!-- EDIT CARD -->
  <Card
    v-if="props.isUnwrapped"
    :class="gridSystemCardUnwrapped"
  >
    <form id="wazuh-form" @submit.prevent="onSubmit">
      <CardHeader class="left-1/2 -translate-x-1/2 items-center relative">
        <div class="w-48 h-20 mx-auto">
          <img
            :src="colorMode === 'dark' ? wazuh_logo_white : wazuh_logo_black"
            alt="wazuh_logo"
            class="-mt-2"
          />
        </div>
      </CardHeader>
      <CardDescription class="px-3 space-y-3 *:flex *:items-center *:mr-2 -mt-4 max-h-50 lg:max-h-60 xl:max-h-70 overflow-auto">
        <div>
          <div class="flex items-center space-x-2 text-label">
            <IconPower class="size-5 text-label"/>
            <h1 :class="bigNameLabel">Enabled:</h1>
            <component :is="system.enabled ? IconCheck : IconX" :class="system.enabled ? 'text-green-badge' : 'text-red-badge'" />
          </div>
        </div>
        <div>
          <FormInput  orientation="vertical" name="wazuh_url" label="URL: ">
            <IconLink class="size-5 text-label"/>
          </FormInput>
        </div>
        <div>
          <FormInput autocomplete="user" orientation="vertical" name="wazuh_user" label="User: ">
            <IconUser class="size-5 text-label"/>
          </FormInput>
        </div>
        <div>
          <FormInput
            autocomplete="new_password"
            orientation="vertical"
            type="password"
            name="wazuh_password_SECRET"
            label="New password:">
            <IconLock class="size-5 text-label"/>
          </FormInput>
        </div>
        <div>
          <FormNumberInput orientation="vertical" name="wazuh_min_active_level" label="Minimal severity level: ">
            <IconShieldExclamation class="size-5 text-label"/>
          </FormNumberInput>
        </div>
      </CardDescription>
      <CardFooter class="bottom-4 absolute justify-between w-full">
        <Button @click="onCancel" variant="red_outline" class="*:size-5!">
          Cancel <IconX />
        </Button>
        <Button class="border-l-2!" form="wazuh-form" type="submit" variant="green_outline">
          Save <IconDeviceFloppy />
        </Button>
      </CardFooter>
    </form>
  </Card>

  <!-- VIEW CARD -->
  <Card v-else :class="gridSystemCard">
    <CardHeader class="left-1/2 -translate-x-1/2 items-center relative">
      <div class="w-48 h-20 mx-auto">
        <img
          :src="colorMode === 'dark' ? wazuh_logo_white : wazuh_logo_black"
          alt="wazuh_logo"
          class="-mt-2"
        />
      </div>
    </CardHeader>
    <CardDescription class="px-3 space-y-2 *:flex *:space-x-1 *:items-center *:mr-2 -mt-4">
      <div>
        <div class="flex items-center space-x-2 text-label">
          <IconPower class="size-5 "/>
          <h1 :class="bigNameLabel">Enabled:</h1>
        </div>
        <component :is="system.enabled ? IconCheck : IconX" :class="system.enabled ? 'text-green-badge' : 'text-red-badge'" />
      </div>
      <div>
        <div class="flex items-center space-x-2 text-label">
          <IconLink class="size-5 "/>
          <h1 :class="bigNameLabel">URL:</h1>
        </div>
        <a class="cursor-pointer truncate text-comment italic text-lg hover:text-blue-badge/50 transition duration-100" :href="system.url">
          {{ system.url }}
        </a>
      </div>
      <div>
        <div class="flex items-center space-x-2 text-label">
          <IconUser class="size-5 "/>
          <h1 :class="bigNameLabel">Username: </h1>
        </div>
        <p class="text-lg text-comment">{{ system.user }}</p>
      </div>
      <div>
        <div class="flex items-center space-x-2 text-label">
          <IconShieldExclamation class="size-5"/>
          <h1 :class="bigNameLabel">Minimal sverity level: </h1>
          <p class="text-lg text-comment">{{ system.min_active_level }}</p>
        </div>
      </div>

    </CardDescription>
    <CardFooter class="bottom-4 absolute w-full">
      <Button @click="onEdit" variant="green_outline" class="w-full *:size-5!">
        Edit configuration <IconEdit />
      </Button>
    </CardFooter>
  </Card>
</template>
