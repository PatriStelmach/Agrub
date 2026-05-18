<script setup lang="ts">
import { ref, nextTick } from 'vue'
import type { ZabbixConfig } from '@/types/types.ts'
import { z } from 'zod'
import { toTypedSchema } from '@vee-validate/zod'
import { useForm } from 'vee-validate'
import zabbix_logo from '@/components/icons/zabbix_logo.jpeg'
import {Card, CardContent, CardFooter, CardHeader} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import { IconEdit, IconDeviceFloppy, IconX } from '@tabler/icons-vue'
import FormInput from '@/helpers_components/form/FormInput.vue'
import { toast } from 'vue-sonner'

const props = defineProps<{
  config: ZabbixConfig
}>()

const emit = defineEmits<{
  save: [config: ZabbixConfig]
}>()

const isEditing = ref(false)

const formSchema = toTypedSchema(
  z.object({
    zabbix_url: z.string().url('Must be a valid URL'),
    zabbix_api_token_SECRET: z.string().min(1, 'API token is required'),
    zabbix_enabled: z.string(),
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
  emit('save', values as ZabbixConfig)
  isEditing.value = false
  toast.success('Zabbix config saved')
})
</script>

<template>
  <Card>
    <CardHeader class="items-center gap-2">
      <img :src="zabbix_logo" alt="zabbix_logo" class="size-16" />
      <span class="font-bold text-lg">Zabbix</span>
    </CardHeader>
    <CardContent class="space-y-3">
      <form v-if="isEditing" @submit="onSubmit" class="space-y-3">
        <FormInput name="zabbix_url" label="URL" />
        <FormInput name="zabbix_api_token_SECRET" label="API Token" type="password" />
        <FormInput name="zabbix_enabled" label="Enabled" />
        <div class="flex justify-end ml-auto! gap-2">
          <Button type="button" variant="red_outline" size="icon-sm" @click="cancelEdit"><IconX /></Button>
          <Button type="submit" variant="green_outline" size="icon-sm"><IconDeviceFloppy /></Button>
        </div>
      </form>
      <div v-else class="space-y-3">
        <div class="space-y-1 text-sm">
          <p><span class="font-semibold">URL:</span> {{ config.zabbix_url }}</p>
          <p><span class="font-semibold">API Token:</span> ••••••••</p>
          <p><span class="font-semibold">Enabled:</span> {{ config.zabbix_enabled }}</p>
        </div>
      </div>
    </CardContent>
    <CardFooter v-if="!isEditing">
      <Button variant="green_outline" class="ml-auto mt-auto!" size="icon-sm" @click="startEdit"><IconEdit/></Button>

    </CardFooter>
  </Card>
</template>
