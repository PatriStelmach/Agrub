<script setup lang="ts" >
import MyPluginsTable from "@/pages/plugins/MyPluginsTable.vue";
import MyServerPagination from "@/helpers/MyServerPagination.vue";
import type { MyPlugin } from "@/types/types.ts";
import {useSearchFilter} from "@/composables/useSearchFilter.ts";
import {useMyPluginStore} from "@/stores/myPluginStore.ts";
import MyClientPagination from "@/helpers/MyClientPagination.vue";

const myPluginStore = useMyPluginStore()
myPluginStore.getAllMyPlugins()

const {filteredData, tableData, updateData, updateSearchData, currentPage, pageSize } =
  useSearchFilter<MyPlugin>(() => myPluginStore.allMyPlugins,(plugin) => plugin.name)


</script>

<template>
  <div>

  <div>
    <MyPluginsTable
      :data="tableData"
      @update:searchData="updateSearchData"
    >
      <MyClientPagination
        :data="filteredData"
        v-model:page-index="currentPage"
        v-model:page-size="pageSize"
        @update:paginatedData="updateData"
      />
    </MyPluginsTable>


  </div>
</div>
</template>
