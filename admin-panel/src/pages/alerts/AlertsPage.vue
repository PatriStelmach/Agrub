<script setup lang="ts" >
import type { AlertObject} from "@/types/types.ts";
import {dashboardData} from "@/data/dashboardData.ts"
import {computed, ref, watch} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";
import {
  IconSend,
  IconLoader,
  IconBellCog,
  IconChevronDown,
  IconEyeCog,
  IconLock,
  IconStatusChange,
  IconUser
} from "@tabler/icons-vue";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
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
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import SortableHead from "@/helpers/SortableHead.vue";
import {useSort} from "@/composables/sorting.ts";

const currentPage = ref(1)
const searchFilter = ref('')
const rowsData = ref(dashboardData)

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<AlertObject>(() => rowsData.value as AlertObject[], 'createdAt')

watch(searchFilter, () => {
  currentPage.value = 1
})

const filteredData = computed(() => {
  if(!searchFilter.value)
    return dashboardData
  return dashboardData.filter((item: AlertObject) =>
    item.header.toLowerCase().includes(searchFilter.value.toLowerCase()))
})

const updateData = (data: AlertObject[]) => {
  rowsData.value = data as AlertObject[]
}

const updatePage = (page: number) => {
  currentPage.value = page
}

</script>

<template>
  <div >
    <div class="relative max-h-[10vh] items-center align-middle">
      <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[1vh]">Alerts dashboard</h1>

      <div class="absolute left-4 top-0 flex  ">
        <ButtonGroup>
          <ButtonGroup>
            <Button variant="outline" size="icon" aria-label="Go Back">
              <ArrowLeftIcon />
            </Button>
            <InputGroup class="relative l-[30vw] w-[20vw]  " >
              <InputGroupInput
                v-model="searchFilter"
                type="search"
                placeholder="Search for alert"/>
              <InputGroupAddon>
                <Search/>
              </InputGroupAddon>
            </InputGroup>

          </ButtonGroup>
        </ButtonGroup>
      </div>
    </div>
    <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[75vh]   ">
      <Table id="alert-table" class="w-99/100 text-md xl:text-xl 2xl:text-4xl  mx-auto  table-fixed">
        <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl">Current Alerts:
          <span class="font-extrabold">{{ dashboardData.length}}</span>
        </TableCaption>
        <TableHeader class="h-10">
          <TableRow class="bg-secondary hover:bg-secondary **:text-md! *:py-4 **:lg:text-xl! **:xl:text-2xl! **:2xl:text-4xl!">
            <SortableHead keyName="header" label="Alert" :sort-key="sortKey" class="w-20/100 pl-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-16/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="status" label="Status" :sort-key="sortKey" class="w-15/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="priority" label="Priority" :sort-key="sortKey" class="w-15/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="technicianGroups" label="Groups" :sort-key="sortKey" class="w-20/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="createdAt" label="Timestamp" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            class="relative cursor-pointer duration-0  hover:bg-chart-1/30"
            v-for="alert in sortedData"
            :key="alert.id"
          >
            <TableCell class="pl-4 py-2 whitespace-break-spaces">{{alert.header}}</TableCell>
            <TableCell class="py-2">
              <Badge
                class="cursor-pointer hover:bg-badge mr-1 ml-[-0.5em] text-md xl:text-xl 2xl:text-4xl"
                variant="secondary"
              >{{alert.source}}</Badge>
            </TableCell>
            <TableCell class="py-2 gap-x-2 items-center">
              <div class="flex gap-x-2 items-center">{{alert.status}}
              <IconLoader v-if="alert.status === 'In Process'"
                          class="size-4 animate-spin text-muted-foreground"/>
              <IconSend v-if="alert.status === 'Sent'"
                        class="size-4 text-emerald-500"/>
              </div>
            </TableCell>

            <TableCell class="py-2"
                       :class="{
              'text-green-500': alert.priority === 'low',
              'text-yellow-500': alert.priority === 'medium',
              'text-destructive': alert.priority === 'high'}"
            >{{alert.priority}}
            </TableCell>

            <TableCell
              class="py-2  whitespace-break-spaces">{{ alert.technicianGroups?.join(", ") }}</TableCell>
            <DateCell v-if="alert.createdAt" class="py-2" :date="alert.createdAt "></DateCell>

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
