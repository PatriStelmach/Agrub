<script setup lang="ts">
import {Skeleton} from "@/components/ui/skeleton";
defineProps<{
  colspan: number
}>()
</script>
<template>
  <tbody>
  <tr v-for="(_, i) in Array.from({ length: 20})" :key="i">
    <td :colspan="colspan" class="p-2">
      <Skeleton class="bg-card w-full h-10"/>
    </td>
  </tr>
  </tbody>
</template>
