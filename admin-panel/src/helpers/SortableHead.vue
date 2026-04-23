<script setup lang="ts">
import {IconArrowNarrowDown, IconArrowNarrowUp} from "@tabler/icons-vue";
import {TableHead} from "@/components/ui/table";
import type {HTMLAttributes} from "vue";

const props = defineProps<{
  keyName: string,
  label: string,
  class?: HTMLAttributes["class"],
  sortKey: string,
  sortOrder: string
}>()


</script>

<template>
  <TableHead :class="props.class" >
    <div  class="flex items-center gap-1  text-lg lg:text-xl xl:text-2xl 2xl:text:4xl">
      <span @click="$emit('update:toggle-sort', props.keyName)" class="cursor-pointer">{{props.label}}</span>
      <IconArrowNarrowUp  v-if="sortKey === props.keyName && sortOrder === 'asc'" />
      <IconArrowNarrowDown v-if="sortKey === props.keyName && sortOrder === 'desc'" /></div>
  </TableHead>
</template>
