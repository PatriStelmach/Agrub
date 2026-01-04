<script setup lang="ts">
import
{
  Table,
  TableBody,
  TableHeader,
  TableRow,
  TableCell,
  TableFooter,
  TableHead,
  TableCaption
} from "@/components/ui/table";
import DateCell from "@/helpers/DateCell.vue";
import {Checkbox} from "@/components/ui/checkbox";
import {cn} from "@/lib/utils.ts";
import {Badge} from "@/components/ui/badge";

const props = defineProps<{
  data: Plugins[]
}>()

export interface Plugins
{
  id: number,
  name: string,
  runningIntervals: string,
  createdAt: Date,
  updatedAt: Date,
  on: boolean,
  tags: string[]
}



</script>

<template>
  <div class=" mt-[2vh] flex mx-auto w-full">
    <Table class="w-9/10 mx-auto my-[1vh]">
      <TableCaption class="border-b border-t py-4">List of your plugins</TableCaption>
        <TableHeader>
          <TableRow>
            <TableHead></TableHead>
            <TableHead class="p-4">Id</TableHead>
            <TableHead class="p-4">Name</TableHead>
            <TableHead class="p-4">Tags</TableHead>
            <TableHead class="p-4">Intervals</TableHead>
            <TableHead class="p-4">Created at</TableHead>
            <TableHead class="p-4">Last modified at</TableHead>
            <TableHead class="p-4">Status</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            onclick=""
            class="cursor-grab duration-0 hover:bg-green-400/10"
            v-for="plugin in props.data"
            :key="plugin.id"
            :class="{'hover:bg-red-400/10': !plugin.on}">
            <Checkbox :id="cn('my-plugin-no-'+plugin.id)" class="size-[1vw] cursor-pointer my-[2vh]"></Checkbox>
            <TableCell class="p-4">{{plugin.id}}</TableCell>
            <TableCell class="p-4">{{plugin.name}}</TableCell>
            <TableCell class="p-4">
              <Badge  v-for="tag in plugin.tags" class="cursor-pointer hover:bg-sidebar-primary mx-1" variant="secondary">{{tag}}</Badge>
            </TableCell>
            <TableCell class="p-4">{{plugin.runningIntervals}}</TableCell>
            <DateCell class="p-4" :date="plugin.createdAt"></DateCell>
            <DateCell class="p-4" :date="plugin.updatedAt"></DateCell>
            <TableCell v-if="plugin.on" class="p-4 text-green-500">On</TableCell>
            <TableCell v-else class="p-4 text-red-500">Off</TableCell>
          </TableRow>
        </TableBody>
      <TableFooter>
      </TableFooter>
      </Table>
    </div>
</template>
