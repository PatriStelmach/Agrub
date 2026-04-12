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
import {IconCancel, IconDeviceFloppy, IconListCheck, IconX, IconPlus} from "@tabler/icons-vue"
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
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {Search} from "lucide-vue-next";
import {ScrollArea} from "@/components/ui/scroll-area";
import {availableTags} from "@/data/tags.ts";

const props = defineProps<{
  data: Plugin[];
}>()

const emit = defineEmits<{
  'update:checked': [plugins:number[]];
}>()


const checkedPlugins = ref<number[]>([])
const tagSearch = ref("")
const existingTags = computed(() => {
  return unwrappedItem.value!.tags.includes(tagSearch.value)
})

const allChecked = computed(() => {
  return props.data.length > 0 && checkedPlugins.value.length === props.data.length
})

const matchedTags = computed(() => {
  const tags = availableTags.filter(t => t.includes(tagSearch.value))
  return tags.filter(t => !unwrappedItem.value!.tags.includes(t))
})

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<Plugin>(() => props.data, 'updatedAt')
const { wrap, isUnwrapped, unwrap, originalItem, unwrappedItem, items } = useWrapping<Plugin>(sortedData)

watch(checkedPlugins, (newChecked) => {
  emit('update:checked', newChecked);
})

const addTag = () => {
  if(unwrappedItem.value && !existingTags.value) {
    unwrappedItem.value.tags.push(tagSearch.value)
    tagSearch.value = ''
  }
}

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
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9999 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl rounded-lg"> Your plugins:
        <span class="font-extrabold">{{ props.data.length}}</span>
      </TableCaption>
      <TableHeader class="h-10 ">
      <TableRow class="bg-secondary hover:bg-secondary ">
        <TableHead class="w-3/100 py-4 pl-0 pr-4 ">
          <IconListCheck
            class="size-[2vh] mx-3 rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500 hover:scale-105
                  hover:bg-green-500 cursor-pointer transition duration-100"
            :class="{'text-green-500 hover:bg-primary hover:shadow-primary': allChecked }"

            @click="checkAll"
          />
            </TableHead>

        <SortableHead keyName="name" label="Plugin" :sort-key="sortKey" class="py-4 w-10/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="creator" label="Creator" :sort-key="sortKey" class="py-4 w-10/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class="py-4 w-18/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="runningIntervals" label="Intervals" :sort-key="sortKey" class="py-4 w-9/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="type" label="Type" :sort-key="sortKey" class="py-4 w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="language" label="Language" :sort-key="sortKey" class="py-4 w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="addedAt" label="Added at" :sort-key="sortKey" class="py-4 w-12/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="updatedAt" label="Last modified" :sort-key="sortKey" class="py-4 w-12/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="on" label="Status" :sort-key="sortKey" class="py-4 w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="py-4 w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            class="cursor-pointer duration-0 border-radius-0 hover:bg-green-500/20 "
            v-for="plugin in items"
            :key="plugin.id"
            @dblclick="unwrap(plugin)"
            :class="{'hover:bg-destructive/20': !plugin.on,
             'bg-selected [&_td]:align-top sticky h-22 lg:h-24 xl:h-26 2xl:h-30 top-13 bottom-11 hover:bg-card z-9999 cursor-auto'
                    : isUnwrapped(plugin.id) }">
            <TableCell class="pr-4">
              <input
                type="checkbox"
                :id="cn('my-plugin-no-'+plugin.id)" class="size-[1vw] cursor-pointer align-middle"
                :value="plugin.id"
                v-model="checkedPlugins"
              />
            </TableCell>
            <TableCell class="py-4">{{plugin.name}}</TableCell>
            <TableCell class="py-4">{{plugin.creator}}</TableCell>

            <TableCell v-if="!isUnwrapped(plugin.id)"  class="py-4 whitespace-break-spaces">
              <Badge
                v-for="(tag, index) in plugin.tags"
                class="cursor-pointer hover:bg-badge mr-1 mb-2  text-sm xl:text-lg 2xl:text-4xl"
                variant="secondary"
                :key="index"
              >{{tag}}</Badge>
            </TableCell>
            <TableCell v-else class="py-4 whitespace-break-spaces" >
              <Badge
                v-for="(tag, index) in unwrappedItem!.tags"
                class="cursor-pointer hover:bg-badge mr-1 mb-2  text-sm xl:text-lg 2xl:text-4xl"
                variant="secondary"
                :key="index"
              >{{tag}}
                <div >
                  <IconX
                    @click="unwrappedItem!.tags.splice(index, 1)"
                    class="h-4 xl:h-8 2xl:h-10 2xl:w-10 hover:text-destructive">
                  </IconX>
                </div>
              </Badge>
              <div>
                <Transition>
                  <span
                    v-if="existingTags"
                    class="text-sm lg:text-md xl:text-xl 2xl:text-4xl text-destructive py-22 cursor-text">
                  Tag already exists</span>
                </Transition>

                <InputGroup  class="w-2/3 my-2">
                  <InputGroupInput
                    v-model="tagSearch"
                    type="search"
                    @keyup.enter="addTag"
                    placeholder="Add new tags"/>
                  <InputGroupAddon><IconPlus class="cursor-pointer" @click="addTag"/></InputGroupAddon>
                </InputGroup>
                <div class="max-h-30 w-2/3 scroll-style overflow-y-auto border-2 p-2">
                  <Badge
                    class="cursor-pointer hover:bg-badge mr-1 mb-1  text-sm xl:text-lg 2xl:text-4xl"
                    variant="secondary"
                    @click="unwrappedItem!.tags.push(tag); tagSearch = ''"
                    v-for="(tag, index) in matchedTags" :key="index">{{tag}}</Badge>
                </div>
              </div>

            </TableCell>

            <TableCell class="py-4">{{plugin.runningIntervals}}</TableCell>
            <TableCell class="py-4">{{plugin.type}}</TableCell>
            <TableCell class="py-4">
              <img
                v-if="plugin.language === 'python'"
                alt="python_icon"
                src="@/components/icons/python_icon.png"
                class="size-6 lg:size-8 xl:size-10 2xl:size-16"
              />
              <img
                v-if="plugin.language === 'bash'"
                alt="bash_icon"
                src="@/components/icons/bash_icon.png"
                class="size-6 lg:size-8 xl:size-10 2xl:size-16"
              />
              <img
                v-if="plugin.language === 'PowerShell'"
                alt="powershell_icon"
                src="@/components/icons/powershell_icon.png"
                class="size-6 lg:size-8 xl:size-10 2xl:size-16"
              />
            </TableCell>
            <DateCell v-if="plugin.addedAt" class="py-4" :date="plugin.addedAt "></DateCell>
            <DateCell v-if="plugin.updatedAt" class="py-4" :date="plugin.updatedAt"></DateCell>
            <TableCell v-if="plugin.on" class="py-4 text-green-500">On</TableCell>
            <TableCell v-else class="py-4 text-destructive">Off</TableCell>
            <TableCell class="py-4">{{plugin.weight}} KB</TableCell>
            <Button
              v-if="isUnwrapped(plugin.id)"
              @click="wrap(false)"
              variant="red_inside"
              class="text-destructive items-center align-middle flex gap-x-2 absolute bottom-2 left-3 ">
              Cancel<IconCancel class="size-4"/>
            </Button>
            <Button
              v-if="isUnwrapped(plugin.id)"
              @click="wrap(true)"
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
