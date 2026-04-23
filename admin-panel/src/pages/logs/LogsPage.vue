<script setup lang="ts">
import type {LogObject} from "@/types/types.ts";
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
import {Button} from "@/components/ui/button";
import {useSort} from "@/composables/sorting.ts";
import SortableHead from "@/helpers/SortableHead.vue";
import {computed, ref, watch} from "vue";
import {logsData} from "@/data/logsData.ts";
import MyPagination from "@/helpers/MyPagination.vue";
import {ButtonGroup} from "@/components/ui/button-group";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import { IconListDetails} from "@tabler/icons-vue";


const { sortedData, sortKey, sortOrder, toggleSort } = useSort<LogObject>(() => rowsData.value, 'createdAt')


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

</script>

<template>
  <div>
    <div class="relative">
      <h1 class="text-center my-[2vh] text-xl xl:text-2xl 2xl:text-4xl border-b pb-[2vh]">Logs Dashboard</h1>

      <div class="absolute top-0 left-4 flex">
        <ButtonGroup>
          <ButtonGroup>
            <Button variant="outline" size="icon" aria-label="Go Back">
              <ArrowLeftIcon />
            </Button>
            <InputGroup >
              <InputGroupInput
                v-model="searchFilter"
                type="search"
                placeholder="Search for log"/>
              <InputGroupAddon>
                <Search/>
              </InputGroupAddon>
            </InputGroup>

          </ButtonGroup>
        </ButtonGroup>
      </div>
    </div>
    <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[77vh]   ">
        <Table id="log-table" class="w-99/100 text-md lg:text-lg xl:text-xl 2xl:text:3xl  mx-auto  table-fixed">
          <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9 bottom-0 py-[1vh] text-md lg:text-lg xl:text-xl 2xl:text:3xl">Current logs:
            <span class="font-extrabold">{{ logsData.length }}</span>
          </TableCaption>
          <TableHeader class="h-10">
            <TableRow class="bg-secondary hover:bg-secondary  **:text-md! **:lg:text-xl! **:xl:text-2xl! **:2xl:text-4xl!">
              <SortableHead keyName="header" label="Header" :sort-key="sortKey" class="w-18/100 pl-4 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="content" label="Content" :sort-key="sortKey" class="w-21/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="w-13/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="technicianGroups" label="Groups" :sort-key="sortKey" class="w-15/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="createdAt" label="Timestamp" :sort-key="sortKey" class="w-14/100  " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <TableHead class="w-5/100"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody >
            <TableRow
              class="relative cursor-pointer duration-0 h-14 lg:h-16 xl:h-18 2xl:h-22 hover:bg-chart-1/30"
              v-for="log in sortedData"
              :key="log.id"
            >
              <TableCell class="pl-4   whitespace-break-spaces">{{log.header}}</TableCell>
              <TableCell class="">
                <Badge
                  variant="source"
                >{{log.source}}</Badge>
              </TableCell>

              <TableCell class=" whitespace-break-spaces"
              >{{log.content}}
              </TableCell>
              <TableCell class=""
                         :class="{
              'text-sky-500': ['not classified', 'unknown'].includes(log.severity.toLowerCase()),
              'text-badge1': ['low', 'ok', 'information'].includes(log.severity.toLowerCase()),
              'text-yellow-500': ['medium', 'warning'].includes(log.severity.toLowerCase()),
              'text-amber-500': log.severity.toLowerCase() === 'average',
              'text-orange-500': log.severity.toLowerCase() === 'high',
              'text-red-500': ['critical', 'disaster'].includes(log.severity.toLowerCase()),
              }"
              >{{log.severity}}
              </TableCell>
              <TableCell
                class="  whitespace-break-spaces">
                <Badge
                  variant="groups"
                  v-for="(group, index) in log.technicianGroups"
                  :key="index"
                >
                  {{ group }}
                </Badge>
              </TableCell>
              <DateCell v-if="log.createdAt" class="" :date="log.createdAt "></DateCell>
              <TableCell>
                <Button variant="orange_outline">
                  <IconListDetails/>
                </Button>
              </TableCell>
            </TableRow>
          </TableBody>
          <TableFooter>
          </TableFooter>
        </Table>
      </div>
    <MyPagination
      class="max-h-[5vh] z-99"
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
    />
  </div>
</template>
