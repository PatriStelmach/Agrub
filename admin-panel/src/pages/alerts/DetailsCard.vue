<script setup lang="ts">

import {computed} from "vue";
import {useMouse} from "@vueuse/core";
import {Card, CardContent, CardHeader} from "@/components/ui/card";
import {Label} from "@/components/ui/label";
import type {AlertDetails} from "@/types/types.ts";

defineProps<{
  data: AlertDetails | null
}>()

const { x, y } = useMouse()
const descriptionBox = computed(() => {
  return {
    position: 'fixed',
    top: `${y.value}px`,
    left: `${x.value}px`,
    pointerEvents: "none",
    transform: 'translate(-10px, 20px)'
  }
})
</script>

<template>
  <Card
    :style="descriptionBox"
    class="z-9999 max-w-120 border-2 border-secondary">
    <CardHeader><Label class=" font-extrabold ">Subject: </Label> {{ data?.subject }}</CardHeader>
    <CardContent class="max-w-full wrap-break-word"><Label class="pb-2 grid font-extrabold">Message:</Label> {{ data?.message}}</CardContent>
  </Card>

</template>

<style scoped>

</style>
