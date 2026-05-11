<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { useForm, Field as VeeField } from 'vee-validate'
import { toast } from 'vue-sonner'
import { z } from 'zod'

import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {useAuthStore} from "@/stores/authStore.ts";
import router from "@/router";
import {onMounted, ref} from "vue";
import {Alert, AlertDescription, AlertTitle} from "@/components/ui/alert";
import { IconAlertCircle, IconX, IconLoader} from "@tabler/icons-vue";

const authStore = useAuthStore()
const showAlert = ref(false)
const isLoading = ref(false)
const mounting = ref(true)

onMounted(() => {
  setTimeout(() => {
    mounting.value = false
  }, 300)
})

const formSchema = toTypedSchema(
  z.object({
    email: z
      .string()
      .email('Invalid email address'),
    password: z
      .string()
      .min(8, 'Password must be at least 8 characters.')
  }),
)

const { handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    email: '',
    password: '',
  },
})

const onSubmit = handleSubmit(async (data) => {
  isLoading.value = true
  try {
    if(!await authStore.login(data))
      showAlert.value = true
  }
  catch { showAlert.value = true }
  finally { isLoading.value = false }
})
</script>

<template>
  <Transition v-if="!mounting" name="fade">
    <Alert
      class="z-999 absolute w-1/3 left-1/2 -translate-x-1/2 top-1/2 translate-y-1/2 border-red-badge/50 border-2 "
      v-if="showAlert" variant="destructive">
      <IconX @click="showAlert = false" class="size-5! absolute top-2 right-2 hover:border cursor-pointer"></IconX>
      <IconAlertCircle />
      <AlertTitle>Unable to log in</AlertTitle>
      <AlertDescription>

        <p>Please verify your credentials</p>
        <ul class="mt-2 list-inside list-disc space-y-1">
          <li>Check your login</li>
          <li>Check your password</li>
          <li>Verify account status with administration</li>
        </ul>
      </AlertDescription>
    </Alert>
  </Transition>


  <Card class="border-blue-badge w-1/3 border-2 shadow-blue-badge/50 shadow-[0_5px_50px_1px] duration-500 mx-auto top-1/2 translate-y-1/2"
        :class="{'blur-xl' : showAlert}">
    <CardHeader>
      <CardTitle class="text-xl text-center">Log in</CardTitle>
      <CardDescription class="text-center">
        Log in to Alert
      </CardDescription>
    </CardHeader>
    <CardContent>

      <form class="p-2"  id="login-form" @submit="onSubmit">
        <FieldGroup>
          <VeeField v-slot="{ field, errors }" name="email">
            <Field :data-invalid="!!errors.length">
              <FieldLabel for="email">
                E-mail address
              </FieldLabel>
              <Input
                id="email"
                type="email"
                v-bind="field"
                placeholder="user@domain.com"
                :aria-invalid="!!errors.length"
                autocomplete="email"
              />
              <FieldError v-if="errors.length" :errors="errors" />
            </Field>
          </VeeField>

          <VeeField v-slot="{ field, errors }" name="password">
            <Field :data-invalid="!!errors.length">
              <FieldLabel for="password">
                Password
              </FieldLabel>
                <Input
                  id="password"
                  v-bind="field"
                  type="password"
                  placeholder="Password"
                  :aria-invalid="!!errors.length"
                  autocomplete="current-password"
                />
              <FieldError v-if="errors.length" :errors="errors" />
            </Field>
          </VeeField>
        </FieldGroup>
      </form>
    </CardContent>
    <CardFooter>
      <Field orientation="horizontal">
        <Button  type="submit" class="w-full" form="login-form">
          <span v-if="!isLoading">Log in</span>
          <IconLoader v-else class="animate-spin duration-75"/>
        </Button>
      </Field>
    </CardFooter>
  </Card>
</template>
