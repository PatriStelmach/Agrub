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
import { Button } from "@/components/ui/button";
import {
  IconEye, IconEyeOff,
  IconFilterOff,
  IconFilterShare,
  IconTrash,
  IconFilterX
} from "@tabler/icons-vue";
import DialogLabel from "@/helpers/DialogLabel.vue";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import { computed, onMounted, reactive } from "vue";
import {
  TagsInput,
  TagsInputInput,
  TagsInputItem, TagsInputItemDelete,
  TagsInputItemText
} from "@/components/ui/tags-input";
import { Badge } from "@/components/ui/badge";
import { useBadgeFilter } from "@/composables/useBadgeFilter.ts";
import { badgesContainer, inputText } from "@/assets/cssFunctions.ts";
import type { DateRange } from "reka-ui";
import { alertSources } from "@/data/alertSources.ts";
import { now, getLocalTimeZone, CalendarDateTime } from '@internationalized/date'
import MyDateRangePicker from "@/helpers/MyDateRangePicker.vue";
import type {AlertHistoryFilters} from "@/types/types.ts";

const emit = defineEmits<{
  'update:filters': [AlertHistoryFilters]
}>()

const tz = getLocalTimeZone()
const currentTime = now(tz)

const filters = reactive({
  severity: [] as number[],
  message: undefined as string | undefined,
  subject: undefined as string | undefined,
  source: undefined as string | undefined,
  origins: [] as string[],
  createdDateRange: { start: undefined, end: undefined } as DateRange,
  closedDateRange: { start: undefined, end: undefined } as DateRange,
  ack: undefined as boolean | undefined,
  unack: undefined as boolean | undefined,
})

const isCreatedValid = computed(() => {
  const { start, end } = filters.createdDateRange;
  if (!start && !end) return true;
  const startAfterEnd = start && end && start.compare(end as CalendarDateTime) > 0;
  const inFuture = (start && start.compare(currentTime) > 0) ||
    (end && end.compare(currentTime) > 0);
  return !startAfterEnd && !inFuture;
});

const isClosedValid = computed(() => {
  const { start, end } = filters.closedDateRange
  if (!start && !end) return true;
  const startAfterEnd = start && end && start.compare(end as CalendarDateTime) > 0;
  const inFuture = (start && start.compare(currentTime) > 0) ||
    (end && end.compare(currentTime) > 0);
  return !startAfterEnd && !inFuture;
});

const areRangesInterValid = computed(() => {
  const latestCreated = filters.createdDateRange.end ?? filters.createdDateRange.start
  const earliestClosed = filters.closedDateRange.start ?? filters.closedDateRange.end
  if (latestCreated && earliestClosed) {
    return latestCreated.compare(earliestClosed as CalendarDateTime) <= 0;
  }
  return true;
});

const {badgeListOpen, addExistingBadge, matchedBadges, badgeSearch } = useBadgeFilter<string[] | null>(
  computed({
    get: () => filters.origins,
    set: (val) => filters.origins = val || []
  }),
  alertSources,
  () => filters.origins
)

onMounted(() => {
  badgeListOpen.value = false
})

const handleInputChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  badgeSearch.value = target.value;
};

const clearOrigins = () => {
  filters.origins = [];
}

const clearOriginInput = () => {
  const input = document.getElementById('origin-input') as HTMLInputElement
  if (input) input.value = '';
  addExistingBadge()
}

const originToggle = () => {
  badgeListOpen.value = !badgeListOpen.value
  clearOriginInput()
}

const clearFilters = () => {
  filters.severity = []
  filters.message = undefined
  filters.subject = undefined
  filters.source = undefined
  filters.unack = undefined
  filters.ack = undefined
  filters.origins = []
  filters.createdDateRange = { start: undefined, end: undefined }
  filters.closedDateRange = { start: undefined, end: undefined }
  badgeListOpen.value = false
  clearOriginInput()
}

const onCancel = () => {
  setTimeout(() => {
    clearFilters()
  }, 200)
}

const onSubmit = () => {
  emit("update:filters", {
    severity: filters.severity,
    message: filters.message,
    subject: filters.subject,
    origin: filters.origins,
    source: filters.source,
    ack: filters.ack,
    unack: filters.unack,
    createdDateFrom: filters.createdDateRange.start?.toDate(tz).toISOString(),
    createdDateTo: filters.createdDateRange.end?.toDate(tz).toISOString(),
    closedDateFrom: filters.closedDateRange.start?.toDate(tz).toISOString(),
    closedDateTo: filters.closedDateRange.end?.toDate(tz).toISOString(),
  })
}
</script>

<template>
  <Sheet>
    <SheetTrigger as-child>
      <slot />
    </SheetTrigger>
    <SheetContent side="left">
      <div class="w-full">
        <SheetHeader>
          <SheetTitle>Filters</SheetTitle>
          <SheetDescription>Set search filters for alerts history</SheetDescription>
          <Button @click="clearFilters" variant="red_outline" class="text-md *:size-5!">
            Clear Filters <IconFilterOff />
          </Button>
        </SheetHeader>

        <div class="grid space-y-3 pl-4">
          <!-- Alert Subject -->
          <div>
            <DialogLabel text="Alert" for="subject-input" />
            <Input placeholder="Alert contains..." v-model="filters.subject" type="text" class="w-3/4" id="subject-input" />
          </div>

          <!-- Severity -->
          <div>
            <DialogLabel text="Severity" for="severity-select" />
            <Select multiple v-model="filters.severity">
              <SelectTrigger id="severity-select" class="cursor-pointer w-2/5">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem
                  :class="`cursor-pointer hover:bg-severity-${value}/70!`"
                  v-for="value in [0, 1, 2, 3, 4, 5]" :key="value" :value="value">
                  {{ value }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>

          <!-- Message -->
          <div>
            <DialogLabel text="Message" for="message-input" />
            <Input placeholder="Message contains..." v-model="filters.message" type="text" class="w-3/4" id="message-input" />
          </div>

          <!-- Source -->
          <div>
            <DialogLabel text="Source" for="source-input" />
            <Input placeholder="Source contains..." v-model="filters.source" type="text" class="w-3/4" id="source-input" />
          </div>

          <!-- Origin -->
          <div>
            <div class="flex mb-2 space-x-2">
              <DialogLabel text="Origin" for="origin-input" />
              <Button @click="originToggle" :variant="badgeListOpen ? 'red_outline' : 'green_outline'" size="icon-sm">
                <component :is="badgeListOpen ? IconEyeOff : IconEye" />
              </Button>
              <Button v-if="badgeListOpen" @click="clearOrigins" size="icon-sm" variant="red_outline">
                <IconTrash />
              </Button>
            </div>
            <div class="flex space-x-2">
              <Transition name="fade">
                <TagsInput
                  v-show="badgeListOpen"
                  v-model="filters.origins"
                  :class="badgeListOpen ? `${inputText} rounded-[0.5rem_0.5rem_0_0]` : `${inputText}`"
                  class="w-3/4">
                  <TagsInputItem v-for="(origin, index) in filters.origins" :key="index" :value="origin">
                    <TagsInputItemText />
                    <TagsInputItemDelete />
                  </TagsInputItem>
                  <TagsInputInput id="origin-input" @input="handleInputChange" @keydown.enter="clearOriginInput" placeholder="Origins..." />
                </TagsInput>
              </Transition>
            </div>
            <Transition name="fade">
              <div v-if="badgeListOpen && matchedBadges.length > 0" :class="badgesContainer">
                <Badge
                  variant="origin" class="mr-1 my-1"
                  @click="filters.origins.push(tag)"
                  v-for="(tag, index) in matchedBadges" :key="index">
                  {{ tag }}
                </Badge>
              </div>
              <div v-else-if="badgeListOpen" :class="badgesContainer" class="h-10 text-center text-sm! text-comment">
                No matched origins
              </div>
            </Transition>
            <Transition name="fade">
              <div v-if="!badgeListOpen">
                <Badge variant="origin" class="mr-1 my-1" v-for="(origin, index) in filters.origins" :key="index">
                  {{ origin }}
                </Badge>
              </div>
            </Transition>
          </div>

          <!-- Status / Checkboxes -->
          <div>
            <DialogLabel text="Status" />
            <div class="flex **:cursor-pointer *:flex *:items-center *:space-x-2">
              <div>
                <input v-model="filters.ack" type="checkbox" id="acknowledged" />
                <DialogLabel class="pb-0 mb-0 text-comment text-sm" text="Unacknowledged" for="acknowledged" />
                <input v-model="filters.unack" type="checkbox" id="unacknowledged" />
                <DialogLabel class="pb-0 mb-0 text-comment text-sm" text="Acknowledged" for="unacknowledged" />
              </div>
            </div>
          </div>

          <!-- Date Pickers -->
          <MyDateRangePicker
            v-model:range="filters.createdDateRange as DateRange"
            labelText="Created at - range"
            labelFor="created-at"
          />
          <Transition name="fade">
            <span class="text-red-badge!" v-if="!isCreatedValid">Invalid date</span>
          </Transition>
          <Transition name="fade">
            <span class="text-red-badge!" v-if="!areRangesInterValid">Created date cannot be later than closed date</span>
          </Transition>

          <MyDateRangePicker
            v-model:range="filters.closedDateRange as DateRange"
            labelText="Closed at - range"
            labelFor="closed-at"
          />
          <Transition name="fade">
            <span class="text-red-badge!" v-if="!isClosedValid">Invalid date</span>
          </Transition>
        </div>
      </div>

      <SheetFooter>
        <SheetClose as-child>
          <Button
            @click.stop="onSubmit"
            :disabled="!isCreatedValid || !isClosedValid || !areRangesInterValid"
            class="text-md" type="button" variant="outline">
            Submit <IconFilterShare class="text-green-badge size-5" />
          </Button>
        </SheetClose>
        <SheetClose as-child>
          <Button class="text-md" @click="onCancel" variant="red_outline">
            Cancel <IconFilterX class="size-5" />
          </Button>
        </SheetClose>
      </SheetFooter>
    </SheetContent>
  </Sheet>
</template>
