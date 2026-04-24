<script setup lang="ts" >
import {type AlertObject} from "@/types/types.ts";
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
  IconUser, IconListDetails
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
  useSearchFilter<AlertObject>(() => alertStore.getAllCurrentAlerts,(item) => item.subject)
const { x, y } = useMouse()
const { sortedData, sortKey, sortOrder, toggleSort } = useSort<AlertObject>(() => tableData.value as AlertObject[], 'createdAt')

const randomString = (length: number): string => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  return Array.from({ length }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
}

function randomAlert() {
  alertStore.addCurrentAlert( {
    id: Math.floor(Math.random() * 10000000000),
    subject: randomString(10),
    source: randomString(10),
    message: randomString(100),
    status: "Sent" ,
    severity: 0 ,
    createdAt: new Date(Date.now()),
  })
}

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
  <div >
    <div class="relative max-h-[10vh] items-center align-middle">
      <h1 class="text-center my-[2vh] text-xl xl:text-2xl 2xl:text-4xl border-b pb-[2vh]">Alerts dashboard</h1>

      <div class="absolute left-4 top-0 flex  ">
        <ButtonGroup>

            <Button variant="outline" size="icon" aria-label="Go Back">
              <ArrowLeftIcon />
            </Button>
          <Button
            @click="randomAlert"
            variant="cyan_outline">Add new mocked</Button>
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
    </div>
    <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[77vh]   ">
      <Card
        v-if="isAlertHovered"
        v-model="hoveredData"
        :style="descriptionBox"
        class="z-9999 max-w-120 border-2 border-badge shadow-md shadow-badge">
        <CardHeader>{{ hoveredData?.subject }}</CardHeader>
        <CardContent class=" wrap-break-word">{{ hoveredData?.message}}</CardContent>
      </Card>
      <Table id="alert-table" class="w-99/100 text-md lg:text-lg xl:text-xl 2xl:text:3xl  mx-auto  table-fixed">
        <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9 bottom-0 py-2 text-md lg:text-lg xl:text-xl 2xl:text:3xl">Current Alerts:
          <span class="font-extrabold">{{ dashboardData.length}}</span>
        </TableCaption>
        <TableHeader class="h-10">
          <TableRow class="bg-secondary hover:bg-secondary **:text-md! *: **:lg:text-xl! **:xl:text-2xl! **:2xl:text-4xl!">
            <SortableHead keyName="subject" label="Alert" :sort-key="sortKey" class="w-15/100 pl-4" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class="w-8/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="message" label="Message" :sort-key="sortKey" class="w-38/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="source" label="Source" :sort-key="sortKey" class="w-15/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="status" label="Status" :sort-key="sortKey" class="w-10/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
            <SortableHead keyName="createdAt" label="Timestamp" :sort-key="sortKey" class="w-14/100 " :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            :id="`${alert.id}_row`"
            class="relative cursor-pointer duration-0  hover:bg-chart-1/30"
            v-for="alert in sortedData"
            :key="alert.id"
          >
            <TableCell class="pl-4  whitespace-nowrap">{{alert.subject}}</TableCell>
            <TableCell class=""

            ><span class=" bg-linear-to-l py-1 px-8 font-bold text-xl rounded-xl" :class="{
                      'from-sky-400 to-sky-400/70': alert.severity === 0,
                      'from-badge1 to-badge/80': alert.severity === 1,
                      'from-yellow-500 to-yellow-500/70': alert.severity === 2,
                      'from-amber-500 to-amber-500/70': alert.severity === 3,
                      'from-orange-500 to-orange-500/70': alert.severity === 4,
                      'from-badge2 to-badge2/70': alert.severity === 5,
                    }">{{alert.severity}}</span>
            </TableCell>
              <TableCell
                @mouseenter="mouseEnter(alert.id)"
                @mouseleave="mouseLeave"
                class="truncate">{{alert.message}}</TableCell>
            <TableCell class="">
              <Badge
                variant="source"
              >{{alert.source}}</Badge>
            </TableCell>
            <TableCell class=" gap-x-2 items-center">
              <div class="flex gap-x-2 items-center">{{alert.status}}
              <IconLoader v-if="alert.status === 'In Process'"
                          class="size-4 animate-spin text-muted-foreground"/>
              <IconSend v-if="alert.status === 'Sent'"
                        class="size-4 text-badge1"/>
              </div>
            </TableCell>
            <DateCell v-if="alert.createdAt" class="" :date="alert.createdAt "></DateCell>



          </TableRow>
        </TableBody>
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
