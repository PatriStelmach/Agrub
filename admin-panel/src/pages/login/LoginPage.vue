<script setup lang="ts">

import {ref} from "vue";
import {Alert, AlertDescription, AlertTitle} from "@/components/ui/alert";
import { IconAlertCircle, IconX} from "@tabler/icons-vue";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import AdLogin from "@/pages/login/AdLogin.vue";
import AlertLogin from "@/pages/login/AlertLogin.vue";

const showAlert = ref(false)
const isLoading = ref(false)


</script>

<template>
  <Transition name="fade">
    <Alert
      class="z-999 absolute w-1/3 left-1/2 -translate-x-1/2 top-1/2 translate-y-1/2 border-red-badge/50 border-2 "
      v-if="showAlert" variant="destructive">
      <IconX @click="showAlert = false" class="size-5! absolute top-2 right-2 hover:border cursor-pointer"></IconX>
      <IconAlertCircle />
      <AlertTitle>Unable to log in!</AlertTitle>
      <AlertDescription>

        <p>Please verify your credentials:</p>
        <ul class="mt-2 list-inside list-disc space-y-1">
          <li>Check your login.</li>
          <li>Check your password.</li>
          <li>Verify account status with administration.</li>
        </ul>
      </AlertDescription>
    </Alert>
  </Transition>

  <div class="left-1/2 absolute -translate-x-1/2 top-1/2 translate-y-1/2">
    <Tabs default-value="ALERT">
      <TabsList class="mx-auto *:cursor-pointer">
        <TabsTrigger value="ALERT">
          Alert credentials
        </TabsTrigger>
        <TabsTrigger value="AD">
          Active Directory credentials
        </TabsTrigger>
      </TabsList>
      <TabsContent value="ALERT">
        <AlertLogin
          v-model:isLoading="isLoading"
          v-model:showAlert="showAlert"
        >

        </AlertLogin>
      </TabsContent>
      <TabsContent  value="AD">
        <AdLogin
          v-model:isLoading="isLoading"
          v-model:showAlert="showAlert"
        >

        </AdLogin>
      </TabsContent>
    </Tabs>
  </div>

</template>
