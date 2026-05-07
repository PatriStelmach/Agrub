<script setup lang="ts">
import PluginsLibraryTable from "@/pages/plugins/PluginsLibraryTable.vue";
import {
  IconFilterCog,
} from "@tabler/icons-vue";

import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {
  type LibraryPlugin,
  type LibraryPluginFilters, undefinedLibraryFilters
} from "@/types/types.ts"
import {tableDiv, topButtonGroup, topDiv, topH1} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers/GoBackButton.vue";
import MyServerPagination from "@/helpers/MyServerPagination.vue";
import {useServerSearchFilter} from "@/composables/useServerSearchFilter.ts";
import api from "@/lib/axios.ts";
import PluginFilters from "@/pages/plugins/PluginFilters.vue";

const getLibraryPluginsRequest = async () => {
  const response = await api.get('/plugins/library', {
    params: {
      name: filters.value.name,
      page: currentPage.value -1,
      language: filters.value.language,
      creator: filters.value.creator,
      pageSize: pageSize.value,
    }
  })
  console.log(`params: {
    name: ${filters.value.name},
      page: ${currentPage.value},
      language: ${filters.value.language},
      creator: ${filters.value.creator},
      pageSize: ${pageSize.value},
  }`);
  if (response.status === 200) {
    items.value = response.data.content
    totalElements.value = response.data.totalElements
  }
}
const {filters, items, pageSize, currentPage, totalElements, sortedHead, updateFilters  } =
  useServerSearchFilter<LibraryPlugin, LibraryPluginFilters>
  (getLibraryPluginsRequest, undefinedLibraryFilters,'createdAt', 'desc')
</script>

<template>
  <div>
    <div :class="topDiv">
      <h1 :class="topH1">Alerts history</h1>
      <ButtonGroup :class="topButtonGroup">
        <ButtonGroup>
          <GoBackButton />
        </ButtonGroup>
        <ButtonGroup>

          <PluginFilters @update:filters="updateFilters">
            <Button variant="outline">
              Filters <IconFilterCog />
            </Button>
          </PluginFilters>
        </ButtonGroup>
      </ButtonGroup>
    </div>
    <div :class="tableDiv">
    <PluginsLibraryTable
      v-model:sorted-head="sortedHead"
      :plugins="items"
      :totalElements="totalElements">
      <MyServerPagination
        :total="totalElements"
        v-model:page-index="currentPage"
        v-model:page-size="pageSize"
      />
    </PluginsLibraryTable>
  </div>
</div>
</template>

