<script setup lang="ts">

import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {useSettingStore} from "@/stores/settingStore.ts";
import {computed, onMounted, ref} from "vue";
import {toast} from "vue-sonner";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import SecurityForm from "@/pages/settings/Configuration/SecurityForm.vue";
import SMTPForm from "@/pages/settings/Configuration/SMTPForm.vue";
import AlertForm from "@/pages/settings/Configuration/AlertForm.vue";
import type {AlertSettings, SecuritySettings, SmtpSettings} from "@/types/types.ts";

const areSettingsLoading = ref(true)

const securityEdit = ref(false)
const alertEdit = ref(false)
const smtpEdit = ref(false)
const settingsStore = useSettingStore()
const allSettings = computed(() => {
  if (settingsStore.systemFullSettings){
    const s = settingsStore.systemFullSettings
    const security = {
      SECURITY_PASSWORD_LIFETIME_DAYS: s.SECURITY_PASSWORD_LIFETIME_DAYS,
      SECURITY_ACCESS_TOKEN_EXP_MINUTES: s.SECURITY_ACCESS_TOKEN_EXP_MINUTES,
      SECURITY_REFRESH_TOKEN_EXP_HOURS: s.SECURITY_REFRESH_TOKEN_EXP_HOURS,
      SECURITY_AD_DOMAIN: s.SECURITY_AD_DOMAIN,
      SECURITY_AD_URL: s.SECURITY_AD_URL,
      SECURITY_LDAP_BASE_DN: s.SECURITY_LDAP_BASE_DN,
      SECURITY_LDAP_USER_DN_PATTERN: s.SECURITY_LDAP_USER_DN_PATTERN,
    } as SecuritySettings
    const smtp = {
      smtp_host: s.smtp_host,
      smtp_port: s.smtp_port,
      smtp_user: s.smtp_user,
      smtp_enabled: s.smtp_enabled,
      smtp_password_SECRET: s.smtp_password_SECRET
    } as SmtpSettings
    const alert = {
      external_system_sync_timer: s.external_system_sync_timer,
      scripts_execution_timeout_seconds: s.scripts_execution_timeout_seconds,
    } as AlertSettings
    return {
      security: security,
      alert: alert,
      smtp: smtp,
    }
  }
  else return {}
})

onMounted(async () => {
  if (!settingsStore.systemFullSettings){
    await settingsStore.getSystemFullSettingsRequest()
      .catch((error) => toast.error(`Error getting system configuration: ${error}`))
  }
  areSettingsLoading.value = false
})

</script>

<template>
<div>
  <TopH1Div h1="System configuration"/>
  <Transition class="w-full" name="fade" mode="out-in">
    <div class="grid grid-cols-2 gap-x-10 " v-if="areSettingsLoading">
      <BigLoadingBlock
        class="w-4/5 h-[40vh] "
      />
      <div class="grid grid-cols-1 grid-rows-2 w-full h-[80vh]">
        <BigLoadingBlock class="h-2/3 w-4/5" />
        <BigLoadingBlock class="h-1/2 w-4/5" />
      </div>
    </div>
    <div v-else class="flex *:w-1/2 *:space-x-2 *:mx-6 h-[80vh]">
      <SecurityForm
        v-model:securityEdit="securityEdit"
        :security="allSettings.security as SecuritySettings"
      />
      <div class="grid-cols-1 grid items-stretch">
        <SMTPForm
          v-model:smtpEdit="smtpEdit"
          :smtp="allSettings.smtp as SmtpSettings"
        />
        <AlertForm
          v-model:alertEdit="alertEdit"
          :alert="allSettings.alert as AlertSettings"
        />
      </div>
    </div>
  </Transition>
</div>
</template>

