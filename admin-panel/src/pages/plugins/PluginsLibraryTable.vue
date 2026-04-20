<script setup lang="ts">

import {cn} from "@/lib/utils.ts";
import DateCell from "@/helpers/DateCell.vue";
import {
  Table,
  TableBody,
  TableCaption, TableCell,
  TableFooter,
  TableHead,
  TableHeader, TableRow
} from "@/components/ui/table";
import {Badge} from "@/components/ui/badge";
import {Checkbox} from "@/components/ui/checkbox";
import type {LibraryPlugin, MyPlugin} from "@/types/types.ts"
import {watch} from "vue";
import SortableHead from "@/helpers/SortableHead.vue";
import {useSort} from "@/composables/sorting.ts";
import {dashboardData} from "@/data/dashboardData.ts";
import {IconAlertTriangleFilled, IconLogs} from "@tabler/icons-vue";

const props = defineProps<{
  data: LibraryPlugin[]
}>()

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<LibraryPlugin>(() => props.data, 'createdAt')


</script>

<template>

  <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[75vh]   ">
    <Table id="alert-table" class="w-99/100 text-md xl:text-xl 2xl:text-4xl  mx-auto  table-fixed">
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl">Current Alerts:
        <span class="font-extrabold">{{ props.data.length}}</span>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow class="bg-secondary hover:bg-secondary **:text-md! *:py-4 **:lg:text-xl! **:xl:text-2xl! **:2xl:text-4xl!">
        <SortableHead keyName="name" label="Name" :sort-key="sortKey" class="pl-4 w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="creator" label="Creator" :sort-key="sortKey" class=" w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-17/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="log" label="Type" :sort-key="sortKey" class=" w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="createdAt" label="Date" :sort-key="sortKey" class=" w-14/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
      </TableRow>
    </TableHeader>
    <TableBody>
      <TableRow
        class=" cursor-grab duration-0 hover:bg-blue-700/30"
        v-for="plugin in sortedData"
        :key="plugin.id">
        <TableCell class="pl-4 y-2">{{plugin.name}}</TableCell>
        <TableCell class="py-2">{{plugin.creator}}</TableCell>
        <TableCell class="py-2">
          <Badge
            v-for="(tag, index) in plugin.tags"
            class="cursor-pointer hover:bg-badge mr-1 mb-2  text-sm lg:text-lg xl:text-xl 2xl:text-3xl"
            variant="secondary"
            :key="index">{{tag}}</Badge>
        </TableCell>
        <TableCell>
          <component class="text-badge size-7 lg:size-8 xl:size-10 2xl:size-16" :class="{'text-yellow-500' : !plugin.log }" :is="plugin.log ? IconLogs : IconAlertTriangleFilled "/>
        </TableCell>
        <TableCell class="py-2">
          <img
            v-if="plugin.language === 'python'"
            alt="python_icon"
            src="@/components/icons/python_icon.png"
            class="size-6 lg:size-7 xl:size-8 2x:size-9"
          />
          <img
            v-if="plugin.language === 'bash'"
            alt="bash_icon"
            src="@/components/icons/bash_icon.png"
            class="size-6 lg:size-7 xl:size-8 2x:size-9"
          />
          <img
            v-if="plugin.language === 'PowerShell'"
            alt="powershell_icon"
            src="@/components/icons/powershell_icon.png"
            class="size-6 lg:size-7 xl:size-8 2x:size-9"
          />
        </TableCell>
        <DateCell class="py-2" :date="plugin.createdAt as Date"></DateCell>
        <TableCell class="py-2">{{plugin.weight}} Kb</TableCell>
      </TableRow>
    </TableBody>
    <TableFooter>
    </TableFooter>
  </Table>
  </div>
</template>
