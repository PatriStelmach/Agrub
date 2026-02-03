<script setup lang="ts">
import DataTable from "@/pages/dashboard/DataTable.vue"
import SiteHeader from "@/pages/dashboard/SiteHeader.vue"
import {dashboardData} from "@/data/dashboardData.ts"
import {computed, ref} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";
import StatsChart from "@/pages/dashboard/StatsChart.vue";

const currentPage = ref(1)

const searchFilter = ref()


const rowsData = ref(dashboardData)

</script>

<template>

  <div>
      <SiteHeader class="py-2" />
      <div class="flex flex-col  my-4">
            <div class="px-4 lg:px-6">
              <DataTable :data="rowsData" />


              <MyPagination
                :data="dashboardData"
                :page="currentPage"
                @update:paginated-data="update"/>
        </div>
      </div>
    <StatsChart class="my-[4vh] mx-auto w-[60vw] h-[50vh]"/>
  </div>
</template>
