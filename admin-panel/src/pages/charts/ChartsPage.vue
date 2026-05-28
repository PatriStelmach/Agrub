<script setup lang="ts">

import TopH1Div from "@/helpers_components/TopH1Div.vue";
import AlertsCount from "@/pages/charts/AlertsCount.vue";
import {RangeCalendar} from "@/components/ui/range-calendar";
import {type Ref, ref} from "vue";
import type {DateRange} from "reka-ui";
import AckOrCloseTime from "@/pages/charts/AckOrCloseTime.vue";
import { getLocalTimeZone, today, fromDate, ZonedDateTime, CalendarDateTime, CalendarDate,  } from '@internationalized/date'
import type {Granularity, XYAnalytics} from "@/types/types.ts";
import AckAndCloseTime from "@/pages/charts/AckAndCloseTime.vue";



const locale = navigator.language
const tz = getLocalTimeZone()
const end = ref<CalendarDate | CalendarDateTime | ZonedDateTime>(today(getLocalTimeZone()))
const start = ref<CalendarDate | CalendarDateTime | ZonedDateTime>(end.value.subtract({ months: 14 }))
const ackValues = ref<XYAnalytics[]>([])
const closeValues = ref<XYAnalytics[]>([])
const currentGranularity = ref<Granularity>('MONTH')

const dateRange = ref({
  start,
  end,
}) as Ref<DateRange>
</script>

<template>
  <div>
    <TopH1Div h1="Analytics"/>
    <div class="max-h-[90vh] mx-6 overflow-auto grid grid-cols-1 gap-y-30 *:h-130">
      <AlertsCount
        :currentGranularity="currentGranularity"
        :locale="locale"
        :tz="tz"
        :end="end as ZonedDateTime"
        :start="start as ZonedDateTime"
      />
      <AckOrCloseTime
        :currentGranularity="currentGranularity"
        @update:values="xyAnalytics => ackValues = xyAnalytics"
        :locale="locale"
        :tz="tz"
        :end="end as ZonedDateTime"
        :start="start as ZonedDateTime"
        header="Average time to acknowledge an alert"
        :type="'avg-ack-time'"
      />
      <AckOrCloseTime
        :currentGranularity="currentGranularity"
        @update:values="xyAnalytics => closeValues = xyAnalytics"
        :locale="locale"
        :tz="tz"
        :end="end as ZonedDateTime"
        :start="start as ZonedDateTime"
        header="Average time to close an alert"
        :type="'avg-close-time'"
      />
      <AckAndCloseTime
        :currentGranularity="currentGranularity"
        header="Average acknowledge and close times compared"
        :start="start as ZonedDateTime"
        :end="end as ZonedDateTime"
        :locale="locale"
        :tz="tz"
        :closed-and-acked="{
          ack: ackValues,
          close: closeValues,
        }"
      />
    </div>
  </div>

</template>
