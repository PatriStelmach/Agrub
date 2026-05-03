<script setup lang="ts">
import {
  Pagination,
  PaginationContent,
  PaginationItem, PaginationNext,
  PaginationPrevious
} from "@/components/ui/pagination";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem, DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Button} from "@/components/ui/button";
import {Label} from "@/components/ui/label";
import {ref} from "vue";

const props = defineProps<{
  total: number
}>()

const emit = defineEmits<{
  'update:current-page': [current: number],
  'update:page-size': [size: number],
}>()

const currentPage = ref<number>(1)
const pageSize = ref<number>(20)

const updatePageSize = (size: number) => {
  pageSize.value = size
  emit('update:page-size', size)
}

const updateCurrentPage = (page: number) => {
  currentPage.value = page
  emit('update:current-page', page)
}

</script>

<template>
  <Pagination
    class="mb-4 mx-auto"
    v-slot="{ page }"
    :items-per-page="pageSize"
    :total="total"
    :default-page="1"
    v-model:page="currentPage">
    <PaginationContent v-slot="{ items }">
      <PaginationPrevious @click="updateCurrentPage(page-1)" />
      <template v-for="(item, index) in items" :key="index">
        <PaginationItem
          v-if="item.type === 'page'"
          :value="item.value"
          :is-active="item.value === page"
          @click="updateCurrentPage(item.value)"
        >
          {{ item.value }}
        </PaginationItem>
      </template>
      <PaginationNext @click="updateCurrentPage(page+1)"/>
    </PaginationContent>
    <Label style="white-space: nowrap"  class="flex items-center mr-2">
      Page size:
    </Label>
    <DropdownMenu>
      <DropdownMenuTrigger as-child>
        <Button variant="outline">
          {{pageSize}}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent v-model="pageSize" align="start">
        <DropdownMenuGroup>
          <DropdownMenuItem
            v-for="(size, index) in [1, 5, 10, 20, 30, 50]"
            :key="index"
            @select="updatePageSize(size)">
            {{size}}
          </DropdownMenuItem>
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  </Pagination>
</template>
