<script setup lang="ts">

import {
  dataTable,
  tableCaption,
  tableHeaders,

} from "@/assets/cssFunctions.ts";
import {
  Table,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table";
import SortableHead from "@/helpers/SortableHead.vue";
import {computed, defineAsyncComponent, ref, watch, watchEffect} from "vue";
import {type AlertDetails, type HistoryAlert} from "@/types/types.ts";
import {Badge} from "@/components/ui/badge";
import {IconCircleDashedCheck, IconCircleDashedX, IconHistory} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import DateCell from "@/helpers/DateCell.vue";
import {useSortRequests} from "@/composables/useSortRequests.ts";
const AlertHistoryDialog = defineAsyncComponent(
  () => import('@/pages/alerts/history/AlertHistoryDialog.vue')
)

const props = defineProps<{
  alerts: HistoryAlert[];
  totalElements: number
}>()

const hoveredAlert = defineModel<AlertDetails | null>('hoveredAlert')
const sortedHead = defineModel<{ sortKey: string; sortOrder: string }>('sortedHead')

const { sortKey, sortOrder, toggleSort } = useSortRequests<HistoryAlert>(() => props.alerts, 'createdAt')

const hoveredId = ref<number | null>(null);

const computedHoveredAlert = computed(() => {
  const alert = props.alerts.find(a => a.id === hoveredId.value);
  return hoveredId.value
    ? { message: alert?.message, subject: alert?.subject, severity: alert?.severity }
    : null;
});

watchEffect(() => {
  hoveredAlert.value = computedHoveredAlert.value;
});

watchEffect(() => {
  sortedHead.value = { sortKey: sortKey.value, sortOrder: sortOrder.value };
});


</script>

<template>
      <Table id="alert-history-table" :class="dataTable">
        <TableCaption :class="tableCaption">
            <slot/>
          <span>Matched alerts: <span  class="font-extrabold">{{ totalElements}}</span></span>
        </TableCaption>
        <TableHeader class="h-10">
          <TableRow :class="tableHeaders">
            <SortableHead keyName="subject" label="Alert" :sort-key="sortKey" class="w-fit *:pl-2" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="max-md:w-1/8 max-lg:w-9/100 w-6/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="message" label="Message" :sort-key="sortKey" class="w-fit md:min-w-1 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-fit " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="originType" label="Origin" :sort-key="sortKey" class="w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="acknowledged" label="ACK" :sort-key="sortKey" class="w-6/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="createdAt" label="Timestamp" :sort-key="sortKey" class="w-fit md:w-13/100 lg:w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="closedAt" label="Closed" :sort-key="sortKey" class="w-fit md:w-13/100 lg:w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <TableHead class="max-md:w-9/100 w-6/100 lg:w-4/100 font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">Actions</TableHead>
          </TableRow>
        </TableHeader>
          <TableRow
            :id="`${alert.id}_row`"
            class="relative cursor-pointer duration-0  hover:bg-accent/50"
            v-for="alert in alerts"
            :key="alert.id">

            <TableCell class="pl-4  whitespace-break-spaces">{{alert.subject}}</TableCell>
            <TableCell>
              <div
                :class="` text-center font-extrabold text-lg border-2 shadow-[0px_0px_10px_2px]
                 shadow-severity-${alert.severity}/70 border-severity-${alert.severity} bg-severity-${alert.severity}/80 rounded-sm `">
                <span >{{alert.severity}}</span>
              </div>
            </TableCell>
            <TableCell
              @mouseenter="hoveredId = alert.id"
              @mouseleave="hoveredId = null"
              class="truncate"
            >{{alert.message}}</TableCell>
            <TableCell >
              <Badge class="whitespace-break-spaces"
                     variant="source"
              >{{alert.source}}</Badge>
            </TableCell>
            <TableCell >
              <Badge class="whitespace-break-spaces"
                     variant="origin"
              >{{alert.originType}}</Badge>
            </TableCell>

            <TableCell class=" gap-x-2 items-center">
              <IconCircleDashedCheck v-if="alert.acknowledged" class="text-green-badge"/>
              <IconCircleDashedX v-else class="text-red-badge"/>
            </TableCell>
            <DateCell  :date="alert.createdAt "></DateCell>
            <DateCell  :date="alert.closedAt "></DateCell>
            <TableCell>
              <AlertHistoryDialog
                :alert="alert"
              >
                <Button size="icon-lg" variant="green_outline">
                  <IconHistory class="size-5"/>
                </Button>
              </AlertHistoryDialog>

            </TableCell>
          </TableRow>
      </Table>
</template>
