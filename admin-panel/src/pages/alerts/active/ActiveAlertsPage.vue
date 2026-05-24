<script setup lang="ts" >
import {type ActiveAlert, type AlertDetails} from "@/types/types.js";
import {Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";

import {useClientSearchFilter} from "@/composables/useClientSearchFilter";
import { useAlertStore } from "@/stores/alertStore";
import { onMounted, ref} from "vue";
import {
  tableDiv,
} from "@/assets/cssFunctions.js";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import ActiveAlertsTable from "@/pages/alerts/active/ActiveAlertsTable.vue";
import MyClientPagination from "@/helpers_components/MyClientPagination.vue";
import DetailsCard from '@/pages/alerts/DetailsCard.vue'

const isLoading = ref(true)
const alertStore = useAlertStore();
onMounted(async () => {
  await alertStore.getCurrentAlertsRequest().finally(() => isLoading.value = false);
})
const hoveredAlert = ref<AlertDetails | null>(null)
const {pageSize, filteredData, updateData, currentPage, searchFilter, tableData } =
  useClientSearchFilter<ActiveAlert>(() => alertStore.currentAlerts,(item) => item.subject)

</script>

<template>
  <div>
    <TopH1Div h1="Alerts dashboard">
      <ButtonGroup>
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
    </TopH1Div>
    <div :class="tableDiv">
      <DetailsCard
        v-if="hoveredAlert"
        :data=hoveredAlert
      />
    <ActiveAlertsTable
      :isLoading="isLoading"
      :tableData="tableData"
      v-model:activeAlerts="alertStore.currentAlerts"
      v-model:hoveredAlert="hoveredAlert"
    >
      <MyClientPagination
        :total="alertStore.currentAlerts.length"
        :data="filteredData"
        v-model:page-index="currentPage"
        v-model:page-size="pageSize"
        @update:paginatedData="updateData"
      />
    </ActiveAlertsTable>
    </div>
  </div>
</template>
