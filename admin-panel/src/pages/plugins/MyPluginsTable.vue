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
import type {Plugin} from "@/types/plugin.ts"

const props = defineProps<{
  data: Plugin[]
}>()

</script>

<template>
  <div class=" mt-[2vh] flex mx-auto w-full">
    <Table class="w-9/10 mx-auto my-[1vh]">
      <TableCaption class="border-b border-t py-[1vh]">List of your plugins</TableCaption>
        <TableHeader>
          <TableRow class="bg-secondary hover:bg-secondary">
            <TableHead></TableHead>
            <TableHead class="p-4">Id</TableHead>
            <TableHead class="p-4">Name</TableHead>
            <TableHead class="p-4">Creator</TableHead>
            <TableHead class="p-4">Tags</TableHead>
            <TableHead class="p-4">Intervals</TableHead>
            <TableHead class="p-4">Language</TableHead>
            <TableHead class="p-4">Added at</TableHead>
            <TableHead class="p-4">Last modified at</TableHead>
            <TableHead class="p-4">Status</TableHead>
            <TableHead class="p-4">Weight</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            onclick=""
            class="cursor-grab duration-0 hover:bg-green-400/30"
            v-for="plugin in props.data"
            :key="plugin.id"
            :class="{'hover:bg-red-400/30': !plugin.on}">
            <TableCell><Checkbox :id="cn('my-plugin-no-'+plugin.id)" class="size-[1vw] cursor-pointer"></Checkbox></TableCell>
            <TableCell class="p-4">{{plugin.id}}</TableCell>
            <TableCell class="p-4">{{plugin.name}}</TableCell>
            <TableCell class="p-4">{{plugin.creator}}</TableCell>
            <TableCell class="p-4">
              <Badge
                v-for="(tag, index) in plugin.tags"
                class="cursor-pointer hover:bg-chart-1 mx-1"
                variant="secondary"
                :key="index"
              >{{tag}}</Badge>
            </TableCell>
            <TableCell class="p-4">{{plugin.runningIntervals}}</TableCell>
            <TableCell class="p-4">{{plugin.language}}</TableCell>
            <DateCell v-if="plugin.addedAt" class="p-4" :date="plugin.addedAt "></DateCell>
            <DateCell v-if="plugin.updatedAt" class="p-4" :date="plugin.updatedAt"></DateCell>
            <TableCell v-if="plugin.on" class="p-4 text-green-500">On</TableCell>
            <TableCell v-else class="p-4 text-red-500">Off</TableCell>
            <TableCell class="p-4">{{plugin.weight}} KB</TableCell>
          </TableRow>
        </TableBody>
      <TableFooter>
      </TableFooter>
      </Table>
    </div>
</template>
