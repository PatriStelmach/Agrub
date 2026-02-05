<script setup lang="ts">
import { z } from "zod"
import {computed, ref} from "vue"
import type {Alert} from "@/types/alert.ts";

import type {
  ColumnDef,
  ColumnFiltersState,
  SortingState,
  VisibilityState,
} from "@tanstack/vue-table"
import {
  IconLock,
  IconChevronDown,
  IconStatusChange,
  IconCircleCheckFilled,
  IconUser,
  IconLayoutColumns,
  IconLoader,
  IconEyeCog,
  IconBellCog
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
  TableBody, TableCaption,
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
import IconTooling from "@/components/icons/IconTooling.vue";

const schema = z.object({
  id: z.number(),
  header: z.string(),
  source: z.string(),
  status: z.string(),
  priority: z.string(),
  technician: z.string(),
  createdAt: z.date(),
  closedAt: z.date(),

})

const props = defineProps<{
  data: Alert[]
}>()

const sorting = ref<SortingState>([])
const columnFilters = ref<ColumnFiltersState>([])
const columnVisibility = ref<VisibilityState>({})
const rowSelection = ref({})


const columns: ColumnDef<Alert>[] = [
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
    accessorKey: "priority",
    header: () => h("div", { class: "flex items-center gap-1" }, [
      "priority",
    ]),
    cell: ({ row }) =>
    {
      const priority = () =>
    {
      const priority = row.getValue("priority") as string
      switch (priority)
      {
        case "low":
          return "bg-green-500"
        case "medium":
          return "bg-yellow-500"
        case "high":
          return "bg-red-500"
      }
    }
      return h(Badge, {
      variant: "secondary",
      size: "sm",
      class: `${priority()} h-full  rounded-md p-2 font-semibold`,
    }, () => [
      h("span", {  }, String(row.getValue("priority"))),
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

    <TabsContent
      value="outline"
      class="relative flex flex-col gap-4 overflow-auto px-4 lg:px-6"
    >
      <div class="overflow-hidden rounded-lg border">
        <Table>
          <TableCaption class="p-[1vh] border-y">
            {{ table.getFilteredSelectedRowModel().rows.length }} of
            {{ table.getFilteredRowModel().rows.length }} row(s) selected.</TableCaption>
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
    </TabsContent>
  </Tabs>
</template>
