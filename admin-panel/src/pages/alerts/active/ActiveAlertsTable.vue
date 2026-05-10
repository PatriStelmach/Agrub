<script setup lang="ts">

import {
  Table,
  TableCaption,
  TableCell, TableFooter,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table";
import {IconCircleDashedCheck, IconCircleDashedX, IconEdit} from "@tabler/icons-vue";
import SortableHead from "@/helpers/SortableHead.vue";
import EditAlertDialog from "@/pages/alerts/active/EditAlertDialog.vue";
import {Button} from "@/components/ui/button";
import DateCell from "@/helpers/DateCell.vue";
import {Badge} from "@/components/ui/badge";
import {dataTable, tableHeaders, tableCaption} from "@/assets/cssFunctions.js";
import type {ActiveAlert, AlertDetails} from "@/types/types.js";
import {useSort} from "@/composables/sorting.js";
import {computed, ref, watch, watchEffect} from "vue";
import SeverityDiv from "@/helpers/SeverityDiv.vue";

const props = defineProps<{
  tableData: ActiveAlert[];
}>()

const hoveredAlert = defineModel<AlertDetails | null>('hoveredAlert')

const hoveredId = ref<number | null>(null);

const computedHoveredAlert = computed(() => {
  const alert = props.tableData.find(a => a.id === hoveredId.value);
  return hoveredId.value
    ? { message: alert?.message, subject: alert?.subject, severity: alert?.severity }
    : null;
});

watchEffect(() => {
  hoveredAlert.value = computedHoveredAlert.value;
});

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<ActiveAlert>(() => props.tableData as ActiveAlert[], 'createdAt')

</script>

<template>
  <Table id="alert-table" :class="dataTable">
    <TableCaption :class="tableCaption">
      <slot/>
      <span>Active Alerts: <span class="font-extrabold">{{ sortedData.length}}</span></span>
    </TableCaption>
    <TableHeader class="h-10">
      <TableRow :class="tableHeaders">
        <SortableHead keyName="subject" label="Alert" :sort-key="sortKey" class="w-fit *:pl-2" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="max-md:w-1/8 max-lg:w-9/100 w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="message" label="Message" :sort-key="sortKey" class="w-fit md:min-w-1/5" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-fit " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="originType" label="Origin" :sort-key="sortKey" class="w-1/10 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="acknowledge" label="ACK" :sort-key="sortKey" class="w-6/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="createdAt" label="Created at" :sort-key="sortKey" class="w-fit md:w-14/100 lg:w-1/10" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <TableHead class="max-md:w-9/100 w-6/100 lg:w-5/100 font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">Actions</TableHead>
      </TableRow>
    </TableHeader>
    <TransitionGroup tag="tbody" name="slide-fade">
      <TableRow
        :id="`${alert.id}_row`"
        class="relative duration-0  hover:bg-accent/50"
        v-for="alert in sortedData"
        :key="alert.id">

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
          <Badge class="whitespace-break-spaces"
                 variant="origin"
          >{{alert.originType}}</Badge>
        </TableCell>

        <TableCell class=" gap-x-2 items-center">
          <IconCircleDashedCheck v-if="alert.acknowledged" class="text-green-badge"/>
          <IconCircleDashedX v-else class="text-red-badge"/>
        </TableCell>
        <DateCell v-if="alert.createdAt"  :date="alert.createdAt "></DateCell>
        <TableCell>
          <EditAlertDialog
            :alert="alert"
          >
            <Button size="icon-lg" variant="green_outline">
              <IconEdit class="size-5"/>
            </Button>
          </EditAlertDialog>

        </TableCell>
      </TableRow>
    </TransitionGroup>

  </Table>
</template>
