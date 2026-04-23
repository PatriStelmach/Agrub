<script setup lang="ts">

import {Table, TableCaption, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import SortableHead from "@/helpers/SortableHead.vue";
import {IconListCheck} from "@tabler/icons-vue";
import {useSort} from "@/composables/sorting.ts";
import type {LogObject, Plugin} from "@/types/types.ts";

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<LogObject>(() => , 'updatedAt')

</script>

<template>
  <Table id="my-plugin-table" class="w-99/100 text-md lg:text-lg xl:text-xl 2xl:text:3xl  mx-auto  table-fixed border-collapse border-spacing-0">
    <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9999 bottom-0 py-[1vh] text-md lg:text-lg xl:text-xl 2xl:text:3xl rounded-lg"> Your plugins:
      <span class="font-extrabold">{{ sortedData.length}}</span>
    </TableCaption>
    <TableHeader class="h-10 ">
      <TableRow class="bg-secondary [&_th]:py-2 hover:bg-secondary ">
        <TableHead class="w-3/100  pl-0 pr-4 ">
          <IconListCheck
            class="size-[2vh] mx-3 rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500 hover:scale-105
                  hover:bg-badge1 cursor-pointer transition duration-100"
            :class="{'text-green-500 hover:bg-primary hover:shadow-primary': allChecked }"

            @click="checkAll"
          />
        </TableHead>

        <SortableHead keyName="name" label="Name" :sort-key="sortKey" class=" w-11/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-22/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="cronExpression" label="Intervals" :sort-key="sortKey" class=" w-11/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="type" label="Type" :sort-key="sortKey" class=" w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="updatedAt" label="Last modified" :sort-key="sortKey" class=" w-15/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="lastModifiedBy" label="Modified by" :sort-key="sortKey" class=" w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="on" label="Status" :sort-key="sortKey" class=" w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
      </TableRow>
    </TableHeader>
</template>
