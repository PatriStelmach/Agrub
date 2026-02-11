<script setup lang="ts">
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
import type { Plugin } from "@/types/plugin.ts";
import type {Paginable} from "@/types/paginable.ts";

const currentPage = ref<number>(1)
const searchFilter = ref('')
const rowsData = ref(myPluginsData)
const checkedPluginsIds = ref<number[]>()

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
    return myPluginsData;
  }
  return myPluginsData.filter((item) =>
    item.name.toLowerCase().includes(searchFilter.value.toLowerCase())) ;
})

const checkedPlugins = (plugins:number[]) =>
{
  checkedPluginsIds.value = plugins
}

</script>

<template>
  <div>
    <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh]   ">Your plugins</h1>
  <div>
    <div class="flex ml-[2vw] my-[2vh] ">
    <ButtonGroup>
      <ButtonGroup class="hidden sm:flex">
        <Button variant="outline" size="icon" aria-label="Go Back">
          <ArrowLeftIcon />
        </Button>
      </ButtonGroup>
      <ButtonGroup>
        <Button variant="cyan_outline">
          Edit
          <component :is="IconPencilCode"/>
        </Button>
        <Button variant="yellow_outline">
          Turn off
          <component :is="IconPlayerPause"/>
        </Button>
        <Button variant="red_outline">
          Delete
        <component :is="IconTrash"/>
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
      <ButtonGroup>

        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <Button variant="outline" size="icon" aria-label="More Options"><component :is="IconPlus"/></Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" class="w-52">
            <DropdownMenuLabel class="border-b">Plugins</DropdownMenuLabel>
            <DropdownMenuGroup>
              <DropdownMenuItem>
                <RouterLink class="flex w-full gap-x-2" to="/import_plugins">
                <IconFileImport/>Import
                </RouterLink>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconTerminal2/>Create</DropdownMenuItem>
              <DropdownMenuItem>
                  <IconDatabase/>Search for plugins
              </DropdownMenuItem>
            </DropdownMenuGroup>
            <DropdownMenuSeparator/>
            <DropdownMenuGroup>
              <DropdownMenuLabel class=" border-b">Tags</DropdownMenuLabel>
              <DropdownMenuItem>
                <component :is="IconPencilCode"/>Create</DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
        <Badge>Add</Badge>
      </ButtonGroup>
    </ButtonGroup>
  </div>
    <MyPluginsTable
      :data="rowsData"
      @update:checked="checkedPlugins"
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
