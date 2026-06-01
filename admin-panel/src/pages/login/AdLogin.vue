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
import {ADLoginSchema} from "@/helpers_functions/formSchemas.ts";
import {useForm} from "vee-validate";
import {useAuthStore} from "@/stores/authStore.ts";

const isLoading = defineModel<boolean>('isLoading')
const showAlert = defineModel<boolean>('showAlert')
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
      showAlert.value = true
  }
  catch { showAlert.value = true }
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
          <FormInput :disabled="showAlert" placeholder="AD email or username..." name="email" type="username" label="Login" orientation="vertical"/>
          <FormInput :disabled="showAlert" name="password" type="password" label="Password" orientation="vertical"/>
        </FieldGroup>
      </form>
      <div class="p-2 flex space-x-2 *:text-comment">
        <IconLockOpen/>
        <span class="text-sm  border-b-2 hover:text-blue-badge/70 hover:border-blue-badge/70 duration-200 cursor-pointer">
          Reset your password
        </span>
      </div>
    </CardContent>
    <CardFooter>
      <Field orientation="horizontal">
        <Button :disabled="showAlert" @click="adSubmit" type="button" class="w-full" form="ad-login-form">
          <span v-if="!isLoading">Log in</span>
          <IconLoader v-else class="animate-spin duration-75"/>
        </Button>
      </Field>
    </CardFooter>
  </Card>
</template>
