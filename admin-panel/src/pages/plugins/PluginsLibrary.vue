<script setup lang="ts">
import {pluginLibraryData} from "@/data/pluginLibrary.ts";
import MyPagination from "@/helpers/MyPagination.vue";
import PluginsLibraryTable from "@/pages/plugins/PluginsLibraryTable.vue";
import {
  IconMessageCode,
} from "@tabler/icons-vue";

import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import type {LibraryPlugin} from "@/types/types.ts"
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {useSearchFilter} from "@/composables/useSearchFilter.ts";
import {topButtonGroup, topDiv, topH1} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers/GoBackButton.vue";

const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage, searchFilter } =
  useSearchFilter<LibraryPlugin>(() => pluginLibraryData,(item) => item.name)

</script>

<template>
  <div>

    <div :class="topDiv">
      <h1 :class="topH1">Plugins library</h1>
        <ButtonGroup :class="topButtonGroup">
          <ButtonGroup>
            <GoBackButton/>


            <InputGroup  >
              <InputGroupInput
                v-model="searchFilter"
                type="search"
                placeholder="Search for plugin"/>
              <InputGroupAddon>
                <Search/>
              </InputGroupAddon>
            </InputGroup>
          </ButtonGroup>
        </ButtonGroup>


      <div>
    </div>

<PluginsLibraryTable :data="tableData" />

    <MyPagination
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
      />
  </div>
</div>
</template>

