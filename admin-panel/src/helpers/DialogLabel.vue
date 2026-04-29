<script setup lang="ts">

import {Label} from "@/components/ui/label";
import type {HTMLAttributes} from "vue";

const props = defineProps<{
  text: string
  for: string
  class?: HTMLAttributes["class"]
}>()
</script>

<template>
  <Label :class="`text-md mr-3 pb-2 font-bold  ${props.class}`" :for="props.for">{{ text }}</Label>
</template>
