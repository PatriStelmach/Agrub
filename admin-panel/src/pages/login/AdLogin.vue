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
import {IconLoader} from "@tabler/icons-vue";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {Button} from "@/components/ui/button";
import {ADLoginSchema} from "@/helpers_functions/formSchemas.ts";
import {useForm} from "vee-validate";
import {useAuthStore} from "@/stores/authStore.ts";

defineEmits<{
  'reset': [];
}>()
const isLoading = defineModel<boolean>('isLoading')
const showWrongPassword = defineModel<boolean>('showWrongPassword')
const authStore = useAuthStore();

const { handleSubmit } = useForm({
  validationSchema: ADLoginSchema,
  initialValues: {
    email: '',
    password: '',
  },
})

const adSubmit = handleSubmit(async (data) => {
  isLoading.value = true
  try {
    if(!await authStore.ADLogin(data))
      showWrongPassword.value = true
  }
  catch { showWrongPassword.value = true }
  finally { isLoading.value = false }
})


</script>

<template>
  <Card class="border-blue-badge w-140 border-2 shadow-blue-badge/40 shadow-[0_5px_50px_1px] duration-500 ">

    <CardHeader>
      <CardTitle class="text-xl text-center">Log in</CardTitle>
      <CardDescription class="text-center">
        Use Active Directory credentials
      </CardDescription>
    </CardHeader>
    <CardContent>

      <form class="p-2"  id="ad-login-form" @submit.prevent="adSubmit">
        <FieldGroup>
          <FormInput :disabled="showWrongPassword" placeholder="AD email or username..." name="email" type="username" label="Login" orientation="vertical"/>
          <FormInput :disabled="showWrongPassword" name="password" type="password" label="Password" orientation="vertical"/>
        </FieldGroup>
      </form>
    </CardContent>
    <CardFooter>
      <Field orientation="horizontal">
        <Button :disabled="showWrongPassword" @click="adSubmit" type="button" class="w-full" form="ad-login-form">
          <span v-if="!isLoading">Log in</span>
          <IconLoader v-else class="animate-spin duration-75"/>
        </Button>
      </Field>
    </CardFooter>
  </Card>
</template>
