<script setup lang="ts">
import AlertsTable from "@/pages/alerts/AlertsTable.vue"
import {dashboardData} from "@/data/dashboardData.ts"
import {computed, ref, watch} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";
import type {AlertObject} from "@/types/types";
import type {Paginable} from "@/types/types.ts";
import AlertsActions from "@/pages/alerts/AlertsActions.vue";
const currentPage = ref(1)
const searchFilter = ref('')
const rowsData = ref(dashboardData)


watch(searchFilter, () => {
  currentPage.value = 1
})

const filteredData = computed(() => {
  if(!searchFilter.value)
    return dashboardData
  return dashboardData.filter((item: AlertObject) =>
    item.header.toLowerCase().includes(searchFilter.value.toLowerCase()))
})

const updateData = (data: Paginable[]) => {
  rowsData.value = data as AlertObject[]
}

const updatePage = (page: number) => {
  currentPage.value = page
}

const updateSearch = (data: string) => {
  searchFilter.value = data.trim()
}


</script>

<template>
  <div>
    <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh] max-h-[5vh]  ">Dashboard</h1>
    <AlertsActions class="max-h-[20vh]"
      @update:search-data="updateSearch"
    />
    <AlertsTable
      :data="rowsData"
      :all="dashboardData"
    />
    <MyPagination
      class="max-h-[5vh]"
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
    />
  </div>
</template>
