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
import {Button} from "@/components/ui/button";
import {DropdownMenu, DropdownMenuTrigger, DropdownMenuItem, DropdownMenuContent, DropdownMenuGroup} from "@/components/ui/dropdown-menu";

const props = defineProps<{
  data: any[]
  currentPage: number

}>()
const currentPage = ref(1)
const itemsPerPage = ref(10)
const emit = defineEmits<{
  'update:paginated-data': [data: any[]]

}>()
const updatedData = computed(() =>
{
  const start = (currentPage.value -1 ) * itemsPerPage.value
  const end = start + itemsPerPage.value

  return props.data.slice(start, end)
})


watch(updatedData, (newValue) =>
{
  emit('update:paginated-data', newValue)
}, {immediate: true})
</script>

<template>
  <Pagination
    class="my-[2vh] w-[20vw] mx-auto"
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
    <Label style="white-space: nowrap"  class="flex items-center mr-2">
      Rows per page:
    </Label>
    <DropdownMenu>
      <DropdownMenuTrigger as-child>
        <Button variant="outline">
          {{itemsPerPage}}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent v-model="itemsPerPage" align="start">
        <DropdownMenuGroup>
          <DropdownMenuItem v-for="pageSize in [5, 10, 20, 30, 50]" @select="itemsPerPage = pageSize">
            {{pageSize}}
          </DropdownMenuItem>

        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  </Pagination>

</template>

