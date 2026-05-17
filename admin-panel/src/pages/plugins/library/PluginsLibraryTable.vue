 <script setup lang="ts">
import DateCell from "@/helpers_components/DateCell.vue";
import {
  Table, TableBody,
  TableCaption, TableCell,
  TableHead,
  TableHeader, TableRow
} from "@/components/ui/table";
import {Badge} from "@/components/ui/badge";
import LanguageImage from "@/helpers_components/LanguageImage.vue";
import {
  type LibraryPlugin
} from "@/types/types.js"
import SortableHead from "@/helpers_components/SortableHead.vue";
import {tableCaption, dataTable, tableHeaders, hoverListRow} from "@/assets/cssFunctions.js";
import {useSort} from "@/composables/sorting.js";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import { IconDownload, IconSourceCode} from "@tabler/icons-vue";
import {ref, watchEffect} from "vue";
import {dateParser} from "@/composables/dateParser.js";
import LoadingTable from "@/helpers_components/LoadingTable.vue";
import {downloadPluginRequest, getPluginDetailsRequest} from "@/helpers_functions/requests.ts";
import {toast} from "vue-sonner";
import PluginDetailsDialog from "@/pages/plugins/PluginDetailsDialog.vue";

const props = defineProps<{
  plugins: LibraryPlugin[]
  totalElements: number
  isLoading: boolean;
}>()

const getDetailsLoading = ref<boolean>(false);

const sortedHead =  defineModel<{ sortKey: string; sortOrder: string }>('sortedHead')
const { sortKey, sortOrder, toggleSort } =
  useSort<LibraryPlugin>(() => props.plugins, 'createdAt')

watchEffect(() => {
  sortedHead.value = { sortKey: sortKey.value, sortOrder: sortOrder.value };
});

const getDetails = async (plugin: LibraryPlugin) => {
  if(plugin.id) {
    getDetailsLoading.value = true
    await getPluginDetailsRequest(plugin.id, 'plugins')
      .then((res) => {
        plugin.code = res?.code
        plugin.description = res?.description
      })
      .catch((err) => toast.error(`Error fetching plugin details: ${err}`))
      .finally(() => getDetailsLoading.value = false)
  }
}

</script>

<template>

    <Table id="plugins-library-table" :class="dataTable">
      <TableCaption :class="tableCaption">
        <slot/>
        <span >Matched plugins: <span class="font-extrabold">{{ totalElements}}</span></span>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow :class="tableHeaders">
          <SortableHead keyName="name" label="Name" :sort-key="sortKey" class=" *:pl-2 w-1/6" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="creator" label="Creator" :sort-key="sortKey" class="w-1/8 lg:w-1/6" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class="w-15/100 lg:w-20/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Date" :sort-key="sortKey" class=" w-11/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <TableHead class="w-7/100 pr-4!"></TableHead>
        </TableRow>
    </TableHeader>
      <Transition name="fade" mode="out-in">
        <LoadingTable :colspan="7" v-if="isLoading"/>
        <TableBody v-else>
          <TableRow
            :class="hoverListRow('cursor-pointer duration-0')"
            v-for="plugin in plugins"
            :key="plugin.id">
            <TableCell class="pl-4 whitespace-break-spaces ">{{plugin.fileName}}</TableCell>
            <TableCell class="whitespace-break-spaces" >{{plugin.creator}}</TableCell>
            <TableCell class="whitespace-break-spaces">
              <Badge
                v-for="(tag, index) in plugin.tags"
                variant="tags"
                :key="index">{{tag}}</Badge>
            </TableCell>
            <TableCell>
              <LanguageImage :language="plugin.language" sizeClass="size-7" />
            </TableCell>
            <DateCell  :date="dateParser(plugin.createdAt).toDate"></DateCell>
            <TableCell >{{plugin.weight}} Kb</TableCell>
            <TableCell>
              <ButtonGroup>
                <PluginDetailsDialog
                  :editable="false"
                  :code="plugin.code ?? ''"
                  :description="plugin.description ?? ''"
                  :is-loading="getDetailsLoading"
                >
                  <Button
                    @click="getDetails(plugin)"
                    variant="orange_outline"
                  >
                    <IconSourceCode/>
                  </Button>
                </PluginDetailsDialog>

                <Button
                  @click="downloadPluginRequest(plugin)"
                  variant="green_outline"
                  class="border-l-2!"
                >
                  <IconDownload/>
                </Button>
              </ButtonGroup>
            </TableCell>
          </TableRow>
        </TableBody>
      </Transition>
  </Table>
</template>
