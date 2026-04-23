<script setup lang="ts">

import {TableCell} from "@/components/ui/table";
import {dateParser} from "@/composables/dateParser.ts";
import {computed} from "vue";

const props = defineProps<{
  date: Date;
}>()

const parsed = computed(() => dateParser(props.date))

</script>

<template>
  <TableCell class="px-2">
    {{ parsed.day }}/{{ parsed.month }}/{{ parsed.year }} -
    {{ parsed.hours + ":" + parsed.minutes   }}
  </TableCell>
</template>


