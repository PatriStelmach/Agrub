<script setup lang="ts" >
import {type ActiveAlert, type AlertDetails} from "@/types/types.js";
import MyClientPagination from "@/helpers/MyClientPagination.vue";
import {Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";

import {useSearchFilter} from "@/composables/useSearchFilter.js";
import { useAlertStore } from "@/stores/alertStore";
import {computed, defineAsyncComponent, onMounted, ref} from "vue";
import {
  tableDiv,
  topH1,
  topDiv, topButtonGroup
} from "@/assets/cssFunctions.js";
import GoBackButton from "@/helpers/GoBackButton.vue";
import AlertsTable from "@/pages/alerts/active/AlertsTable.vue";

const alertStore = useAlertStore();
onMounted(() => {
  alertStore.getCurrentAlertsRequest()
})

const DetailsCard = defineAsyncComponent(() => import('@/pages/alerts/active/DetailsCard.vue'))
const hoveredAlert = ref<AlertDetails | null>(null)

const { updatePage, filteredData, updateData, currentPage, searchFilter, tableData } =
  useSearchFilter<ActiveAlert>(() => alertStore.getAllCurrentAlerts,(item) => item.subject)

const updateHovered = (data: AlertDetails | null) => {
  hoveredAlert.value = data
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
    <AlertsTable
      :tableData="tableData"
      @update:hovered-alert="updateHovered"
    />

    </div>
    <MyClientPagination
      class="max-h-[5vh] z-99"
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
    />
  </div>

</template>
