<script setup lang="ts">
import type {logObject, LogObject} from "@/types/types.ts";
import {
  IconSend,
  IconLoader,
  IconListCheck,
  IconX,
} from "@tabler/icons-vue"
import { Badge } from '@/components/ui/badge'
import {
  Table,
  TableBody, TableCaption,
  TableCell, TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import DateCell from "@/helpers/DateCell.vue";
import {cn} from "@/lib/utils.ts";
import {Button} from "@/components/ui/button";
import {useSort} from "@/composables/sorting.ts";
import SortableHead from "@/helpers/SortableHead.vue";
import {computed, ref, watch} from "vue";
import {logsData} from "@/data/logsData.ts";


const { sortedData, sortKey, sortOrder, toggleSort } = useSort<LogObject>(() => logsData, 'createdAt')


const currentPage = ref(1)
const searchFilter = ref('')
const rowsData = ref(logsData)


watch(searchFilter, () => {
  currentPage.value = 1
})

const filteredData = computed(() => {
  if(!searchFilter.value)
    return logsData
  return logsData.filter((item: LogObject) =>
    item.header.toLowerCase().includes(searchFilter.value.toLowerCase()))
})

const updateData = (data: LogObject[]) => {
  rowsData.value = data as LogObject[]
}

const updatePage = (page: number) => {
  currentPage.value = page
}

const updateSearch = (data: string) => {
  searchFilter.value = data.trim()
}



</script>

<template>
  <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[70vh]   ">
    <Table id="log-table" class="w-99/100 text-md xl:text-xl 2xl:text-4xl  mx-auto  table-fixed">
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9999 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl">Current logs:
        <span class="font-extrabold">{{ logsData.length }}</span>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow class="bg-secondary hover:bg-secondary *:py-4 **:text-md! **:lg:text-xl! **:xl:text-2xl! **:2xl:text-4xl!">
          <SortableHead keyName="header" label="Header" :sort-key="sortKey" class="w-24/100 pl-4 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-12/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="content" label="Content" :sort-key="sortKey" class="24/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="technicianGroups" label="Groups" :sort-key="sortKey" class="w-30/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Created at" :sort-key="sortKey" class="w-14/100  " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        </TableRow>
      </TableHeader>
      <TableBody >
        <TableRow
          class="relative cursor-pointer duration-0 h-14 lg:h-16 xl:h-18 2xl:h-22 hover:bg-chart-1/30"
          v-for="log in sortedData"
          :key="log.id"
        >
          <TableCell class="pl-4 py-2 whitespace-pre-wrap">{{log.header}}</TableCell>
          <TableCell class="py-2">
            <Badge
              class="cursor-pointer hover:bg-badge mr-1 ml-[-0.5em] text-md xl:text-xl 2xl:text-4xl"
              variant="secondary"
            >{{log.source}}</Badge>
          </TableCell>

          <TableCell class="py-2"
          >{{log.content}}
          </TableCell>

          <TableCell
            class="py-2  whitespace-pre-wrap">{{ log.technicianGroups?.join(", ") }}</TableCell>
          <DateCell v-if="log.createdAt" class="py-2" :date="log.createdAt "></DateCell>

        </TableRow>
      </TableBody>
      <TableFooter>
      </TableFooter>
    </Table>
  </div>
</template>
