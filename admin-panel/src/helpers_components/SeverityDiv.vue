<script setup lang="ts">
import type {HTMLAttributes} from "vue";

const props = defineProps<{
  severity: 0 | 1 | 2 | 3 | 4 | 5 | undefined
  class?: HTMLAttributes["class"]
}>();
</script>

<template>
  <div
    :class="`flex justify-center items-center tabular-nums min-w-10 max-w-20  h-6 text-center font-extrabold text-md! border-3 shadow-[0px_2px_10px_2px] light:shadow-md
                 shadow-severity-${severity}/80 border-severity-${severity}  bg-severity-${severity}/30 rounded-md  ${props.class}`">
    <span :class="`text-severity-${severity}`" >{{severity}}</span>
  </div>
</template>
