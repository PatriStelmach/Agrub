<script setup lang="ts">
import {isValidCron} from "cron-validator";
import cronParser  from 'cron-parser';
import cronstrue from 'cronstrue'
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
import type {MyPlugin} from "@/types/types.ts"
import {
  IconCancel,
  IconDeviceFloppy,
  IconListCheck,
  IconX,
  IconPlus,
  IconLogs,
  IconAlertTriangleFilled,
  IconClockBolt, IconTrash, IconDatabase, IconStatusChange, IconTerminal2, IconFileImport,
  IconPencilCode
} from "@tabler/icons-vue"
import {computed, ref, watch} from "vue";
import {useSort} from "@/composables/sorting.ts";
import SortableHead from "@/helpers/SortableHead.vue";
import {useWrapping} from "@/composables/unwrapping.ts";
import {Button} from "@/components/ui/button";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
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
import {Input} from "@/components/ui/input";
import {dateParser} from "@/composables/dateParser.ts";

const props = defineProps<{
  data: MyPlugin[];
}>()

const emit = defineEmits<{
  'update:checked': [plugins:number[]];
  'update:search-data': [data:string]
}>()

const { sortedData, sortKey, sortOrder, toggleSort } = useSort<MyPlugin>(() => props.data, 'updatedAt')
const { wrap, isUnwrapped, unwrap, originalItem, unwrappedItem, items } = useWrapping<MyPlugin>(sortedData)
// const cronDate = computed(() => {
//   return unwrappedItem.value ? dateParser(cronParser.parse(unwrappedItem.value.cronExpression).next().toDate()) : null
// })

const searchFilter = ref<string>("")
const checkedPlugins = ref<number[]>([])
const tagSearch = ref("")
const tagsListOpen = ref(false)


const blockedEdit = computed(() => checkedPlugins.value.length !== 1)
const existingTags = computed(() =>  unwrappedItem.value!.tags.includes(tagSearch.value))

const blockedRemoveAndChange = computed(() =>
  !checkedPlugins.value.length || (checkedPlugins.value.length && unwrappedItem.value))

const allChecked = computed(() =>  props.data.length > 0 &&
  checkedPlugins.value.length === props.data.length)

const matchedTags = computed(() => availableTags.filter(t => t.includes(tagSearch.value))
  .filter(t => !unwrappedItem.value!.tags.includes(t)))

const nextCron = computed(() =>
  unwrappedItem.value ? dateParser(cronParser.parse(unwrappedItem.value.cronExpression).next().toDate()).fullDate : '')

const cronDescription = computed(() => {
  const cron = unwrappedItem.value!.cronExpression
  if(cron) {
    try {
      cronParser.parse(cron, {strict:true})
      return [cronstrue.toString(cron), nextCron, true]
    } catch (e) {
      return [e, false]
    }
  }
  return [cron, true]
})


watch(checkedPlugins, (newChecked) => {
  emit('update:checked', newChecked);
})

watch(searchFilter, () => {
  emit('update:search-data', searchFilter.value)
}, {immediate: true})

const addTag = () => {
  if(unwrappedItem.value && !existingTags.value && tagSearch.value) {
    unwrappedItem.value.tags.push(tagSearch.value)
    tagSearch.value = ''
  }
}

const checkAll = () => {
  return !allChecked.value ?
    checkedPlugins.value = props.data.map(plugin => plugin.id) : checkedPlugins.value = []
}

const changeStatus = () => {
  if(!unwrappedItem.value) {
    checkedPlugins.value.forEach(p => {
      const plugin = sortedData.value.find(s => s.id === p)
      if (plugin)
        plugin.active = !plugin.active
    })
  }
}

const check = (id: number) => {
  return checkedPlugins.value.some(p => p === id) ?
    checkedPlugins.value = checkedPlugins.value.filter(p => p !== id) : checkedPlugins.value.push(id)
}

const savePlugin = () => {
  if(unwrappedItem.value) {
    wrap(true)
    tagsListOpen.value = false
    tagSearch.value = ''
  }
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

  <div class=" mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[70vh]   ">
    <Table id="my-plugin-table" class="w-99/100  text-md lg:text-lg xl:text-xl 2xl:text-2xl  mx-auto  table-fixed border-collapse border-spacing-0">
      <TableCaption class="bg-secondary border-b border-t text-foreground sticky z-9 bottom-0 py-[1vh] text-md xl:text-xl 2xl:text-4xl "> Your plugins:
        <span class="font-extrabold">{{ sortedData.length}}</span>
      </TableCaption>
      <TableHeader class="h-10  ">
      <TableRow class="bg-secondary [&_th]:py-4 hover:bg-secondary **:text-md! **:lg:text-xl! **:xl:text-2xl! **:2xl:text-4xl! ">
        <TableHead class="w-3/100  pl-0 pr-4 ">
          <IconListCheck
            class="size-[2vh] mx-3 rounded-sm
                  hover:shadow-[0_0_10px_1px] hover:shadow-green-500 hover:scale-105
                  hover:bg-green-500 cursor-pointer transition duration-100"
            :class="{'text-green-500 hover:bg-primary hover:shadow-primary': allChecked }"

            @click="checkAll"
          />
            </TableHead>

        <SortableHead keyName="name" label="Name" :sort-key="sortKey" class=" w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-17/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="cronExpression" label="Cron" :sort-key="sortKey" class=" w-25/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="type" label="Type" :sort-key="sortKey" class=" w-6/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="updatedAt" label="Last modified" :sort-key="sortKey" class=" w-14/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="on" label="Status" :sort-key="sortKey" class=" w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
        <TableBody >
          <TableRow
            class="cursor-pointer duration-0 border-radius-0 [&_td]:py-2 [&_td]:pr-4 hover:bg-green-500/20 "
            v-for="plugin in items"
            :key="plugin.id"
            @click="!isUnwrapped(plugin.id) ? check(plugin.id): true;"
            :class="{'hover:bg-destructive/20': !plugin.active,
             'bg-selected [&_td]:align-top  sticky h-60! lg:h-70! xl:h-80! 2xl:h-90! [&_td]:pt-4! top-14 bottom-11 hover:bg-card z-9 cursor-auto'
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
                class="cursor-pointer hover:bg-badge mr-1 mb-2  text-sm lg:text-lg xl:text-xl 2xl:text-3xl"
                variant="secondary"
                :key="index"
              >{{tag}}</Badge>
            </TableCell>
            <TableCell v-else class=" whitespace-break-spaces" >
              <Transition name="slide-fade">
                <div v-if="tagsListOpen" class="mb-4">
                  <InputGroup
                    class="w-full xl:h-14 2xl:h-18  "
                    :class="{'rounded-br-none rounded-bl-none' : matchedTags.length || tagSearch === ''}">
                    <InputGroupInput
                      class="text-lg! lg:text-xl! xl:text-2xl! 2xl:text-4xl!"
                      v-model="tagSearch"
                      type="search"
                      @keyup.enter="addTag"
                      @keyup.esc="tagsListOpen=!tagsListOpen"
                      placeholder="Add new tags"/>
                    <InputGroupAddon><IconPlus class="size-4 lg:size-6 xl:size-7 2xl:size-10 cursor-pointer" @click="addTag"/></InputGroupAddon>
                  </InputGroup>
                  <div class="max-h-30 w-full mb-2  overflow-y-auto border-2 border-t-0! border-input p-2 rounded-b-md" v-if="matchedTags.length ">
                    <Badge
                      class="cursor-pointer hover:bg-badge mr-1 mb-1  text-sm lg:text-lg xl:text-xl 2xl:text-3xl"
                      variant="secondary"
                      @click="unwrappedItem!.tags.push(tag); tagSearch = ''"
                      v-for="(tag, index) in matchedTags" :key="index">{{tag}}</Badge>
                  </div>
                  <Transition name="fade" class="w-full">
                  <span
                    v-if="existingTags"
                    class="text-sm lg:text-md xl:text-xl 2xl:text-3xl text-destructive cursor-text w-full">
                  Tag already exists</span>
                  </Transition>
                </div>
              </Transition>
                <div class="w-full justify-between">
                  <Badge
                    v-for="(tag, index) in unwrappedItem!.tags"
                    class="cursor-pointer hover:bg-badge mr-1 mb-2  text-sm lg:text-lg xl:text-xl 2xl:text-3xl h-4 lg:h-7 xl:h-8 2xl:h-12"
                    variant="secondary"
                    :key="index"
                  >{{tag}}
                    <div>
                      <IconX
                        @click="unwrappedItem!.tags.splice(index, 1)"
                        class="size-4 lg:size-5 xl:size-6 2xl:size-10 hover:text-destructive">
                      </IconX>
                    </div>
                  </Badge>
                  <Button class="align-middle size-6 lg:size-7 xl:size-8 2xl:size-12 "
                          :variant="tagsListOpen ? 'red_inside': 'green_inside'" @click="tagsListOpen=!tagsListOpen; tagSearch=''">
                    <component stroke="2" class=" size-4 lg:size-5 xl:size-6 2xl:size-10" :is=" tagsListOpen ? IconX: IconPlus" />
                  </Button>
                </div>

            </TableCell>

            <TableCell v-if="!isUnwrapped(plugin.id)" class="whitespace-break-spaces">
              {{ cronstrue.toString(plugin.cronExpression) }}
              <br>
              <span>Next run: {{ dateParser(cronParser.parse(plugin.cronExpression).next().toDate()).fullDate }}
              </span>

            </TableCell>
            <TableCell v-else class="grid space-y-2">
              <InputGroup class="relative w-full xl:h-14 2xl:h-18 mb-2">
                <InputGroupInput
                  class=" text-center text-lg! lg:text-xl! xl:text-2xl! 2xl:text-4xl!"
                  type="text"
                  placeholder="cron expression"
                  v-model="unwrappedItem!.cronExpression"
                />
                <InputGroupAddon><IconClockBolt class="absolute left-4 size-4 lg:size-6 xl:size-7 2xl:size-10"/></InputGroupAddon>
              </InputGroup>

                <span
                  class="grid gap-y-2 w-full text-center whitespace-break-spaces text-sm lg:text-lg xl:text-xl 2xl:text-3xl"
                  :class="{'text-destructive' : !cronDescription[2]}"
                >
                  <span>{{ cronDescription[0]}}</span>
                  <span v-if="cronDescription[2]">
                    Next run: {{ cronDescription[1]}}</span>
                </span>

            </TableCell>
            <TableCell v-if="!isUnwrapped(plugin.id)" class="">
              <component class="text-badge size-7 lg:size-8 xl:size-10 2xl:size-16" :class="{'text-yellow-500' : !plugin.log }" :is="plugin.log ? IconLogs : IconAlertTriangleFilled "/>
            </TableCell>
            <TableCell v-else class="">
              <RadioGroup v-model="unwrappedItem!.log" :default-value="plugin.log"
                          class=" **:text-lg **:lg:text-xl **:xl:text-2xl **:2xl:text-3xl">
                <div class="flex items-center space-x-2 ">
                  <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id=radio-log :value="true" />
                  <Label class="cursor-pointer " for="radio-log">log</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id="radio-alert" :value="false" />
                  <Label class="cursor-pointer" for="radio-alert">alert</Label>
                </div>
              </RadioGroup>
            </TableCell>
            <TableCell class="">
              <img
                v-if="plugin.language === 'python'"
                alt="python_icon"
                src="@/components/icons/python_icon.png"
                class="size-7 lg:size-8 xl:size-10 2xl:size-16"
              />
              <img
                v-if="plugin.language === 'bash'"
                alt="bash_icon"
                src="@/components/icons/bash_icon.png"
                class="size-7 lg:size-8 xl:size-10 2xl:size-16"
              />
              <img
                v-if="plugin.language === 'PowerShell'"
                alt="powershell_icon"
                src="@/components/icons/powershell_icon.png"
                class="size-7 lg:size-8 xl:size-10 2xl:size-16"
              />
            </TableCell>
            <DateCell class=" text-md lg:text-lg xl:text-xl 2xl:text-2xl " v-if="plugin.updatedAt" :date="plugin.updatedAt"></DateCell>
            <TableCell v-if="!isUnwrapped(plugin.id)" class=" text-green-500" :class="{'text-destructive' : !plugin.active}">{{ plugin.active ? 'On' : 'Off'}}</TableCell>
            <TableCell v-else class="">
              <RadioGroup
                @update:model-value="unwrappedItem!.active = $event === 'on'"
                :model-value="unwrappedItem!.active ? 'on' : 'off'"
                :default-value="plugin.active ? 'on' : 'off'"
                class=" **:text-lg **:lg:text-xl **:xl:text-2xl **:2xl:text-3xl">
                <div class="flex items-center space-x-2">
                  <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id=radio-on value="on" />
                  <Label class="cursor-pointer text-green-500"  for="radio-on">On</Label>
                </div>
                <div class="flex items-center space-x-2">
                  <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id="radio-off" value="off" />
                  <Label class="cursor-pointer text-destructive" for="radio-off">Off</Label>
                </div>
              </RadioGroup>

            </TableCell>
            <TableCell class=" text-md lg:text-lg xl:text-xl 2xl:text-2xl ">{{plugin.weight}} KB</TableCell>
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
