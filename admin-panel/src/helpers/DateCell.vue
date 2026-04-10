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
  return parsedData.value.getHours() == 0 ? '00' : parsedData.value.getHours()
})
const minutes = computed(() => {
  return parsedData.value.getMinutes() == 0 ? '00' : parsedData.value.getMinutes()
})

</script>

<template>
  <TableCell class="p-0" v-model="parsedData">
    {{parsedData.getDate()}}/{{parsedData.getMonth()+1}}/{{parsedData.getFullYear()}} -
    {{hours + ":" + (((minutes as number) < 10 && minutes !== '00') ? '0' + minutes: minutes) }}
  </TableCell>
</template>


