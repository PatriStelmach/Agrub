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
  IconCheck, IconEye, IconEyeOff,
  IconFilterOff,
  IconFilterShare,
  IconTrash,
  IconLockOpen, IconLockOpen2,
  IconX
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
import {onMounted, onUnmounted, type Ref, ref, watch, watchEffect} from "vue";
import {
  TagsInput,
  TagsInputInput,
  TagsInputItem, TagsInputItemDelete,
  TagsInputItemText
} from "@/components/ui/tags-input";
import {Badge} from "@/components/ui/badge";
import {useBadgeFilter} from "@/composables/useBadgeFilter.ts";
import type {MyPlugin} from "@/types/types.ts";
import {availableTags} from "@/data/tags.ts";
import {alertSources} from "@/data/alertSources.ts";
import {badgesContainer, inputText} from "@/assets/cssFunctions.ts";
import { getLocalTimeZone, today } from '@internationalized/date'
import type {DateRange} from "reka-ui";
import {
  DateRangeFieldInput,
  DateRangeFieldRoot,
} from 'reka-ui'
import MyDateRangePicker from "@/helpers/MyDateRangePicker.vue";

const severity = ref<number[]>([0,1,2,3,4,5]);
const message = ref("")
const subject = ref("")
const source = ref("")
const origins = ref<string[]>([])

const start = today(getLocalTimeZone())
const end = start.add({ days: 7 })
const dateRange = ref({
  start,
  end,
}) as Ref<DateRange>

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

const onCancel = () => {
  setTimeout(() => {
    severity.value = [0,1,2,3,4,5]
    message.value = ""
    subject.value = ""
    source.value = ""
    origins.value = []
    badgeListOpen.value = false
    clearOriginInput()
  }, 200)
}

</script>

<template>
  <Sheet>
    <SheetTrigger as-child>
      <slot/>
    </SheetTrigger>
    <SheetContent side="left" >
      <div class="mx-auto w-full ">
        <SheetHeader >
          <SheetTitle>Filters</SheetTitle>
          <SheetDescription>Set search filters for alerts history</SheetDescription>
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
          <div>
            <DialogLabel text="Status" for=""/>
            <div class="flex **:cursor-pointer *:flex *:items-center *:space-x-2">
              <div>
                <input type="checkbox" id="acknowledged"  :default-value="true"/>
                <DialogLabel class="pb-0 mb-0 text-comment text-sm" text="Acknowledged" for="acknowledged"/>
              </div>
              <div>
                <input type="checkbox" id="unacknowledged" :default-value="true"/>
                <DialogLabel class="pb-0 mb-0 text-comment text-sm" text="Unacknowledged" for="unacknowledged"/>
              </div>
            </div>
          </div>
          <MyDateRangePicker label-text="Created at - range" label-for="created-at"/>
          <MyDateRangePicker label-text="Closed at - range" label-for="closed-at"/>
        </div>

      </div>
      <SheetFooter class="mb-2">
        <Button
          variant="outline">Submit<IconFilterShare class="text-green-badge"/></Button>
        <SheetClose as-child>
          <Button
            @click="onCancel"
            variant="red_outline"
          >Cancel<IconFilterOff/></Button>
        </SheetClose>
      </SheetFooter>
    </SheetContent>
  </Sheet>
</template>

