<script setup lang="ts">
import { ref, nextTick } from 'vue'
import type { WazuhConfig } from '@/types/types.ts'
import { z } from 'zod'
import { toTypedSchema } from '@vee-validate/zod'
import { useForm } from 'vee-validate'
import wazuh_logo from '@/components/icons/wazuh_logo.png'
import {Card, CardContent, CardFooter, CardHeader} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import { IconEdit, IconDeviceFloppy, IconX } from '@tabler/icons-vue'
import FormInput from '@/helpers_components/form/FormInput.vue'
import { toast } from 'vue-sonner'

const props = defineProps<{
  config: WazuhConfig
}>()

const emit = defineEmits<{
  save: [config: WazuhConfig]
}>()

const isEditing = ref(false)

const formSchema = toTypedSchema(
  z.object({
    wazuh_url: z.string().url('Must be a valid URL'),
    wazuh_user: z.string().min(1, 'User is required'),
    wazuh_password_SECRET: z.string().min(1, 'Password is required'),
    wazuh_enabled: z.string(),
    wazuh_min_active_level: z.string(),
  })
)

const { handleSubmit, resetForm } = useForm({
  validationSchema: formSchema,
})

function startEdit() {
  isEditing.value = true
  nextTick(() => resetForm({ values: { ...props.config } }))
}

function cancelEdit() {
  isEditing.value = false
}

const onSubmit = handleSubmit((values) => {
  emit('save', values as WazuhConfig)
  isEditing.value = false
  toast.success('Wazuh config saved')
})
</script>

<template>
  <Card>
    <CardHeader class="items-center gap-2">
      <img :src="wazuh_logo" alt="wazuh_logo" class="size-16" />
      <span class="font-bold text-lg">Wazuh</span>
    </CardHeader>
    <CardContent class="space-y-3 ">
      <form v-if="isEditing" @submit="onSubmit" class="space-y-3">
        <FormInput name="wazuh_url" label="URL" />
        <FormInput name="wazuh_user" label="User" />
        <FormInput name="wazuh_password_SECRET" label="Password" type="password" />
        <FormInput name="wazuh_min_active_level" label="Min Active Level" />
        <FormInput name="wazuh_enabled" label="Enabled" />
        <div class="flex justify-end ml-auto! gap-2">
          <Button type="button" variant="red_outline" size="icon-sm" @click="cancelEdit"><IconX /></Button>
          <Button type="submit" variant="green_outline" size="icon-sm"><IconDeviceFloppy /></Button>
        </div>
      </form>
      <div v-else class="space-y-3">
        <div class="space-y-1 text-sm">
          <p><span class="font-semibold">URL:</span> {{ config.wazuh_url }}</p>
          <p><span class="font-semibold">User:</span> {{ config.wazuh_user }}</p>
          <p><span class="font-semibold">Password:</span> ••••••••</p>
          <p><span class="font-semibold">Enabled:</span> {{ config.wazuh_enabled }}</p>
          <p><span class="font-semibold">Min Level:</span> {{ config.wazuh_min_active_level }}</p>
        </div>
      </div>
    </CardContent>
    <CardFooter v-if="!isEditing">
      <Button variant="green_outline" class="ml-auto mt-auto" size="icon-sm" @click="startEdit"><IconEdit/></Button>
    </CardFooter>
  </Card>
</template>
