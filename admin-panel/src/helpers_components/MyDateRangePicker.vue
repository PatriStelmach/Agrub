<script setup lang="ts">
import {
  type DateRange,
  DateRangeFieldInput,
  DateRangeFieldRoot,
} from 'reka-ui'
import {now, getLocalTimeZone,  } from '@internationalized/date'
import DialogLabel from "@/helpers_components/DialogLabel.vue";

const range = defineModel<DateRange>('range', { required: true })
const props = defineProps<{
  labelText: string;
  labelFor: string;
}>()

const locale = navigator.language
const tz = getLocalTimeZone()

</script>

<template>
  <div class="flex flex-col gap-2">
    <DialogLabel :text="props.labelText"
      :for="props.labelFor"
    >
    </DialogLabel>
    <DateRangeFieldRoot
      class="flex w-95/100 text-sm select-none bg-input/60 items-center rounded-lg text-center text-comment border shadow-sm p-1 data-invalid:border-red-badge"
      v-model="range"
      :weekStartsOn="1"
      :hourCycle="24"
      :granularity="'minute'"
      :locale="locale"
      :numberOfMonths="1"
      :id="props.labelFor"
      :maxValue="now(tz)"
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
            class="rounded-md p-0.5 focus:outline-none focus:shadow-[0_0_5px_2px] focus:shadow-green-badge data-placeholder:text-comment"
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
            class="rounded-md p-0.5 focus:outline-none focus:shadow-[0_0_5px_2px] focus:shadow-green-badge data-placeholder:text-comment"
            type="end"
          >
            {{ item.value }}
          </DateRangeFieldInput>
        </template>


    </DateRangeFieldRoot>
  </div>
</template>
