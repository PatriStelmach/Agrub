<script setup lang="ts" >
import MyPluginsTable from "@/pages/plugins/my/MyPluginsTable.vue";
import type { MyPlugin } from "@/types/types.js";
import {useClientSearchFilter} from "@/composables/useClientSearchFilter.ts";
import ClientPagination from "@/helpers_components/ClientPagination.vue";
import {onMounted, ref} from "vue";
import {
  getAllMyPluginsRequest,
  getPluginTagsRequest
} from "@/helpers_functions/requests/pluginsRequests.ts";
import { toast } from "vue-sonner";

const allMyPlugins = ref<MyPlugin[]>([]);
const isLoading = ref<boolean>(true)
const tags = ref<string[]>([])

onMounted(async () => {
    await Promise.all([
      getAllMyPluginsRequest()
        .then(res => allMyPlugins.value = res)
        .catch(error => toast.error(`Error fetching plugins: ${error.message}`)),
      getPluginTagsRequest()
        .then(res => tags.value = res)
        .catch(error => toast.error(`Error fetching plugins tags: ${error.message}`))
    ]).finally(() => isLoading.value = false)
})

const {filteredData, tableData, updateData, updateSearchData, currentPage, pageSize } =
  useClientSearchFilter<MyPlugin>(() => allMyPlugins.value,(plugin) => plugin.name)

</script>

<template>
  <div>
    <MyPluginsTable
      v-model:allMyPlugins="allMyPlugins"
      :isLoading="isLoading"
      :data="tableData"
      :availableTags="tags"
      @update:searchData="updateSearchData"
    >
      <ClientPagination
        :total="allMyPlugins.length"
        :data="filteredData"
        v-model:page-index="currentPage"
        v-model:page-size="pageSize"
        @update:paginatedData="updateData"
      />
    </MyPluginsTable>
  </div>
</template>
