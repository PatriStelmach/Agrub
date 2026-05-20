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
    <Field :orientation="props.orientation" :data-invalid="!!errors.length">
      <MyFieldLabel class="justify-end! " :text="label" :for="fieldId" />
      <div class="max-md:w-40 md:w-70 lg:w-90 xl:w-120">
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
      </div>

      <FieldError v-if="errors.length" :errors="errors" />
    </Field>
  </VeeField>
</template>
