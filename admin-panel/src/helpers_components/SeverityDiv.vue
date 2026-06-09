<script setup lang="ts">

import type {HTMLAttributes} from "vue";
import {SeverityRecord} from "@/types/types.ts";

const props = defineProps<{
  severity: 0 | 1 | 2 | 3 | 4 | 5 | undefined
  class?: HTMLAttributes["class"]
}>();
</script>

<template>
  <div
    :class="` ${props.class} flex justify-center items-center tabular-nums min-w-18 max-w-20 h-6  text-center font-extrabold text-xs! *:text-xs! border-3 shadow-[0px_2px_10px_2px] light:shadow-md
                 shadow-severity-${severity}/80 border-severity-${severity}/80  bg-severity-${severity}/30 rounded-md  ${props.class}`">
    <span :class="`text-severity-${severity}/90`" >{{ SeverityRecord[severity!] }}</span>
  </div>
</template>
