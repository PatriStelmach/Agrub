<script setup lang="ts">

import {
  dataTable,
  tableCaption,
  tableHeaders,

} from "@/assets/cssFunctions.ts";
import {
  Table, TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table";
import SortableHead from "@/helpers_components/SortableHead.vue";
import {computed, ref, watchEffect} from "vue";
import {type AlertDetails, type HistoryAlert} from "@/types/types.ts";
import {Badge} from "@/components/ui/badge";
import {IconCircleCheck, IconCircleX, IconHistory} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import DateCell from "@/helpers_components/DateCell.vue";
import {useSortRequests} from "@/composables/useSortRequests.ts";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {hoverListRow} from "@/assets/cssFunctions.ts";
import LoadingTable from "@/helpers_components/LoadingTable.vue";
import AlertHistoryDialog from '@/pages/alerts/history/AlertHistoryDialog.vue'
import {useRouter} from "vue-router";

const props = defineProps<{
  alerts: HistoryAlert[]
  isLoading: boolean
}>()

const router = useRouter()
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
        </TableCaption>
        <TableHeader class="h-10">
          <TableRow :class="tableHeaders">
            <SortableHead keyName="subject" label="Alert" :sort-key="sortKey" class="w-fit *:pl-2" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="max-md:w-1/8 max-lg:w-9/100 w-6/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="message" label="Message" :sort-key="sortKey" class="w-fit md:min-w-1 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-fit " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="originType" label="Origin" :sort-key="sortKey" class="w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="isAcknowledged" label="ACK" :sort-key="sortKey" class="w-6/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="createdAt" label="Created at" :sort-key="sortKey" class="w-fit md:w-13/100 lg:w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="closedAt" label="Closed at" :sort-key="sortKey" class="w-fit md:w-13/100 lg:w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <TableHead class="max-md:w-9/100 w-6/100 lg:w-5/100 font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <Transition name="fade" mode="out-in">
          <LoadingTable :colspan="9" v-if="isLoading"/>
          <TableBody v-else>
            <TableRow
              :id="`${alert.id}_row`"
              :class="hoverListRow('relative cursor-pointer duration-0')"
              v-for="alert in alerts"
              :key="alert.id">

              <TableCell class="pl-4  whitespace-break-spaces">{{alert.subject}}</TableCell>
              <TableCell>
                <SeverityDiv
                  :severity="alert.severity"
                />
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
                <RouterLink
                  :to="(alert.originType === 'ZABBIX' || alert.originType === 'WAZUH' || alert.originType === 'NAGIOS') ?
               `/my_systems/${alert.originType}` :
                `/my_plugins/${alert.originType}`">
                  <Badge
                    class="whitespace-break-spaces"
                    variant="origin"
                  >{{alert.originType}}</Badge>
                </RouterLink>
              </TableCell>

              <TableCell class=" gap-x-2 items-center">
                <IconCircleCheck v-if="alert.isAcknowledged" class="text-green-badge"/>
                <IconCircleX v-else class="text-red-badge"/>
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
          </TableBody>
        </Transition>
      </Table>
</template>
