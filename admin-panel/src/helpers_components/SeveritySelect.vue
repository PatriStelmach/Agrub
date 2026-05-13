<script setup lang="ts">

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";

const severity = defineModel<0 | 1 | 2 | 3 | 4 | 5 | undefined>('severity');
</script>

<template>
  <Select
    v-model="severity"
  >
    <SelectTrigger class="text-center font-extrabold cursor-pointer max-w-30 ">
      <SelectValue />
    </SelectTrigger>
    <SelectContent>
      <SelectItem
        :class='`tabelar-nums font-mono font-extrabold cursor-pointer hover:bg-severity-${value}/50! `'
        v-for="value in [0,1,2,3,4,5]" :key="value" :value="value">{{value}}</SelectItem>
    </SelectContent>
  </Select>
</template>

<style scoped>

</style>
