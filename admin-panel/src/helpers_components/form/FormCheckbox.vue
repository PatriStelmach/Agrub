<script setup lang="ts">
import { Field as VeeField } from "vee-validate";
import { Field, FieldError } from "@/components/ui/field";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
const props = defineProps<{
  name: string
  label: string
}>()

const fieldId = props.name
</script>

<template>
  <VeeField v-slot="{ field, errors }" :name="name">
    <Field orientation="horizontal" :data-invalid="!!errors.length">
      <input
        type="checkbox"
        :id="fieldId"
        :checked="field.value"
        @update:checked="field.onChange"
      />
      <MyFieldLabel :text="label" :for="fieldId" class="cursor-pointer" />
      <FieldError v-if="errors.length" :errors="errors" />
    </Field>
  </VeeField>
</template>

