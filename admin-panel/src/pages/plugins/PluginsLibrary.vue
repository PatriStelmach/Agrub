<script setup lang="ts">
import {pluginLibraryData} from "@/data/pluginLibrary.ts";
import {computed, ref, watch} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";
import PluginsLibraryTable from "@/pages/plugins/PluginsLibraryTable.vue";
import {
  IconFileImport,
  IconPencilCode,

} from "@tabler/icons-vue";

import {ArrowLeftIcon, MoreHorizontalIcon} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import type {MyPlugin} from "@/types/my.plugin.ts";

const itemsPerPage = 10;
const currentPage = ref(1)
const searchFilter = ref()


const rowsData = ref()

const update = (data:any[]) =>
{
  rowsData.value = data
}
const filteredData = computed(() =>
{
  if(!searchFilter.value)
  {
    return rowsData.value
  }
  return rowsData.value
    .filter((item:MyPlugin) => item.name.toLowerCase().includes(searchFilter.value.toLowerCase()))
})

</script>

<template>
  <div class="  mx-auto w-full">
    <div class="flex ml-[2vw] my-[2vh] ">
    <ButtonGroup>

      <ButtonGroup class="hidden sm:flex">
        <Button variant="outline" size="icon" aria-label="Go Back">
          <ArrowLeftIcon />
        </Button>
        <Button class="text-green-600 hover:bg-green-700!" variant="outline">
          Add to your plugins
          <component :is="IconFileImport"/>
        </Button>
      </ButtonGroup>
    </ButtonGroup>

    <Input
      v-model="searchFilter"
      class="absolute left-[40vw] w-[20vw]" type="search"></Input>

    </div>
<PluginsLibraryTable :data="filteredData" />

    <MyPagination
      :items-per-page="itemsPerPage"
      :data="pluginLibraryData"
      :current-page="currentPage"
      @update:paginated-data="update"/>
  </div>
</template>

