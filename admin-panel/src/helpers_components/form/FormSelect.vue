<script setup lang="ts">
import { Field as VeeField } from "vee-validate";
import { Field, FieldError } from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";

const props = defineProps<{
  name: string
  label: string
  options: string[]
  placeholder?: string
}>()

const fieldId = props.name
</script>

<template>
  <VeeField v-slot="{ field, errors }" :name="name">
    <Field :data-invalid="!!errors.length">
      <MyFieldLabel :text="label" :for="fieldId" />
      <Select v-bind="field" :default-value="field.value" @update:model-value="field.onChange">
        <SelectTrigger :id="fieldId" :aria-invalid="!!errors.length">
          <SelectValue :placeholder="placeholder ?? 'Select...'" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem v-for="option in options" :key="option" :value="option">
            {{ option }}
          </SelectItem>
        </SelectContent>
      </Select>
      <FieldError v-if="errors.length" :errors="errors" />
    </Field>
  </VeeField>
</template>
