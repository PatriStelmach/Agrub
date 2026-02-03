<script setup lang="ts">

import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious
} from "@/components/ui/pagination";
import {Label} from "@/components/ui/label";
import {computed, ref, watch} from "vue";
import {Button} from "@/components/ui/button";
import {DropdownMenu, DropdownMenuTrigger, DropdownMenuItem, DropdownMenuContent, DropdownMenuGroup} from "@/components/ui/dropdown-menu";
import type {Plugin} from "@/types/plugin.ts";

const props = defineProps<{
  data: Plugin[]
  page: number
}>()
const currentPage = ref<number>(1)
const itemsPerPage = ref<number>(10)

const emit = defineEmits<{
  'update:paginated-data': [data: Plugin[]],
  'update:pages': [page: number]
}>()

const updatedData = computed(() =>
{
  const start = (currentPage.value -1 ) * itemsPerPage.value
  const end = start + itemsPerPage.value

  return props.data.slice(start, end)
})

const updatedPage = computed(() =>
{
  return currentPage.value
})

watch(() => props.page, () =>
{
  currentPage.value = props.page;
})

watch([updatedPage, updatedData], ([newPage, newData]) =>
{
  emit("update:pages", newPage);
  emit('update:paginated-data', newData)
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

