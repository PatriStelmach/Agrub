<script setup lang="ts">
import { reactive, ref, watch } from "vue";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { IconLoader, IconDeviceFloppy} from "@tabler/icons-vue";
import {initialRule, MatchType, type Rule} from "@/types/types.ts";
import SeveritySelect from "@/helpers_components/SeveritySelect.vue";
import {addNewRuleRequest} from "@/helpers_functions/requests/groupsRequests.ts";
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

const form = reactive<Rule>({
  ...initialRule(userGroupId),
  userGroupId,
})

watch(open, (newOpen) => {
  if (newOpen) Object.assign(form, initialRule(userGroupId))
})

const onSubmit = async () => {
  isLoading.value = true
  addNewRuleRequest(form)
    .then((res) => {
      open.value = false
      emits('update:rule', res)
      toast.success('New rule added successfully!')
    })
    .catch((err) => {
      toast.error(err.message)
    })
    .finally(() => isLoading.value = false)
}
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

      <form id="rule-form" class="p-2" @submit.prevent="onSubmit">
        <div class="space-y-4 lg:**:text-lg!">
          <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2">
              <label class="text-md mr-3 font-bold text-label" for="sourcePattern">Source pattern</label>
              <Input id="sourcePattern" v-model="form.sourcePattern" placeholder="e.g. system-*" />
            </div>
            <div>
              <label class="text-md mr-3 font-bold text-label" for="sourceType">Source match type</label>
              <Select v-model="form.sourceType">
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
      </form>
      <DialogFooter class="gap-2">
        <Button variant="red_outline" type="button" @click="open = false">
          Cancel
        </Button>
        <Button variant="green_outline" form="rule-form" type="submit">
          <IconLoader v-if="isLoading" class="animate-spin" />
          <span class="gap-x-2 flex items-center" v-else>Save <IconDeviceFloppy /></span>
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
