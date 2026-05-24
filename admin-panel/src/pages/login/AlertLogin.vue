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
import {useAuthStore} from "@/stores/authStore.ts";
import {useForm} from "vee-validate";
import {alertLoginSchema} from "@/helpers_functions/formSchemas.ts";

const isLoading = defineModel<boolean>('isLoading')
const showAlert = defineModel<boolean>('showAlert')
const authStore = useAuthStore();

const { handleSubmit } = useForm({
  validationSchema: alertLoginSchema,
  initialValues: {
    email: '',
    password: '',
  },
})

const onSubmit = handleSubmit(async (data) => {
  console.log(data);
  isLoading.value = true
  try {
    if(!await authStore.alertLogin(data))
      showAlert.value = true
  }
  catch { showAlert.value = true }
  finally { isLoading.value = false }
})

</script>

<template>
  <Card class="border-blue-badge w-140 border-2 shadow-blue-badge/40 shadow-[0_5px_50px_1px] duration-500 "
        :class="{'blur-xl' : showAlert}">

    <CardHeader>
      <CardTitle class="text-xl text-center">Log in</CardTitle>
      <CardDescription class="text-center">
        Use Alert credentials
      </CardDescription>
    </CardHeader>
    <CardContent>

      <form class="p-2"  id="alert-login-form" @submit="onSubmit">
        <FieldGroup>
          <FormInput placeholder="user@example.com" name="email" type="email" label="E-mail address" orientation="vertical"/>
          <FormInput placeholder="password..." name="password" type="password" label="Password" orientation="vertical"/>
        </FieldGroup>
      </form>
    </CardContent>
    <CardFooter>
      <Field orientation="horizontal">
        <Button  type="submit" class="w-full" form="alert-login-form">
          <span v-if="!isLoading">Log in</span>
          <IconLoader v-else class="animate-spin duration-75"/>
        </Button>
      </Field>
    </CardFooter>
  </Card>
</template>
