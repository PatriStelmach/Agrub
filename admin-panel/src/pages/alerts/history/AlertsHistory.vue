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
import {type AlertDetails, type AlertHistoryFilters, type HistoryAlert} from "@/types/types.js";
import {defineAsyncComponent, onMounted, type Ref, ref} from "vue";
import { IconFilterCog} from "@tabler/icons-vue";
import GoBackButton from "@/helpers/GoBackButton.vue";
import {useAlertStore} from "@/stores/alertStore.ts";
import AlertsHistoryTable from "@/pages/alerts/history/AlertsHistoryTable.vue";
import MyServerPagination from "@/helpers/MyServerPagination.vue";
import AlertsFilters from "@/pages/alerts/history/AlertsFilters.vue";
import type {DateRange} from "reka-ui";
const DetailsCard = defineAsyncComponent(() => import('@/pages/alerts/active/DetailsCard.vue'))

const alertStore = useAlertStore()

onMounted(async () => {
  alerts.value = await alertStore.getAlertsHistory(currentPage.value, pageSize.value, filters.value)
})

const hoveredAlert = ref<AlertDetails | null>(null)
const alerts = ref<HistoryAlert[]>([])
const searchFilter = ref<string | null>(null);
const pageSize = ref<number>(20);
const currentPage = ref<number>(1);
const filters = ref<AlertHistoryFilters>({
  severity:[0,1,2,3,4,5]
});


const updateCurrentPage = async (page: number) => {
  currentPage.value = page;
  await alertStore.getAlertsHistory(currentPage.value, pageSize.value, filters.value)
}
const updatePageSize = async (size: number) => {
  pageSize.value = size;
  alerts.value = await alertStore.getAlertsHistory(currentPage.value, pageSize.value, filters.value)
}
const updateHovered = (data: AlertDetails | null) => {
  hoveredAlert.value = data
}

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
          <AlertsFilters>
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
        :alerts="alerts"
        @update:hovered-alert="updateHovered"
      >
        <MyServerPagination
          :total="alerts.length"
          @update:current-page="updateCurrentPage"
          @update:page-size="updatePageSize"
        />
      </AlertsHistoryTable>
    </div>
  </div>
</template>
