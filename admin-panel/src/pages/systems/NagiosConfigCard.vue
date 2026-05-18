<script setup lang="ts">
import { ref, nextTick } from 'vue'
import type { NagiosConfig } from '@/types/types.ts'
import { z } from 'zod'
import { toTypedSchema } from '@vee-validate/zod'
import { useForm } from 'vee-validate'
import nagios_logo from '@/components/icons/nagios_logo.webp'
import {Card, CardContent, CardFooter, CardHeader} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import { IconEdit, IconDeviceFloppy, IconX } from '@tabler/icons-vue'
import FormInput from '@/helpers_components/form/FormInput.vue'
import { toast } from 'vue-sonner'

const props = defineProps<{
  config: NagiosConfig
}>()

const emit = defineEmits<{
  save: [config: NagiosConfig]
}>()

const isEditing = ref(false)

const formSchema = toTypedSchema(
  z.object({
    nagios_url: z.string().url('Must be a valid URL'),
    nagios_enabled: z.string(),
    nagios_user: z.string().min(1, 'User is required'),
    nagios_pass_SECRET: z.string().min(1, 'Password is required'),
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
  emit('save', values as NagiosConfig)
  isEditing.value = false
  toast.success('Nagios config saved')
})
</script>

<template>
  <Card>
    <CardHeader class="items-center gap-2">
      <img :src="nagios_logo" alt="nagios_logo" class="size-16" />
      <span class="font-bold text-lg">Nagios</span>
    </CardHeader>
    <CardContent class="space-y-3 ">
      <form v-if="isEditing" @submit="onSubmit" class="space-y-3">
        <FormInput name="nagios_url" label="URL" />
        <FormInput name="nagios_user" label="User" />
        <FormInput name="nagios_pass_SECRET" label="Password" type="password" />
        <FormInput name="nagios_enabled" label="Enabled" />
        <div class="flex justify-end ml-auto! gap-2">
          <Button type="button" variant="red_outline" size="icon-sm" @click="cancelEdit"><IconX /></Button>
          <Button type="submit" variant="green_outline" size="icon-sm"><IconDeviceFloppy /></Button>
        </div>
      </form>
      <div v-else class="space-y-3">
        <div class="space-y-1 text-sm">
          <p><span class="font-semibold">URL:</span> {{ config.nagios_url }}</p>
          <p><span class="font-semibold">User:</span> {{ config.nagios_user }}</p>
          <p><span class="font-semibold">Password:</span> ••••••••</p>
          <p><span class="font-semibold">Enabled:</span> {{ config.nagios_enabled }}</p>
        </div>
      </div>
    </CardContent>
    <CardFooter v-if="!isEditing">
      <Button variant="green_outline" class=" ml-auto mt-auto" size="icon-sm" @click="startEdit"><IconEdit/></Button>

    </CardFooter>
  </Card>
</template>
