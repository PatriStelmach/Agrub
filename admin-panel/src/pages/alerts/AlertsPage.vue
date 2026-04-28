<script setup lang="ts" >
import {type ActiveAlert} from "@/types/types.ts";
import {dashboardData} from "@/data/dashboardData.ts"
import MyPagination from "@/helpers/MyPagination.vue";
import {
  IconSend,
  IconLoader,
  IconBellCog,
  IconChevronDown,
  IconEyeCog,
  IconLock,
  IconStatusChange,
  IconUser, IconListDetails, IconCancel, IconBrandOkRu, IconCircleDashedCheck, IconCircleDashedX
} from "@tabler/icons-vue";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
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
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import SortableHead from "@/helpers/SortableHead.vue";
import {useSort} from "@/composables/sorting.ts";
import {useSearchFilter} from "@/composables/useSearchFilter.ts";
import { useAlertStore } from "@/stores/alertStore";
import {computed, onMounted, ref} from "vue";
import {generateRandomString} from "ts-randomstring/lib";
import {useMouse} from "@vueuse/core";
import {Card, CardContent, CardHeader} from "@/components/ui/card";
import {Label} from "@/components/ui/label";
import {
  tableCaption,
  dataTable,
  tableHeaders,
  tableDiv,
  topH1,
  topDiv, topButtonGroup
} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers/GoBackButton.vue";


const alertStore = useAlertStore();
onMounted(() => {
  alertStore.getCurrentAlertsRequest()
})

const isAlertHovered = ref<boolean>(false);
const hoveredId = ref<number | null>(null);

const hoveredData = computed(() =>
  sortedData.value.find(p => p.id === hoveredId.value))

const descriptionBox = computed(() => {
  return {
    position: 'fixed',
    top: `${y.value}px`,
    left: `${x.value}px`,
    pointerEvents: "none",
    transform: 'translate(-10px, 20px)'
  }
})

const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage, searchFilter } =
  useSearchFilter<ActiveAlert>(() => alertStore.getAllCurrentAlerts,(item) => item.subject)
const { x, y } = useMouse()
const { sortedData, sortKey, sortOrder, toggleSort } = useSort<ActiveAlert>(() => tableData.value as ActiveAlert[], 'createdAt')


const mouseEnter = (id: number) => {
  isAlertHovered.value = true
  hoveredId.value = id
}

const mouseLeave = () => {
  isAlertHovered.value = false
  hoveredId.value = null
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
      <Card
        v-if="isAlertHovered"
        v-model="hoveredData"
        :style="descriptionBox"
        class="z-9999 max-w-120 border-2 border-badge shadow-sm shadow-badge">
        <CardHeader><Label class=" font-extrabold ">Subject: </Label> {{ hoveredData?.subject }}</CardHeader>
        <CardContent class="max-w-full wrap-break-word"><Label class="pb-2 grid font-extrabold">Message:</Label> {{ hoveredData?.message}}</CardContent>
      </Card>
      <Table id="alert-table" :class="dataTable">
        <TableCaption :class="tableCaption">Active Alerts:
          <span class="font-extrabold">{{ sortedData.length}}</span>
        </TableCaption>
        <TableHeader class="h-10">
          <TableRow :class="tableHeaders">
            <SortableHead keyName="subject" label="Alert" :sort-key="sortKey" class="w-15/100 pl-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="w-8/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="message" label="Message" :sort-key="sortKey" class="w-33/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-10/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="originType" label="Origin" :sort-key="sortKey" class="w-10/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="acknowledge" label="ACK" :sort-key="sortKey" class="w-10/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="createdAt" label="Timestamp" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
          <TransitionGroup tag="tbody" name="slide-fade">
            <TableRow
              :id="`${alert.id}_row`"
              class="relative cursor-pointer duration-0  hover:bg-chart-1/30"
              v-for="alert in sortedData"
              :key="alert.id"
            >
              <TableCell class="pl-4  whitespace-break-spaces">{{alert.subject}}</TableCell>
              <TableCell

              ><div
                :class="` bg-linear-to-l w-full h-full text-md lg:text-lg xl:text-xl text-center font-bold
                from-severity-${alert.severity}/50 via-severity-${alert.severity} to-severity-${alert.severity}/50`">
                {{alert.severity}}
              </div>
              </TableCell>
              <TableCell
                @mouseenter="mouseEnter(alert.id)"
                @mouseleave="mouseLeave"
                class="truncate">{{alert.message}}</TableCell>
              <TableCell >
                <Badge
                  variant="source"
                >{{alert.source}}</Badge>
              </TableCell>
              <TableCell >
                <Badge
                  variant="origin"
                >{{alert.originType}}</Badge>
              </TableCell>
              <TableCell class=" gap-x-2 items-center">
                <IconCircleDashedCheck v-if="alert.acknowledged" class="text-badge1"/>
                <IconCircleDashedX v-else class="text-badge2"/>
              </TableCell>
              <DateCell v-if="alert.createdAt"  :date="alert.createdAt "></DateCell>
            </TableRow>
          </TransitionGroup>

        <TableFooter>
        </TableFooter>
      </Table>

    </div>
    <MyPagination
      class="max-h-[5vh] z-99"
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
    />
  </div>

</template>
