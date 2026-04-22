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

const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage, searchFilter } =
  useSearchFilter<LibraryPlugin>(() => pluginLibraryData,(item) => item.name)

</script>

<template>
  <div>
    <div class="relative max-h-[10vh] items-center align-middle">
      <div class="absolute left-4 top-0">

        <ButtonGroup >
          <ButtonGroup>
            <Button variant="outline" size="icon" aria-label="Go Back">
              <ArrowLeftIcon />
            </Button>

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

      </div>
        <h1 class="text-center my-[2vh] text-xl xl:text-2xl 2xl:text-4xl border-b pb-[2vh]">Plugins library</h1>
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

