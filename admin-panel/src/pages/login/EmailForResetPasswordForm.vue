<script setup lang="ts">

import {IconLoader, IconSend2} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {useForm} from "vee-validate";
import {resetPasswordSchema} from "@/helpers_functions/formSchemas.ts";
import axios from "axios";
import {api_url} from "@/types/types.ts";
import {ref} from "vue";

const isLoading = ref(false)
const sentResetEmail = defineModel<boolean>('sentResetEmail')

const emits = defineEmits<{
  'email': [string]
}>()
const { handleSubmit } = useForm({
  validationSchema: resetPasswordSchema,
  initialValues: {
    reset_password_email: "",
  }
})

const onSubmit = handleSubmit( async (data) => {
  isLoading.value = true
  try {
    const res = await axios.post(`${api_url}/auth/forgot-password`, null,
      {
        params: {
          email: data.reset_password_email
        }})
    if (res.status === 200) {
      emits('email', data.reset_password_email)
      sentResetEmail.value = true
    }
  }
  catch (error) {
    throw (error)
  }
  finally {
    isLoading.value = false
  }
})
</script>

<template>
  <form id="reset-password-form" class="w-full space-y-4 flex flex-col justify-end">
    <FormInput
      type="email"
      name="reset_password_email"
      label="E-mail address"
      orientation="vertical"
    />
    <Button
      form="reset-password-form"
      type="submit"
      @click.prevent="onSubmit"
      class="ml-auto"
      variant="blue_outline"
    >
      Send
      <IconLoader v-if="isLoading" class="animate-spin"/>
      <IconSend2 v-else/>
    </Button>
  </form>
</template>

<style scoped>

</style>
