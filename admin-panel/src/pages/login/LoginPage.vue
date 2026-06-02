<script setup lang="ts">

import {ref} from "vue";
import {Alert, AlertDescription, AlertTitle} from "@/components/ui/alert";
import {IconAlertCircle, IconX, IconLockOpen} from "@tabler/icons-vue";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import AdLogin from "@/pages/login/AdLogin.vue";
import AlertLogin from "@/pages/login/AlertLogin.vue";

import NewPasswordAfterResetForm from "@/pages/login/NewPasswordAfterResetForm.vue";
import EmailForResetPasswordForm from "@/pages/login/EmailForResetPasswordForm.vue";

const showWrongPassword = ref(false)
const showResetPassword = ref(false)
const sentResetEmail = ref(false)
const isLoading = ref(false)
const email = ref("")



</script>

<template>
  <Transition name="fade">
    <Alert
      class="z-999 absolute w-1/3 left-1/2 gap-y-4 -translate-x-1/2 top-80 *:text-lg border-red-badge/50 border-2 "
      v-if="showWrongPassword" variant="destructive">
      <IconX @click="showWrongPassword = false" class="size-5! absolute top-2 right-2 hover:border cursor-pointer"></IconX>
      <IconAlertCircle class="size-5! mt-2 ml-auto" />
      <AlertTitle class="text-xl! mt-2 ">Unable to log in!</AlertTitle>
      <AlertDescription class="mb-2">

        <p>Please verify your credentials:</p>
        <ul class="mt-2 list-inside list-disc space-y-1">
          <li>Check your login.</li>
          <li>Check your password.</li>
          <li>Verify account status with administration.</li>
        </ul>
      </AlertDescription>
    </Alert>
  </Transition>

  <Transition name="fade">
    <Alert
      class="z-999 absolute w-1/3 left-1/2 gap-y-4 -translate-x-1/2 top-80 border-blue-badge/50 border-2 "
      v-if="showResetPassword" variant="default">
      <IconX @click="showResetPassword = false; sentResetEmail = false" class="size-5! absolute top-2 right-2 hover:border cursor-pointer"></IconX>
      <IconLockOpen class="size-5! mt-2" />
      <AlertTitle class="text-xl mt-2 ">Reset your password</AlertTitle>
      <AlertDescription class="space-y-2 mb-2 mr-7">
        <span>Enter your e-mail address to receive a link with instructions.</span>
        <NewPasswordAfterResetForm
          :email="email"
          v-model:sentResetEmail="sentResetEmail"
          v-if="sentResetEmail">

        </NewPasswordAfterResetForm>
        <EmailForResetPasswordForm
          @email="(data) => email = data"
          v-else
          v-model:sentResetEmail="sentResetEmail"
        />
      </AlertDescription>

    </Alert>
  </Transition>


    <Tabs :class="{'blur-xl' : showWrongPassword || showResetPassword}" class="left-1/2 absolute -translate-x-1/2 top-1/2 translate-y-1/2 duration-300" default-value="ALERT">
      <TabsList class="mx-auto *:cursor-pointer">
        <TabsTrigger :disabled="showWrongPassword" value="ALERT">
          Alert credentials
        </TabsTrigger>
        <TabsTrigger :disabled="showWrongPassword" value="AD">
          Active Directory credentials
        </TabsTrigger>
      </TabsList>
      <TabsContent  value="ALERT">
        <AlertLogin
          @reset="showResetPassword = true"
          v-model:isLoading="isLoading"
          v-model:showWrongPassword="showWrongPassword"
        >

        </AlertLogin>
      </TabsContent>
      <TabsContent value="AD">
        <AdLogin
          v-model:isLoading="isLoading"
          v-model:showWrongPassword="showWrongPassword"
        >

        </AdLogin>
      </TabsContent>
    </Tabs>

</template>
