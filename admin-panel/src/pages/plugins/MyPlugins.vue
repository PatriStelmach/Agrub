<script setup lang="ts" >
import MyPluginsTable from "@/pages/plugins/MyPluginsTable.vue";
import MyPagination from "@/helpers/MyPagination.vue";
import type { MyPlugin } from "@/types/types.ts";
import {useSearchFilter} from "@/composables/useSearchFilter.ts";
import {useMyPluginStore} from "@/stores/myPluginStore.ts";
const myPluginStore = useMyPluginStore()
myPluginStore.getAllMyPlugins()
const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage } =
  useSearchFilter<MyPlugin>(() => myPluginStore.allMyPlugins,(plugin) => plugin.name)


</script>

<template>
  <div>

  <div>
    <MyPluginsTable
      :data="tableData"
      @update:searchData="updateSearchData"
    />

    <MyPagination
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
    />
  </div>
</div>
</template>
