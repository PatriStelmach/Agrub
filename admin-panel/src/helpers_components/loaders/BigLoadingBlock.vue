<script setup lang="ts">
import {Skeleton} from "@/components/ui/skeleton";
import type {HTMLAttributes} from "vue";
const props = defineProps<{
  class?: HTMLAttributes["class"]
}>()
</script>
<template>
  <div
    class=" px-6 py-2 pr-3  gap-6 max-h-[90vh] overflow-y-auto">
    <Skeleton class="w-full" :class="props.class"/>
  </div>
</template>
