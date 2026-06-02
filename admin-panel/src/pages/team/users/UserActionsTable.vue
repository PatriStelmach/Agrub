<script setup lang="ts">
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
  TableCaption
} from '@/components/ui/table'
import type { ActionResponse } from "@/types/types.js";
import { dateParser } from "@/composables/dateParser.js";
import {dataTable, hoverListRow, tableCaption, tableHeaders} from "@/assets/cssFunctions.js";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {
  IconCircleCheck,
  IconCircleX
} from "@tabler/icons-vue";
import SortableHead from "@/helpers_components/SortableHead.vue";
import { useSort } from "@/composables/sorting.js";
import {type HTMLAttributes, watchEffect} from "vue";
import LoadingTable from "@/helpers_components/LoadingTable.vue";

const props = defineProps<{
  maxH: string
  maxW?: string
  class?: HTMLAttributes["class"]
  actions: ActionResponse[]
  isLoading: boolean
}>()

const sortedHead = defineModel<{ sortKey: string; sortOrder: string }>('sortedHead')

const { sortKey, sortOrder, toggleSort } = useSort(
  () => props.actions,
  'createdAt'
)

watchEffect(() => {
  sortedHead.value = { sortKey: sortKey.value, sortOrder: sortOrder.value };
});



const link = (action: ActionResponse) => {
  return !!action.closedAt ? `/alerts_history/${action.alertId}`: `/active_alerts/${action.alertId}`;
}

</script>
<template>
  <div
    :style="`max-height: ${maxH}`"
    :class="`mx-auto overflow-auto ${props.class} `">
    <Table :class="`${dataTable}  overflow-x-scroll max-w-[${maxW}] `">
      <TableCaption :class="tableCaption">
        <slot/>
      </TableCaption>
      <TableHeader class="h-8">
        <TableRow v-if="!actions.length">
          <TableHead colspan="6" class="px-4 py-8 text-center text-slate-400 italic">
            No actions recorded yet.
          </TableHead>
        </TableRow>
        <TableRow v-else :class="tableHeaders">
          <SortableHead keyName="ackUpdate" label="ACK" class="w-7/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <TableHead class="px-2 18-100">
          <span
            class=" items-center h-full truncate mx-0  font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">
            Severity
          </span>
          </TableHead>
          <SortableHead keyName="message" label="Comment" class="w-30/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead key-name="subject" class="w-28/100" label="Alert" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Date" class="pr-2 w-17/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        </TableRow>
      </TableHeader>
      <Transition name="fade" mode="out-in">
        <LoadingTable :colspan="6" v-if="isLoading"/>
        <TableBody v-else>
          <TableRow
            v-for="action in actions"
            :key="action.id"
            :class="hoverListRow('transition-colors')"
          >
            <TableCell>
              <IconCircleCheck v-if="action.ackUpdate" class="text-green-badge"/>
              <IconCircleX v-else-if="!action.ackUpdate  && action.ackUpdate !== null" class="text-red-badge" />
              <p class="text-comment ml-2" v-else-if="action.ackUpdate === null">
                —
              </p>
            </TableCell>
            <TableCell >
              <div class="flex space-x-1 items-center" v-if="action.newSeverity">
                <SeverityDiv :severity="action.previousSeverity"/>
                <span :class="`bg-linear-to-r from-severity-${action.previousSeverity} to-severity-${action.newSeverity} bg-clip-text text-md text-transparent font-extrabold `">
                  ➠
              </span>
                <SeverityDiv :severity="action.newSeverity"/>
              </div>
              <div class="text-comment" v-else>
                —
              </div>
            </TableCell>
            <TableCell class="whitespace-break-spaces text-comment" :title="action.message">
              {{ action.message || '—' }}
            </TableCell>
            <TableCell>
              <RouterLink
                :to="link(action)">
                <span
                  class="hover:border-b font-semibold text-blue-badge/80 hover:border-b-blue-600 hover:text-blue-600 text-xs whitespace-break-spaces"
                >{{ action.subject }}</span>
              </RouterLink>
            </TableCell>
            <TableCell class=" pr-2 tabular-nums text-comment whitespace-break-spaces">
              {{ dateParser(action.createdAt!).fullDate }}
            </TableCell>
          </TableRow>

        </TableBody>
      </Transition>
    </Table>
  </div>

</template>
