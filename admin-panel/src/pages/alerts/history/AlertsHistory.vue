<script setup lang="ts">
import {
  tableDiv,
  topButtonGroup,
  topDiv,
  topH1
} from "@/assets/cssFunctions.js";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {Button} from "@/components/ui/button";
import {Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {
  type AlertDetails,
  type AlertHistoryFilters,
  undefinedFilters,
  type HistoryAlert
} from "@/types/types.js";
import {defineAsyncComponent, onMounted, ref, watch} from "vue";
import { IconFilterCog} from "@tabler/icons-vue";
import GoBackButton from "@/helpers/GoBackButton.vue";
import {useAlertStore} from "@/stores/alertStore.ts";
import AlertsHistoryTable from "@/pages/alerts/history/AlertsHistoryTable.vue";
import MyPagination from "@/helpers/MyPagination.vue";
import AlertsFilters from "@/pages/alerts/history/AlertsFilters.vue";
const DetailsCard = defineAsyncComponent(() => import('@/pages/alerts/DetailsCard.vue'))

const alertStore = useAlertStore()


const alerts = ref<HistoryAlert[]>([])
const searchFilter = ref<string | null>(null);
const pageSize = ref<number>(20);
const currentPage = ref<number>(1);
const filters = ref<AlertHistoryFilters>(undefinedFilters);
const totalElements = ref<number>(0);
const hoveredAlert = ref<AlertDetails | null>(null)
const sortedHead = ref<{ sortKey: string; sortOrder: string }>({
  sortKey: 'createdAt',
  sortOrder: 'desc'
})


const getCurrentStateRequest = async () => {
  const response = await alertStore.getAlertsHistory(
    currentPage.value,
    pageSize.value,
    filters.value,
    sortedHead.value.sortKey,
    sortedHead.value.sortOrder
  )
  alerts.value = response?.alerts
  totalElements.value = response?.totalElements
}

const updateFilters = async (data: AlertHistoryFilters) => {
  filters.value = data
  currentPage.value = 1
  await getCurrentStateRequest()
}

watch([currentPage, pageSize], async () => {
  await getCurrentStateRequest()
})

watch(sortedHead, async () => {
  await getCurrentStateRequest()
}, { deep: true })

</script>

<template>

  <div>
    <div :class="topDiv">
      <h1 :class="topH1">Alerts history</h1>
      <ButtonGroup :class="topButtonGroup">
        <ButtonGroup>
          <GoBackButton/>
        </ButtonGroup>

        <ButtonGroup>
          <InputGroup >
            <InputGroupInput
              v-model="searchFilter"
              type="search"
              placeholder="Search for alert"/>
            <InputGroupAddon>
              <Search/>
            </InputGroupAddon>
          </InputGroup>
          <AlertsFilters
            @update:filters="updateFilters"
          >
            <Button  variant="outline">
              Filters <IconFilterCog/>
            </Button>
          </AlertsFilters>
        </ButtonGroup>

      </ButtonGroup>
    </div>
    <div :class="tableDiv">
      <DetailsCard
        v-if="hoveredAlert"
        :data=hoveredAlert
      />
      <AlertsHistoryTable
        v-model:hovered-alert="hoveredAlert"
        v-model:sorted-head="sortedHead"
        :alerts="alerts"
        :totalElements="totalElements"
      >
        <MyPagination
          :total="totalElements"
          v-model:page-index="currentPage"
          v-model:page-size="pageSize"
        />
      </AlertsHistoryTable>
    </div>
  </div>
</template>
