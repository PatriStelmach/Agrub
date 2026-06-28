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
  type LibraryPlugin, type PluginDetails
} from "@/types/types.js"
import SortableHead from "@/helpers_components/SortableHead.vue";
import {tableCaption, dataTable, tableHeaders, hoverListRow} from "@/assets/cssFunctions.js";
import {Button} from "@/components/ui/button";
import { IconDownload, IconLoader, IconCode} from "@tabler/icons-vue";
import {ref, watchEffect} from "vue";
import {dateParser} from "@/helpers_functions/dateParser";
import LoadingTable from "@/helpers_components/LoadingTable.vue";
import {downloadPluginRequest, getPluginDetailsRequest} from "@/helpers_functions/requests/pluginsRequests.ts";
import {toast} from "vue-sonner";
import PluginDetailsDialog from "@/pages/plugins/PluginDetailsDialog.vue";
import {useAuthStore} from "@/stores/authStore.ts";
import {useServerSort} from "@/composables/useServerSort.ts";

defineProps<{
  plugins: LibraryPlugin[]
  isLoading: boolean;
}>()

const authStore = useAuthStore()
const getDetailsLoading = ref<boolean>(false)
const isDownloading = ref(false)
const isCodeDialogOpen = ref(false)
const openedPluginDetails = ref<PluginDetails>({ code: '', description: ''})

const sortedHead =  defineModel<{ sortKey: string; sortOrder: string }>('sortedHead')
const { sortKey, sortOrder, toggleSort } =
  useServerSort<LibraryPlugin>('createdAt')

watchEffect(() => {
  sortedHead.value = { sortKey: sortKey.value, sortOrder: sortOrder.value }
})

const getDetails = async (plugin: LibraryPlugin) => {
  if(plugin.id) {
    isCodeDialogOpen.value = true
    getDetailsLoading.value = true
    await getPluginDetailsRequest(plugin.id, 'plugins')
      .then((res) => {
        openedPluginDetails.value.code = res?.code
        openedPluginDetails.value.description = res?.description
      })
      .catch((err) => toast.error(`Error fetching plugin details: ${err.message}`))
      .finally(() => getDetailsLoading.value = false)
  }
}

const downloadPlugin = (plugin: LibraryPlugin) => {
  isDownloading.value = true
  downloadPluginRequest(plugin.id)
    .then(() => toast.success(`Successfully downloaded "${plugin.fileName}"`))
    .catch(error => toast.error(`Error downloading plugin: ${error.message}`))
    .finally(() => isDownloading.value = false)
}

</script>

<template>

  <PluginDetailsDialog
    v-model:isCodeDialogOpen="isCodeDialogOpen"
    :editable="false"
    :code="openedPluginDetails.code ?? ''"
    :description="openedPluginDetails.description ?? ''"
    :is-loading="getDetailsLoading"
  />

    <Table id="plugins_library_table" :class="dataTable">
      <TableCaption :class="tableCaption">
        <slot/>
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
            :id="`library_plugin_${plugin.id}`"
            :class="hoverListRow('cursor-pointer duration-0')"
            v-for="plugin in plugins"
            :key="plugin.id">
            <TableCell :id="`library_plugin_name_${plugin.id}`" class="pl-4 whitespace-break-spaces ">{{plugin.fileName}}</TableCell>
            <TableCell :id="`library_plugin_creator_${plugin.id}`" class="whitespace-break-spaces" >{{plugin.creator}}</TableCell>
            <TableCell :id="`library_plugin_tags_${plugin.id}`" class="whitespace-break-spaces">
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
            <TableCell class="flex space-x-2 ">
              <Button
                :id="`details_library_plugin_${plugin.id}`"
                @click="getDetails(plugin)"
                variant="blue_outline"
              >
                <IconCode/>
              </Button>
                <Button
                  :id="`download_plugin_${plugin.id}`"
                  v-if="authStore.isAdmin"
                  @click="downloadPlugin(plugin)"
                  variant="green_outline"
                  class="border-l-2!"
                >
                  <IconLoader v-if="isDownloading" class="animate-spin"/>
                  <IconDownload v-else/>
                </Button>
            </TableCell>
          </TableRow>
        </TableBody>
      </Transition>
  </Table>
</template>
