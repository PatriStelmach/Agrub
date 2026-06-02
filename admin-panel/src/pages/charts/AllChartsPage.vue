<script setup lang="ts">

import TopH1Div from "@/helpers_components/TopH1Div.vue";
import ChartAlertsCountBySeverity from "@/pages/charts/ChartAlertsCountBySeverity.vue";
import { IconCalendarQuestion, IconSend2, IconLoader} from "@tabler/icons-vue";
import {computed, onMounted, type Ref, ref} from "vue";
import type {DateRange} from "reka-ui";
import ChartAckOrCloseTime from "@/pages/charts/ChartAckOrCloseTime.vue";
import { getLocalTimeZone, today, ZonedDateTime, CalendarDateTime, CalendarDate,  } from '@internationalized/date'
import type {CumulativeData, Granularity} from "@/types/types.ts";
import ChartAckAndCloseTime from "@/pages/charts/ChartAckAndCloseTime.vue";
import {getAnalyticsAlertsCountRequest} from "@/helpers_functions/requests.ts";
import {toast} from "vue-sonner";
import {Button} from "@/components/ui/button";
import {ButtonGroup} from "@/components/ui/button-group";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {RangeCalendar} from "@/components/ui/range-calendar";
import MyFieldLabel from "@/helpers_components/form/MyFieldLabel.vue";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import ChartAllAlerts from "@/pages/charts/ChartAllAlerts.vue";
import MyTagInput from "@/helpers_components/MyTagInput.vue";
import {alertOriginType} from "@/data/alertOriginType.ts";

const locale = navigator.language
const tz = getLocalTimeZone()

onMounted(async () => {
  await onDateRangeChange()
})

const origin = ref<string[]>([])
const end = ref<CalendarDate | CalendarDateTime | ZonedDateTime>(today(tz).add({ days: 1}))
const start = ref<CalendarDate | CalendarDateTime | ZonedDateTime>(end.value.subtract({ days: 7 }))
const currentGranularity = ref<Granularity>('DAY')
const granularityAfterReload = ref<Granularity>(currentGranularity.value)
const areChartsLoading = ref<boolean>(true)
const rawAnalyticsData = ref<CumulativeData>()
const isPopoverOpen = ref(false)

const dateRange = ref({
  start,
  end,
}) as Ref<DateRange>

const dateRangeAfterReload = ref<DateRange>(dateRange.value)

const periodLabel = computed(() => {
  if(dateRangeAfterReload.value.start && dateRangeAfterReload.value.end){
    const timestamp =
      dateRangeAfterReload.value.end!.toDate(tz).getTime() -
      dateRangeAfterReload.value.start!.toDate(tz).getTime()

    const diffDays = Math.ceil(timestamp / (1000 * 60 * 60 * 24))

    switch (granularityAfterReload.value) {
      case 'DAY':
        return `${diffDays} days`

      case 'WEEK':
        return `${Math.ceil(diffDays / 7)} weeks`

      case 'MONTH':
        return `${Math.ceil(diffDays / 30)} months`
    }
  }
  return ''
})

const onDateRangeChange = async () => {
  areChartsLoading.value = true
  await getAnalyticsAlertsCountRequest(
    dateRange.value.start as ZonedDateTime,
    dateRange.value.end as ZonedDateTime,
    currentGranularity.value,
    origin.value
  )
    .catch((e) => toast.error(`Error retrieving Analytics data: ${e}`))
    .then((res) => {
      rawAnalyticsData.value = res as CumulativeData
      granularityAfterReload.value = currentGranularity.value
      dateRangeAfterReload.value = dateRange.value
    })
    .finally(() => {
      areChartsLoading.value = false
      isPopoverOpen.value = false
    })
}

</script>

<template>
  <div>
    <TopH1Div h1="Analytics">
      <ButtonGroup>
        <Popover v-model:open="isPopoverOpen">
          <PopoverTrigger as-child>
            <Button variant="green_outline"> Charts configuration<IconCalendarQuestion/>
            </Button>
          </PopoverTrigger>
          <PopoverContent class="w-fit p-5 shadow-xl bg-background">
            <RangeCalendar
              v-model="dateRange"
              class="rounded-md border-2  shadow-xl"
              :locale="locale"
              :number-of-months="3"
              :disable-days-outside-current-view="true"
              :isDateDisabled="date => date > today(tz).add({ days: 1 })"
            />
            <div class="mt-3">
              <div class="flex space-x-2 items-baseline">
                <h1 :class="bigNameLabel">Granularity:</h1>
                <div class="flex *:cursor-pointer space-x-2">
                  <MyFieldLabel text="DAYS" for="days" />
                  <input
                    v-model="currentGranularity"
                    id="days"
                    type="radio"
                    value="DAY"
                    name="granularity"
                  />
                </div>

                <div class="flex *:cursor-pointer space-x-2">
                  <MyFieldLabel text="WEEKS" for="weeks" />
                  <input
                    v-model="currentGranularity"
                    id="weeks"
                    type="radio"
                    value="WEEK"
                    name="granularity"
                  />
                </div>

                <div class="flex *:cursor-pointer space-x-2">
                  <MyFieldLabel text="MONTHS" for="months" />
                  <input
                    v-model="currentGranularity"
                    id="months"
                    type="radio"
                    value="MONTH"
                    name="granularity"
                  />
                </div>

              </div>
            </div>
            <div class="flex space-x-2 items-baseline ">
              <h1 :class="bigNameLabel">Date range:</h1>
              <span class="text-label font-semibold">{{ dateRange.start }} — {{ dateRange.end }}</span>
              <Button
                @click="onDateRangeChange"
                class="ml-auto"
                variant="green_outline"
              >
                Submit
                <IconLoader v-if="areChartsLoading" class="animate-spin"/>
                <IconSend2 v-else/>
              </Button>
            </div>
            <div>
              <MyTagInput
                class="w-3/4"
                :all-tags="alertOriginType"
                input-id="origin-input"
                :can-add-new="false"
                v-model:tags="origin"
                tagsLabel="Origins: "
              />
            </div>
          </PopoverContent>
        </Popover>
      </ButtonGroup>

    </TopH1Div>
      <Transition
        class="max-h-[90vh] px-6 overflow-auto grid grid-cols-1 gap-y-30 *:h-[60vh]"
        tag="div"
        name="fade">
        <div v-if="areChartsLoading">
          <BigLoadingBlock
            v-for=" i in [1,2,3,5]"
            :key="i"
            class="h-140"
          />
        </div>
        <div v-else>
          <ChartAllAlerts
            :locale="locale"
            :tz="tz"
            :start="dateRangeAfterReload.start as ZonedDateTime"
            :end="dateRangeAfterReload.end as ZonedDateTime"
            :currentGranularity="granularityAfterReload"
            :rawAnalyticsData="rawAnalyticsData?.alerts ?? []"
            :period-label="periodLabel"
          />
          <ChartAlertsCountBySeverity
            :periodLabel="periodLabel"
            :rawAnalyticsData="rawAnalyticsData?.severity ?? []"
            :currentGranularity="granularityAfterReload"
            :locale="locale"
            :tz="tz"
            :start="dateRangeAfterReload.start as ZonedDateTime"
            :end="dateRangeAfterReload.end as ZonedDateTime"
          />
          <ChartAckOrCloseTime
            :periodLabel="periodLabel"
            :rawAnalyticsData="rawAnalyticsData?.ack ?? []"
            :currentGranularity="granularityAfterReload"
            :locale="locale"
            :tz="tz"
            :start="dateRangeAfterReload.start as ZonedDateTime"
            :end="dateRangeAfterReload.end as ZonedDateTime"
            header="Average time to acknowledge an alert"
            :type="'avg-ack-time'"
          />
          <ChartAckOrCloseTime
            :periodLabel="periodLabel"
            :rawAnalyticsData="rawAnalyticsData?.close ?? []"
            :currentGranularity="granularityAfterReload"
            :locale="locale"
            :tz="tz"
            :start="dateRangeAfterReload.start as ZonedDateTime"
            :end="dateRangeAfterReload.end as ZonedDateTime"
            header="Average time to close an alert"
            :type="'avg-close-time'"
          />
          <ChartAckAndCloseTime
            :periodLabel="periodLabel"
            :currentGranularity="granularityAfterReload"
            header="Average acknowledge and close times compared"
            :start="dateRangeAfterReload.start as ZonedDateTime"
            :end="dateRangeAfterReload.end as ZonedDateTime"
            :locale="locale"
            :tz="tz"
            :closed-and-acked="{
          ack: rawAnalyticsData?.ack ?? [],
          close: rawAnalyticsData?.close ?? [],
        }"
          />
        </div>
      </Transition>

  </div>

</template>


