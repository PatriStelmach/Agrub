<script setup lang="ts">
import { ref, watch } from "vue";
import { Field as VeeField, useForm } from "vee-validate";
import { toTypedSchema } from "@vee-validate/zod";
import { z } from "zod";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Field, FieldError } from "@/components/ui/field";
import { Button } from "@/components/ui/button";
import { IconSend} from "@tabler/icons-vue";
import {initialRule, MatchType, type Rule, type UserGroup} from "@/types/types.ts";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
import SeveritySelect from "@/helpers_components/SeveritySelect.vue";
import FormInput from "@/helpers_components/form/FormInput.vue";
import FormSelect from "@/helpers_components/form/FormSelect.vue";
import FormCheckbox from "@/helpers_components/form/FormCheckbox.vue";
import {addNewRuleRequest} from "@/helpers_functions/requests.ts";
import {toast} from "vue-sonner";
import {useRoute} from "vue-router";

const route = useRoute();
const emits = defineEmits<{
  'update:rule': [Rule]
}>()

const open = defineModel<boolean>('open')
const userGroupId = Number(route.params.id)
const matchTypeOptions = Object.values(MatchType)
const isLoading = ref(false)

const formSchema = toTypedSchema(
  z.object({
    userGroupId: z.number(),
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
watch(open, (newOpen) => {
  if (newOpen) setValues(initialRule(userGroupId));
  console.log();
})

const onSubmit = handleSubmit( async (data) => {
  console.log(data)
  isLoading.value = true
  addNewRuleRequest(data)
    .then((res) => {
      open.value = false
      emits('update:rule', res)
    })
    .catch((err) => {
      toast.error(err.message)
    })
    .finally(() => isLoading.value = false)
})
</script>

<template>
  <Dialog v-model:open="open">
    <DialogTrigger as-child>
      <slot />
    </DialogTrigger>

    <DialogContent class="max-lg:max-w-3xl! lg:max-w-4xl">
      <DialogHeader>
        <DialogTitle class="text-xl lg:text-2xl">Add new rule to group: <span class="italic"> {{ route.params.name }} </span></DialogTitle>
      </DialogHeader>

      <form id="rule-form" class="p-2 " @submit="onSubmit">
        <div class="space-y-4 lg:**:text-lg!">
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="sourcePattern" label="Source pattern" placeholder="e.g. system-*" />
            <FormSelect name="sourceType" label="Source match type" :options="matchTypeOptions" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="contentPattern" label="Content pattern" placeholder="e.g. error*" />
            <FormSelect name="contentType" label="Content match type" :options="matchTypeOptions" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="subjectPattern" label="Subject pattern" placeholder="e.g. ALERT*" />
            <FormSelect name="subjectMatchType" label="Subject match type" :options="matchTypeOptions" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <FormInput name="originPattern" label="Origin pattern" placeholder="e.g. ZABBIX" />
            <FormSelect name="originMatchType" label="Origin match type" :options="matchTypeOptions" />
          </div>
          <VeeField v-slot="{ field }" name="minSeverity">
            <Field>
              <MyFieldLabel text="Minimum severity (0–5)" for="minSeverity" />
              <SeveritySelect
                id="minSeverity"
                @update:model-value="field.onChange"
                :default-value="field.value"
              />
              <FieldError />
            </Field>
          </VeeField>
          <FormCheckbox name="playSound" label="Play sound on alert" />
        </div>
      </form>
      <DialogFooter class="gap-2">
        <Button variant="red_outline" type="button" @click="open = false">
          Cancel
        </Button>
        <Button variant="green_outline" form="rule-form" type="submit">
          Save <IconSend />
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>

</template>
