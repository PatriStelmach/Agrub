<script setup lang="ts">
import DataTable from "@/pages/dashboard/DataTable.vue"
import SiteHeader from "@/pages/dashboard/SiteHeader.vue"
import {dashboardData} from "@/data/dashboardData.ts"
import {computed, ref, watch} from "vue";
import MyPagination from "@/helpers/MyPagination.vue";
import StatsChart from "@/pages/dashboard/StatsChart.vue";
import type {Plugin} from "@/types/plugin.ts";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuItem, DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {
  IconBellCog, IconChevronDown,
  IconEyeCog,
  IconLayoutColumns,
  IconLock,
  IconStatusChange, IconUser
} from "@tabler/icons-vue";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import type {Alert} from "@/types/alert";
import type {Paginable} from "@/types/paginable.ts";

const currentPage = ref(1)

const searchFilter = ref()


const rowsData = ref(dashboardData)

watch(searchFilter, () =>
{
  currentPage.value = 1
})

const updateData = (data: Paginable[]) =>
{
  rowsData.value = data as Alert[]
}

const updatePage = (page: number) =>
{
  currentPage.value = page
}

const filteredData = computed(() =>
{
  if(!searchFilter.value)
  {
    return dashboardData
  }
  return dashboardData.filter((item) =>
    item.header.toLowerCase().includes(searchFilter.value.toLowerCase()))
})

</script>

<template>

  <div>
      <SiteHeader class="py-2" />
    <div class="flex items-center justify-between px-4 lg:px-6">
      <div class="flex items-center gap-2">
        <ButtonGroup>
          <Button variant="outline" size="icon" aria-label="Go Back">
            <ArrowLeftIcon />
          </Button>
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button class="text-chart-2 hover:bg-chart-2/70!" variant="outline">
                <IconLayoutColumns />
                Customize Columns
                <IconChevronDown />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent class="w-[10vw]">
              <template v-for="column in table.getAllColumns().filter((column) => typeof column.accessorFn !== 'undefined' && column.getCanHide())" :key="column.id">
                <DropdownMenuCheckboxItem
                  class="capitalize"
                  :model-value="column.getIsVisible()"
                  @update:model-value="(value) => {

                  column.toggleVisibility(value)
                }"
                >
                  {{ column.id }}
                </DropdownMenuCheckboxItem>
              </template>
            </DropdownMenuContent>
          </DropdownMenu>
          <DropdownMenu class="z-9999">
            <DropdownMenuTrigger as-child>
              <Button variant="outline" class="text-chart-3 hover:bg-chart-3/80!">
                <IconBellCog/>
                Alert actions
                <IconChevronDown/>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent class="w-[10vw] z-99999">
              <DropdownMenuItem>
                <IconEyeCog/>Change priority
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconUser/>Change technician
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconStatusChange/>Change status
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconLock/>Close
              </DropdownMenuItem>

            </DropdownMenuContent>
          </DropdownMenu>
          <InputGroup class="relative l-[30vw] w-[20vw]  " >
            <InputGroupInput
              v-model="searchFilter"
              type="search"
              placeholder="Search for alert"/>
            <InputGroupAddon>
              <Search/>
            </InputGroupAddon>
          </InputGroup>
        </ButtonGroup>
      </div>
    </div>
      <div class="flex flex-col  my-4">
            <div class="px-4 lg:px-6">
              <DataTable :data="rowsData" class="z-0"/>


              <MyPagination
                :data="filteredData"
                :page="currentPage"
                @update:paginated-data="updateData"
                @update:pages="updatePage"
              />
        </div>
      </div>
    <StatsChart class="my-[4vh] mx-auto w-[60vw] h-[50vh]"/>
  </div>
</template>
