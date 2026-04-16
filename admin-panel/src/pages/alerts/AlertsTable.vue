<script setup lang="ts">

import {
  computed,
  onMounted,
  onUnmounted,
  ref,
  watch
} from "vue"
import type { AlertObject} from "@/types/types.ts";


import {
  IconSend,
  IconLoader,
  IconListCheck,
  IconX,
  IconDeviceFloppy,
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {Button} from "@/components/ui/button";
import {useSort} from "@/composables/sorting.ts";
import SortableHead from "@/helpers/SortableHead.vue";
import {useWrapping} from "@/composables/unwrapping.ts";

const props = defineProps<{
  data: AlertObject[];
  all: AlertObject[];
}>()

const emit = defineEmits<{
  'update:checked': [alerts:number[]];
}>()




const checkedAlerts = ref<number[]>([])

const allChecked = computed(() => {
  return props.data.length > 0 && checkedAlerts.value.length === props.data.length
})

watch(checkedAlerts, (newChecked: number[]) => {
  emit('update:checked', newChecked);
})

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<AlertObject>(() => props.data, 'createdAt')
const { unwrap, wrap, isUnwrapped} = useWrapping(sortedData)


const checkAll = () => {
  if(!allChecked.value) {
    checkedAlerts.value = props.data.map(alert => alert.id);
  }
  else
    checkedAlerts.value = [] ;
}

</script>

<template>
  <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[70vh] scroll-style ">
    <Table id="alert-table" class="w-99/100 text-md xl:text-xl 2xl:text-4xl  mx-auto  table-fixed">
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9999 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl">Current Alerts:
        <span class="font-extrabold">{{ props.all.length}}</span>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow class="bg-secondary hover:bg-secondary">
          <TableHead class="w-3/100 pl-0 pr-4 ">
            <IconListCheck
              class="size-[2vh] mx-3 rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500 hover:scale-105
                  hover:bg-green-500 cursor-pointer transition duration-100"
              :class="{'text-green-500 hover:bg-primary hover:shadow-primary': allChecked }"

              @click="checkAll"
            />
          </TableHead>
          <SortableHead keyName="header" label="Alert" :sort-key="sortKey" class="w-23/100 py-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-12/100 py-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="status" label="Status" :sort-key="sortKey" class="w-10/100 py-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="priority" label="Priority" :sort-key="sortKey" class="w-10/100 py-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="technicianGroups" label="Groups" :sort-key="sortKey" class="w-28/100 py-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Created at" :sort-key="sortKey" class="w-14/100 py-4 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        </TableRow>
      </TableHeader>
      <TableBody >
        <TableRow
          class="relative cursor-pointer duration-0 h-14 lg:h-16 xl:h-18 2xl:h-22 hover:bg-chart-1/30"
          v-for="alert in sortedData"
          :key="alert.id"
          @dblclick="unwrap(alert.id)"
          :class="{'bg-selected [&_td]:align-top sticky h-22 lg:h-24 xl:h-26 2xl:h-30 top-13 bottom-11 hover:bg-card z-9999'
          : isUnwrapped(alert.id)}"
        >
          <TableCell class="pr-4  ">
            <input
              type="checkbox"
              :id="cn('my-plugin-no-'+alert.id)" class="size-[1vw] cursor-pointer align-middle"
              :value="alert.id"
              v-model="checkedAlerts"
            />
          </TableCell>
          <TableCell class="py-4 whitespace-pre-wrap">{{alert.header}}</TableCell>
          <TableCell class="py-4">
            <Badge
              class="cursor-pointer hover:bg-badge mr-1 ml-[-0.5em] text-md xl:text-xl 2xl:text-4xl"
              variant="secondary"
            >{{alert.source}}</Badge>
          </TableCell>
          <TableCell class="py-4 gap-x-2 items-center">
            <div class="flex gap-x-2 items-center">{{alert.status}}
            <IconLoader v-if="alert.status === 'In Process'"
                        class="size-4 animate-spin text-muted-foreground"/>
            <IconSend v-if="alert.status === 'Sent'"
                      class="size-4 text-emerald-500"/>
            </div>
          </TableCell>

          <TableCell class="py-4"
                     :class="{
            'text-green-500': alert.priority === 'low',
            'text-yellow-500': alert.priority === 'medium',
            'text-destructive': alert.priority === 'high'}"
          >{{alert.priority}}
          </TableCell>

          <TableCell
            class="py-4  whitespace-pre-wrap">{{ alert.technicianGroups?.join(", ") }}</TableCell>
          <DateCell v-if="alert.createdAt" class="py-4" :date="alert.createdAt "></DateCell>
            <Button
              v-if="isUnwrapped(alert.id)"
              @click="wrap(false)"
              variant="red_inside"
              class="text-destructive items-center align-middle flex gap-x-2 absolute bottom-2 left-3 ">
              Cancel<IconX class="size-4"/>
            </Button>

        </TableRow>
      </TableBody>
      <TableFooter>
      </TableFooter>
    </Table>
  </div>
</template>
