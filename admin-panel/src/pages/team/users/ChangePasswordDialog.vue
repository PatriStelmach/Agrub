<script setup lang="ts">

import {
  Dialog, DialogClose,
  DialogContent, DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTrigger
} from "@/components/ui/dialog";
import {useForm} from "vee-validate";
import {changePasswordSchema} from "@/helpers_functions/formSchemas.ts";
import {ref, watch} from "vue";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {Button} from "@/components/ui/button";
import {IconSend2, IconLoader, IconX, IconAlertCircle} from "@tabler/icons-vue";
import {Alert, AlertDescription, AlertTitle} from "@/components/ui/alert";
import {useAuthStore} from "@/stores/authStore.ts";
import {useUserStore} from "@/stores/userStore.ts";

const isSending = ref(false);
const isPasswordWrong = ref(false);

const { handleSubmit, values } = useForm({
  validationSchema: changePasswordSchema,
})

const isOpen = ref(false)
const authStore = useAuthStore();
const userStore = useUserStore();

const onSubmit = handleSubmit(async () => {
  isSending.value = true
  await userStore.changeUserPasswordRequest(values.oldPassword!, values.newPassword!)
    .then(() => isOpen.value = false)
    .catch(() => {
      isPasswordWrong.value = true
    })
    .finally(() => {
      authStore.refreshToken()
      isSending.value = false
    })
})

watch(isOpen, () => {
  if(isOpen.value === false) isPasswordWrong.value = false
})
</script>

<template>
  <Dialog  v-model:open="isOpen">
    <DialogTrigger type="button" as-child>
      <slot/>
    </DialogTrigger>
    <DialogContent :show-close-button="false" >
      <Transition name="fade">
        <div v-if="isPasswordWrong" class="absolute inset-0 z-50 flex items-center justify-center">
          <div
            class="absolute inset-0 bg-background/20 backdrop-blur-xs cursor-not-allowed"
            @click.stop
          />
          <Alert
            variant="destructive"
            class="z-50 relative w-[calc(100%-2rem)] border-red-badge/50 border-2 bg-background"
          >
            <IconX @click="isPasswordWrong = false" class="size-5! absolute top-2 right-2 hover:border cursor-pointer" />
            <IconAlertCircle />
            <AlertTitle>Wrong password!</AlertTitle>
            <AlertDescription>
              <ul class="mt-2 list-inside list-disc space-y-1">
                <li>Try again.</li>
                <li>Check your current password.</li>
                <li>Verify account status with administration.</li>
              </ul>
            </AlertDescription>
          </Alert>

        </div>
      </Transition>
      <div :class="{ 'blur-md pointer-events-none' :isPasswordWrong }">
        <DialogHeader>
          Change your password
        </DialogHeader>
        <DialogDescription>
          Password policy: minimum 8 characters, etc.
        </DialogDescription>
        <form id="change-password"  class="my-6 space-y-2" @submit="onSubmit">
          <FormInput name="oldPassword" label="Current password:" orientation="vertical" type="password" autocomplete="password">
          </FormInput>
          <FormInput name="newPassword" label="New password:" orientation="vertical" type="password" autocomplete="password">
          </FormInput>
          <FormInput name="confirmPassword" label="Confirm new password:" orientation="vertical" type="password" autocomplete="password">
          </FormInput>
        </form>
        <DialogFooter class="flex justify-between! w-full">
          <DialogClose as-child>
            <Button
              variant="red_outline"
            >
              Cancel <IconX/>
            </Button>
          </DialogClose>
          <Button variant="green_outline" form="change-password" type="submit">
            Submit
            <IconSend2 v-if="!isSending" class="size-4"/>
            <IconLoader v-else class="animate-spin size-4"/>
          </Button>
        </DialogFooter>
      </div>

    </DialogContent>
  </Dialog>
</template>

<style scoped>

</style>
