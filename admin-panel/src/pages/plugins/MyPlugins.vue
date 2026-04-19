<script setup lang="ts" >
import MyPluginsTable from "@/pages/plugins/MyPluginsTable.vue";
import {myPluginsData} from "@/data/myPlugins.ts";
import {ArrowLeftIcon, Search} from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'

import
{
  IconFileImport,
  IconDatabase,
  IconTerminal2,
  IconPencilCode,
  IconPlayerPause, IconTrash, IconPlus
} from "@tabler/icons-vue";
import {computed, ref, watch} from "vue";
import {Badge} from "@/components/ui/badge";
import MyPagination from "@/helpers/MyPagination.vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import type { MyPlugin } from "@/types/types.ts";
import MyPluginsActions from "@/pages/plugins/MyPluginsActions.vue";

const currentPage = ref<number>(1)
const searchFilter = ref('')
const rowsData = ref(myPluginsData)
const checkedPluginsIds = ref<number[]>()

const filteredData = computed(() => {
  if(!searchFilter.value) {
    return myPluginsData;
  }
  return myPluginsData.filter((item: MyPlugin) =>
    item.name.toLowerCase().includes(searchFilter.value.toLowerCase())) ;
})

watch(searchFilter, () => {
  currentPage.value = 1
})

const updateData = (data:MyPlugin[]) => {
  rowsData.value = data as MyPlugin[]
}
const updateSearchData = (data: string) => {
  searchFilter.value = data.trim()

}
const updatePage = (page: number) => {
  currentPage.value = page
}

const checkedPlugins = (plugins:number[]) => {
  checkedPluginsIds.value = plugins
}

</script>

<template>
  <div>
    <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh] max-h-[5vh] ">Your plugins</h1>
  <div>
    <MyPluginsTable
      :data="rowsData"
      @update:checked="checkedPlugins"
      @update:searchData="updateSearchData"
    />

    <MyPagination
      :data="filteredData"
      :page="currentPage"
      @update:paginated-data="updateData"
      @update:pages="updatePage"
    />
  </div>
</div>
</template>
