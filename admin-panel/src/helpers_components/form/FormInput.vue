<script setup lang="ts">
import { Field as VeeField } from "vee-validate";
import { Field, FieldError } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";

const props = defineProps<{
  name: string
  label: string
  ruleId: number
  placeholder?: string
  type?: string
}>()

const fieldId = `${props.name}-${props.ruleId}`
</script>

<template>
  <VeeField v-slot="{ field, errors }" :name="name">
    <Field :data-invalid="!!errors.length">
      <MyFieldLabel :text="label" :for="fieldId" />
      <Input
        :id="fieldId"
        :type="type ?? 'text'"
        :placeholder="placeholder"
        :aria-invalid="!!errors.length"
        @update:model-value="field.onChange"
        :default-value="field.value"
        v-bind="field"
      />
      <FieldError v-if="errors.length" :errors="errors" />
    </Field>
  </VeeField>
</template>
