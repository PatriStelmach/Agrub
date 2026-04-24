<script setup lang="ts">
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
import {Language, type LibraryPlugin, type MyPlugin} from "@/types/types.ts"
import SortableHead from "@/helpers/SortableHead.vue";
import {useSort} from "@/composables/sorting.ts";
import {IconAlertTriangleFilled, IconLogs} from "@tabler/icons-vue";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import { IconDownload, IconSourceCode} from "@tabler/icons-vue";

const props = defineProps<{
  data: LibraryPlugin[]
}>()

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<LibraryPlugin>(() => props.data, 'createdAt')


</script>

<template>

  <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[77vh]   ">
    <Table id="plugins-library-table" class="w-99/100 text-md lg:text-lg xl:text-xl 2xl:text:3xl  mx-auto  table-fixed">
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9 py-2 bottom-0  text-md lg:text-lg xl:text-xl 2xl:text:3xl">Current Alerts:
        <span class="font-extrabold">{{ props.data.length}}</span>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow class="bg-secondary hover:bg-secondary **:text-md! *: **:lg:text-lg! **:xl:text-xl! **:2xl:text-3xl!">
          <SortableHead keyName="name" label="Name" :sort-key="sortKey" class="pl-4 w-21/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="creator" label="Creator" :sort-key="sortKey" class=" w-15/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-20/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="log" label="Type" :sort-key="sortKey" class=" w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Date" :sort-key="sortKey" class=" w-15/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <TableHead class="w-7/100"></TableHead>

        </TableRow>
    </TableHeader>
      <TransitionGroup tag="tbody" name="slide-fade">
        <TableRow
          class=" cursor-pointer duration-0 hover:bg-blue-700/30"
          v-for="plugin in sortedData"
          :key="plugin.id">
          <TableCell class="pl-4 ">{{plugin.name}}</TableCell>
          <TableCell class="">{{plugin.creator}}</TableCell>
          <TableCell class="whitespace-break-spaces">
            <Badge
              v-for="(tag, index) in plugin.tags"
              variant="tags"
              :key="index">{{tag}}</Badge>
          </TableCell>
          <TableCell>
            <component class="text-badge size-7 lg:size-8 xl:size-10 2xl:size-16" :class="{'text-yellow-500' : !plugin.log }" :is="plugin.log ? IconLogs : IconAlertTriangleFilled "/>
          </TableCell>
          <TableCell class="">
            <img
              v-if="plugin.language === Language.PYTHON"
              alt="python_icon"
              src="@/components/icons/python_icon.png"
              class="size-7 lg:size-8 xl:size-10 2xl:size-16"
            />
            <img
              v-if="[Language.BASH, Language.SH].includes(plugin.language)"
              alt="bash_icon"
              src="@/components/icons/bash_icon.png"
              class="size-7 lg:size-8 xl:size-10 2xl:size-16"
            />
            <img
              v-if="[Language.POWERSHELL, Language.POWERSHELL_MODULE].includes(plugin.language)"
              alt="powershell_icon"
              src="@/components/icons/powershell_icon.png"
              class="size-7 lg:size-8 xl:size-10 2xl:size-16"
            />
          </TableCell>
          <DateCell class="" :date="plugin.createdAt as Date"></DateCell>
          <TableCell class="">{{plugin.weight}} Kb</TableCell>
          <TableCell>
            <ButtonGroup>
              <Button
                variant="orange_outline"
              >
                <IconSourceCode/>
              </Button>
              <Button
                variant="green_outline"
                class="border-l!"
              >
                <IconDownload/>
              </Button>
            </ButtonGroup>
          </TableCell>
        </TableRow>
      </TransitionGroup>
    <TableFooter>
    </TableFooter>
  </Table>
  </div>
</template>
