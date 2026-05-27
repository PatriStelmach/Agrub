<script setup lang="ts">

import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {bigNameLabel, smallNameLabel} from "@/assets/cssFunctions.ts";
import { IconShieldLock, IconChevronsRight, IconPhoneRinging} from "@tabler/icons-vue";
import {useSettingStore} from "@/stores/settingStore.ts";
import {computed, onMounted, ref} from "vue";
import {toast} from "vue-sonner";
import {Mails} from "lucide-vue-next"

const areSettingsLoading = ref(false);
const settingsStore = useSettingStore()
const allSettings = computed(() => {
  if (settingsStore.systemFullSettings){
    const security = {
      'Password lifetime days': settingsStore.systemFullSettings.SECURITY_PASSWORD_LIFETIME_DAYS,
      'Access token lifetime minutes': settingsStore.systemFullSettings.SECURITY_ACCESS_TOKEN_EXP_MINUTES,
      'Refresh token lifetime hours' : settingsStore.systemFullSettings.SECURITY_REFRESH_TOKEN_EXP_HOURS,
      'Active Directory domain' : settingsStore.systemFullSettings.SECURITY_AD_DOMAIN,
      'Active Directory URL' : settingsStore.systemFullSettings.SECURITY_AD_URL,
      'LDAP base dn' : settingsStore.systemFullSettings.SECURITY_LDAP_BASE_DN,
      'LDAP user dn pattern' : settingsStore.systemFullSettings.SECURITY_LDAP_USER_DN_PATTERN
    }
    const smtp = {
      'SMTP host': settingsStore.systemFullSettings.smtp_host,
      'SMTP port': settingsStore.systemFullSettings.smtp_port,
      'SMTP user': settingsStore.systemFullSettings.smtp_user,
      'SMTP enabled': settingsStore.systemFullSettings.smtp_enabled
    }
    const alert = {
      'External systems synchronisation timer (seconds)': settingsStore.systemFullSettings.external_system_sync_timer,
      'Scripts execution timeout (seconds)': settingsStore.systemFullSettings.scripts_execution_timeout_seconds
    }
    return {
      security: security,
      alert: alert,
      smtp: smtp,
    }
  }
  else return {}
})

onMounted(async () => {
  if(!settingsStore.systemFullSettings) {
    areSettingsLoading.value = true;
    await settingsStore.getSystemFullSettingsRequest()
      .catch((error) => toast.error(`Error getting system configuration: ${error}`))
      .finally(() => areSettingsLoading.value = false)
  }
})

</script>

<template>
<div>
  <TopH1Div h1="System configuration"/>
  <div class="flex *:w-1/2 *:space-x-2 *:mx-6 h-[80vh]">
    <div class="">
      <div class="flex space-x-2 mb-4 w-fit p-2 border-b-3 border-blue-badge/50">
        <IconShieldLock/>
        <h1 :class="bigNameLabel">Security settings</h1>
      </div>
      <ul class="space-y-2">
        <li class="flex items-center space-x-2" v-for="(value, key) in allSettings.security" :key="key">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
          <h1 :class="bigNameLabel">{{ key }}: </h1>
          <span class="text-comment mt-0.5">{{ value }}</span>
        </li>
      </ul>
    </div>
    <div class="grid-cols-1 grid items-stretch">
      <div class="h-1/2  ">
        <div class="flex space-x-2 mb-4 p-2 border-b-3 border-blue-badge/50 w-fit" >
          <Mails/>
          <h1 :class="bigNameLabel">SMTP settings</h1>
        </div>
        <ul class="space-y-2">
          <li class="flex items-center space-x-2" v-for="(value, key) in allSettings.smtp" :key="key">
            <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
            <h1 :class="bigNameLabel">{{ key }}: </h1>
            <span class="text-comment mt-0.5">{{ value }}</span>
          </li>
        </ul>
      </div>
      <div class="h-1/2">
        <div class="flex space-x-2 mb-4 p-2 border-b-3 border-blue-badge/50 w-fit" >
          <IconPhoneRinging/>
          <h1 :class="bigNameLabel">Alert settings</h1>
        </div>
        <ul class="space-y-2">
          <li class="flex items-center space-x-2" v-for="(value, key) in allSettings.alert" :key="key">
            <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
            <h1 :class="bigNameLabel">{{ key }}: </h1>
            <span class="text-comment mt-0.5">{{ value }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</div>
</template>

