<script setup lang="ts">
import { Field as VeeField } from "vee-validate";
import { Field, FieldError } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
import type {HTMLAttributes} from "vue";

const props = defineProps<{
  name: string
  label: string
  placeholder?: string
  type?: string
  class?: HTMLAttributes["class"]
  labelClass?: HTMLAttributes["class"]
  orientation: "horizontal" | "vertical"
  autocomplete?: string
}>()

const fieldId = props.name
</script>

<template>
  <VeeField v-slot="{ field, errors }" :name="name">
    <Field class="gap-y-1" :orientation="props.orientation" :class="props.class" :data-invalid="!!errors.length">
      <div class="flex space-x-2 items-center">
        <slot/>
        <MyFieldLabel :class="labelClass" :text="label" :for="fieldId" />
      </div>
      <Input
        :autocomplete="autocomplete"
        :id="fieldId"
        :type="type ?? 'text'"
        :placeholder="placeholder"
        :aria-invalid="!!errors.length"
        @update:model-value="field.onChange"
        :default-value="field.value"
        v-bind="field"
      />
      <FieldError class="text-center" v-if="errors.length" :errors="errors" />
    </Field>
  </VeeField>
</template>
