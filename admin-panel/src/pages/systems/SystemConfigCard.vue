<script setup lang="ts">
import wazuh_logo_black from "/icons/wazuh_logo_black.svg"
import wazuh_logo_white from "/icons/wazuh_logo_white.svg"
import zabbix_logo from "/icons/zabbix_logo.svg"
import nagios_logo from "/icons/nagios_logo.svg"
import {ref, watch} from "vue"
import { useColorMode } from "@vueuse/core"
import { bigNameLabel, gridCard } from "@/assets/cssFunctions.ts"
import {initialValuesMap, useSystemForm} from "@/helpers_functions/formSchemas.ts"
import { IconDeviceFloppy, IconLock, IconLockOff, IconCheck, IconEdit, IconX } from "@tabler/icons-vue"
import { Card, CardDescription, CardFooter, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ButtonGroup } from "@/components/ui/button-group"
import FormInput from "@/helpers_components/form/FormInput.vue"
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue"
import type { MonitoringSystemsConfig } from "@/types/types.ts"

const props = defineProps<{
  system: MonitoringSystemsConfig
  isUnwrapped: boolean
}>()

const emit = defineEmits<{
  'edit': [string]
  'cancel': []
  'save': [MonitoringSystemsConfig]
}>()

const changePassword = ref(false)
const mode = useColorMode()

const systemLogo = () => {
  switch (props.system.name) {
    case 'zabbix': return zabbix_logo
    case 'nagios': return nagios_logo
    case 'wazuh': return mode.value === 'light' ? wazuh_logo_black : wazuh_logo_white
  }
}

const { handleSubmit, setValues } = useSystemForm(props.system)

const onSubmit = handleSubmit((values) => {
  emit('save', values as any)
})

const onEdit = () => {
  setValues(props.system as MonitoringSystemsConfig)
  emit('edit', props.system.name)
}

const onCancel = () => {
  changePassword.value = false
  emit('cancel')
}

watch(() => props.isUnwrapped, (val) => {
  if (val) setValues(initialValuesMap[props.system.name](props.system) as any)
})
</script>

<template>
  <!-- EDIT CARD -->
  <Card
    v-if="props.isUnwrapped"
    class="cursor-auto! relative h-90! lg:h-100! xl:h-120! border-blue-badge/50 shadow-blue-badge/50!"
    :class="gridCard"
  >
    <form @submit.prevent="onSubmit">
      <CardHeader class="left-1/2 -translate-x-1/2 items-center relative">
        <div class="w-48 h-20 mx-auto">
          <img
            :src="systemLogo()"
            :alt="system.name"
            :class="{ '-mt-3': system.name === 'wazuh', '-mt-4': system.name === 'nagios' }"
          />
        </div>
      </CardHeader>
      <CardDescription class="px-3 space-y-3 *:flex *:items-center *:mr-2 -mt-4 max-h-50 lg:max-h-60 xl:max-h-70 overflow-auto">
        <div>
          <h1 class="ml-auto mr-3" :class="bigNameLabel">Enabled:</h1>
          <div class="max-md:w-40 md:w-70 lg:w-90 xl:w-120">
            <component :is="system.enabled ? IconCheck : IconX" :class="system.enabled ? 'text-green-badge' : 'text-red-badge'" />
          </div>
        </div>
        <div>
          <FormInput orientation="horizontal" :name="`${system.name}_url`" label="URL: " />
        </div>
        <div v-if="system.user">
          <FormInput orientation="horizontal" :name="`${system.name}_user`" label="User: " />
        </div>
        <div class="grid! grid-cols-1! space-y-3" v-if="changePassword">
          <div>
            <FormInput
              orientation="horizontal"
              type="password"
              :name="`${system.name}_password_SECRET`"
              :label="`Old ${system.name === 'zabbix' ? 'API key' : 'password'}:`"
            />
          </div>
          <div>
            <FormInput
              orientation="horizontal"
              type="password"
              :name="`${system.name}_password_new_SECRET`"
              :label="`New ${system.name === 'zabbix' ? 'API key' : 'password'}:`"
            />
          </div>
        </div>
        <div v-if="system.min_active_level">
          <FormNumberInput
            orientation="horizontal"
            :step="1" :min="0" :max="16"
            label="Minimal severity: "
            name="wazuh_min_active_level"
            class="text-lg text-comment flex"
          />
        </div>
      </CardDescription>
      <CardFooter class="bottom-4 absolute w-full">
        <ButtonGroup class="mx-auto">
          <Button @click="onCancel" variant="red_outline" class="*:size-5!">
            Cancel <IconX />
          </Button>
          <Button
            class="border-l-2!"
            :variant="changePassword ? 'red_outline' : 'blue_outline'"
            type="button"
            @click="changePassword = !changePassword"
          >
            Change {{ system.name === 'zabbix' ? 'API token' : 'password' }}
            <component :is="changePassword ? IconLockOff : IconLock" />
          </Button>
          <Button class="border-l-2!" type="submit" variant="green_outline">
            Save <IconDeviceFloppy />
          </Button>
        </ButtonGroup>
      </CardFooter>
    </form>
  </Card>

  <!-- VIEW CARD -->
  <Card v-else class="cursor-auto! relative h-90! lg:h-100! xl:h-120!" :class="gridCard">
    <CardHeader class="left-1/2 -translate-x-1/2 items-center relative">
      <div class="w-48 h-20 mx-auto">
        <img
          :src="systemLogo()"
          :alt="system.name"
          :class="{ '-mt-3': system.name === 'wazuh', '-mt-4': system.name === 'nagios' }"
        />
      </div>
    </CardHeader>
    <CardDescription class="px-3 space-y-2 *:flex *:space-x-1 *:items-center *:mr-2 -mt-4">
      <div>
        <h1 :class="bigNameLabel">Enabled:</h1>
        <component :is="system.enabled ? IconCheck : IconX" :class="system.enabled ? 'text-green-badge' : 'text-red-badge'" />
      </div>
      <div>
        <h1 :class="bigNameLabel">URL:</h1>
        <a class="cursor-pointer truncate text-comment italic text-lg hover:text-blue-badge/50 transition duration-100" :href="system.url">
          {{ system.url }}
        </a>
      </div>
      <div v-if="system.user">
        <h1 :class="bigNameLabel">User: </h1>
        <p class="text-lg text-comment">{{ system.user }}</p>
      </div>
      <div v-if="system.min_active_level">
        <h1 :class="bigNameLabel">Minimal severity level: </h1>
        <p class="text-lg text-comment">{{ system.min_active_level }}</p>
      </div>
    </CardDescription>
    <CardFooter class="bottom-4 absolute w-full">
      <Button @click="onEdit" variant="green_outline" class="w-full *:size-5!">
        Edit configuration <IconEdit />
      </Button>
    </CardFooter>
  </Card>
</template>
