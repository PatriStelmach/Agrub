<script setup lang="ts" >
import MyPluginsTable from "@/pages/plugins/my/MyPluginsTable.vue";
import type { MyPlugin } from "@/types/types.js";
import {useClientSearchFilter} from "@/composables/useClientSearchFilter.js";
import {useMyPluginStore} from "@/stores/myPluginStore.js";
import MyClientPagination from "@/helpers_components/MyClientPagination.vue";
import {onMounted, ref} from "vue";
import {getPluginTagsResponse} from "@/helpers_functions/requests.js";

const myPluginStore = useMyPluginStore()
const isLoading = ref<boolean>(true)
const tags = ref<string[]>([])

onMounted(async () => {
    await Promise.all([
      myPluginStore.getAllMyPluginsRequest(),
      tags.value = await getPluginTagsResponse() || []
    ]).finally(() => isLoading.value = false)

})

const {filteredData, tableData, updateData, updateSearchData, currentPage, pageSize } =
  useClientSearchFilter<MyPlugin>(() => myPluginStore.allMyPlugins,(plugin) => plugin.name)



</script>

<template>
  <div>
    <MyPluginsTable
      :isLoading="isLoading"
      :data="tableData"
      :availableTags="tags"
      @update:searchData="updateSearchData"
    >
      <MyClientPagination
        :total="myPluginStore.allMyPlugins.length"
        :data="filteredData"
        v-model:page-index="currentPage"
        v-model:page-size="pageSize"
        @update:paginatedData="updateData"
      />
    </MyPluginsTable>
  </div>
</template>
