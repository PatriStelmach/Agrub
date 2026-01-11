<script setup lang="ts">
import {pluginLibraryData} from "@/data/pluginLibrary.ts";
import {computed, ref, watch} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";
import PluginsLibraryTable from "@/pages/plugins/PluginsLibraryTable.vue";
import {
  IconFileImport,
  IconPencilCode,

} from "@tabler/icons-vue";

import {ArrowLeftIcon, MoreHorizontalIcon, Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import type {MyPlugin} from "@/types/my.plugin.ts";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";

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

  return pluginLibraryData.filter((item) =>
    item.name.toLowerCase().includes(searchFilter.value.toLowerCase()))
})

const sort = ref<'id' | 'name' | 'creator' | 'createdAt' | 'language' | 'weight' | 'tags'>('id');



</script>

<template>
  <div>
    <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh] font-mono md ">Plugins library</h1>

  <div class="  mx-auto w-full">
    <div class="flex ml-[2vw] my-[2vh] ">


      <ButtonGroup class="hidden sm:flex">
        <Button variant="outline" size="icon" aria-label="Go Back">
          <ArrowLeftIcon />
        </Button>
        <Button class="text-green-600 hover:bg-green-700!" variant="outline">
          Add to your plugins
          <component :is="IconFileImport"/>
        </Button>

      <InputGroup class="relative l-[30vw] w-[20vw]  " >
        <InputGroupInput
          v-model="searchFilter"
          type="search"
          placeholder="Search for plugin"/>
        <InputGroupAddon>
          <Search/>
        </InputGroupAddon>
      </InputGroup>
    </ButtonGroup>

    </div>
<PluginsLibraryTable :data="filteredData" />

    <MyPagination
      :data="pluginLibraryData"
      :current-page="currentPage"
      @update:paginated-data="update"/>
  </div>
  </div>
</template>

