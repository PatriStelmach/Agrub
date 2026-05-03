<script setup lang="ts">
import { Icon } from '@iconify/vue'
import {
  DateRangePickerArrow,
  DateRangePickerCalendar,
  DateRangePickerCell,
  DateRangePickerCellTrigger,
  DateRangePickerContent,
  DateRangePickerField,
  DateRangePickerGrid,
  DateRangePickerGridBody,
  DateRangePickerGridHead,
  DateRangePickerGridRow,
  DateRangePickerHeadCell,
  DateRangePickerHeader,
  DateRangePickerHeading,
  DateRangePickerInput,
  DateRangePickerNext,
  DateRangePickerPrev,
  DateRangePickerRoot,
  DateRangePickerTrigger,
} from 'reka-ui'
import { getLocalTimeZone } from '@internationalized/date'
import DialogLabel from "@/helpers/DialogLabel.vue";

const props = defineProps<{
  labelText: string;
  labelFor: string;
}>()

const emits = defineEmits<{
  'update-from': [Date],
  'update-to': [Date],
}>()

const tz = getLocalTimeZone()
const now = new Date();
</script>

<template>
  <div class="flex flex-col gap-2">
    <DialogLabel :text="props.labelText"
      :for="props.labelFor"
    >
      Booking
    </DialogLabel>
    <DateRangePickerRoot
      :id="props.labelFor"
      :is-date-unavailable="date => date.toDate(tz) > now"
    >
      <DateRangePickerField
        v-slot="{ segments }"
        class="flex w-4/5 select-none bg-input/60 items-center rounded-lg text-center text-comment border shadow-sm p-1 data-invalid:border-red-badge"
      >
        <template
          v-for="item in segments.start"
          :key="item.part"
        >
          <DateRangePickerInput
            v-if="item.part === 'literal'"
            :part="item.part"
            type="start"
          >
            {{ item.value }}
          </DateRangePickerInput>
          <DateRangePickerInput
            v-else
            :part="item.part"
            class="rounded-md p-0.5 focus:outline-none focus:shadow-[0_0_5px_2px] focus:shadow-secondary data-placeholder:text-green-badge"
            type="start"
          >
            {{ item.value }}
          </DateRangePickerInput>
        </template>
        <span class="mx-2">

          -
        </span>
        <template
          v-for="item in segments.end"
          :key="item.part"
        >
          <DateRangePickerInput
            v-if="item.part === 'literal'"
            :part="item.part"
            type="end"
          >
            {{ item.value }}
          </DateRangePickerInput>
          <DateRangePickerInput
            v-else
            :part="item.part"
            class="rounded-md p-0.5 focus:outline-none focus:shadow-[0_0_5px_2px] focus:shadow-secondary data-placeholder:text-green-badge"
            type="end"
          >
            {{ item.value }}
          </DateRangePickerInput>
        </template>

        <DateRangePickerTrigger class="ml-4  focus:outline-none rounded p-1 cursor-pointer  hover:scale-110 duration-150">
          <Icon
            icon="radix-icons:calendar"
            class="w-4 h-4"
          />
        </DateRangePickerTrigger>
      </DateRangePickerField>

      <DateRangePickerContent
        :side-offset="4"
        class="rounded-xl z-99 bg-card border-2 shadow-sm will-change-[transform,opacity] data-[state=open]:data-[side=top]:animate-slideDownAndFade data-[state=open]:data-[side=right]:animate-slideLeftAndFade data-[state=open]:data-[side=bottom]:animate-slideUpAndFade data-[state=open]:data-[side=left]:animate-slideRightAndFade"
      >
        <DateRangePickerArrow class="fill-foreground" />
        <DateRangePickerCalendar
          v-slot="{ weekDays, grid }"
          class="p-4"
        >
          <DateRangePickerHeader class="flex items-center justify-between">
            <DateRangePickerPrev
              class="inline-flex items-center cursor-pointer text-primary justify-center rounded-md bg-transparent w-7 h-7 hover:bg-green-badge/50 active:scale-98 active:transition-all focus:shadow-[0_0_0_2px] focus:shadow-primary"
            >
              <Icon
                icon="radix-icons:chevron-left"
                class="w-4 h-4"
              />
            </DateRangePickerPrev>

            <DateRangePickerHeading class="text-sm text-comment font-medium" />
            <DateRangePickerNext
              class="inline-flex items-center cursor-pointer text-primary justify-center rounded-md bg-transparent w-7 h-7 hover:bg-green-badge/50 active:scale-98 active:transition-all focus:shadow-[0_0_0_2px] focus:shadow-primary"
            >
              <Icon
                icon="radix-icons:chevron-right"
                class="w-4 h-4"
              />
            </DateRangePickerNext>
          </DateRangePickerHeader>
          <div
            class="flex flex-col space-y-4 pt-4 sm:flex-row sm:space-x-4 sm:space-y-0"
          >
            <DateRangePickerGrid
              v-for="month in grid"
              :key="month.value.toString()"
              class="w-full border-collapse select-none space-y-1"
            >
              <DateRangePickerGridHead>
                <DateRangePickerGridRow class="mb-1 flex w-full justify-between">
                  <DateRangePickerHeadCell
                    v-for="day in weekDays"
                    :key="day"
                    class="w-8 rounded-md text-sm text-comment"
                  >
                    {{ day }}
                  </DateRangePickerHeadCell>
                </DateRangePickerGridRow>
              </DateRangePickerGridHead>
              <DateRangePickerGridBody>
                <DateRangePickerGridRow
                  v-for="(weekDates, index) in month.rows"
                  :key="`weekDate-${index}`"
                  class="flex w-full"
                >
                  <DateRangePickerCell
                    v-for="weekDate in weekDates"
                    :key="weekDate.toString()"
                    :date="weekDate"
                  >
                    <DateRangePickerCellTrigger
                      :day="weekDate"
                      :month="month.value"
                      class="relative flex items-center mx-1 justify-center rounded-md whitespace-nowrap text-sm font-normal text-primary w-8 h-8 outline-none focus:shadow-[0_0_0_2px] focus:shadow-primary data-[outside-view]:text-foreground/40 data-[selected]:bg-green-badge/50 data-[selected]:text-comment hover:bg-green-badge/70 data-[highlighted]:bg-green-badge/70 data-[unavailable]:pointer-events-none data-[unavailable]:text-input data-[unavailable]:line-through before:absolute before:top-[5px] before:hidden before:rounded-full before:w-1 before:h-1 before:bg-primary data-[today]:before:block data-[today]:before:bg-green-badge/80 "
                    />
                  </DateRangePickerCell>
                </DateRangePickerGridRow>
              </DateRangePickerGridBody>
            </DateRangePickerGrid>
          </div>
        </DateRangePickerCalendar>
      </DateRangePickerContent>
    </DateRangePickerRoot>
  </div>
</template>
