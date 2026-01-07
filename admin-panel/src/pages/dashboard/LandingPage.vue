<script setup lang="ts">
import DataTable from "@/pages/dashboard/DataTable.vue"
import SiteHeader from "@/pages/navbar/SiteHeader.vue"
import {dashboardData} from "@/data/dashboardData.ts"
import {computed, ref} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";

const currentPage = ref(1)

const searchFilter = ref()


const rowsData = ref(dashboardData)

const update = (data:any[]) =>
{
  rowsData.value = data
}
const filteredData = computed(() =>
{
  if(!searchFilter.value)
  {
    return rowsData.value
  }
  return dashboardData.filter((item) =>
  {
    item.technician.toLowerCase().includes(searchFilter.value.toLowerCase())
  })
})
</script>

<template>

  <div>
      <SiteHeader class="py-2" />
      <div class="flex flex-col  my-4">
            <div class="px-4 lg:px-6">
              <DataTable :data="rowsData" />


              <MyPagination
                :data="dashboardData"
                :current-page="currentPage"
                @update:paginated-data="update"/>
        </div>
      </div>
  </div>
</template>
