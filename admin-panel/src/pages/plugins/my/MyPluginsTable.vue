<script setup lang="ts">
import cronParser from 'cron-parser';
import cronstrue from 'cronstrue'
import {
  Table, TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table";
import DateCell from "@/helpers_components/DateCell.vue";
import {cn} from "@/lib/utils.ts";
import {Badge} from "@/components/ui/badge";
import LanguageImage from "@/helpers_components/LanguageImage.vue";
import {type MyPlugin} from "@/types/types.ts"
import {
  IconCancel,
  IconClockBolt,
  IconDeviceFloppy,
  IconLabel,
  IconCode,
  IconStatusChange,
  IconSend2,
  IconCodeDots,
  IconTrash,
  IconPlayerPlay,
  IconLoader
} from "@tabler/icons-vue"
import {computed, ref, watch, watchEffect} from "vue";
import {useSort} from "@/composables/sorting.ts";
import SortableHead from "@/helpers_components/SortableHead.vue";
import {
  tableCaption,
  dataTable,
  tableHeaders,
  tableDiv, smallNameLabel
} from "@/assets/cssFunctions.ts";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {useWrapping} from "@/composables/unwrapping.ts";
import {getPluginDetailsRequest, runScriptRequest} from "@/helpers_functions/requests.ts";
import {Button} from "@/components/ui/button";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {Search} from "lucide-vue-next";
import {RadioGroup, RadioGroupItem} from "@/components/ui/radio-group";
import {Label} from "@/components/ui/label";
import {ButtonGroup} from "@/components/ui/button-group";
import {dateParser} from "@/composables/dateParser.ts";
import {useMyPluginStore} from "@/stores/myPluginStore.ts";
import {inputText} from "@/assets/cssFunctions.ts";
import SeveritySelect from "@/helpers_components/SeveritySelect.vue";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import LoadingTable from "@/helpers_components/LoadingTable.vue";
import MyTagInput from '@/helpers_components/MyTagInput.vue'

const props = defineProps<{
  data: MyPlugin[];
  availableTags: string[];
  isLoading: boolean;
}>()
const emit = defineEmits<{
  'update:search-data': [data:string]
}>()


import PluginDetailsDialog from '@/pages/plugins/PluginDetailsDialog.vue'
import {toast} from "vue-sonner";
import {useRoute, useRouter} from "vue-router";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {Input} from "@/components/ui/input";
import {useAuthStore} from "@/stores/authStore.ts";
const myPluginStore = useMyPluginStore()
const authStore = useAuthStore()
const { sortedData, sortKey, sortOrder, toggleSort } = useSort<MyPlugin>(() => props.data, 'updatedAt')
const { isUnwrapped, unwrap, unwrappedItem, save } = useWrapping(sortedData, 'fullName')
const router = useRouter()
const route = useRoute()
const paramRoute = computed(() => route.params.plugin)

const args = ref<string>('')
const searchFilter = ref<string>("")
const checkedPlugins = ref<string[]>([])
const loadingTrigger = ref<boolean>(false)
const blockedCheckbox = computed(() => !!unwrappedItem.value)
const getDetailsLoading = ref<boolean>(false)
const isEditLoading = ref<boolean>(false)
const isCodeDialogOpen = ref<boolean>(false)

const blockDeleteAndChangeStatus = computed(() =>
  !checkedPlugins.value.length ||
  !!unwrappedItem.value ||
  !authStore.isAdmin
)

const blockTrigger = computed(() =>
  !checkedPlugins.value.length ||
  !!unwrappedItem.value ||
  checkedPlugins.value.length > 1
)

const allChecked = computed(() =>  checkedPlugins.value && checkedPlugins.value.length === props.data.length)

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

const cronWrappedDescription = (cron: string) => {
  if(cron) {
    try {
      cronParser.parse(cron, {strict:true})
      return [cronstrue.toString(cron), nextUnwrappedCron, true]
    } catch (e) {
      return [e, false]
    }
  }
  return [cron, true]
}

watch(searchFilter, () => {
  emit('update:search-data', searchFilter.value)
}, {immediate: true})

const checkAll = () => {
  return checkedPlugins.value.length < props.data.length ?
    checkedPlugins.value = props.data.map(plugin => plugin.fullName) : checkedPlugins.value = []
}

const changeStatus = () => {
  if(!unwrappedItem.value && authStore.isAdmin) {
    myPluginStore.changeStatus(checkedPlugins.value)
      .catch(e => toast.error(`Error changing status: ${e}`))
      .then(()=> toast.success(`Successfully changed status`))
      .finally(()=> checkedPlugins.value = [])
  }
}

const deletePlugins = () => {
  if(!unwrappedItem.value && authStore.isAdmin) {
    myPluginStore.deleteMyPlugins(checkedPlugins.value)
      .catch(e => toast.error(`Error deleting plugins: ${e}`))
      .then(()=> toast.success(`Successfully deleted plugins`))
      .finally(()=> checkedPlugins.value = [])
  }
}

const getDetails = async (fileName: string) => {
  await getPluginDetailsRequest(fileName, 'local-scripts')
    .then((res) => {
      if(unwrappedItem.value) {
        unwrappedItem.value.code = res?.code
        unwrappedItem.value.description = res?.description
      }
    })
    .catch((err) => toast.error(`Error fetching plugin details: ${err}`))
    .finally(() => getDetailsLoading.value = false)
}

const nextRun = (plugin: MyPlugin) => {
  return plugin.cronExpression ?  dateParser(cronParser.parse(plugin.cronExpression).next().toDate()).fullDate.toString() : ''
}

const updateDetails = (code: string, description: string) => {
  if(unwrappedItem.value) {
    unwrappedItem.value.code = code
    unwrappedItem.value.description = description
  }
}

const triggerScript = async (args: string) => {
  if (checkedPlugins.value.length === 1) {
    loadingTrigger.value = true
    await runScriptRequest(checkedPlugins.value.find(v => v), args)
      .then((res) => {
        toast.success(`Script triggered: ${res}`)
      })
      .catch((error) => toast.error(error))
      .finally(() => loadingTrigger.value = false)
  }
  else {
    toast.info('You can trigger only one script at once')
  }
}

const savePlugin = async () => {
  isEditLoading.value = true
  try {
    if (unwrappedItem.value && authStore.isAdmin) {
      await save(() => myPluginStore.editMyPlugin(unwrappedItem.value!))
    }
  } catch (e) {
    toast.error(`Editing "${unwrappedItem.value?.name}" failed with error ${e}`);
  }
  finally {
    isEditLoading.value = false
    onCloseAndSave()
  }
}

watchEffect(async () => {
    if(paramRoute.value) {
      const plugin = sortedData.value.find((pl) => pl.name === paramRoute.value)
      if(plugin) {
        unwrap(plugin.fullName)
      }
      if(unwrappedItem.value) {
        getDetailsLoading.value = true
        setTimeout(async () => {
          await getDetails(unwrappedItem.value!.fullName)
        },500 )
      }
    }
    else {
      unwrappedItem.value = null
    }
})

const onCloseAndSave = () => {
  router.replace({path: '/my_plugins'})
}

const onEdit = (plugin: MyPlugin) => {
  if(isUnwrapped(plugin.fullName)) {
    return
  }
  else {
    router.replace({path: `/my_plugins/${plugin.name}`})
  }
}


</script>

<template>
<TopH1Div h1="Your plugins">
  <ButtonGroup >
    <Popover>
      <PopoverTrigger as-child>
        <Button
          :disabled="blockTrigger"
          variant="green_outline"
        >
          Trigger
          <IconPlayerPlay/>
        </Button>
      </PopoverTrigger>
      <PopoverContent class="w-90">
        <div class="space-y-2">
          <div class="flex items-center space-x-2">
            <IconCodeDots class="text-comment"/>
            <h1 :class="smallNameLabel">Arguments</h1>
          </div>
          <h2 class="text-xs text-comment">Set execute arguments for script</h2>
          <h2 class="text-xs text-comment">Use space between arguments: "arg1 arg2 arg3"</h2>
          <div class="flex items-center space-x-2">
            <Input
              class="h-8"
              v-model="args"
            />
            <Button
              @click="triggerScript(args)"
              class="h-8"
              variant="green_outline"
            >
              <IconSend2 v-if="!loadingTrigger"/>
              <IconLoader v-else class="animate-spin"/>
            </Button>
          </div>
        </div>
      </PopoverContent>
    </Popover>
    <Button
      v-if="authStore.isAdmin"
      class="border-l-2!"
      @click="changeStatus"
      :disabled="blockDeleteAndChangeStatus"
      variant="blue_outline">
      On/Off
      <IconStatusChange/>
    </Button>
    <Button
      v-if="authStore.isAdmin"
      class="border-l-2!"
      @click="deletePlugins"
      :disabled="blockDeleteAndChangeStatus"
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
</TopH1Div>

  <div :class="tableDiv ">
    <Table id="my-plugin-table" :class="dataTable">
      <TableCaption :class="tableCaption">
        <slot/>
      </TableCaption>
      <TableHeader class="h-10">
        <TableRow :class="tableHeaders">
        <TableHead class="w-14 px-4 ">
          <input
            v-model="allChecked"
            :disabled="blockedCheckbox"
            type="checkbox"
            class="size-[1vw] cursor-pointer align-middle"
            @click="checkAll"
          />
            </TableHead>
              <SortableHead keyName="name" label="Name" :sort-key="sortKey" class=" w-fit" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="tags" label="Tags" :sort-key="sortKey" class=" w-fit" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="cronExpression" label="Cron" :sort-key="sortKey" class=" w-fit" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="severity" label="Severity" :sort-key="sortKey" class=" w-10/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="language" label="Language" :sort-key="sortKey" class=" w-8/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="updatedAt" label="Last modified" :sort-key="sortKey" class=" w-13/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="active" label="Status" :sort-key="sortKey" class=" w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
              <SortableHead keyName="weight" label="Weight" :sort-key="sortKey" class="  w-7/100" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          </TableRow>
        </TableHeader>
      <Transition name="fade" mode="out-in">
        <LoadingTable :colspan="9" v-if="isLoading"/>
        <TableBody v-else>
          <TableRow
            class="cursor-pointer duration-0 border-radius-0  [&_td]:py-2 [&_td]:pr-4 hover:bg-green-badge/20"
            v-for="plugin in sortedData"
            :key="plugin.fullName"
            @click="onEdit(plugin) "
            :class="{'hover:bg-destructive/20': !plugin.active,
             'bg-selected [&_td]:align-top cursor-auto sticky h-40! [&_td]:pt-4! top-9 bottom-22 hover:bg-card z-9 '
                    : isUnwrapped(plugin.fullName) }">
            <TableCell @click.stop class="px-4">

              <input
                @click.stop
                :disabled="blockedCheckbox"
                type="checkbox"
                :id="cn('my-plugin-no-'+plugin.fullName)" class="size-[1vw] cursor-pointer align-middle"
                :value="plugin.fullName"
                v-model="checkedPlugins"
              />
            </TableCell>
            <TableCell v-if="isUnwrapped(plugin.fullName) && unwrappedItem && authStore.isAdmin">
              <InputGroup
                class="w-full xl:h-10 2xl:h-12 ">
                <InputGroupInput
                  :class="inputText"
                  v-model="unwrappedItem.name"
                  type="text"
                  placeholder="plugin name"/>
                <InputGroupAddon><IconLabel class="size-4 lg:size-5 xl:size-6 2xl:size-7 cursor-pointer"/></InputGroupAddon>
              </InputGroup>
            </TableCell>
            <TableCell v-else class=" whitespace-break-spaces">{{plugin.name}}
            </TableCell>

            <!-- Tags -->
            <TableCell class="whitespace-break-spaces" >
              <MyTagInput
                v-if="unwrappedItem && isUnwrapped(plugin.fullName) && authStore.isAdmin"
                v-model:tags="unwrappedItem.tags"
                :all-tags="availableTags"
                input-id="tags-input"
                :can-add-new="true"
                tags-label="Tags"/>
              <TransitionGroup v-else name="fade">
                <Badge
                  v-for="(tag, index) in plugin.tags"
                  variant="tags"
                  :key="index"
                >{{tag}}</Badge>
              </TransitionGroup>
            </TableCell>
            <TableCell v-if="!isUnwrapped(plugin.fullName) && authStore.isAdmin" class="grid space-y-2">
              <InputGroup class="relative w-full xl:h-10 2xl:h-12 mb-2">
                <InputGroupInput
                  class=" text-center input-text"
                  type="text"
                  placeholder="cron expression"
                  v-model="unwrappedItem!.cronExpression"
                />
                <InputGroupAddon><IconClockBolt class="absolute left-4 size-4 xl:size-5 2xl:size-6"/></InputGroupAddon>
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
            <TableCell v-else class="whitespace-break-spaces">
              <span
                class="grid w-full whitespace-break-spaces "
                :class="{'text-destructive' : !cronWrappedDescription(plugin.cronExpression)[2]}"
              >
                <span>{{cronWrappedDescription(plugin.cronExpression)[0]}}</span>
                <span  v-if="cronWrappedDescription(plugin.cronExpression)[2]">Next run: {{nextRun(plugin)}}
                </span>
                <span class="text-primary truncate" v-else-if="!cronWrappedDescription(plugin.cronExpression)[0]">
                  Next run: -------------
                </span>
              </span>
            </TableCell>

            <TableCell v-if="isUnwrapped(plugin.fullName) && unwrappedItem && authStore.isAdmin" >
              <SeveritySelect
                v-model:severity="unwrappedItem.severity"
              />
            </TableCell>
            <TableCell v-else  >
              <SeverityDiv
                :severity="plugin.severity"
              />
            </TableCell>
            <TableCell>
              <LanguageImage :language="plugin.language" sizeClass="size-7 lg:size-8" />
            </TableCell>
            <DateCell class="" v-if="plugin.updatedAt" :date="plugin.updatedAt"></DateCell>
            <TableCell v-if="isUnwrapped(plugin.fullName) && unwrappedItem && authStore.isAdmin" >
              <div class="flex items-center gap-x-2">
                <input v-model="unwrappedItem.active" type="radio" class="size-4 cursor-pointer lg:size-5 xl:size-6 2xl:size-8" id=radio-on :value="true" />
                <Label class="cursor-pointer text-green-badge"  for="radio-on">On</Label>
              </div>
              <div class="flex items-center gap-x-2">
                <input v-model="unwrappedItem.active" type="radio" class="size-4 cursor-pointer lg:size-5 xl:size-6 2xl:size-8" id="radio-off" :value="false" />
                <Label class="cursor-pointer text-destructive" for="radio-off">Off</Label>
              </div>
            </TableCell>
            <TableCell v-else class=" text-green-badge" :class="{'text-destructive' : !plugin.active}">{{ plugin.active ? 'On' : 'Off'}}</TableCell>
            <TableCell class="">{{plugin.weight}} KB
              <ButtonGroup v-if="isUnwrapped(plugin.fullName) && unwrappedItem" class="flex **:truncate absolute bottom-4 right-3 *:items-center *:align-middle *:flex">
                <Button
                  @click.stop="onCloseAndSave"
                  variant="red_outline">
                  Cancel<IconCancel class="size-4 xl:size-5"/>
                </Button>
                <Button
                  :disabled="getDetailsLoading"
                  variant="blue_outline" class="border-l-2! p-0">
                  <PluginDetailsDialog
                    v-model:isCodeDialogOpen="isCodeDialogOpen"
                    :editable="authStore.isAdmin"
                    :code="unwrappedItem.code ?? ''"
                    :description="unwrappedItem.description ?? ''"
                    @update:save-changes="updateDetails"
                  >
                    <Button
                      @click.stop
                      class="m-0 rounded-none bg-transparent! hover:scale-100 text-blue-badge hover:text-primary"
                    >
                      Code
                      <IconLoader v-if="getDetailsLoading" class="animate-spin"/>
                      <IconCode v-else class="size-4 xl:size-5"/>
                    </Button>
                  </PluginDetailsDialog>
                </Button>
                <Button
                  v-if="authStore.isAdmin"
                  :disabled="!cronDescription[2]"
                  class="border-l-2!"
                  @click.stop="savePlugin"
                  variant="green_outline">
                  Save
                  <IconLoader v-if="isEditLoading" class="animate-spin"/>
                  <IconDeviceFloppy v-else class="size-4 xl:size-5"/>
                </Button>
              </ButtonGroup></TableCell>
          </TableRow>
        </TableBody>
      </Transition>
      </Table>
    </div>
</template>
