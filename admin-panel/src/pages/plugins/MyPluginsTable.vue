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
import {cn} from "@/lib/utils.ts";
import {Badge} from "@/components/ui/badge";
import type {Plugin} from "@/types/plugin.ts"
import {
  IconListCheck
} from "@tabler/icons-vue"
import {computed, ref, watch} from "vue";

const props = defineProps<{
  data: Plugin[];
}>()

const emit = defineEmits<{
  'update:checked': [plugins:number[]];
}>()

const checkedPlugins = ref<number[]>([])
const allChecked = computed(() =>
{
  return props.data.length > 0 && checkedPlugins.value.length === props.data.length
})

const checkAll = () =>
{
  if(!allChecked.value)
  {
    checkedPlugins.value = props.data.map(plugin => plugin.id);
  }
  else
    checkedPlugins.value = [] ;
}

watch(checkedPlugins, (newChecked) =>
{
  emit('update:checked', newChecked);
})

</script>

<template>
  <div class=" mt-[2vh] flex mx-auto w-full">
    <Table class="w-9/10 mx-auto my-[1vh]">
      <TableCaption class="border-b border-t py-[1vh]">List of your plugins</TableCaption>
        <TableHeader>
          <TableRow class="bg-secondary hover:bg-secondary">
            <TableHead class="p-2">
                <IconListCheck
                  class="h-full mx-3 hover:text-green-500 hover:border-green-500 hover:border rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500-3 hover:scale-115 cursor-pointer transition duration-100"
                  :class="{'text-green-500': allChecked }"

                  @click="checkAll"
                />
            </TableHead>
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
            class="cursor-grab duration-0 hover:bg-green-400/30"
            v-for="plugin in props.data"
            :key="plugin.id"
            :class="{'hover:bg-red-400/30': !plugin.on}">
            <TableCell class="px-4">
              <input
                type="checkbox"
                :id="cn('my-plugin-no-'+plugin.id)" class="size-[1vw] cursor-pointer"
                :value="plugin.id"
                v-model="checkedPlugins"
              />
            </TableCell>
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
