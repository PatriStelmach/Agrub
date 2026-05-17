<script setup lang="ts">
import {TableHead} from "@/components/ui/table";
import type {HTMLAttributes} from "vue";
import {IconSortAscending, IconSortDescending} from "@tabler/icons-vue";

const props = defineProps<{
  keyName: string,
  label: string,
  class?: HTMLAttributes["class"],
  sortKey: string,
  sortOrder: string
}>()


</script>

<template>
  <TableHead :class="props.class" class="px-0">
    <div @click="$emit('update:toggle-sort', props.keyName)"  class="flex items-center h-full mx-0 px-1 gap-2 cursor-pointer "
         :class="{' rounded-sm bg-foreground/80 text-background' : sortKey === props.keyName}">
      <span

        class="font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">{{props.label}}</span>
      <IconSortAscending v-if="sortKey === props.keyName && sortOrder === 'asc'" />
      <IconSortDescending v-if="sortKey === props.keyName && sortOrder === 'desc'" /></div>
  </TableHead>
</template>
