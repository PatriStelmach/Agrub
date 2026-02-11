<script setup lang="ts">
import { z } from "zod"
import {computed, ref, watch} from "vue"
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
  IconBellCog, IconListCheck
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
import {cn} from "@/lib/utils.ts";
import type {Plugin} from "@/types/plugin.ts";
//
// const schema = z.object({
//   id: z.number(),
//   header: z.string(),
//   source: z.string(),
//   status: z.string(),
//   priority: z.string(),
//   technician: z.string(),
//   createdAt: z.date(),
//   closedAt: z.date(),
//
// })
//
// const props = defineProps<{
//   data: Alert[]
// }>()
//
// const sorting = ref<SortingState>([])
// const columnFilters = ref<ColumnFiltersState>([])
// const columnVisibility = ref<VisibilityState>({})
// const rowSelection = ref({})
//
//
// const columns: ColumnDef<Alert>[] = [
//   {
//     id: "select",
//     header: ({ table }) => h(Checkbox, {
//       "modelValue": table.getIsAllPageRowsSelected() || (table.getIsSomePageRowsSelected() && "indeterminate"),
//       "onUpdate:modelValue": value => table.toggleAllPageRowsSelected(!!value),
//       "aria-label": "Select all",
//     }),
//     cell: ({ row }) => h(Checkbox, {
//       "modelValue": row.getIsSelected(),
//       "onUpdate:modelValue": value => row.toggleSelected(!!value),
//       "aria-label": "Select row",
//     }),
//     enableSorting: false,
//     enableHiding: false,
//   },
//   {
//     accessorKey: "header",
//     header: "Alert",
//     cell: ({ row }) => h("div", String(row.getValue("header"))),
//     enableHiding: false,
//   },
//   {
//     accessorKey: "source",
//     header: "Source",
//     cell: ({ row }) => h(Badge, {
//       variant: "outline",
//     }, () => String(row.getValue("source"))),
//   },
//   {
//     accessorKey: "status",
//     header: "Status",
//     cell: ({ row }) => {
//       const status = row.getValue("status") as string
//       return h("div", { class: "flex items-center gap-2" }, [
//         status === "Done"
//           ? h(IconCircleCheckFilled, { class: "h-4 w-4 text-emerald-500" })
//           : h(IconLoader, { class: "h-4 w-4 animate-spin text-muted-foreground" }),
//         h("span", {}, status),
//       ])
//     },
//   },
//   {
//     accessorKey: "priority",
//     header: () => h("div", { class: "flex items-center gap-1" }, [
//       "priority",
//     ]),
//     cell: ({ row }) =>
//     {
//       const priority = () =>
//     {
//       const priority = row.getValue("priority") as string
//       switch (priority)
//       {
//         case "low":
//           return "bg-green-500"
//         case "medium":
//           return "bg-yellow-500"
//         case "high":
//           return "bg-red-500"
//       }
//     }
//       return h(Badge, {
//       variant: "secondary",
//       size: "sm",
//       class: `${priority()} h-full p-1.5 rounded-md`,
//     }, () => [
//       h("span", {  }, String(row.getValue("priority"))),
//     ])
//     }
//   },
//   {
//     accessorKey: "technician",
//     header: "Technician",
//     cell: ({ row }) => {
//       const technician = row.getValue("technician") as string
//       const isAssigned = technician !== "Assign technician"
//
//       if (isAssigned) {
//         return h("span", {}, technician)
//       }
//
//       return h(Select, {}, {
//         default: () => [
//           h(SelectTrigger, { class: "w-full" }, {
//             default: () => h(SelectValue, { placeholder: "Assign technician" }),
//           }),
//           h(SelectContent, {}, {
//             default: () => [
//               h(SelectItem, { value: "eddie" }, () => "Eddie Lake"),
//             ],
//           }),
//         ],
//       })
//     },
//   },
//   {
//     accessorKey: "createdAt",
//     header: "Created at",
//     cell: ({ row }) =>
//     {
//       const date = row.getValue("createdAt") as Date
//
//       return h(date, {} )
//     }
//   },
//   {
//     accessorKey: "closedAt",
//     header: "Closed at",
//
//     cell: ({ row }) =>
//     {
//       const date = row.getValue("closedAt") as Date
//       return h("span", {},
//         [
//           date == null
//           ? h("span", {}, String("- - - - - - - - - -"))
//             : h("span", {}, String(date)),
//         ])
//     }
//   }
// ]
//
// const table = useVueTable({
//   get data() {
//     return props.data
//   },
//   columns,
//   getCoreRowModel: getCoreRowModel(),
//   getPaginationRowModel: getPaginationRowModel(),
//   getSortedRowModel: getSortedRowModel(),
//   getFilteredRowModel: getFilteredRowModel(),
//   onSortingChange: (updaterOrValue) => {
//     sorting.value = typeof updaterOrValue === "function"
//       ? updaterOrValue(sorting.value)
//       : updaterOrValue
//   },
//   onColumnFiltersChange: (updaterOrValue) => {
//     columnFilters.value = typeof updaterOrValue === "function"
//       ? updaterOrValue(columnFilters.value)
//       : updaterOrValue
//   },
//   onColumnVisibilityChange: (updaterOrValue) => {
//     columnVisibility.value = typeof updaterOrValue === "function"
//       ? updaterOrValue(columnVisibility.value)
//       : updaterOrValue
//   },
//   onRowSelectionChange: (updaterOrValue) => {
//     rowSelection.value = typeof updaterOrValue === "function"
//       ? updaterOrValue(rowSelection.value)
//       : updaterOrValue
//   },
//   state: {
//     get sorting() { return sorting.value },
//     get columnFilters() { return columnFilters.value },
//     get columnVisibility() { return columnVisibility.value },
//     get rowSelection() { return rowSelection.value },
//   },
// })




const props = defineProps<{
  data: Alert[];
}>()

const emit = defineEmits<{
  'update:checked': [alerts:number[]];
}>()

const checkedAlerts = ref<number[]>([])
const allChecked = computed(() =>
{
  return props.data.length > 0 && checkedAlerts.value.length === props.data.length
})

const checkAll = () =>
{
  if(!allChecked.value)
  {
    checkedAlerts.value = props.data.map(plugin => plugin.id);
  }
  else
    checkedAlerts.value = [] ;
}

watch(checkedAlerts, (newChecked) =>
{
  emit('update:checked', newChecked);
})
</script>

<template>
  <div class=" mt-[2vh] flex mx-auto w-full">
    <Table class="w-9/10 mx-auto my-[1vh]">
      <TableCaption class="border-b border-t py-[1vh]">List of your plugins</TableCaption>
      <TableHeader>
        <TableRow class="bg-secondary hover:bg-secondary">
          <TableHead class="p-2">
            <IconListCheck
              class="size-6 mx-3 hover:text-green-500 hover:border-green-500 hover:border rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500-3 hover:scale-115 cursor-pointer transition duration-100"
              :class="{'text-green-500': allChecked }"

              @click="checkAll"
            />
          </TableHead>
          <TableHead class="p-4">Alert</TableHead>
          <TableHead class="p-4">Source</TableHead>
          <TableHead class="p-4">Status</TableHead>
          <TableHead class="p-4">Priority</TableHead>
          <TableHead class="p-4">Technician</TableHead>
          <TableHead class="p-4">Created at</TableHead>
          <TableHead class="p-4">Closed at</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody >
        <TableRow
          class="cursor-grab"
          v-for="alert in props.data"
          :key="alert.id">
          <TableCell class="px-4">
            <input
              type="checkbox"
              :id="cn('my-plugin-no-'+alert.id)" class="size-[1vw] cursor-pointer"
              :value="alert.id"
              v-model="checkedAlerts"
            />
          </TableCell>
          <TableCell class="p-4">{{alert.header}}</TableCell>
          <TableCell class="p-4">
            <Badge
              class="cursor-pointer hover:bg-chart-1 mx-1"
              variant="secondary"
            >{{alert.source}}</Badge>
          </TableCell>
          <TableCell
            v-if="alert.status === 'In Process'"
            class="p-4 gap-x-2 flex items-center">{{alert.status}}
          <IconLoader class="size-4 animate-spin text-muted-foreground"/>
          </TableCell>
          <TableCell
            v-if="alert.status === 'Done'"
            class="p-4 gap-x-2 flex items-center">{{alert.status}}
            <IconCircleCheckFilled class="size-4 text-emerald-500"/>
          </TableCell>
          <TableCell class="p-4"
                     :class="{
            'text-green-500': alert.priority === 'low',
            'text-yellow-500': alert.priority === 'medium',
            'text-red-500': alert.priority === 'high'}"
          >{{alert.priority}}
          </TableCell>
          <TableCell class="p-4">{{alert.technician}}</TableCell>
          <DateCell v-if="alert.createdAt" class="p-4" :date="alert.createdAt "></DateCell>
          <DateCell v-if="alert.closedAt" class="p-4" :date="alert.closedAt"></DateCell>
          <TableCell v-else class="p-4">- - - - - - - - -</TableCell>
        </TableRow>
      </TableBody>
      <TableFooter>
      </TableFooter>
    </Table>
  </div>
</template>
