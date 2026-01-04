<script setup lang="ts">

import {TableCell} from "@/components/ui/table";

const props = defineProps<{
  date: Date;
}>()

const hours = props.date.getHours() == 0 ? '00' : props.date.getHours()
const minutes = props.date.getMinutes() == 0 ? '00' : props.date.getMinutes()

</script>

<template>
  <TableCell>{{props.date.getDate()}} / {{props.date.getMonth()+1}} / {{props.date.getFullYear()}}  -  {{hours + ":" + minutes}}</TableCell>
</template>


