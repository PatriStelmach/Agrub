<script setup lang="ts">

import {
  Table, TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table";
import {IconCircleCheck, IconCircleX, IconEdit} from "@tabler/icons-vue";
import SortableHead from "@/helpers_components/SortableHead.vue";
import EditAlertDialog from "@/pages/alerts/active/EditAlertDialog.vue";
import {Button} from "@/components/ui/button";
import DateCell from "@/helpers_components/DateCell.vue";
import {Badge} from "@/components/ui/badge";
import {dataTable, tableHeaders, tableCaption, hoverListRow} from "@/assets/cssFunctions.js";
import type {ActiveAlert, AlertDetails} from "@/types/types.js";
import {useClientSort} from "@/composables/useClientSort";
import {computed, ref, watchEffect} from "vue";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {dateParser} from "@/helpers_functions/dateParser.js";
import LoadingTable from "@/helpers_components/LoadingTable.vue";
import {useAlertStore} from "@/stores/alertStore.ts";
import {useRoute} from "vue-router";

const props = defineProps<{
  tableData: ActiveAlert[]
  isLoading: boolean
}>()

const alertStore = useAlertStore();
const route = useRoute();
const { sortedData, sortKey, sortOrder, toggleSort } = useClientSort<ActiveAlert>(() => props.tableData as ActiveAlert[], 'createdAt');

const isDialogOpen = ref(false)
const hoveredAlert = defineModel<AlertDetails | null>('hoveredAlert');
const hoveredId = ref<number | null>(null);
const openedAlert = computed(()=> alertStore.currentAlerts.find(a => a.id === Number(route.params.alert)))

const computedHoveredAlert = computed(() => {
  const alert = props.tableData.find(a => a.id === hoveredId.value);
  return hoveredId.value
    ? { message: alert?.message, subject: alert?.subject, severity: alert?.severity }
    : null;
});

watchEffect(() => {
  hoveredAlert.value = computedHoveredAlert.value;
});

watchEffect( () => {
  if(!props.isLoading && openedAlert.value) {
    isDialogOpen.value = true
  }
})


</script>

<template>
  <EditAlertDialog
    v-model:isDialogOpen="isDialogOpen"
    v-model:alert="openedAlert"
  />
  <Table id="alert-table" :class="dataTable">
    <TableCaption :class="tableCaption">
      <slot/>
    </TableCaption>
    <TableHeader class="h-10">
      <TableRow :class="tableHeaders">
        <SortableHead keyName="subject" label="Alert" :sort-key="sortKey" class="w-fit *:pl-2" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="max-md:w-1/8 max-lg:w-9/100 w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="message" label="Message" :sort-key="sortKey" class="w-fit md:min-w-1/5" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-fit " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="originType" label="Origin" :sort-key="sortKey" class="w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="isAcknowledged" label="ACK" :sort-key="sortKey" class="w-6/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="createdAt" label="Created at" :sort-key="sortKey" class="w-fit md:w-15/100 lg:w-1/9" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <TableHead class="max-md:w-9/100 w-6/100 lg:w-5/100 font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">Actions</TableHead>
      </TableRow>
    </TableHeader>
    <Transition name="fade" mode="out-in">
      <LoadingTable :colspan="8" v-if="isLoading"/>
      <TableBody v-else>
        <TableRow
          :id="`${alert.id}_row`"
          :class="hoverListRow('relative duration-0 ')"
          v-for="alert in sortedData"
          :key="alert.id"
        >
          <TableCell class="pl-4  whitespace-break-spaces">{{alert.subject}}</TableCell>
          <TableCell>
            <SeverityDiv :severity="alert.severity"/>
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
               `/settings/systems/${alert.originType}` :
                `/my_plugins/${alert.source}`">
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
          <DateCell   :date="dateParser(alert.createdAt).toDate "></DateCell>
          <TableCell>
            <RouterLink :to="`/active_alerts/${alert.id}`">
              <Button
                size="icon-sm"
                variant="green_outline">
                <IconEdit class="size-4"/>
              </Button>
            </RouterLink>
          </TableCell>
        </TableRow>
      </TableBody>
    </Transition>
  </Table>
</template>
