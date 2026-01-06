<script setup lang="ts">
import DataTable from "@/pages/dashboard/DataTable.vue"
import SiteHeader from "@/pages/navbar/SiteHeader.vue"
import {dashboardData} from "@/data/dashboardData.ts"
</script>

<template>

  <div>
      <SiteHeader class="py-2" />
      <div class="flex flex-col  my-4">
            <div class="px-4 lg:px-6">
              <DataTable :data="dashboardData" />
        </div>
      </div>
  </div>
</template>
