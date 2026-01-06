<script setup lang="ts">
import MyPluginsTable from "@/pages/plugins/MyPluginsTable.vue";
import {myPluginsData} from "@/data/myPlugins.ts";
import { ArrowLeftIcon, MoreHorizontalIcon } from 'lucide-vue-next'
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
  IconPlayerPause, IconTrash
} from "@tabler/icons-vue";
import { ref} from "vue";
import {Badge} from "@/components/ui/badge";
import MyPagination from "@/helpers/MyPagination.vue";

const currentPage = ref(1)
const itemsPerPage = 10

const updatedData = ref()
const update = (data:any[]) =>
{
  updatedData.value = data
}

</script>

<template>
  <div>
    <ButtonGroup class="ml-[2vw] mt-[2vh] ">
      <ButtonGroup class="hidden sm:flex">
        <Button variant="outline" size="icon" aria-label="Go Back">
          <ArrowLeftIcon />
        </Button>
      </ButtonGroup>
      <ButtonGroup>
        <Button class="text-green-500 hover:bg-green-600!" variant="outline">
          Edit
          <component :is="IconPencilCode"/>
        </Button>
        <Button class="text-yellow-500 hover:bg-yellow-600!" variant="outline">
          Turn off
          <component :is="IconPlayerPause"/>
        </Button>
        <Button class="text-destructive hover:bg-destructive/70!" variant="outline">
          Delete
        <component :is="IconTrash"/>
        </Button>
      </ButtonGroup>
      <ButtonGroup>
        <Badge>Add</Badge>
        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <Button variant="outline" size="icon" aria-label="More Options"><MoreHorizontalIcon/></Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" class="w-52">
            <DropdownMenuLabel class="border-b">Plugins</DropdownMenuLabel>
            <DropdownMenuGroup>
              <DropdownMenuItem>
                <component :is="IconFileImport"/>Import</DropdownMenuItem>
              <DropdownMenuItem>
                <component :is="IconTerminal2"/>Create</DropdownMenuItem>
              <DropdownMenuItem>
                <component :is="IconDatabase"/>Search for plugins</DropdownMenuItem>
            </DropdownMenuGroup>
            <DropdownMenuSeparator/>
            <DropdownMenuGroup>
              <DropdownMenuLabel class=" border-b">Tags</DropdownMenuLabel>
              <DropdownMenuItem>
                <component :is="IconPencilCode"/>Create</DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
      </ButtonGroup>
    </ButtonGroup>

    <MyPluginsTable :data="updatedData"/>

    <MyPagination
      :items-per-page="itemsPerPage"
      :data="myPluginsData"
      :current-page="currentPage"
      @update:paginated-data="update"/>
  </div>

</template>
