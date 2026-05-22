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
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import {watch} from "vue";

defineProps<{
  total: number
}>()

const currentPage = defineModel<number>('pageIndex', { default: 1 })
const pageSize = defineModel<number>('pageSize', { default: 100 })

watch(pageSize, () => {
  currentPage.value = 1
})

</script>

<template>
  <Pagination
    class="mb-4 mx-auto"
    v-slot="{ page }"
    :items-per-page="pageSize"
    :total="total"
    v-model:page="currentPage"
  >
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

    <Label style="white-space: nowrap" class="flex items-center mr-2">
      Page size:
    </Label>

    <DropdownMenu>
      <DropdownMenuTrigger as-child>
        <Button variant="outline">
          {{ pageSize }}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="start">
        <DropdownMenuGroup>
          <DropdownMenuItem
            v-for="size in [1, 5, 10, 20, 30, 50, 100, 200]"
            :key="size"
            @select="pageSize = size"
          >
            {{ size }}
          </DropdownMenuItem>
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  </Pagination>
</template>
