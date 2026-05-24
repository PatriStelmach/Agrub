<script setup lang="ts">

import type {HTMLAttributes} from "vue";
import {FieldLabel} from "@/components/ui/field";

const props = defineProps<{
  text: string
  for?: string
  class?: HTMLAttributes["class"]
}>()
</script>

<template>
  <FieldLabel :class="`text-md  xl:text-xl 2xl:text-2xl font-bold text-label ${props.class}`" :for="props.for">{{ text }}</FieldLabel>
</template>
