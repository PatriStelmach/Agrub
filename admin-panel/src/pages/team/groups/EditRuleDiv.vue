<script setup lang="ts">
import { reactive, ref, watch } from "vue";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { IconLoader, IconDeviceFloppy, IconCancel } from "@tabler/icons-vue";
import { MatchType, type Rule } from "@/types/types.ts";
import SeveritySelect from "@/helpers_components/SeveritySelect.vue";

const props = defineProps<{
  rule: Rule
}>()

const emit = defineEmits<{
  save: [data: Rule]
  cancel: []
}>()

const matchTypeOptions = Object.values(MatchType)
const isLoading = ref(false)

const form = reactive<Rule>({ ...props.rule })

watch(() => props.rule, (newRule) => {
  if (newRule) Object.assign(form, newRule)
}, { immediate: true })

const onSubmit = async () => {
  isLoading.value = true
  emit('save', { ...form, id: props.rule.id, userGroupId: props.rule.userGroupId })
  isLoading.value = false
}
</script>

<template>
  <div class="border">
    <div class="p-4">
      <form id="edit-rule-form" @submit.prevent="onSubmit">
        <div class="space-y-4">
          <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2">
              <label class="text-md mr-3 font-bold text-label" for="sourcePattern">Source pattern</label>
              <Input id="sourcePattern" v-model="form.sourcePattern" placeholder="e.g. system-*" />
            </div>
            <div>
              <label class="text-md mr-3 font-bold text-label" for="sourceType">Source match type</label>
              <Select class="w-full!" v-model="form.sourceType">
                <SelectTrigger id="sourceType"><SelectValue placeholder="Select..." /></SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="opt in matchTypeOptions" :key="opt" :value="opt">{{ opt }}</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2">
              <label class="text-md mr-3 font-bold text-label" for="contentPattern">Content pattern</label>
              <Input id="contentPattern" v-model="form.contentPattern" placeholder="e.g. error*" />
            </div>
            <div>
              <label class="text-md mr-3 font-bold text-label" for="contentType">Content match type</label>
              <Select v-model="form.contentType">
                <SelectTrigger id="contentType"><SelectValue placeholder="Select..." /></SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="opt in matchTypeOptions" :key="opt" :value="opt">{{ opt }}</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2">
              <label class="text-md mr-3 font-bold text-label" for="subjectPattern">Subject pattern</label>
              <Input id="subjectPattern" v-model="form.subjectPattern" placeholder="e.g. ALERT*" />
            </div>
            <div>
              <label class="text-md mr-3 font-bold text-label" for="subjectMatchType">Subject match type</label>
              <Select v-model="form.subjectMatchType">
                <SelectTrigger id="subjectMatchType"><SelectValue placeholder="Select..." /></SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="opt in matchTypeOptions" :key="opt" :value="opt">{{ opt }}</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2">
              <label class="text-md mr-3 font-bold text-label" for="originPattern">Origin pattern</label>
              <Input id="originPattern" v-model="form.originPattern" placeholder="e.g. ZABBIX" />
            </div>
            <div>
              <label class="text-md mr-3 font-bold text-label" for="originMatchType">Origin match type</label>
              <Select v-model="form.originMatchType">
                <SelectTrigger id="originMatchType"><SelectValue placeholder="Select..." /></SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="opt in matchTypeOptions" :key="opt" :value="opt">{{ opt }}</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-md mr-3 font-bold text-label" for="minSeverity">Minimum severity (0–5)</label>
              <SeveritySelect v-model:severity="form.minSeverity" />
            </div>
            <div class="flex items-center gap-2">
              <input id="playSound" type="checkbox" v-model="form.playSound" class="size-4" />
              <label class="text-md font-bold text-label" for="playSound">Play sound on alert</label>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <Button type="button" variant="red_outline" @click="emit('cancel')">
            <IconCancel /> Cancel
          </Button>
          <Button type="submit" variant="green_outline">
            <IconLoader v-if="isLoading" class="animate-spin" />
            <span class="gap-x-2 flex items-center" v-else>Save <IconDeviceFloppy /></span>
          </Button>
        </div>
      </form>
    </div>
  </div>
</template>
