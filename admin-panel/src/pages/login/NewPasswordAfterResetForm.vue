<script setup lang="ts">
import {IconLoader, IconSend2} from "@tabler/icons-vue";
import {useForm} from "vee-validate";
import {changeForgottenPasswordSchema} from "@/helpers_functions/formSchemas.ts";
import {InputOTP, InputOTPGroup, InputOTPSeparator, InputOTPSlot} from "@/components/ui/input-otp";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
import { Field as VeeField } from "vee-validate";
import { Field, FieldError } from "@/components/ui/field";
import {REGEXP_ONLY_DIGITS} from "vue-input-otp";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {Button} from "@/components/ui/button";
import {ref} from "vue";
import axios from "axios";
import {api_url} from "@/types/types.ts";
import {useAuthStore} from "@/stores/authStore.ts";

const isLoading = ref(false);
const authStore = useAuthStore();
const props = defineProps<{
  email: string;
}>()

const { handleSubmit } = useForm({
  validationSchema: changeForgottenPasswordSchema
})

const onSubmit = handleSubmit(async (data) => {
  isLoading.value = true;
  try {
    const res = await axios.post(`${api_url}/auth/reset-password`,{
        token: data.token,
        newPassword: data.newPassword,
      },{
      headers: {
        'Content-Type': 'application/json'
      }
    })
    if (res.status == 200) {
      await authStore.alertLogin({
        email: props.email,
        password: data.newPassword
      })
        .catch(error => alert(error))
        .finally(() => isLoading.value = false)
    }
  }
  catch (error) {
    alert(error);
  }
  finally {
    isLoading.value = false
  }
})
</script>

<template>
<form id="new-password-form" class="space-y-4 w-full">


  <VeeField v-slot="{ field, errors }" name="token">
    <Field class="gap-y-1" orientation="vertical" :data-invalid="!!errors.length">
        <MyFieldLabel text="Verification token" for="token:" />
      <InputOTP
        :pattern="REGEXP_ONLY_DIGITS"
        id="token"
        :aria-invalid="!!errors.length"
        @update:model-value="field.onChange"
        :default-value="field.value"
        v-bind="field"
        :maxlength="6">
        <InputOTPGroup>
          <InputOTPSlot :index="0" />
          <InputOTPSlot :index="1" />
          <InputOTPSlot :index="2" />
        </InputOTPGroup>
        <InputOTPSeparator />
        <InputOTPGroup>
          <InputOTPSlot :index="3" />
          <InputOTPSlot :index="4" />
          <InputOTPSlot :index="5" />
        </InputOTPGroup>
      </InputOTP>
      <FieldError class="text-center" v-if="errors.length" :errors="errors" />
    </Field>
  </VeeField>

  <div class="space-y-4 flex flex-col justify-end">
    <FormInput
      type="password"
      class="w-full"
      name="newPassword"
      label="New password"
      orientation="vertical"
    />
    <Button
      class="ml-auto"
      variant="blue_outline"
      form="new-password-form"
      type="submit"
      @click.prevent="onSubmit"
    >
      Submit
      <IconLoader v-if="isLoading" class="animate-spin"/>
      <IconSend2 v-else/>
    </Button>
  </div>

</form>
</template>

<style scoped>

</style>
