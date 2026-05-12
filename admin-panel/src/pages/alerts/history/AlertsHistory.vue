<script setup lang="ts">
import {tableDiv, topButtonGroup, topDiv, topH1} from "@/assets/cssFunctions.js";
import {Button} from "@/components/ui/button";
import {ButtonGroup} from "@/components/ui/button-group";
import {
  type AlertDetails,
  type AlertHistoryFilters,
  type HistoryAlert,
  undefinedAlertsFilters
} from "@/types/types.js";
import {defineAsyncComponent, onMounted, ref} from "vue";
import {IconFilterCog} from "@tabler/icons-vue";
import GoBackButton from "@/helpers_components/GoBackButton.vue";
import AlertsHistoryTable from "@/pages/alerts/history/AlertsHistoryTable.vue";
import MyServerPagination from "@/helpers_components/MyServerPagination.vue";
import AlertsFilters from "@/pages/alerts/history/AlertsFilters.vue";
import {useServerSearchFilter} from "@/composables/useServerSearchFilter.ts";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";
const DetailsCard = defineAsyncComponent(() => import('@/pages/alerts/DetailsCard.vue'))

const isLoading = ref(true);

const getAlertsHistory = async () => {
  try {
    const response = await api.get('/alerts/history', {
      params: {
        page: currentPage.value - 1,
        pageSize: pageSize.value,
        sortKey: sortedHead.value.sortKey,
        sortOrder: sortedHead.value.sortOrder,
        severity: filters.value.severity,
        message: filters.value.message,
        subject: filters.value.subject,
        source: filters.value.source,
        origin: filters.value.origin,
        ack: filters.value.ack,
        unack: filters.value.unack,
        createdDateFrom: filters.value.createdDateFrom,
        createdDateTo: filters.value.createdDateTo,
        closedDateFrom: filters.value.closedDateFrom,
        closedDateTo: filters.value.closedDateTo,
      },
      paramsSerializer: {
        indexes: null
      }
    })

    if (response.status === 200) {
      items.value = response.data.content.map((a: HistoryAlert) => ({
        ...a,
        createdAt: new Date(a.createdAt),
        closedAt: new Date(a.closedAt)
      }))
      totalElements.value = response.data.totalElements
      console.log(response.data.totalElements)

      toast.info('Alerts history fetched')
    }
  } catch {
    toast.error('Error getting alerts history')
  }
}

onMounted(async () => {
  await getAlertsHistory().finally( () => isLoading.value = false)
})

const {
  filters,
  items,
  pageSize,
  currentPage,
  totalElements,
  sortedHead,
  updateFilters
} = useServerSearchFilter<HistoryAlert, AlertHistoryFilters>(
  getAlertsHistory,
  undefinedAlertsFilters,
  'createdAt',
  'desc'
)

const hoveredAlert = ref<AlertDetails | null>(null)


</script>

<template>
  <div>
    <div :class="topDiv">
      <h1 :class="topH1">Alerts history</h1>
      <ButtonGroup :class="topButtonGroup">
        <ButtonGroup>
          <GoBackButton />
        </ButtonGroup>
        <ButtonGroup>

          <AlertsFilters @update:filters="updateFilters">
            <Button variant="outline">
              Filters <IconFilterCog />
            </Button>
          </AlertsFilters>
        </ButtonGroup>
      </ButtonGroup>
    </div>
    <div :class="tableDiv">
      <DetailsCard
        v-if="hoveredAlert"
        :data="hoveredAlert"
      />
      <AlertsHistoryTable
        :isLoading="isLoading"
        v-model:hovered-alert="hoveredAlert"
        v-model:sorted-head="sortedHead"
        :alerts="items"
        :totalElements="totalElements"
      >
        <MyServerPagination
          :total="totalElements"
          v-model:page-index="currentPage"
          v-model:page-size="pageSize"
        />
      </AlertsHistoryTable>
    </div>
  </div>
</template>
