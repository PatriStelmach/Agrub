<script setup lang="ts">
import {tableDiv} from "@/assets/cssFunctions.js";
import {Button} from "@/components/ui/button";
import {ButtonGroup} from "@/components/ui/button-group";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {
  type AlertDetails,
  type AlertHistoryFilters,
  type HistoryAlert,
  undefinedAlertsFilters
} from "@/types/types.js";
import {onMounted, ref} from "vue";
import {IconFilterCog} from "@tabler/icons-vue";
import AlertsHistoryTable from "@/pages/alerts/history/AlertsHistoryTable.vue";
import MyServerPagination from "@/helpers_components/MyServerPagination.vue";
import AlertsFilters from "@/pages/alerts/history/AlertsFilters.vue";
import {useServerSearchFilter} from "@/composables/useServerSearchFilter.ts";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";
import DetailsCard from '@/pages/alerts/DetailsCard.vue'

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
  isLoading,
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
    <TopH1Div h1="Alerts history">
      <ButtonGroup>
        <AlertsFilters @update:filters="updateFilters">
          <Button variant="outline">
            Filters <IconFilterCog />
          </Button>
        </AlertsFilters>
      </ButtonGroup>
    </TopH1Div>
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
