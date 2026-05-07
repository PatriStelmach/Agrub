 <script setup lang="ts">
import DateCell from "@/helpers/DateCell.vue";
import {
  Table,
  TableCaption, TableCell,
  TableFooter,
  TableHead,
  TableHeader, TableRow
} from "@/components/ui/table";
import {Badge} from "@/components/ui/badge";
import {Language, type LibraryPlugin} from "@/types/types.ts"
import SortableHead from "@/helpers/SortableHead.vue";
import {tableCaption, dataTable, tableHeaders} from "@/assets/cssFunctions.ts";
import {useSort} from "@/composables/sorting.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import { IconDownload, IconSourceCode} from "@tabler/icons-vue";
import {watchEffect} from "vue";

const props = defineProps<{
  plugins: LibraryPlugin[]
  totalElements: number
}>()

const sortedHead =  defineModel<{ sortKey: string; sortOrder: string }>('sortedHead')


const { sortKey, sortOrder, toggleSort } =
  useSort<LibraryPlugin>(() => props.plugins, 'createdAt')

watchEffect(() => {
  sortedHead.value = { sortKey: sortKey.value, sortOrder: sortOrder.value };
});

</script>

<template>

    <Table id="plugins-library-table" :class="dataTable">
      <TableCaption :class="tableCaption">
        <slot/>
        <span >Plugins Library: <span class="font-extrabold">{{ totalElements}}</span></span>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow :class="tableHeaders">
          <SortableHead keyName="name" label="Name" :sort-key="sortKey" class="pl-4! w-fit" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="creator" label="Creator" :sort-key="sortKey" class=" w-fit" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-20/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Date" :sort-key="sortKey" class=" w-14/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <TableHead class="w-7/100"></TableHead>

        </TableRow>
    </TableHeader>
      <TransitionGroup name="slide-fade">
        <TableRow
          class=" cursor-pointer duration-0 hover:bg-blue-700/30"
          v-for="plugin in plugins"
          :key="plugin.id">
          <TableCell class="pl-4 ">{{plugin.fileName}}</TableCell>
          <TableCell >{{plugin.creator}}</TableCell>
          <TableCell class="whitespace-break-spaces">
            <Badge
              v-for="(tag, index) in plugin.tags"
              variant="tags"
              :key="index">{{tag}}</Badge>
          </TableCell>
          <TableCell >
            <img
              v-if="plugin.language === Language.PYTHON"
              alt="python_icon"
              src="@/components/icons/python_icon.png"
              class="size-7 "
            />
            <img
              v-if="[Language.BASH, Language.SH].includes(plugin.language)"
              alt="bash_icon"
              src="@/components/icons/bash_icon.png"
              class="size-7 "
            />
            <img
              v-if="[Language.POWERSHELL, Language.POWERSHELL_MODULE].includes(plugin.language)"
              alt="powershell_icon"
              src="@/components/icons/powershell_icon.png"
              class="size-7 "
            />
          </TableCell>
          <DateCell  :date="plugin.createdAt as Date"></DateCell>
          <TableCell >{{plugin.weight}} Kb</TableCell>
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
</template>
