<script setup lang="ts">
import {pluginLibraryData} from "@/data/pluginLibrary.ts";
import {computed, ref, watch} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";
import PluginsLibraryTable from "@/pages/plugins/PluginsLibraryTable.vue";
import {
  IconFileImport,
} from "@tabler/icons-vue";

import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import type {Plugin} from "@/types/plugin.ts"
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import type {Paginable} from "@/types/paginable.ts";

const currentPage = ref<number>(1)
const searchFilter = ref('')
const rowsData = ref<Plugin[]>(pluginLibraryData)


watch(searchFilter, () =>
{
  currentPage.value = 1
})

const updateData = (data:Paginable[]) =>
{
  rowsData.value = data as Plugin[]
}

const updatePage = (page: number) =>
{
  currentPage.value = page
}

const filteredData = computed(() =>
{
  if(!searchFilter.value)
  {
    return pluginLibraryData
  }
  return pluginLibraryData.filter((item) =>
    item.name.toLowerCase().includes(searchFilter.value.toLowerCase()))
})


</script>

<template>
  <div>
    <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh] font-mono md ">Plugins library</h1>
  <div class="mx-auto w-full">

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
<PluginsLibraryTable :data="rowsData" />

    <MyPagination
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
      />
  </div>
  </div>
</template>

