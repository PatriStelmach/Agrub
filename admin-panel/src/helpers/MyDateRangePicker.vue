<script setup lang="ts">
import { Icon } from '@iconify/vue'
import {
  type DateRange,
  DateRangeFieldInput,
  DateRangeFieldRoot,
} from 'reka-ui'
import { getLocalTimeZone } from '@internationalized/date'
import DialogLabel from "@/helpers/DialogLabel.vue";
import {computed} from "vue";
import {now } from '@internationalized/date';

const props = defineProps<{
  labelText: string;
  labelFor: string;
  range: DateRange
}>()

const emits = defineEmits<{
  'update:range': [DateRange]
}>()

const rangeModel = computed<DateRange>({
  get: () => props.range,
  set: (val) => {
    emits('update:range', val)
  }
})

const locale = navigator.language
const tz = getLocalTimeZone()

const checkUnavailableDate = () => {
  if(props.range.end && props.range.start)
    return props.range.end.compare(now(tz)) > 0 || props.range.start.compare(now(tz)) > 0
  return false
}
</script>

<template>
  <div class="flex flex-col gap-2">
    <DialogLabel :text="props.labelText"
      :for="props.labelFor"
    >
    </DialogLabel>
    <DateRangeFieldRoot
      class="flex w-95/100 select-none bg-input/60 items-center rounded-lg text-center text-comment border shadow-sm p-1 data-invalid:border-red-badge"
      v-model="rangeModel"
      :weekStartsOn="1"
      :hourCycle="24"
      :granularity="'minute'"
      :locale="locale"
      :numberOfMonths="1"
      :id="props.labelFor"
      :isDateUnavailable=" checkUnavailableDate"
      v-slot="{ segments }"
    >
        <template
          v-for="item in segments.start"
          :key="item.part"
        >
          <DateRangeFieldInput
            v-if="item.part === 'literal'"
            :part="item.part"
            type="start"
          >
            {{ item.value }}
          </DateRangeFieldInput>
          <DateRangeFieldInput
            v-else
            :part="item.part"
            class="rounded-md p-0.5 focus:outline-none focus:shadow-[0_0_5px_2px] focus:shadow-green-badge data-placeholder:text-green-badge"
            type="start"
          >
            {{ item.value }}
          </DateRangeFieldInput>
        </template>
        <span class="mx-2">
          -
        </span>
        <template
          v-for="item in segments.end"
          :key="item.part"
        >
          <DateRangeFieldInput
            v-if="item.part === 'literal'"
            :part="item.part"
            type="end"
          >
            {{ item.value }}
          </DateRangeFieldInput>
          <DateRangeFieldInput
            v-else
            :part="item.part"
            class="rounded-md p-0.5 focus:outline-none focus:shadow-[0_0_5px_2px] focus:shadow-green-badge data-placeholder:text-green-badge"
            type="end"
          >
            {{ item.value }}
          </DateRangeFieldInput>
        </template>


    </DateRangeFieldRoot>
  </div>
</template>
