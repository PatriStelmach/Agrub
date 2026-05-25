<script setup lang="ts" generic="T">
import {
  Pagination,
  PaginationContent, PaginationFirst,
  PaginationItem, PaginationLast, PaginationNext,
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
import {computed, watchEffect} from "vue";
import {ChevronFirstIcon, ChevronLastIcon} from "lucide-vue-next";

const props = defineProps<{
  data: T[]
  total: number
}>()

const emit = defineEmits<{
  'update:paginated-data': [data: T[]],
}>()

const currentPage = defineModel<number>('pageIndex', { default: 1 })
const pageSize = defineModel<number>('pageSize', { default: 100 })

const updatedData = computed(() => {
  const start = (currentPage.value -1 ) * pageSize.value
  const end = start + pageSize.value

  return props.data.slice(start, end)
})

watchEffect(() => {
  emit('update:paginated-data', updatedData.value)
})

</script>

<template>
  <Pagination
    class="gap-y-2 mx-auto grid "
    v-slot="{ page }"
    :items-per-page="pageSize"
    :total="data.length"
    v-model:page="currentPage"
  >
    <PaginationContent v-slot="{ items }">
      <PaginationFirst
        class="gap-0 space-0"
      >
        <ChevronFirstIcon class="size-[2vh]" />
      </PaginationFirst>
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
      <PaginationLast
        class="gap-0 space-0"
      >
        <ChevronLastIcon class="size-[2vh]" />
      </PaginationLast>
    </PaginationContent>

    <div class="flex space-x-2 mx-auto items-center">
      <Label style="white-space: nowrap" class="flex items-center mr-2">
        Page size:
      </Label>

      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <Button variant="outline">
            {{ pageSize }}
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="center">
          <DropdownMenuGroup>
            <DropdownMenuItem
              v-for="size in [1, 5, 10, 20, 30, 50, 100, 150, 200]"
              :key="size"
              @select="pageSize = size"
            >
              {{ size }}
            </DropdownMenuItem>
          </DropdownMenuGroup>
        </DropdownMenuContent>
      </DropdownMenu>

      <span class="text-md" >Results: <span class="font-extrabold text-md">{{ total}}</span></span>
    </div>
  </Pagination>
</template>
