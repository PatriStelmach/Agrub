<script setup lang="ts" >
import type { AlertObject} from "@/types/types.ts";
import {dashboardData} from "@/data/dashboardData.ts"
import MyPagination from "@/helpers/MyPagination.vue";
import {
  IconSend,
  IconLoader,
  IconBellCog,
  IconChevronDown,
  IconEyeCog,
  IconLock,
  IconStatusChange,
  IconUser, IconListDetails
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
import {useSearchFilter} from "@/composables/useSearchFilter.ts";

const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage, searchFilter } =
  useSearchFilter<AlertObject>(() => dashboardData,(item) => item.header)

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<AlertObject>(() => tableData.value as AlertObject[], 'createdAt')

</script>

<template>
  <div >
    <div class="relative max-h-[10vh] items-center align-middle">
      <h1 class="text-center my-[2vh] text-xl xl:text-2xl 2xl:text-4xl border-b pb-[2vh]">Alerts dashboard</h1>

      <div class="absolute left-4 top-0 flex  ">
        <ButtonGroup>
          <ButtonGroup>
            <Button variant="outline" size="icon" aria-label="Go Back">
              <ArrowLeftIcon />
            </Button>
            <InputGroup >
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
    <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[77vh]   ">
      <Table id="alert-table" class="w-99/100 text-md lg:text-lg xl:text-xl 2xl:text:3xl  mx-auto  table-fixed">
        <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9 bottom-0 py-[1vh] text-md lg:text-lg xl:text-xl 2xl:text:3xl">Current Alerts:
          <span class="font-extrabold">{{ dashboardData.length}}</span>
        </TableCaption>
        <TableHeader class="h-10">
          <TableRow class="bg-secondary hover:bg-secondary **:text-md! *: **:lg:text-xl! **:xl:text-2xl! **:2xl:text-4xl!">
            <SortableHead keyName="header" label="Alert" :sort-key="sortKey" class="w-24/100 pl-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="status" label="Status" :sort-key="sortKey" class="w-15/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="w-13/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="technicianGroups" label="Groups" :sort-key="sortKey" class="w-15/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="createdAt" label="Timestamp" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <TableHead class="w-5/100"></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            class="relative cursor-pointer duration-0  hover:bg-chart-1/30"
            v-for="alert in sortedData"
            :key="alert.id"
          >
            <TableCell class="pl-4  whitespace-break-spaces">{{alert.header}}</TableCell>
            <TableCell class="">
              <Badge
                class="cursor-pointer hover:bg-badge mr-1 ml-[-0.5em] text-md lg:text-lg xl:text-xl 2xl:text:3xl"
                variant="secondary"
              >{{alert.source}}</Badge>
            </TableCell>
            <TableCell class=" gap-x-2 items-center">
              <div class="flex gap-x-2 items-center">{{alert.status}}
              <IconLoader v-if="alert.status === 'In Process'"
                          class="size-4 animate-spin text-muted-foreground"/>
              <IconSend v-if="alert.status === 'Sent'"
                        class="size-4 text-emerald-500"/>
              </div>
            </TableCell>
            <TableCell class=""
                       :class="{
              'text-sky-500': ['not classified', 'unknown'].includes(alert.severity.toLowerCase()),
              'text-lime-500': ['low', 'ok', 'information'].includes(alert.severity.toLowerCase()),
              'text-yellow-500': ['medium', 'warning'].includes(alert.severity.toLowerCase()),
              'text-amber-500': alert.severity.toLowerCase() === 'average',
              'text-orange-500': alert.severity.toLowerCase() === 'high',
              'text-red-500': ['critical', 'disaster'].includes(alert.severity.toLowerCase()),
              }"
            >{{alert.severity}}
            </TableCell>

            <TableCell
              class="  whitespace-break-spaces">{{ alert.technicianGroups?.join(", ") }}</TableCell>
            <DateCell v-if="alert.createdAt" class="" :date="alert.createdAt "></DateCell>
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
