<script setup lang="ts" generic="T">

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


const props = defineProps<{
  data: T[]
  page: number
}>()
const currentPage = ref<number>(1)
const itemsPerPage = ref<number>(50)

const emit = defineEmits<{
  'update:paginated-data': [data: T[]],
  'update:pages': [page: number]
}>()

const updatedData = computed(() => {
  const start = (currentPage.value -1 ) * itemsPerPage.value
  const end = start + itemsPerPage.value

  return props.data.slice(start, end)
})

const updatedPage = computed(() => {
  return currentPage.value
})

watch(() => props.page, () => {
  currentPage.value = props.page;
})

const updatePage = (pageSize: number) => {
  itemsPerPage.value = pageSize
  currentPage.value = 1
}

watch([updatedPage, updatedData], ([newPage, newData]) => {
  emit("update:pages", newPage);
  emit('update:paginated-data', newData)
}, {immediate: true})

</script>

<template>
  <Pagination
    class="my-[2vh] w-[20vw] mx-auto **:text-[1.5vh]"
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
      <PaginationNext/>
    </PaginationContent>
    <Label class="whitespace-nowrap flex items-center mr-2">
      Page Count:
    </Label>
    <DropdownMenu>
      <DropdownMenuTrigger as-child>
        <Button variant="outline" class="size-[4vh]">
          {{itemsPerPage}}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent v-model="itemsPerPage" align="start">
        <DropdownMenuGroup>
          <DropdownMenuItem v-for="(pageSize, index) in [1, 5, 10, 20, 30, 50, 100, 200]" :key="index" @select="updatePage(pageSize)">
            {{pageSize}}
          </DropdownMenuItem>
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  </Pagination>

</template>

