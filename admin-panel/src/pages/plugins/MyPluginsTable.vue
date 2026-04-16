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
import {
  IconCancel,
  IconDeviceFloppy,
  IconListCheck,
  IconX,
  IconPlus,
  IconMinus,
  IconArrowBarDown,
  IconArrowBarUp,
  IconLabel,
  IconLogs,
  IconAlertCircle,
  IconAlertTriangleFilled,
  IconUserEdit, IconTrash, IconDatabase, IconStatusChange, IconTerminal2, IconFileImport,
  IconPencilCode
} from "@tabler/icons-vue"
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
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {ScrollArea} from "@/components/ui/scroll-area";
import {availableTags} from "@/data/tags.ts";
import {RadioGroup, RadioGroupItem} from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import {
  DropdownMenu,
  DropdownMenuContent, DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {ButtonGroup} from "@/components/ui/button-group";
import {myPluginsData} from "@/data/myPlugins.ts";


const props = defineProps<{
  data: Plugin[];
}>()

const emit = defineEmits<{
  'update:checked': [plugins:number[]];
  'update:search-data': [data:string]
}>()


const searchFilter = ref<string>("")
const checkedPlugins = ref<number[]>([])
const tagSearch = ref("")
const tagsListOpen = ref(false)
const blockedRemoveAndChange = computed(() => !checkedPlugins.value.length)
const blockedEdit = computed(() => checkedPlugins.value.length !== 1)
const existingTags = computed(() =>  unwrappedItem.value!.tags.includes(tagSearch.value))
const allChecked = computed(() =>  props.data.length > 0 &&
  checkedPlugins.value.length === props.data.length)
const matchedTags = computed(() => availableTags.filter(t => t.includes(tagSearch.value))
  .filter(t => !unwrappedItem.value!.tags.includes(t)))

const intervals = computed(() => {
  return sortedData.value.map(item => {
    item
  })
})

const correctTime = computed(() => {
  if(unwrappedItem.value) {
    const time = unwrappedItem.value.runningIntervals!
    return  ![time.h, time.m, time.s].some( v => v < 0)
      && time.h <= 23 && time.m <= 59 && time.s <= 59
  } else { return true }
})

watch(checkedPlugins, (newChecked) => {
  emit('update:checked', newChecked);
})

watch(searchFilter, () => {
  emit('update:search-data', searchFilter.value)
}, {immediate: true})

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<Plugin>(() => props.data, 'updatedAt')
const { wrap, isUnwrapped, unwrap, originalItem, unwrappedItem, items } = useWrapping<Plugin>(sortedData)

const addTag = () => {
  if(unwrappedItem.value && !existingTags.value) {
    unwrappedItem.value.tags.push(tagSearch.value)
    tagSearch.value = ''
  }
}

const checkAll = () => {
  !allChecked.value ?
    checkedPlugins.value = props.data.map(plugin => plugin.id) : checkedPlugins.value = []
}

const changeStatus = () => {
  checkedPlugins.value.forEach(p => {
    const plugin = sortedData.value.find(s => s.id === p)
    if(plugin)
      plugin.on = !plugin.on
  })
}

const time = (seconds: number) => {
  return {
    h:Math.floor(seconds / 3600),
    m:Math.floor((seconds % 3600) / 60),
    s:seconds % 60
  }
}

const check = (id: number) => {
  checkedPlugins.value.some(p => p === id) ?
    checkedPlugins.value = checkedPlugins.value.filter(p => p !== id) : checkedPlugins.value.push(id)
}

const savePlugin = () => {
  wrap(true)
  tagsListOpen.value=false
  tagSearch.value=''
  Object.values(unwrappedItem.value!.runningIntervals!).forEach(v => v === null ? 0 : true)

}

</script>

<template>

  <div class="flex ml-[1vw] my-[2vh] ">
    <ButtonGroup>
      <ButtonGroup class="hidden sm:flex">
        <Button variant="outline" size="icon" aria-label="Go Back">
          <ArrowLeftIcon />
        </Button>
      </ButtonGroup>
      <ButtonGroup>
        <Button
          :disabled="blockedEdit"
          @click="unwrap(checkedPlugins[0]!)"
          variant="green_outline">
          Edit
          <IconPencilCode/>
        </Button>
        <Button
          @click="changeStatus"
          :disabled="blockedRemoveAndChange"
          variant="yellow_outline">
          Change Status
          <IconStatusChange/>
        </Button>
        <Button
          :disabled="blockedRemoveAndChange"
          variant="red_outline">
          Delete
          <IconTrash/>
        </Button>
        <InputGroup class="relative l-[30vw] w-[20vw]  " >
          <InputGroupInput
            v-model="searchFilter"
            type="search"
            placeholder="Search for plugin"/>
          <InputGroupAddon>
            <Search/>
          </InputGroupAddon>
        </InputGroup>
      </ButtonGroup>
      <ButtonGroup>

        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <Button variant="outline" size="icon" aria-label="More Options"><IconPlus/></Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" class="w-52">
            <DropdownMenuLabel class="border-b">Plugins</DropdownMenuLabel>
            <DropdownMenuGroup>
              <DropdownMenuItem>
                <RouterLink class="flex w-full gap-x-2" to="/import_plugins">
                  <IconFileImport/>Import
                </RouterLink>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconTerminal2/>Create</DropdownMenuItem>
              <DropdownMenuItem>
                <IconDatabase/>Search for plugins
              </DropdownMenuItem>
            </DropdownMenuGroup>
            <DropdownMenuSeparator/>
            <DropdownMenuGroup>
              <DropdownMenuLabel class=" border-b">Tags</DropdownMenuLabel>
              <DropdownMenuItem>
                <component :is="IconPencilCode"/>Create</DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
        <Badge>Add</Badge>
      </ButtonGroup>
    </ButtonGroup>
  </div>

  <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[70vh] scroll-style ">
    <Table id="my-plugin-table" class="w-99/100 text-md xl:text-xl 2xl:text-4xl  mx-auto  table-fixed border-collapse border-spacing-0">
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9999 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl rounded-lg"> Your plugins:
        <span class="font-extrabold">{{ sortedData.length}}</span>
      </TableCaption>
      <TableHeader class="h-10 ">
      <TableRow class="bg-secondary [&_th]:py-4 hover:bg-secondary ">
        <TableHead class="w-3/100  pl-0 pr-4 ">
          <IconListCheck
            class="size-[2vh] mx-3 rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500 hover:scale-105
                  hover:bg-green-500 cursor-pointer transition duration-100"
            :class="{'text-green-500 hover:bg-primary hover:shadow-primary': allChecked }"

            @click="checkAll"
          />
            </TableHead>

        <SortableHead keyName="name" label="Plugin" :sort-key="sortKey" class=" w-11/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-22/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="runningIntervals" label="Intervals" :sort-key="sortKey" class=" w-11/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="type" label="Type" :sort-key="sortKey" class=" w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="updatedAt" label="Last modified" :sort-key="sortKey" class=" w-15/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="lastModifiedBy" label="Modified by" :sort-key="sortKey" class=" w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="on" label="Status" :sort-key="sortKey" class=" w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            class="cursor-pointer duration-0 border-radius-0 [&_td]:py-3 hover:bg-green-500/20 "
            v-for="plugin in items"
            :key="plugin.id"
            @click="!isUnwrapped(plugin.id) ? check(plugin.id): true; time(plugin.runningIntervals!)"
            :class="{'hover:bg-destructive/20': !plugin.on,
             'bg-selected [&_td]:align-top  sticky h-22 lg:h-24 xl:h-26 2xl:h-30 top-13 bottom-11 hover:bg-card z-9999 cursor-auto'
                    : isUnwrapped(plugin.id) }">
            <TableCell class="pr-4">
              <input
                type="checkbox"
                :id="cn('my-plugin-no-'+plugin.id)" class="size-[1vw] cursor-pointer align-middle"
                :value="plugin.id"
                v-model="checkedPlugins"
              />
            </TableCell>
            <TableCell class=" whitespace-break-spaces">{{plugin.name}}</TableCell>

            <TableCell v-if="!isUnwrapped(plugin.id)"  class=" whitespace-break-spaces">
              <Badge
                v-for="(tag, index) in plugin.tags"
                class="cursor-pointer hover:bg-badge mr-1 mb-2  text-sm xl:text-lg 2xl:text-4xl"
                variant="secondary"
                :key="index"
              >{{tag}}</Badge>
            </TableCell>
            <TableCell v-else class=" whitespace-break-spaces" >
                <div class="w-full justify-between">
                  <Badge
                    v-for="(tag, index) in unwrappedItem!.tags"
                    class="cursor-pointer hover:bg-badge mr-1 mb-2  text-sm xl:text-lg 2xl:text-4xl h-6 xl:h-10 2xl:h-12"
                    variant="secondary"
                    :key="index"
                  >{{tag}}
                    <div>
                      <IconX
                        @click="unwrappedItem!.tags.splice(index, 1)"
                        class="h-4 xl:h-8 2xl:h-10 hover:text-destructive">
                      </IconX>
                    </div>
                  </Badge>
                  <Button class="align-middle size-6 xl:size-10 2xl:size-12 "
                          :variant="tagsListOpen ? 'red_inside': 'green_inside'" @click="tagsListOpen=!tagsListOpen; tagSearch=''">
                    <component stroke="2" class=" size-4 xl:size-8 2xl:size-10" :is=" tagsListOpen ? IconX: IconPlus" />
                  </Button>
                </div>
                <Transition name="slide-fade">
                  <div v-if="tagsListOpen">
                    <Transition name="fade" class="w-full">
                  <span
                    v-if="existingTags"
                    class="text-sm lg:text-md xl:text-xl 2xl:text-4xl text-destructive cursor-text w-full">
                  Tag already exists</span>
                    </Transition>
                    <InputGroup  class="w-2/3 xl:h-14 2xl:h-18 my-2">
                      <InputGroupInput
                        class="text-sm lg:text-md xl:text-xl 2xl:text-4xl"
                        v-model="tagSearch"
                        type="search"
                        @keyup.enter="addTag"
                        @keyup.esc="tagsListOpen=!tagsListOpen"
                        placeholder="Add new tags"/>
                      <InputGroupAddon><IconPlus class="cursor-pointer" @click="addTag"/></InputGroupAddon>
                    </InputGroup>
                    <div class="max-h-30 w-2/3 scroll-style overflow-y-auto border-2 p-2" v-if="matchedTags.length ">
                      <Badge
                        class="cursor-pointer hover:bg-badge mr-1 mb-1  text-sm xl:text-lg 2xl:text-4xl"
                        variant="secondary"
                        @click="unwrappedItem!.tags.push(tag); tagSearch = ''"
                        v-for="(tag, index) in matchedTags" :key="index">{{tag}}</Badge>
                    </div>
                  </div>
                </Transition>
            </TableCell>

            <TableCell v-if="!isUnwrapped(plugin.id)" class="">{{ plugin.runningIntervals }}h:{{ plugin.runningIntervals  }}m:{{ plugin.runningIntervals }}s
            </TableCell>
            <TableCell v-else class=" space-y-2">
              <div class="w-4/5 flex h-6 xl:h-10 2xl:h-12 mr-4 border-3 border-input bg-input/30 rounded-lg">
                <input
                  v-for="index in  ['h', 'm', 's'] as const " :key="index"
                  class="w-1/3 text-center text-md xl:text-2xl 2xl:text-3xl"
                  :class="{ 'border-[0_3px_0_3px] border-input' : index == 'm'}"
                  type="number"
                  :placeholder="index"
                  :max="{h : 23, m: 59, s: 59}[index]"
                  min="0"
                  v-model="unwrappedItem!.runningIntervals!"
                />
<!--                <input-->
<!--                  class="w-1/3 text-center text-md xl:text-2xl 2xl:text-3xl"-->
<!--                  type="number"-->
<!--                  placeholder="hours"-->
<!--                  max="23"-->
<!--                  min="0"-->
<!--                />-->
<!--                <input-->
<!--                  class="w-1/3 text-center text-md xl:text-2xl 2xl:text-3xl"-->
<!--                  type="number"-->
<!--                  placeholder="minutes"-->
<!--                  max="59"-->
<!--                  min="0"-->
<!--                />-->
<!--                <input-->
<!--                  class="w-1/3 text-center text-md xl:text-2xl 2xl:text-3xl"-->
<!--                  type="number"-->
<!--                  placeholder="seconds"-->
<!--                  max="59"-->
<!--                  min="0"-->
<!--                />-->
                <input/>
                <input/>
              </div>
              <Transition name="fade">
                <span class="text-destructive" v-if="!correctTime">Wrong time format</span>
              </Transition>
            </TableCell>
            <TableCell v-if="!isUnwrapped(plugin.id)" class="">
              <component class="text-badge size-7" :class="{'text-yellow-500' : plugin.type === 'alert' }" :is="plugin.type === 'log' ? IconLogs : IconAlertTriangleFilled "/>
            </TableCell>
            <TableCell v-else class="">
              <RadioGroup v-model="unwrappedItem!.type" :default-value="plugin.type">
                <div class="flex items-center space-x-2">
                  <RadioGroupItem id=radio-log value="log" />
                  <Label class="cursor-pointer" for="radio-log">log</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <RadioGroupItem id="radio-alert" value="alert" />
                  <Label class="cursor-pointer" for="radio-alert">alert</Label>
                </div>
              </RadioGroup>
            </TableCell>
            <TableCell class="">
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
            <DateCell v-if="plugin.updatedAt" :date="plugin.updatedAt"></DateCell>
            <TableCell><div class="flex items-center gap-x-2 "><IconUserEdit stroke="1.5"/> {{ plugin.lastModifiedBy}}</div></TableCell>
            <TableCell v-if="!isUnwrapped(plugin.id)" class=" text-green-500" :class="{'text-destructive' : !plugin.on}">{{ plugin.on ? 'On' : 'Off'}}</TableCell>
            <TableCell v-else class="">
              <RadioGroup
                @update:model-value="unwrappedItem!.on = $event === 'on'"
                :model-value="unwrappedItem!.on ? 'on' : 'off'"
                :default-value="plugin.on ? 'on' : 'off'">
                <div class="flex items-center space-x-2">
                  <RadioGroupItem id=radio-on value="on" />
                  <Label class="cursor-pointer text-green-500"  for="radio-on">On</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <RadioGroupItem id="radio-off" value="off" />
                  <Label class="cursor-pointer text-destructive" for="radio-off">Off</Label>
                </div>
              </RadioGroup>

            </TableCell>
            <TableCell class="">{{plugin.weight}} KB</TableCell>
            <Button
              v-if="isUnwrapped(plugin.id)"
              @click="wrap(false); tagsListOpen=false; tagSearch=''; checkAll"
              variant="red_inside"
              class="text-destructive items-center align-middle flex gap-x-2 absolute bottom-2 left-3 ">
              Cancel<IconCancel class="size-4"/>
            </Button>
            <Button
              v-if="isUnwrapped(plugin.id)"
              @click="savePlugin"
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
