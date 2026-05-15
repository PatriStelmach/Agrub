<script setup lang="ts">

defineProps<{
  severity: 0 | 1 | 2 | 3 | 4 | 5 | undefined
}>();
</script>

<template>
  <div
    :class="` tabular-nums min-w-10 max-w-20 text-center font-extrabold text-lg border-3 shadow-[0px_2px_10px_2px] light:shadow-md
                 shadow-severity-${severity}/70 border-severity-${severity}  bg-severity-${severity}/30 rounded-sm `">
    <span :class="`text-severity-${severity}`" >{{severity}}</span>
  </div>
</template>

<style scoped>

</style>
