<script setup lang="ts">

import {IconCaretDown, IconCaretUp} from "@tabler/icons-vue";
import {TableHead} from "@/components/ui/table";
import type {HTMLAttributes} from "vue";
import type {Paginable} from '@/types/types.ts';

const props = defineProps<{
  keyName: string,
  label: string,
  class?: HTMLAttributes["class"],
  sortKey: string,
  sortOrder: string
}>()


</script>

<template>
  <TableHead :class="props.class" @click="$emit('update:toggle-sort', props.keyName)">
    <div class="flex items-center gap-1">
      <span class="cursor-pointer">{{props.label}}</span>
      <IconCaretUp v-if="sortKey === props.keyName && sortOrder === 'asc'" />
      <IconCaretDown v-if="sortKey === props.keyName && sortOrder === 'desc'" /></div>
  </TableHead>
</template>
