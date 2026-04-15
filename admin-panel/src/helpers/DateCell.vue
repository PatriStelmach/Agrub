<script setup lang="ts">

import {TableCell} from "@/components/ui/table";
import {computed} from "vue";

const props = defineProps<{
  date: Date;
}>()

const parsedData = computed(() => {
  return new Date(props.date);
})

const hours = computed(() => {
  return parsedData.value.getHours() < 10 ? '0' + parsedData.value.getHours() : parsedData.value.getHours()
})
const minutes = computed(() => {
  return parsedData.value.getMinutes() < 10 ? '0' + parsedData.value.getMinutes() : parsedData.value.getMinutes()
})
const day = computed(() => {
  return parsedData.value.getDate() < 10 ? '0' + parsedData.value.getDate() : parsedData.value.getDate()
})
const month = computed(() => {
  return parsedData.value.getUTCMonth()+1 < 10 ? '0' + (parsedData.value.getUTCMonth()+1).toString() : (parsedData.value.getUTCMonth()+1).toString()
})




</script>

<template>
  <TableCell class="px-2" v-model="parsedData">
    {{ day }}/{{ month }}/{{ parsedData.getFullYear() }} -
    {{ hours + ":" + ((( minutes as number ) < 10 && minutes !== '00') ? '0' + minutes: minutes ) }}
  </TableCell>
</template>


