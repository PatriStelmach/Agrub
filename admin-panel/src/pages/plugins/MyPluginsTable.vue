<script setup lang="ts">
import cronParser from 'cron-parser';
import cronstrue from 'cronstrue'
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table";
import DateCell from "@/helpers/DateCell.vue";
import {cn} from "@/lib/utils.ts";
import {Badge} from "@/components/ui/badge";
import {Language, type MyPlugin} from "@/types/types.ts"
import {
  IconCancel,
  IconClockBolt,
  IconDeviceFloppy,
  IconLabel,
  IconMessageCode,
  IconPencilCode,
  IconPlus,
  IconStatusChange,
  IconTrash,
  IconX
} from "@tabler/icons-vue"
import {computed, defineAsyncComponent, ref, watch} from "vue";
import {useSort} from "@/composables/sorting.ts";
import SortableHead from "@/helpers/SortableHead.vue";
import {
  tableCaption,
  dataTable,
  tableHeaders,
  topH1,
  topButtonGroup
} from "@/assets/cssFunctions.ts";
import {useWrapping} from "@/composables/unwrapping.ts";
import {Button} from "@/components/ui/button";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {availableTags} from "@/data/tags.ts";
import {RadioGroup, RadioGroupItem} from "@/components/ui/radio-group";
import {Label} from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {ButtonGroup} from "@/components/ui/button-group";
import {dateParser} from "@/composables/dateParser.ts";
import {useMyPluginStore} from "@/stores/myPluginStore.ts";
import {useBadgeFilter} from "@/composables/useBadgeFilter.ts";
import {inputText} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers/GoBackButton.vue";

const props = defineProps<{
  data: MyPlugin[];
}>()

const emit = defineEmits<{
  'update:search-data': [data:string]
}>()

const PluginDetailsDialog = defineAsyncComponent( () => import ("@/pages/plugins/PluginDetailsDialog.vue"))

const store = useMyPluginStore()
const { sortedData, sortKey, sortOrder, toggleSort } = useSort<MyPlugin>(() => props.data, 'updatedAt')
const { wrap, isUnwrapped, unwrap, unwrappedItem } = useWrapping(sortedData, 'fileName')
const { badgeListOpen, addNonExistingBadge, existingBadge, matchedBadges, badgeSearch } = useBadgeFilter<MyPlugin | null>(
  unwrappedItem,
  availableTags,
  () => unwrappedItem.value?.tags ?? []
)
const showInfoDialog = ref<boolean>(false)
const searchFilter = ref<string>("")
const checkedPlugins = ref<string[]>([])

const blockedCheckbox = computed(() => !!unwrappedItem.value)
const blockedEdit = computed(() => checkedPlugins.value.length !== 1 || unwrappedItem.value)

const blockedRemoveAndChange = computed(() =>
  !checkedPlugins.value.length || (checkedPlugins.value.length && unwrappedItem.value))

const allChecked = computed(() =>  props.data.length > 0 &&
  checkedPlugins.value.length === props.data.length)

const nextUnwrappedCron = computed(() =>
  unwrappedItem.value ? dateParser(cronParser.parse(unwrappedItem.value.cronExpression).next().toDate()).fullDate : '')

const cronDescription = computed(() => {
  const cron = unwrappedItem.value?.cronExpression
  if(cron) {
    try {
      cronParser.parse(cron, {strict:true})
      return [cronstrue.toString(cron), nextUnwrappedCron, true]
    } catch (e) {
      return [e, false]
    }
  }
  return [cron, true]
})

watch(searchFilter, () => {
  emit('update:search-data', searchFilter.value)
}, {immediate: true})

const checkAll = () => {
  return !allChecked.value ?
    checkedPlugins.value = props.data.map(plugin => plugin.fileName) : checkedPlugins.value = []
}

const changeStatus = () => {
  if(!unwrappedItem.value) {
     console.log(store.changeStatus(checkedPlugins.value))
  }
}

const deletePlugins = () => {
  if(!unwrappedItem.value) {
    console.log(store.deleteMyPlugins(checkedPlugins.value))
  }
}

const getDetails = async (fileName: string) => {
  if(unwrappedItem.value) {
    const details = await store.getMyPluginDetails(fileName)
    unwrappedItem.value.code = details.code
    unwrappedItem.value.description = details.description
  }
}

const nextRun = (plugin: MyPlugin) => {
  return plugin.cronExpression ?  dateParser(cronParser.parse(plugin.cronExpression).next().toDate()).fullDate : ''
}

const check = (fileName: string) => {
  return checkedPlugins.value.some(p => p === fileName) ?
    checkedPlugins.value = checkedPlugins.value.filter(p => p !== fileName) : checkedPlugins.value.push(fileName)
}

const updateDetails = (code: string, description: string) => {
  if(unwrappedItem.value) {
    unwrappedItem.value.code = code
    unwrappedItem.value.description = description
    console.log(unwrappedItem.value)
  }
}

const closePlugin = () => {
  if(unwrappedItem.value) {
    wrap(false);
    badgeListOpen.value = false;
    badgeSearch.value = '';
  }
}

const savePlugin = async () => {
  if(unwrappedItem.value) {
    const response = await store.editMyPlugin(unwrappedItem.value)
    if(response.success) {
      wrap(true, response.message )
      badgeListOpen.value = false
      badgeSearch.value = ''
    }
    showInfoDialog.value = true
  }
}

</script>

<template>
<div class="relative">
  <h1 :class="topH1">Your plugins</h1>
    <ButtonGroup :class="topButtonGroup">
      <ButtonGroup class=" flex">
        <GoBackButton/>
      </ButtonGroup>
      <ButtonGroup >
        <Button
          :disabled="blockedEdit"
          @click="unwrap(checkedPlugins[0]!); getDetails(checkedPlugins[0]!)"
          variant="green_outline">
          Edit
          <IconPencilCode/>
        </Button>
        <Button
          @click="changeStatus"
          :disabled="blockedRemoveAndChange"
          variant="orange_outline">
          On/Off
          <IconStatusChange/>
        </Button>
        <Button
          @click="deletePlugins"
          :disabled="blockedRemoveAndChange"
          variant="red_outline">
          Delete
          <IconTrash/>
        </Button>
        <InputGroup class=" border-l-2! " >
          <InputGroupInput
            v-model="searchFilter"
            type="search"
            placeholder="Search for plugin"/>
          <InputGroupAddon>
            <Search/>
          </InputGroupAddon>
        </InputGroup>
      </ButtonGroup>
    </ButtonGroup>
</div>

  <div class="  mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-h-[77vh]   ">
    <Table id="my-plugin-table" :class="dataTable">
      <TableCaption :class="tableCaption">My Plugins:
        <span class="font-extrabold">{{ sortedData.length}}</span>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow :class="tableHeaders">
        <TableHead class="w-3/100 px-4 ">
          <input
            :disabled="blockedCheckbox"
            type="checkbox"
            class="size-[1vw] cursor-pointer align-middle"
            @click="checkAll"
          />
            </TableHead>

        <SortableHead keyName="name" label="Name" :sort-key="sortKey" class=" w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-17/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="cronExpression" label="Cron" :sort-key="sortKey" class=" w-23/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="updatedAt" label="Last modified" :sort-key="sortKey" class=" w-14/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="active" label="Status" :sort-key="sortKey" class=" w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
          <TransitionGroup tag="tbody" name="slide-fade">
            <TableRow
              class="cursor-pointer duration-0 border-radius-0  [&_td]:py-2 [&_td]:pr-4 hover:bg-green-badge/20 "
              v-for="plugin in sortedData"
              :key="plugin.fileName"
              @click="blockedCheckbox ? true: check(plugin.fileName)"
              :class="{'hover:bg-destructive/20': !plugin.active,
             'bg-selected [&_td]:align-top  sticky h-40! lg:h-70! xl:h-80! 2xl:h-90! [&_td]:pt-4! top-11 bottom-11 hover:bg-card z-9 cursor-auto'
                    : isUnwrapped(plugin.fileName) }">
              <TableCell class="px-4">
                <input
                  :disabled="blockedCheckbox"
                  type="checkbox"
                  :id="cn('my-plugin-no-'+plugin.fileName)" class="size-[1vw] cursor-pointer align-middle"
                  :value="plugin.fileName"
                  v-model="checkedPlugins"
                />
              </TableCell>
              <TableCell v-if="isUnwrapped(plugin.fileName)">
                <InputGroup
                  class="w-full xl:h-10 2xl:h-12 ">
                  <InputGroupInput
                    :class="inputText"
                    v-model="plugin.name"
                    type="text"
                    placeholder="plugin name"/>
                  <InputGroupAddon><IconLabel class="size-4 lg:size-5 xl:size-6 2xl:size-8 cursor-pointer"/></InputGroupAddon>
                </InputGroup>
              </TableCell>
              <TableCell v-else class=" whitespace-break-spaces">{{plugin.name}}</TableCell>
              <TableCell v-if="!isUnwrapped(plugin.fileName)"  class=" whitespace-break-spaces">
                <Badge
                  v-for="(tag, index) in plugin.tags"
                  variant="tags"
                  :key="index"
                >{{tag}}</Badge>
              </TableCell>
              <TableCell v-else class=" whitespace-break-spaces" >
                <Transition name="slide-fade">
                  <div v-if="badgeListOpen" class="mb-4">
                    <InputGroup
                      class="w-full xl:h-10 2xl:h-12  "
                      :class="{'rounded-br-none rounded-bl-none' : matchedBadges.length || badgeSearch === ''}">
                      <InputGroupInput
                        :class="inputText"
                        v-model="badgeSearch"
                        type="search"
                        @keyup.enter="addNonExistingBadge"
                        @keyup.esc="badgeListOpen=!badgeListOpen"
                        placeholder="Add new tags"/>
                      <InputGroupAddon><IconPlus class="size-4 lg:size-5 xl:size-6 2xl:size-8 cursor-pointer" @click="addNonExistingBadge"/></InputGroupAddon>
                    </InputGroup>
                    <div class="max-h-30 w-full mb-2  overflow-y-auto border-2 border-t-0! border-input p-2 rounded-b-md" v-if="matchedBadges.length ">
                      <Badge
                        variant="tags"
                        @click="unwrappedItem?.tags.push(tag); badgeSearch = ''"
                        v-for="(tag, index) in matchedBadges" :key="index">{{tag}}</Badge>
                    </div>
                    <Transition name="fade" class="w-full">
                  <span
                    v-if="existingBadge"
                    class="text-destructive cursor-text w-full">
                  Tag already exists</span>
                    </Transition>
                  </div>
                </Transition>
                <div class="w-full justify-between">
                  <Badge
                    v-for="(tag, index) in unwrappedItem?.tags"
                    variant="tags"
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
                          :variant="badgeListOpen ? 'red_inside': 'green_inside'" @click="badgeListOpen=!badgeListOpen; badgeSearch=''">
                    <component stroke="2" class=" size-4 lg:size-5 xl:size-6 2xl:size-10" :is=" badgeListOpen ? IconX: IconPlus" />
                  </Button>
                </div>
              </TableCell>
              <TableCell v-if="!isUnwrapped(plugin.fileName)" class="whitespace-break-spaces">
                {{ plugin.cronExpression ? cronstrue.toString(plugin.cronExpression) : ''}}
                <br>
                <span>Next run: {{ nextRun(plugin) }}
              </span>
              </TableCell>
              <TableCell v-else class="grid space-y-2">
                <InputGroup class="relative w-full xl:h-10 2xl:h-12 mb-2">
                  <InputGroupInput
                    class=" text-center input-text"
                    type="text"
                    placeholder="cron expression"
                    v-model="unwrappedItem!.cronExpression"
                  />
                  <InputGroupAddon><IconClockBolt class="absolute left-4 size-4 lg:size-5 xl:size-6 2xl:size-8"/></InputGroupAddon>
                </InputGroup>
                <span
                  class="grid gap-y-2 w-full text-center whitespace-break-spaces t"
                  :class="{'text-destructive' : !cronDescription[2]}"
                >
                  <span>{{ cronDescription[0]}}</span>
                  <span v-if="cronDescription[2]">
                    Next run: {{ cronDescription[1]}}</span>
                </span>
              </TableCell>
              <TableCell v-if="isUnwrapped(plugin.fileName) && unwrappedItem" >
                <Select
                  v-model="unwrappedItem.severity"
                >
                  <SelectTrigger class="cursor-pointer w-full ">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem
                      :class='`cursor-pointer hover:bg-severity-${value}/50! `'
                      v-for="value in [0,1,2,3,4,5]" :key="value" :value="value">{{value}}</SelectItem>
                  </SelectContent>
                </Select>
              </TableCell>
              <TableCell v-else  >
                <div
                  :class="`bg-linear-to-l w-2/3 text-center font-bold text-xl
                   from-severity-${plugin.severity}/50 via-severity-${plugin.severity} to-severity-${plugin.severity}/50`">{{plugin.severity}}</div>
              </TableCell>
              <TableCell >
                <img
                  v-if="plugin.language === Language.PYTHON"
                  alt="python_icon"
                  src="@/components/icons/python_icon.png"
                  class="size-7 lg:size-8"
                />
                <img
                  v-if="[Language.BASH, Language.SH].includes(plugin.language)"
                  alt="bash_icon"
                  src="@/components/icons/bash_icon.png"
                  class="size-7 lg:size-8 "
                />
                <img
                  v-if="[Language.POWERSHELL, Language.POWERSHELL_MODULE].includes(plugin.language)"
                  alt="powershell_icon"
                  src="@/components/icons/powershell_icon.png"
                  class="size-7 lg:size-8 "
                />
              </TableCell>
              <DateCell class="" v-if="plugin.updatedAt" :date="plugin.updatedAt"></DateCell>
              <TableCell v-if="isUnwrapped(plugin.fileName) && unwrappedItem" >
                <RadioGroup
                  @update:model-value="unwrappedItem.active = $event === 'on'"
                  :model-value="unwrappedItem.active ? 'on' : 'off'"
                  :default-value="plugin.active ? 'on' : 'off'"
                  class="">
                  <div class="flex items-center space-x-2">
                    <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id=radio-on value="on" />
                    <Label class="cursor-pointer text-green-badge"  for="radio-on">On</Label>
                  </div>
                  <div class="flex items-center space-x-2">
                    <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id="radio-off" value="off" />
                    <Label class="cursor-pointer text-destructive" for="radio-off">Off</Label>
                  </div>
                </RadioGroup>
              </TableCell>
              <TableCell v-else class=" text-green-badge" :class="{'text-destructive' : !plugin.active}">{{ plugin.active ? 'On' : 'Off'}}</TableCell>
              <TableCell class="">{{plugin.weight}} KB</TableCell>
              <ButtonGroup v-if="isUnwrapped(plugin.fileName) && unwrappedItem" class="flex  absolute bottom-4 right-3 *:items-center *:align-middle *:flex">
                <Button
                  @click="closePlugin"
                  variant="red_outline">
                  Cancel<IconCancel class="size-4 xl:size-5"/>
                </Button>
                <Button variant="orange_outline" class="p-0">
                  <PluginDetailsDialog
                    :code="unwrappedItem.code ?? ''"
                    :description="unwrappedItem.description ?? ''"
                    @update:save-changes="updateDetails"
                  >
                    <Button
                      class="border-0! m-0 rounded-none bg-transparent! text-severity-3 hover:text-primary"
                    >
                      Details<IconMessageCode class="size-4 xl:size-5"/>
                    </Button>
                  </PluginDetailsDialog>
                </Button>

                <Button
                  @click="savePlugin"
                  variant="green_outline">
                  Save<IconDeviceFloppy class="size-4 xl:size-5"/>
                </Button>
              </ButtonGroup>
            </TableRow>
          </TransitionGroup>

      <TableFooter>
      </TableFooter>
      </Table>
    </div>
</template>
