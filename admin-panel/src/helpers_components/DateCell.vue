<script setup lang="ts">

import {TableCell} from "@/components/ui/table";
import {dateParser} from "@/helpers_functions/dateParser.js";
import {computed} from "vue";

const props = defineProps<{
  date: Date;
}>()

const parsed = computed(() => dateParser(props.date))

</script>

<template>
  <TableCell class="px-2 relative">
    <span class="tabular-nums whitespace-break-spaces" v-if="props.date">
          {{ parsed.day }}/{{ parsed.month }}/{{ parsed.year }} -
    {{ parsed.hours + ":" + parsed.minutes   }}
    </span>
    <span v-else>
      ----------
    </span>

  </TableCell>
</template>


