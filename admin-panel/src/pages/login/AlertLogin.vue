<script setup lang="ts">

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle
} from "@/components/ui/card";
import {Field, FieldGroup} from "@/components/ui/field";
import {IconLoader, IconLockOpen} from "@tabler/icons-vue";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {Button} from "@/components/ui/button";
import {useAuthStore} from "@/stores/authStore.ts";
import {useForm} from "vee-validate";
import {alertLoginSchema} from "@/helpers_functions/formSchemas.ts";

defineEmits<{
  'reset': [];
}>()

const isLoading = defineModel<boolean>('isLoading')
const showWrongPassword = defineModel<boolean>('showWrongPassword')
const authStore = useAuthStore();

const { handleSubmit } = useForm({
  validationSchema: alertLoginSchema,
  initialValues: {
    alert_email: '',
    alert_password: '',
  },
})

const onSubmit = handleSubmit(async (data) => {
  isLoading.value = true
  try {
    if(!await authStore.alertLogin({
      email: data.alert_email,
      password: data.alert_password
    }))
      showWrongPassword.value = true
  }
  catch { showWrongPassword.value = true }
  finally { isLoading.value = false }
})

</script>

<template>
  <Card class="border-blue-badge w-140 border-2 shadow-blue-badge/40 shadow-[0_5px_50px_1px] duration-500 "
       >

    <CardHeader>
      <CardTitle class="text-xl text-center">Sign in</CardTitle>
      <CardDescription class="text-center">
        Use Alert credentials
      </CardDescription>
    </CardHeader>
    <CardContent>

      <form class="p-2"  id="alert-login-form" @submit="onSubmit">
        <FieldGroup>
          <FormInput :disabled="showWrongPassword" placeholder="user@example.com..." name="alert_email" type="email" label="E-mail address" orientation="vertical"/>
          <FormInput autocomplete="password" :disabled="showWrongPassword" name="alert_password" type="password" label="Password" orientation="vertical"/>
        </FieldGroup>
      </form>
      <div class="p-2 flex space-x-2 *:text-comment">
        <IconLockOpen/>
        <span
          @click="$emit('reset')"
          class="text-sm  border-b-2 hover:text-blue-badge/70 hover:border-blue-badge/70 duration-200 cursor-pointer">
          Reset your password
        </span>
      </div>
    </CardContent>
    <CardFooter>
      <Field orientation="horizontal">
        <Button id="alert_sign_in" :disabled="showWrongPassword" type="submit" class="w-full" form="alert-login-form">
          <span v-if="!isLoading">Sign in</span>
          <IconLoader v-else class="animate-spin duration-75"/>
        </Button>
      </Field>
    </CardFooter>
  </Card>
</template>
