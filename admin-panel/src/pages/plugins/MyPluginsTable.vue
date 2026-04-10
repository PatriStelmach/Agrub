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
import type {Plugin} from "@/types/types.ts"
import {IconCancel, IconDeviceFloppy, IconListCheck} from "@tabler/icons-vue"
import {computed, ref, watch} from "vue";
import {useSort} from "@/composables/sorting.ts";
import SortableHead from "@/helpers/SortableHead.vue";
import {useWrapping} from "@/composables/unwrapping.ts";
import {Button} from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {Input} from "@/components/ui/input";

const props = defineProps<{
  data: Plugin[];
}>()

const emit = defineEmits<{
  'update:checked': [plugins:number[]];
}>()


const checkedPlugins = ref<number[]>([])
const { sortedData, sortKey, sortOrder, toggleSort } = useSort<Plugin>(() => props.data, 'updatedAt')
const {wrap, isUnwrapped, unwrap } = useWrapping('#my-plugin-table')

const allChecked = computed(() => {
  return props.data.length > 0 && checkedPlugins.value.length === props.data.length
})

watch(checkedPlugins, (newChecked) => {
  emit('update:checked', newChecked);
})

const checkAll = () => {
  if(!allChecked.value) {
    checkedPlugins.value = props.data.map(plugin => plugin.id);
  }
  else {
    checkedPlugins.value = [];
  }
}

</script>

<template>
  <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[70vh] scroll-style ">
    <Table id="my-plugin-table" class="w-99/100 text-md xl:text-xl 2xl:text-4xl  mx-auto  table-fixed border-collapse border-spacing-0">
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9999 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl"> Your plugins:
        <span class="font-extrabold">{{ props.data.length}}</span>
      </TableCaption>
      <TableHeader class="h-10">
      <TableRow class="bg-secondary hover:bg-secondary">
        <TableHead class="w-2/100 pl-0 pr-4 ">
          <IconListCheck
            class="size-[2vh] mx-3 rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500 hover:scale-105
                  hover:bg-green-500 cursor-pointer transition duration-100"
            :class="{'text-green-500 hover:bg-primary hover:shadow-primary': allChecked }"

            @click="checkAll"
          />
            </TableHead>

        <SortableHead keyName="name" label="Plugin" :sort-key="sortKey" class="p-4 w-10/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="creator" label="Creator" :sort-key="sortKey" class="p-4 w-10/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class="p-4 w-24/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="runningIntervals" label="Intervals" :sort-key="sortKey" class="p-4 w-9/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="language" label="Language" :sort-key="sortKey" class="p-4 w-9/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="addedAt" label="Added at" :sort-key="sortKey" class="p-4 w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="updatedAt" label="Last modified at" :sort-key="sortKey" class="p-4 w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="on" label="Status" :sort-key="sortKey" class="p-4 w-5/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="p-4 w-5/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            class="cursor-pointer duration-0 border-radius-0 hover:bg-green-500/20"
            v-for="plugin in sortedData"
            :key="plugin.id"
            @dblclick="unwrap(plugin.id)"
            :class="{'hover:bg-red-500/20': !plugin.on,
             'bg-selected [&_td]:align-top sticky h-22 lg:h-24 xl:h-26 2xl:h-30 top-13 bottom-11 hover:bg-card z-9999 '
                    : isUnwrapped(plugin.id) }">
            <TableCell class="pr-4">
              <input
                type="checkbox"
                :id="cn('my-plugin-no-'+plugin.id)" class="size-[1vw] cursor-pointer align-middle"
                :value="plugin.id"
                v-model="checkedPlugins"
              />
            </TableCell>
            <TableCell class="p-4">{{plugin.name}}</TableCell>
            <TableCell class="p-4">{{plugin.creator}}</TableCell>

            <TableCell v-if="!isUnwrapped(plugin.id)"  class="p-4 whitespace-break-spaces">
              <Badge
                v-for="(tag, index) in plugin.tags"
                class="cursor-pointer hover:bg-chart-1 mr-1 mb-1  text-sm xl:text-lg 2xl:text-4xl"
                variant="secondary"
                :key="index"
              >{{tag}}</Badge>
            </TableCell>
            <TableCell v-else class="p-4 whitespace-break-spaces" >
              <Badge
                v-for="(tag, index) in plugin.tags"
                class="cursor-pointer hover:bg-chart-1 mr-1 mb-1  text-sm xl:text-lg 2xl:text-4xl"
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
            <Button
              v-if="isUnwrapped(plugin.id)"
              @click="wrap"
              variant="red_inside"
              class="text-red-500 items-center align-middle flex gap-x-2 absolute bottom-2 left-3 ">
              Cancel<IconCancel class="size-4"/>
            </Button>
            <Button
              v-if="isUnwrapped(plugin.id)"
              variant="green_inside"
              class="text-green-500 items-center flex gap-x-2 absolute bottom-2 right-3">
              Save<IconDeviceFloppy class="size-5"/>
            </Button>
          </TableRow>
        </TableBody>
      <TableFooter>
      </TableFooter>
      </Table>
    </div>
</template>
