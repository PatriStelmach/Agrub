<script setup lang="ts" >
import {type ActiveAlert, type AlertDetails} from "@/types/types.js";
import {Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";

import {useClientSearchFilter} from "@/composables/useClientSearchFilter";
import { useAlertStore } from "@/stores/alertStore";
import {computed, defineAsyncComponent, onMounted, ref} from "vue";
import {
  tableDiv,
  topH1,
  topDiv, topButtonGroup
} from "@/assets/cssFunctions.js";
import GoBackButton from "@/helpers/GoBackButton.vue";
import ActiveAlertsTable from "@/pages/alerts/active/ActiveAlertsTable.vue";
import MyClientPagination from "@/helpers/MyClientPagination.vue";
const DetailsCard = defineAsyncComponent(() => import('@/pages/alerts/DetailsCard.vue'))

const alertStore = useAlertStore();
onMounted(() => {
  alertStore.getCurrentAlertsRequest()
})
const hoveredAlert = ref<AlertDetails | null>(null)
const {pageSize, filteredData, updateData, currentPage, searchFilter, tableData } =
  useClientSearchFilter<ActiveAlert>(() => alertStore.currentAlerts,(item) => item.subject)

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
                placeholder="Search..."/>
              <InputGroupAddon>
                <Search/>
              </InputGroupAddon>
            </InputGroup>
        </ButtonGroup>
    </div>
    <div :class="tableDiv">
      <DetailsCard
        v-if="hoveredAlert"
        :data=hoveredAlert
      />
    <ActiveAlertsTable
      :tableData="tableData"
      v-model:activeAlerts="alertStore.currentAlerts"
      v-model:hoveredAlert="hoveredAlert"
    >
      <MyClientPagination
        :data="filteredData"
        v-model:page-index="currentPage"
        v-model:page-size="pageSize"
        @update:paginatedData="updateData"
      />
    </ActiveAlertsTable>
    </div>
  </div>
</template>
