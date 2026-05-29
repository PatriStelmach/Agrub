<script setup lang="ts">

import {useForm} from "vee-validate";
import {securitySettingSchema} from "@/helpers_functions/formSchemas.ts";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {
  IconDeviceFloppy,
  IconChevronsRight,
  IconEdit,
  IconLoader,
  IconShieldLock,
  IconX
} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import {ref} from "vue";
import FormInput from "@/helpers_components/form/FormInput.vue";
import type {SecuritySettings} from "@/types/types.ts";
import {toast} from "vue-sonner";
import {useSettingStore} from "@/stores/settingStore.ts";

const props = defineProps<{
  security: SecuritySettings
}>()

const settingStore = useSettingStore()
const securityEdit = defineModel<boolean>('securityEdit')
const securityLoading = ref(false)

const { values, handleSubmit, resetForm} = useForm({
  validationSchema: securitySettingSchema,
  initialValues: props.security,
})

const onSubmit = handleSubmit(async (data) => {
  securityLoading.value = true
  const changedValues: Record<string, string> = {}

  for (const key of Object.keys(props.security) as (keyof SecuritySettings)[]) {
    if (values[key] !== props.security[key]) {
      changedValues[key] = String(values[key])
    }
  }

  if (Object.keys(changedValues).length === 0) {
    toast.info("No changes detected.")
    securityLoading.value = false
    return
  }

  await settingStore.updateSystemFullSettingsRequest(changedValues)
    .then(() => {
      toast.success('Security settings updated')
      onCancel()
    })
    .catch((error) => toast.error(`Error updating security settings: ${error.message}`))
    .finally(() => securityLoading.value = false)

})

const onCancel = () => {
  securityEdit.value = !securityEdit.value
  resetForm({values: props.security })
}
</script>

<template>

  <div>
    <div class="flex space-x-2 mb-4 items-center w-fit p-2 border-b-3 border-blue-badge/50">
      <IconShieldLock/>
      <h1 :class="bigNameLabel">Security settings</h1>
      <Button
        v-if="!securityEdit"
        size="icon-sm"
        variant="green_outline"
        @click="onCancel"
        ><IconEdit/>
      </Button>

    </div>
    <form class="space-y-6" id="security-settings-form" v-if="securityEdit">
      <div>
        <FormNumberInput name="SECURITY_PASSWORD_LIFETIME_DAYS" label="Password lifetime (days)" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormNumberInput>
      </div>
      <div>
        <FormNumberInput name="SECURITY_ACCESS_TOKEN_EXP_MINUTES" label="Access token lifetime (minutes)" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormNumberInput>
      </div>
      <div>
        <FormNumberInput name="SECURITY_REFRESH_TOKEN_EXP_HOURS" label="Refresh token lifetime (hours)" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormNumberInput>
      </div>
      <div>
        <FormInput name="SECURITY_AD_DOMAIN" label="Active Directory domain" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormInput>
      </div>
      <div>
        <FormInput name="SECURITY_AD_URL" label="Active Directory URL" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormInput>
      </div>
      <div>
        <FormInput name="SECURITY_LDAP_BASE_DN" label="LDAP base dn" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormInput>
      </div>
      <div>
        <FormInput name="SECURITY_LDAP_USER_DN_PATTERN" label="LDAP user dn pattern" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormInput>
      </div>
      <div class="flex space-x-2 justify-end">
        <Button
          type="button"
          variant="red_outline"
          v-if="securityEdit"
          @click="onCancel"
        >Cancel <IconX/>
        </Button>
        <Button
          form="security-settings-form"
          type="submit"
          variant="green_outline"
          @click.prevent="onSubmit"
        >
          <IconLoader v-if="securityLoading" class="animate-spin"/>
          <span class="flex items-center gap-x-2" v-else>
            Save
            <IconDeviceFloppy/>
          </span>
        </Button>
      </div>
    </form>
    <!-- lista danych -->
    <ul v-else class="space-y-2">
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">Password lifetime (days):</h1>
        <span class="text-comment mt-0.5">{{ security.SECURITY_PASSWORD_LIFETIME_DAYS }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">Access token lifetime (minutes):</h1>
        <span class="text-comment mt-0.5">{{ security.SECURITY_ACCESS_TOKEN_EXP_MINUTES }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">Refresh token lifetime (hours):</h1>
        <span class="text-comment mt-0.5">{{ security.SECURITY_REFRESH_TOKEN_EXP_HOURS }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">Active Directory domain:</h1>
        <span class="text-comment mt-0.5">{{ security.SECURITY_AD_DOMAIN }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">Active Directory URL:</h1>
        <span class="text-comment mt-0.5">{{ security.SECURITY_AD_URL }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">LDAP base dn:</h1>
        <span class="text-comment mt-0.5">{{ security.SECURITY_LDAP_BASE_DN }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">LDAP user dn pattern:</h1>
        <span class="text-comment mt-0.5">{{ security.SECURITY_LDAP_USER_DN_PATTERN }}</span>
      </li>
    </ul>

  </div>

</template>
