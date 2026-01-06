<script setup lang="ts">

import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious
} from "@/components/ui/pagination";
import {pluginLibraryData} from "@/data/pluginLibrary.ts";
import {computed, ref, watch} from "vue";

const props = defineProps<{
  itemsPerPage: number
  data: any[]
  currentPage: number

}>()
const currentPage = ref(1)

const emit = defineEmits<{
  'update:paginated-data': [data: any[]]
}>()
const updatedData = computed(() =>
{
  const start = (currentPage.value -1 ) * props.itemsPerPage
  const end = start + props.itemsPerPage

  return props.data.slice(start, end)
})

watch(updatedData, (newValue) =>
{
  emit('update:paginated-data', newValue)
}, {immediate: true})
</script>

<template>

  <Pagination
    class="my-[2vh]"
    v-slot="{ page }"
    :items-per-page="itemsPerPage"
    :total="data.length"
    :default-page="1"
    v-model:page="currentPage">
    <PaginationContent v-slot="{ items }">
      <PaginationPrevious />
      <template v-for="(item, index) in items" :key="index">
        <PaginationItem
          v-if="item.type === 'page'"
          :value="item.value"
          :is-active="item.value === page"
        >
          {{ item.value }}
        </PaginationItem>
      </template>
      <PaginationNext />
    </PaginationContent>
  </Pagination>
</template>

