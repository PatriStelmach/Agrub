<script setup lang="ts">

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {SeverityRecord, SeverityRecordNoUnknown} from "@/types/types.ts";

const severity = defineModel<0 | 1 | 2 | 3 | 4 | 5 | undefined>('severity');
</script>

<template>
  <Select
    v-model="severity"
  >
    <SelectTrigger if="severity-select" class="text-center font-extrabold cursor-pointer max-w-30 ">
      <SelectValue :placeholder="SeverityRecord[severity!]" />
    </SelectTrigger>
    <SelectContent>
      <SelectItem
        :class='`tabelar-nums  font-extrabold cursor-pointer hover:bg-severity-${value}/50! `'
        v-for="(key, value) in SeverityRecordNoUnknown" :key="key" :value="value">{{key}}</SelectItem>
    </SelectContent>
  </Select>
</template>

<style scoped>

</style>
