<script setup lang="ts">

import {useForm} from "vee-validate";
import {agrubSettingSchema} from "@/helpers_functions/formSchemas.ts";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {
  IconDeviceFloppy,
  IconChevronsRight,
  IconEdit,
  IconLoader,
  IconX,
} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import {ref} from "vue";
import type {AgrubSettings} from "@/types/types.ts";
import {toast} from "vue-sonner";
import {useSettingStore} from "@/stores/settingStore.ts";
import agrub_logo_dark from "/icons/agrub_logo_dark.png"
import agrub_logo_light from "/icons/agrub_logo_light.png"
import {useColorMode} from "@vueuse/core";

const props = defineProps<{
  agrub: AgrubSettings
}>()

const mode = useColorMode()
const settingStore = useSettingStore()
const agrubEdit = defineModel<boolean>('agrubEdit')
const agrubLoading = ref(false)

const { handleSubmit, resetForm} = useForm({
  validationSchema: agrubSettingSchema,
  initialValues: props.agrub,
})

const onSubmit = handleSubmit(async (data) => {
  agrubLoading.value = true
  const changedValues: Record<string, string> = {}

  for (const key of Object.keys(props.agrub) as (keyof AgrubSettings)[]) {
    if (data[key] !== props.agrub[key]) {
      changedValues[key] = String(data[key])
    }
  }

  if (Object.keys(changedValues).length === 0) {
    toast.info("No changes detected.")
    agrubLoading.value = false
    return
  }

  await settingStore.updateSystemFullSettingsRequest(changedValues)
    .then(() => {
      toast.success('agrub settings updated')
      onCancel()
    })
    .catch((error) => toast.error(`Error updating agrub settings: ${error.message}`))
    .finally(() => agrubLoading.value = false)

})

const onCancel = () => {
  agrubEdit.value = !agrubEdit.value
  resetForm({values: props.agrub
  })
}
</script>

<template>

  <div>
    <div class="flex space-x-2 mb-4 items-center w-fit p-2 border-b-3 border-blue-badge/50">
      <img
        :src="mode === 'light' ? agrub_logo_light : agrub_logo_dark"
        class="size-10"
        alt="agrub_logo"/>
      <h1 :class="bigNameLabel">Agrub settings</h1>
      <Button
        v-if="!agrubEdit"
        size="icon-sm"
        variant="green_outline"
        @click="onCancel"
        ><IconEdit/>
      </Button>

    </div>
    <form class="space-y-6" id="agrub-settings-form" v-if="agrubEdit">
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
          v-if="agrubEdit"
          @click="onCancel"
        >Cancel <IconX/>
        </Button>
        <Button
          form="agrub-settings-form"
          type="submit"
          variant="green_outline"
          @click.prevent="onSubmit"
        >
          <IconLoader v-if="agrubLoading" class="animate-spin"/>
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
        <span class="text-comment mt-0.5">{{ agrub.external_system_sync_timer }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">Scripts execution timeout (seconds):</h1>
        <span class="text-comment mt-0.5">{{ agrub.scripts_execution_timeout_seconds }}</span>
      </li>
    </ul>

  </div>

</template>
