<script setup lang="ts">

import {useForm} from "vee-validate";
import {securitySettingSchema} from "@/helpers_functions/formSchemas.ts";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";

const props = defineProps<{
  security: Record<string, string | number>
}>()

const { values, handleSubmit, resetForm} = useForm({
  validationSchema: securitySettingSchema,
  initialValues: {
    SECURITY_PASSWORD_LIFETIME_DAYS: props.security['Password lifetime days'] as number,
    SECURITY_ACCESS_TOKEN_EXP_MINUTES: props.security['Access token lifetime (minutes)'] as number,
    SECURITY_REFRESH_TOKEN_EXP_HOURS: props.security['Refresh token lifetime (hours)'] as number,
    SECURITY_AD_DOMAIN: props.security['Active Directory domain'] as string,
    SECURITY_AD_URL: props.security['Active Directory URL'] as string,
    SECURITY_LDAP_BASE_DN: props.security['Active Directory domain'] as string,
    SECURITY_LDAP_USER_DN_PATTERN: props.security['LDAP user dn pattern'] as string,
  }
})
</script>

<template>
  <form>
     <div>
       <FormNumberInput name="SECURITY_PASSWORD_LIFETIME_DAYS" label="Password lifetime days" orientation="vertical">

       </FormNumberInput>
     </div>
    <div>
      <FormNumberInput name="SECURITY_ACCESS_TOKEN_EXP_MINUTES" label="Password lifetime days" orientation="vertical">

      </FormNumberInput>
    </div>
    <div>
      <FormNumberInput name="SECURITY_REFRESH_TOKEN_EXP_HOURS" label="Password lifetime days" orientation="vertical">

      </FormNumberInput>
    </div>
  </form>
</template>
