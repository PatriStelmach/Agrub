<script setup lang="ts">
import { z } from "zod"
import {computed, ref} from "vue"

const schema = z.object({
  id: z.number(),
  header: z.string(),
  source: z.string(),
  status: z.string(),
  level: z.string(),
  technician: z.string(),

})
import type {
  ColumnDef,
  ColumnFiltersState,
  SortingState,
  VisibilityState,
} from "@tanstack/vue-table"
import {
  IconChevronDown,
  IconChevronLeft,
  IconChevronRight,
  IconChevronsLeft,
  IconChevronsRight,
  IconCircleCheckFilled,
  IconDotsVertical,
  IconLayoutColumns,
  IconLoader,
  IconPlus,
} from "@tabler/icons-vue"
import {
  FlexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useVueTable,
} from "@tanstack/vue-table"
import { Badge } from '@/components/ui/badge'

import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'

import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Table,
  TableBody,
  TableCell, TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/ui/tabs'
import {h} from "vue";
import DateCell from "@/helpers/DateCell.vue";
import MyPagination from "@/helpers/MyPagination.vue";
import {pluginLibraryData} from "@/data/pluginLibrary.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";

const props = defineProps<{
  data: TableData[]
}>()

interface TableData {
  id: number
  header: string
  source: string
  status: string
  level: string
  technician: string,
  createdAt: Date,
  closedAt: Date,
}

const sorting = ref<SortingState>([])
const columnFilters = ref<ColumnFiltersState>([])
const columnVisibility = ref<VisibilityState>({})
const rowSelection = ref({})


const columns: ColumnDef<TableData>[] = [
  {
    id: "select",
    header: ({ table }) => h(Checkbox, {
      "modelValue": table.getIsAllPageRowsSelected() || (table.getIsSomePageRowsSelected() && "indeterminate"),
      "onUpdate:modelValue": value => table.toggleAllPageRowsSelected(!!value),
      "aria-label": "Select all",
    }),
    cell: ({ row }) => h(Checkbox, {
      "modelValue": row.getIsSelected(),
      "onUpdate:modelValue": value => row.toggleSelected(!!value),
      "aria-label": "Select row",
    }),
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: "header",
    header: "Alert",
    cell: ({ row }) => h("div", String(row.getValue("header"))),
    enableHiding: false,
  },
  {
    accessorKey: "source",
    header: "Source",
    cell: ({ row }) => h(Badge, {
      variant: "outline",
    }, () => String(row.getValue("source"))),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => {
      const status = row.getValue("status") as string
      return h("div", { class: "flex items-center gap-2" }, [
        status === "Done"
          ? h(IconCircleCheckFilled, { class: "h-4 w-4 text-emerald-500" })
          : h(IconLoader, { class: "h-4 w-4 animate-spin text-muted-foreground" }),
        h("span", {}, status),
      ])
    },
  },
  {
    accessorKey: "level",
    header: () => h("div", { class: "flex items-center gap-1" }, [
      "Level",
    ]),
    cell: ({ row }) =>
    {
      const level = () =>
    {
      const level = row.getValue("level") as string
      switch (level)
      {
        case "low":
          return "bg-green-500"
        case "medium":
          return "bg-yellow-500"
        case "high":
          return "bg-orange-500"
        case "extreme":
          return "bg-red-500"
      }
    }
      return h(Button, {
      variant: "ghost",
      size: "sm",
      class: "h-auto p-1 text-xs font-mono",
    }, () => [
      h("span", { class: `${level()} rounded-md p-1 font-semibold` }, String(row.getValue("level"))),
    ])
    }
  },
  {
    accessorKey: "technician",
    header: "Technician",
    cell: ({ row }) => {
      const technician = row.getValue("technician") as string
      const isAssigned = technician !== "Assign technician"

      if (isAssigned) {
        return h("span", {}, technician)
      }

      return h(Select, {}, {
        default: () => [
          h(SelectTrigger, { class: "w-full" }, {
            default: () => h(SelectValue, { placeholder: "Assign technician" }),
          }),
          h(SelectContent, {}, {
            default: () => [
              h(SelectItem, { value: "eddie" }, () => "Eddie Lake"),
            ],
          }),
        ],
      })
    },
  },
  {
    accessorKey: "createdAt",
    header: "Created at",
    cell: ({ row }) =>
    {
      const date = row.getValue("createdAt") as Date

      return h(date, {} )
    }
  },
  {
    accessorKey: "closedAt",
    header: "Closed at",

    cell: ({ row }) =>
    {
      const date = row.getValue("closedAt") as Date
      return h("span", {},
        [
          date == null
          ? h("span", {}, String("- - - - - - - - - -"))
            : h("span", {}, String(date)),
        ])
    }
  }
]

const table = useVueTable({
  get data() {
    return props.data
  },
  columns,
  getCoreRowModel: getCoreRowModel(),
  getPaginationRowModel: getPaginationRowModel(),
  getSortedRowModel: getSortedRowModel(),
  getFilteredRowModel: getFilteredRowModel(),
  onSortingChange: (updaterOrValue) => {
    sorting.value = typeof updaterOrValue === "function"
      ? updaterOrValue(sorting.value)
      : updaterOrValue
  },
  onColumnFiltersChange: (updaterOrValue) => {
    columnFilters.value = typeof updaterOrValue === "function"
      ? updaterOrValue(columnFilters.value)
      : updaterOrValue
  },
  onColumnVisibilityChange: (updaterOrValue) => {
    columnVisibility.value = typeof updaterOrValue === "function"
      ? updaterOrValue(columnVisibility.value)
      : updaterOrValue
  },
  onRowSelectionChange: (updaterOrValue) => {
    rowSelection.value = typeof updaterOrValue === "function"
      ? updaterOrValue(rowSelection.value)
      : updaterOrValue
  },
  state: {
    get sorting() { return sorting.value },
    get columnFilters() { return columnFilters.value },
    get columnVisibility() { return columnVisibility.value },
    get rowSelection() { return rowSelection.value },
  },
})
</script>

<template>
  <Tabs
    default-value="outline"
    class="w-full flex-col justify-start gap-6"
  >
    <div class="flex items-center justify-between px-4 lg:px-6">
      <div class="flex items-center gap-2">
        <ButtonGroup>
          <Button variant="outline" size="icon" aria-label="Go Back">
            <ArrowLeftIcon />
          </Button>
        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <Button class="text-chart-2 hover:bg-chart-2/70!" variant="outline">
              <IconLayoutColumns />
              <span class="hidden lg:inline">Customize Columns</span>
              <span class="lg:hidden">Columns</span>
              <IconChevronDown />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" class="w-56">
            <template v-for="column in table.getAllColumns().filter((column) => typeof column.accessorFn !== 'undefined' && column.getCanHide())" :key="column.id">
              <DropdownMenuCheckboxItem
                class="capitalize"
                :model-value="column.getIsVisible()"
                @update:model-value="(value) => {

                  column.toggleVisibility(value)
                }"
              >
                {{ column.id }}
              </DropdownMenuCheckboxItem>
            </template>
          </DropdownMenuContent>
        </DropdownMenu>
        <InputGroup class="relative l-[30vw] w-[20vw]  " >
          <InputGroupInput
            v-model="searchFilter"
            type="search"
            placeholder="Search for plugin"/>
          <InputGroupAddon>
            <Search/>
          </InputGroupAddon>
        </InputGroup>
        </ButtonGroup>
      </div>
    </div>
    <TabsContent
      value="outline"
      class="relative flex flex-col gap-4 overflow-auto px-4 lg:px-6"
    >
      <div class="overflow-hidden rounded-lg border">
        <Table>
          <TableHeader class="bg-muted sticky top-0 z-10">
            <TableRow v-for="headerGroup in table.getHeaderGroups()" :key="headerGroup.id">
              <TableHead v-for="header in headerGroup.headers" :key="header.id" :col-span="header.colSpan">
                <FlexRender v-if="!header.isPlaceholder" :render="header.column.columnDef.header" :props="header.getContext()" />
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody class="**:data-[slot=table-cell]:first:w-8">
            <template v-if="table.getRowModel().rows.length">
              <TableRow class="duration-0" v-for="row in table.getRowModel().rows" :key="row.id">
                <TableCell v-for="cell in row.getVisibleCells()" :key="cell.id">
                  <DateCell v-if="cell.getValue() instanceof Date" :date="cell.getValue() as Date"></DateCell>
                  <FlexRender v-else :render="cell.column.columnDef.cell" :props="cell.getContext()" />
                </TableCell>
              </TableRow>
            </template>
            <TableRow v-else>
              <TableCell
                :col-span="columns.length"
                class="h-24 text-center"
              >
                No results.
              </TableCell>
            </TableRow>

          </TableBody>
        </Table>
      </div>
      <div class="flex items-center justify-between px-4">
        <div class="text-muted-foreground hidden flex-1 text-sm lg:flex">
          {{ table.getFilteredSelectedRowModel().rows.length }} of
          {{ table.getFilteredRowModel().rows.length }} row(s) selected.
        </div>

      </div>
    </TabsContent>
  </Tabs>
</template>
