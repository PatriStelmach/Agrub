<script setup lang="ts">
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
    <div @click="$emit('update:toggle-sort', props.keyName)"  class="flex items-center h-full px-1 gap-2 cursor-pointer "
         :class="{'bg-foreground/70 text-secondary' : sortKey === props.keyName}">
      <span

        class="text-sm lg:text-md xl:text-lg 2xl:text:xl">{{props.label}}</span>
      <i class="pi pi-sort-amount-up" v-if="sortKey === props.keyName && sortOrder === 'asc'" />
      <i class="pi pi-sort-amount-down-alt" v-if="sortKey === props.keyName && sortOrder === 'desc'" /></div>
  </TableHead>
</template>
