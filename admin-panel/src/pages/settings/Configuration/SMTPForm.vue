<script setup lang="ts">

import {useForm} from "vee-validate";
import {smtpSettingSchema} from "@/helpers_functions/formSchemas.ts";
import FormInput from "@/helpers_components/form/FormInput.vue";
import FormCheckbox from "@/helpers_components/form/FormCheckbox.vue";
import FormSelect from "@/helpers_components/form/FormSelect.vue";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {
  IconDeviceFloppy,
  IconChevronsRight,
  IconEdit,
  IconLoader,
  IconX,
} from "@tabler/icons-vue";
import {Mails} from "lucide-vue-next"
import {Button} from "@/components/ui/button";
import {ref} from "vue";
import type {SmtpSettings} from "@/types/types.ts";
import {toast} from "vue-sonner";
import {useSettingStore} from "@/stores/settingStore.ts";

const props = defineProps<{
  smtp: SmtpSettings
}>()

const settingStore = useSettingStore()
const smtpEdit = defineModel<boolean>('smtpEdit')
const smtpLoading = ref(false)

const { handleSubmit, resetForm} = useForm({
  validationSchema: smtpSettingSchema,
  initialValues: props.smtp
})

const onSubmit = handleSubmit(async (data) => {
  smtpLoading.value = true
  const changedValues: Record<string, string> = {}

  for (const key of Object.keys(props.smtp) as (keyof SmtpSettings)[]) {
    if (data[key] !== props.smtp[key]) {
      if (key === 'smtp_password_SECRET' && !data[key]) continue
      changedValues[key] = String(data[key])
    }
  }

  if (Object.keys(changedValues).length === 0) {
    toast.info("No changes detected.")
    smtpLoading.value = false
    return
  }

  await settingStore.updateSystemFullSettingsRequest(changedValues)
    .then(() => {
      toast.success('SMTP settings updated')
      onCancel()
    })
    .catch((error) => toast.error(`Error updating SMTP settings: ${error.message}`))
    .finally(() => smtpLoading.value = false)

})

const onCancel = () => {
  smtpEdit.value = !smtpEdit.value
  resetForm({ values: props.smtp })
}
</script>

<template>

  <div>
    <div class="flex space-x-2 mb-4 items-center w-fit p-2 border-b-3 border-blue-badge/50">
      <Mails/>
      <h1 :class="bigNameLabel">SMTP settings</h1>
      <Button
        v-if="!smtpEdit"
        size="icon-sm"
        variant="green_outline"
        @click="onCancel"
        ><IconEdit/>
      </Button>

    </div>
    <form class="space-y-6" id="smtp-settings-form" v-if="smtpEdit">
      <div>
        <FormInput name="smtp_host" label="SMTP host" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormInput>
      </div>
      <div>
        <FormSelect name="smtp_port" label="SMTP port" :options="['25', '587', '465', '2525']" orientation="vertical"/>
      </div>
      <div>
        <FormInput name="smtp_user" label="SMTP user" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormInput>
      </div>
      <div>
        <FormInput name="smtp_password_SECRET" label="SMTP password" type="password" orientation="vertical">
          <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        </FormInput>
      </div>
      <div>
        <FormCheckbox name="smtp_enabled" label="SMTP enabled"/>
      </div>
      <div class="flex space-x-2 justify-end">
        <Button
          type="button"
          variant="red_outline"
          v-if="smtpEdit"
          @click="onCancel"
        >Cancel <IconX/>
        </Button>
        <Button
          form="smtp-settings-form"
          type="submit"
          variant="green_outline"
          @click.prevent="onSubmit"
        >
          <IconLoader v-if="smtpLoading" class="animate-spin"/>
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
        <h1 :class="bigNameLabel">SMTP host:</h1>
        <span class="text-comment mt-0.5">{{ smtp.smtp_host }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">SMTP port:</h1>
        <span class="text-comment mt-0.5">{{ smtp.smtp_port }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">SMTP user:</h1>
        <span class="text-comment mt-0.5">{{ smtp.smtp_user }}</span>
      </li>
      <li class="flex items-center space-x-2">
        <IconChevronsRight stroke="1.5" class="text-comment mb-1"/>
        <h1 :class="bigNameLabel">SMTP enabled:</h1>
        <span class="text-comment mt-0.5">{{ smtp.smtp_enabled }}</span>
      </li>
    </ul>

  </div>

</template>
