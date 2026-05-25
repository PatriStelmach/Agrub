<script setup lang="ts">
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import type { ActionResponse } from "@/types/types.ts";
import { dateParser } from "@/composables/dateParser.ts";
import {dataTable, hoverListRow, tableHeaders} from "@/assets/cssFunctions.ts";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {
  IconCircleCheck,
  IconCircleX
} from "@tabler/icons-vue";
import SortableHead from "@/helpers_components/SortableHead.vue";
import { useSort } from "@/composables/sorting.ts";

const props = defineProps<{
  actions: ActionResponse[] | undefined;
  maxH: number
}>()

const { sortedData, sortKey, sortOrder, toggleSort } = useSort(
  () => props.actions ?? [],
  'createdAt'
)
</script>

<template>
  <div :class="`mx-[1%] w-98/100 relative overflow-y-auto  max-h-[${maxH}vh]`">
    <Table :class="`${dataTable} min-w-250! overflow-auto`">
      <TableHeader class="h-8">
        <TableRow :class="tableHeaders">
          <SortableHead keyName="ackUpdate" label="New ack" class="w-30" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <TableHead class="px-0 w-15/100">
          <span
            class=" items-center h-full mx-0 px-1 font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">
            Severity change
          </span>
          </TableHead>
          <SortableHead keyName="message" label="Comment" class="w-fit" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="author" label="User" class="w-50" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Date" class="w-50 pr-2" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        </TableRow>
      </TableHeader>
      <TableBody>
        <TableRow v-if="!actions?.length">
          <TableCell colspan="6" class="px-4 py-8 text-center text-slate-400 italic">
            No actions recorded yet.
          </TableCell>
        </TableRow>
        <template v-else>
          <TableRow
            v-for="action in sortedData"
            :key="action.id"
            :class="hoverListRow('transition-colors')"
          >
            <TableCell>
              <div v-if="action.ackUpdate">
                <div class="flex space-x-2 text-center  items-center" v-if="action.ack">
                  <IconCircleX class="text-red-badge" />
                  <span class="bg-linear-to-l from-severity-1 to-severity-5 bg-clip-text text-md text-transparent font-extrabold ">
                  ➠
              </span>
                  <IconCircleCheck class="text-green-badge"/>
                </div>
                <div class="flex space-x-2 text-center  items-center" v-else>
                  <IconCircleCheck class="text-green-badge" />
                  <span class="bg-linear-to-l from-severity-5 to-severity-1 bg-clip-text text-md text-transparent font-extrabold ">
                  ➠
              </span>
                  <IconCircleX class="text-red-badge"/>
                </div>
              </div>
            </TableCell>
            <TableCell >
              <div class="flex space-x-2 items-center" v-if="action.newSeverity">
                <SeverityDiv :severity="action.previousSeverity"/>
                <span :class="`bg-linear-to-r from-severity-${action.previousSeverity} to-severity-${action.newSeverity} bg-clip-text text-md text-transparent font-extrabold `">
                  ➠
              </span>
                <SeverityDiv :severity="action.newSeverity"/>
              </div>
              <div v-else>

              </div>
            </TableCell>
            <TableCell class="whitespace-break-spaces text-comment" :title="action.message">
              {{ action.message || '—' }}
            </TableCell>
            <TableCell class="font-medium text-comment text-xs">
              {{ action.author }}
            </TableCell>
            <TableCell class=" pr-2 text-xs tabular-nums text-date">
              {{ dateParser(action.createdAt).fullDate }}
            </TableCell>
          </TableRow>
        </template>
      </TableBody>
    </Table>
  </div>

</template>
