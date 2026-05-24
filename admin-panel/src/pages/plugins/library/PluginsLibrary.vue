<script setup lang="ts">
import PluginsLibraryTable from "@/pages/plugins/library/PluginsLibraryTable.vue";
import {
  IconFilterCog,
} from "@tabler/icons-vue";

import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {
  type LibraryPlugin,
  type LibraryPluginFilters, undefinedLibraryFilters
} from "@/types/types.js"
import {tableDiv} from "@/assets/cssFunctions.js";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import MyServerPagination from "@/helpers_components/MyServerPagination.vue";
import {useServerSearchFilter} from "@/composables/useServerSearchFilter.js";
import api from "@/lib/axios.js";
import PluginFilters from "@/pages/plugins/PluginFilters.vue";
import {onMounted, ref} from "vue";
import {getPluginTagsResponse} from "@/helpers_functions/requests.js";

const tags = ref<string[]>([])
const getLibraryPluginsRequest = async () => {
  const response = await api.get('/plugins/library', {
    params: {
      name: filters.value.name,
      page: currentPage.value -1,
      language: filters.value.language,
      creator: filters.value.creator,
      pageSize: pageSize.value,
      sortKey: sortedHead.value.sortKey,
      sortOrder: sortedHead.value.sortOrder,
      tags: filters.value.tags,
      maxWeight: filters.value.maxWeight
    },
    paramsSerializer: {
      indexes: null
    }
  })

  if (response.status === 200) {
    items.value = response.data.content.map((p: any) => ({
      ...p,
      createdAt: new Date(p.updatedAt),
    }))
    totalElements.value = response.data.totalElements
  }
}

onMounted(async () => {
  await Promise.all([
    getLibraryPluginsRequest(),
    tags.value = await getPluginTagsResponse() || []
  ]).finally(() => isLoading.value = false)
})

const {filters, items, pageSize, currentPage, totalElements, sortedHead, updateFilters, isLoading  } =
  useServerSearchFilter<LibraryPlugin, LibraryPluginFilters>
  (getLibraryPluginsRequest, undefinedLibraryFilters,'createdAt', 'desc')
</script>

<template>
  <div>
    <TopH1Div h1="Plugins library">
      <ButtonGroup>
        <PluginFilters @update:filters="updateFilters">
          <Button variant="blue_outline">
            Filters <IconFilterCog />
          </Button>
        </PluginFilters>
      </ButtonGroup>
    </TopH1Div>
    <div :class="tableDiv">
    <PluginsLibraryTable
      :isLoading="isLoading"
      v-model:sorted-head="sortedHead"
      :plugins="items">
      <MyServerPagination
        :total="totalElements"
        v-model:page-index="currentPage"
        v-model:page-size="pageSize"
      />
    </PluginsLibraryTable>
  </div>
</div>
</template>

