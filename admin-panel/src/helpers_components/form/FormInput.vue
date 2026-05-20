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
  orientation: "horizontal" | "vertical"
}>()

const fieldId = props.name
</script>

<template>
  <VeeField v-slot="{ field, errors }" :name="name">
    <Field :orientation="props.orientation" :class="props.class" :data-invalid="!!errors.length">
        <MyFieldLabel class="justify-end" :text="label" :for="fieldId" />
      <div>
        <Input
          class="max-md:w-40 md:w-70 lg:w-90 xl:w-120"
          :id="fieldId"
          :type="type ?? 'text'"
          :placeholder="placeholder"
          :aria-invalid="!!errors.length"
          @update:model-value="field.onChange"
          :default-value="field.value"
          v-bind="field"
        />
        <FieldError v-if="errors.length" :errors="errors" />
      </div>
    </Field>
  </VeeField>
</template>
