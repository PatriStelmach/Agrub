<script setup lang="ts">
import { ref, watch } from "vue";
import { Field as VeeField, useForm } from "vee-validate";
import { toTypedSchema } from "@vee-validate/zod";
import { z } from "zod";

import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Field, FieldError } from "@/components/ui/field";
import { Button } from "@/components/ui/button";

import { MatchType, type Rule } from "@/types/types.ts";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
import SeveritySelect from "@/helpers_components/SeveritySelect.vue";
import FormInput from "@/helpers_components/form/FormInput.vue";
import FormSelect from "@/helpers_components/form/FormSelect.vue";
import FormCheckbox from "@/helpers_components/form/FormCheckbox.vue";

const props = defineProps<{
  isLoading: boolean
  rule: Rule
  ruleId: number
}>()

const emits = defineEmits<{
  'update:rule': [Rule]
}>()

const updatedRule = ref<Rule>({ ...props.rule })
const matchTypeOptions = Object.values(MatchType)

const formSchema = toTypedSchema(
  z.object({
    sourcePattern: z.string().trim().max(255, 'Maximum 255 characters'),
    sourceType: z.nativeEnum(MatchType),
    contentPattern: z.string().trim().max(255, 'Maximum 255 characters'),
    contentType: z.nativeEnum(MatchType),
    subjectPattern: z.string().trim().max(255, 'Maximum 255 characters'),
    subjectMatchType: z.nativeEnum(MatchType),
    originPattern: z.string().trim().max(255, 'Maximum 255 characters'),
    originMatchType: z.nativeEnum(MatchType),
    minSeverity: z.coerce.number().int().min(0).max(5),
    playSound: z.boolean(),
  })
)

const { handleSubmit, setValues } = useForm({ validationSchema: formSchema })
watch(() => props.rule, (newRule) => setValues(newRule), { immediate: true })

const onSubmit = handleSubmit(() => emits('update:rule', updatedRule.value))
</script>

<template>
  <Card class="mx-auto max-w-4xl border-2">
    <CardHeader>
      <CardTitle class="text-xl">Edit rule</CardTitle>
    </CardHeader>
    <CardContent>
      <form :id="`rule-form-${ruleId}`" class="p-2" @submit="onSubmit">
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="sourcePattern" label="Source pattern" placeholder="e.g. system-*" :rule-id="ruleId" />
            <FormSelect name="sourceType" label="Source match type" :options="matchTypeOptions" :rule-id="ruleId" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="contentPattern" label="Content pattern" placeholder="e.g. error*" :rule-id="ruleId" />
            <FormSelect name="contentType" label="Content match type" :options="matchTypeOptions" :rule-id="ruleId" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="subjectPattern" label="Subject pattern" placeholder="e.g. ALERT*" :rule-id="ruleId" />
            <FormSelect name="subjectMatchType" label="Subject match type" :options="matchTypeOptions" :rule-id="ruleId" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="originPattern" label="Origin pattern" placeholder="e.g. ZABBIX" :rule-id="ruleId" />
            <FormSelect name="originMatchType" label="Origin match type" :options="matchTypeOptions" :rule-id="ruleId" />
          </div>
          <VeeField v-slot="{ field }" name="minSeverity">
            <Field>
              <MyFieldLabel :text="'Minimum severity (0–5)'" :for="`minSeverity-${ruleId}`" />
              <SeveritySelect
                :id="`minSeverity-${ruleId}`"
                @update:model-value="field.onChange"
                :default-value="field.value"
              />
              <FieldError />
            </Field>
          </VeeField>
          <FormCheckbox name="playSound" label="Play sound on alert" :rule-id="ruleId" />
        </div>
      </form>
    </CardContent>
    <CardFooter class="gap-2 justify-end">
      <Button variant="red_outline" type="button" @click="updatedRule = props.rule">
        Cancel
      </Button>
      <Button :form="`rule-form-${ruleId}`" type="submit" :disabled="isLoading" />
    </CardFooter>
  </Card>
</template>
