<script setup lang="ts">
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet'
import {Button} from "@/components/ui/button";
import {
  IconEye, IconEyeOff,
  IconFilterOff,
  IconFilterShare,
  IconTrash,
  IconFilterX
} from "@tabler/icons-vue";
import DialogLabel from "@/helpers/DialogLabel.vue";
import {Input} from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {onMounted,  type Ref, ref, watch, watchEffect} from "vue";
import {
  TagsInput,
  TagsInputInput,
  TagsInputItem, TagsInputItemDelete,
  TagsInputItemText
} from "@/components/ui/tags-input";
import {Badge} from "@/components/ui/badge";
import {useBadgeFilter} from "@/composables/useBadgeFilter.ts";
import {badgesContainer, inputText} from "@/assets/cssFunctions.ts";
import type {DateRange} from "reka-ui";
import {alertSources} from "@/data/alertSources.ts";
import MyDateRangePicker from "@/helpers/MyDateRangePicker.vue";

const severity = ref<number[]>([0,1,2,3,4,5]);
const message = ref("")
const subject = ref("")
const source = ref("")
const origins = ref<string[]>([])
const createdDateRange = ref<DateRange>
({ start: undefined, end: undefined })
const closedDateRange = ref<DateRange>
({ start: undefined, end: undefined })

const { badgeListOpen, addBadge, existingBadge, matchedBadges, badgeSearch, availableBadges } = useBadgeFilter<string[] | null>(
  origins,
  alertSources,
  () => origins.value ?? []
)
onMounted(() => {
  badgeListOpen.value = false
})

watchEffect(() => {
  if(!severity.value.length)
    severity.value = [0,1,2,3,4,5]
})

const handleInputChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  badgeSearch.value = target.value;
};

const clearOrigins = () => {
  origins.value = [];
}

const clearOriginInput = () => {
  const input = document.getElementById('origin-input') as HTMLInputElement
  if (input) input.value = '';
  badgeSearch.value = ''
}

const originToggle = () => {
  badgeListOpen.value = !badgeListOpen.value
  clearOriginInput()
}

const clearFilters = () => {
  severity.value = [0,1,2,3,4,5]
  message.value = ""
  subject.value = ""
  source.value = ""
  origins.value = []
  badgeListOpen.value = false
  clearOriginInput()
  createdDateRange.value = {start: undefined, end: undefined}
  closedDateRange.value = {start: undefined, end: undefined}
}

const onCancel = () => {
  setTimeout(() => {
    clearFilters()
  }, 200)
}


const updateCreatedRange = (data: DateRange) => {
  createdDateRange.value = data
  console.log(data)
}

const updateClosedRange = (data: DateRange) => {
  closedDateRange.value = data
  console.log(data)
}
</script>

<template>
  <Sheet>
    <SheetTrigger as-child>
      <slot/>
    </SheetTrigger>
    <SheetContent side="left">
      <div class="w-full ">
        <SheetHeader >
          <SheetTitle>Filters</SheetTitle>
          <SheetDescription>Set search filters for alerts history</SheetDescription>
          <Button @click="clearFilters" variant="red_outline" class="text-md *:size-5!">Clear Filters<IconFilterOff/></Button>
        </SheetHeader>
        <div class="grid space-y-3 pl-4">
          <div>
            <DialogLabel text="Alert" for="subject-input"/>
            <Input
              placeholder="Alert contains..."
              v-model="subject"
              type="text"
              class="w-3/4"
              id="subject-input"
            />
          </div>
          <div>
            <DialogLabel text="Severity" for="severity-select"/>
              <Select
                multiple
                v-model="severity"
              >
                <SelectTrigger id="severity-select" class="cursor-pointer w-2/5">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent >
                  <SelectItem
                    :class='` cursor-pointer hover:bg-severity-${value}/70! `'
                    v-for="value in [0,1,2,3,4,5]" :key="value" :value="value">{{value}}</SelectItem>
                </SelectContent>
              </Select>
          </div>
          <div>
            <DialogLabel text="Message" for="message-input"/>
            <Input
              placeholder="Message contains..."
              v-model="message"
              type="text"
              class="w-3/4"
              id="message-input"
            />
          </div>
          <div>
            <DialogLabel text="Source" for="source-input"/>
            <Input
              placeholder="Source contains..."
              v-model="source"
              type="text"
              class="w-3/4"
              id="source-input"
          />
          </div>
          <div>
            <div class="flex mb-2 space-x-2">
              <DialogLabel text="Origin" for="origin-input"/>
              <Button
                @click="originToggle"
                :variant="badgeListOpen ? 'red_outline' : 'green_outline'"
                size="icon-sm">
                <component :is="badgeListOpen ? IconEyeOff :  IconEye"/>
              </Button>
              <Button v-if="badgeListOpen" @click="clearOrigins" size="icon-sm" variant="red_outline"><IconTrash/></Button>
            </div>
              <div  class="flex space-x-2">
                <Transition name="fade">
                  <TagsInput
                    v-show="badgeListOpen"
                    class=" w-3/4"
                    v-model="origins"
                    :class="  badgeListOpen ? `${inputText} rounded-[0.5rem_0.5rem_0_0]` : `${inputText}`"
                    type="search">
                    <TagsInputItem v-for="(origin, index) in origins" :key="index" :value="origin">
                      <TagsInputItemText />
                      <TagsInputItemDelete />
                    </TagsInputItem>
                    <TagsInputInput
                      id="origin-input"
                      @input="handleInputChange"
                      @keydown.enter="clearOriginInput"
                      placeholder="Origins..." />
                  </TagsInput>
                </Transition>
              </div>
            <Transition name="fade">
              <div
                v-if="badgeListOpen && matchedBadges.length > 0"
                :class="badgesContainer">
                <Badge
                  variant="origin"
                  class="mr-1 my-1"
                  @click="origins.push(tag)"
                  v-for="(tag, index) in matchedBadges" :key="index">{{tag}}
                </Badge>
              </div>
              <div
                v-else-if="badgeListOpen"
                :class="badgesContainer"
                class="h-10 text-center text-sm! text-comment"
              >No matched origins</div>
            </Transition>
            <Transition name="fade">
              <div v-if="!badgeListOpen">
                <Badge variant="origin" class="mr-1 my-1" v-for="(origin, index) in origins" :key="index">
                  {{ origin }}
                </Badge>
              </div>
            </Transition>
          </div>
          <MyDateRangePicker
            :range="createdDateRange"
            @update:range="updateCreatedRange"
            label-text="Created at - range"
            label-for="created-at"/>
          <MyDateRangePicker
            :range="closedDateRange"
            @update:range="updateClosedRange"
            label-text="Closed at - range"
            label-for="closed-at"/>
        </div>
      </div>
      <SheetFooter>
        <Button
          class="text-md"
          variant="outline"
          type="submit">Submit<IconFilterShare class="text-green-badge size-5"/></Button>
        <SheetClose as-child>
          <Button
            class="text-md"
            type="reset"
            @click="onCancel"
            variant="red_outline"
          >Cancel<IconFilterX class="size-5"/></Button>
        </SheetClose>
      </SheetFooter>
    </SheetContent>
  </Sheet>
</template>

