<script setup lang="ts">

import {useForm} from "vee-validate";
import {alertSettingSchema} from "@/helpers_functions/formSchemas.ts";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {
  IconDeviceFloppy,
  IconChevronsRight,
  IconEdit,
  IconLoader,
  IconX,
  IconPhoneRinging,
} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import {ref} from "vue";
import type {AlertSettings} from "@/types/types.ts";
import {toast} from "vue-sonner";
import {useSettingStore} from "@/stores/settingStore.ts";

const props = defineProps<{
  alert: AlertSettings
}>()

const settingStore = useSettingStore()
const alertEdit = defineModel<boolean>('alertEdit')
const alertLoading = ref(false)

const { values, handleSubmit, resetForm} = useForm({
  validationSchema: alertSettingSchema,
  initialValues: props.alert,
})

const onSubmit = handleSubmit(async (data) => {
  alertLoading.value = true
  const changedValues: Record<string, string> = {}

  for (const key of Object.keys(props.alert) as (keyof AlertSettings)[]) {
    if (values[key] !== props.alert[key]) {
      changedValues[key] = String(values[key])
    }
  }

  if (Object.keys(changedValues).length === 0) {
    toast.info("No changes detected.")
    alertLoading.value = false
    return
  }

  await settingStore.updateSystemFullSettingsRequest(changedValues)
    .then(() => {
      toast.success('Alert settings updated')
      onCancel()
    })
    .catch((error) => toast.error(`Error updating alert settings: ${error.message}`))
    .finally(() => alertLoading.value = false)

})

const onCancel = () => {
  alertEdit.value = !alertEdit.value
  resetForm({values: props.alert
  })
}
</script>

<template>

  <div>
    <div class="flex space-x-2 mb-4 items-center w-fit p-2 border-b-3 border-blue-badge/50">
      <IconPhoneRinging/>
      <h1 :class="bigNameLabel">Alert settings</h1>
      <Button
        v-if="!alertEdit"
        size="icon-sm"
        variant="green_outline"
        @click="onCancel"
        ><IconEdit/>
      </Button>

    </div>
    <form class="space-y-6" id="alert-settings-form" v-if="alertEdit">
      <div>
        <FormNumberInput name="external_system_sync_timer" label="External systems synchronisation timer (seconds)" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormNumberInput>
      </div>
      <div>
        <FormNumberInput name="scripts_execution_timeout_seconds" label="Scripts execution timeout (seconds)" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormNumberInput>
      </div>
      <div class="flex space-x-2 justify-end">
        <Button
          type="button"
          variant="red_outline"
          v-if="alertEdit"
          @click="onCancel"
        >Cancel <IconX/>
        </Button>
        <Button
          form="alert-settings-form"
          type="submit"
          variant="green_outline"
          @click.prevent="onSubmit"
        >
          <IconLoader v-if="alertLoading" class="animate-spin"/>
          <span class="flex items-center gap-x-2" v-else>
            Save
            <IconDeviceFloppy/>
          </span>
        </Button>
      </div>
    </form>
    <ul v-else class="space-y-2">
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">External systems synchronisation timer (seconds):</h1>
        <span class="text-comment mt-0.5">{{ alert.external_system_sync_timer }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">Scripts execution timeout (seconds):</h1>
        <span class="text-comment mt-0.5">{{ alert.scripts_execution_timeout_seconds }}</span>
      </li>
    </ul>

  </div>

</template>
