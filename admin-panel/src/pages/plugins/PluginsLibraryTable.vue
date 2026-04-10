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
import type {Plugin} from "@/types/types.ts"
import {watch} from "vue";

const props = defineProps<{
  data: Plugin[]
}>()



</script>

<template>

  <Table
    class="mx-[2%] w-96/100 my-[1vh]">
    <TableCaption class="border-b border-t py-[1vh]">List of plugins made by users</TableCaption>
    <TableHeader>
      <TableRow class="bg-secondary hover:bg-secondary">
        <TableHead></TableHead>
        <TableHead class="p-4">Name</TableHead>
        <TableHead class="p-4">Creator</TableHead>
        <TableHead class="p-4">Tags</TableHead>
        <TableHead class="p-4">Language</TableHead>
        <TableHead class="p-4">Created at</TableHead>
        <TableHead class="p-4">Weight</TableHead>
      </TableRow>
    </TableHeader>
    <TableBody>
      <TableRow
        class=" cursor-grab duration-0 hover:bg-blue-700/30"
        v-for="plugin in data"
        :key="plugin.id">
        <TableCell class="px-4 ">
          <input
            type="checkbox"
            :id="cn('plugin-no-'+plugin.id)" class="size-[1vw] cursor-pointer"/>
        </TableCell>
        <TableCell class="p-4">{{plugin.name}}</TableCell>
        <TableCell class="p-4">{{plugin.creator}}</TableCell>
        <TableCell class="p-4">
          <Badge
            v-for="(tag, index) in plugin.tags"
            class="cursor-pointer hover:bg-chart-1 mx-1"
            variant="secondary"
            :key="index">{{tag}}</Badge>
        </TableCell>
        <TableCell class="p-4">
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
        <DateCell class="p-4" :date="plugin.createdAt as Date"></DateCell>
        <TableCell class="p-4">{{plugin.weight}} Kb</TableCell>
      </TableRow>
    </TableBody>
    <TableFooter>
    </TableFooter>
  </Table>
</template>
