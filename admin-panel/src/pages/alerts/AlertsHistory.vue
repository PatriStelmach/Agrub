<script setup lang="ts">

import {
  dataTable,
  tableCaption,
  tableDiv, tableHeaders,
  topButtonGroup,
  topDiv,
  topH1
} from "@/assets/cssFunctions.ts";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {Button} from "@/components/ui/button";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {useSort} from "@/composables/sorting.ts";
import {api_url, type ClosedAlert} from "@/types/types.ts";
import {onMounted, ref} from "vue";
import {Table, TableCaption, TableHeader, TableRow} from "@/components/ui/table";
import SortableHead from "@/helpers/SortableHead.vue";

import axios from "axios";
import { toast } from "vue-sonner";
import GoBackButton from "@/helpers/GoBackButton.vue";



const searchFilter = ref<string | null>(null);
const closedAlerts = ref<ClosedAlert[]>([]);
const pageSize = ref<number>(50);
const page = ref<number>(1);

onMounted(() => {
  getAlertsHistory(page.value, pageSize.value)
})

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<ClosedAlert>(() => closedAlerts.value, 'closedAt')

const getAlertsHistory = async (page: number, pageSize: number, id?: number, filter?: string, source?: string, originSource?: string) => {
  try {
    const response = await axios.get(`${api_url}/alerts/history`, {
      params: {
        id: id,
        page: page,
        pageSize: pageSize,
        filter: filter,
        source: source,
        originSource: originSource
      }
    })
    if (response.status === 200) {
      toast.info('Alerts history fetched')
      console.log(response.data)
      return response.data;
    }
  }
  catch {
    toast.error('Error getting alerts history');
  }
}

</script>

<template>
  <div>
    <div :class="topDiv">
      <h1 :class="topH1">Alerts dashboard</h1>

      <ButtonGroup :class="topButtonGroup">

        <GoBackButton/>
        <InputGroup >
          <InputGroupInput
            v-model="searchFilter"
            type="search"
            placeholder="Search for alert"/>
          <InputGroupAddon>
            <Search/>
          </InputGroupAddon>
        </InputGroup>

      </ButtonGroup>
    </div>
    <div :class="tableDiv">

      <Table id="alert-history-table" :class="dataTable">
        <TableCaption :class="tableCaption"> Matched alerts:
          <span class="font-extrabold">{{ sortedData.length}}</span>
        </TableCaption>
        <TableHeader class="h-10">
          <TableRow :class="tableHeaders">
            <SortableHead keyName="subject" label="Alert" :sort-key="sortKey" class="w-15/100 pl-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="w-8/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="message" label="Message" :sort-key="sortKey" class="w-38/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-15/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="status" label="Status" :sort-key="sortKey" class="w-10/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="createdAt" label="Timestamp" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="closedAt" label="Closed" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
        <TransitionGroup tag="tbody" name="slide-fade">
          <TableRow
            :id="`${alert.id}_row`"
            class="relative cursor-pointer duration-0  hover:bg-chart-1/30"
            v-for="alert in sortedData"
            :key="alert.id"
          >
          </TableRow>
        </TransitionGroup>
      </Table>

    </div>
  </div>
</template>
