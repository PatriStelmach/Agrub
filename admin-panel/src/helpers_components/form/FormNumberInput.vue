<script setup lang="ts">
import { Field as VeeField } from "vee-validate";
import { Field, FieldError } from "@/components/ui/field";
import { NumberField, NumberFieldContent, NumberFieldDecrement, NumberFieldIncrement, NumberFieldInput } from "@/components/ui/number-field";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
import type {HTMLAttributes} from "vue";

const props = defineProps<{
  name: string
  label: string
  placeholder?: string
  min?: number
  max?: number
  step?: number
  class?: HTMLAttributes["class"]
  orientation: "vertical" | "horizontal"
}>()

const fieldId = props.name
</script>

<template>
  <VeeField v-slot="{ field, errors }" :name="name">
    <Field class="gap-y-1" :orientation="props.orientation" :class="props.class" :data-invalid="!!errors.length">
      <div class="flex space-x-2 items-center">
        <slot/>
        <MyFieldLabel :text="label" :for="fieldId" />
      </div>
        <NumberField
          :id="fieldId"
          :min="min"
          :max="max"
          :step="step"
          :default-value="field.value"
          :aria-invalid="!!errors.length"
          @update:model-value="field.onChange"
        >
          <NumberFieldContent>
            <NumberFieldDecrement />
            <NumberFieldInput :placeholder="placeholder" />
            <NumberFieldIncrement />
          </NumberFieldContent>
        </NumberField>
      <FieldError class="text-center" v-if="errors.length" :errors="errors" />
    </Field>
  </VeeField>
</template>
