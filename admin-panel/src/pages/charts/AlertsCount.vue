<script setup lang="ts">

import { VisAxis, VisGroupedBar, VisXYContainer, VisLine } from '@unovis/vue'
import { ChartContainer, ChartCrosshair, ChartLegendContent, ChartTooltip, ChartTooltipContent, componentToString } from '@/components/ui/chart'
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {computed, onMounted, ref} from "vue";
import {type AlertCountAnalytics, alertCountChartConfig, type ChartDataPoint, type Granularity} from "@/types/types.ts";
import {getAnalyticsAlertsCount} from "@/helpers_functions/requests.ts";
import { fromDate, ZonedDateTime, CalendarDateTime, CalendarDate,  } from '@internationalized/date'
import {toast} from "vue-sonner";
import {getWeekNumber} from "reka-ui/date";


const props = defineProps<{
  start: CalendarDate | CalendarDateTime | ZonedDateTime
  end: CalendarDate | CalendarDateTime | ZonedDateTime
  locale: string
  tz: string
  currentGranularity: Granularity
}>()

const rawAnalyticsData = ref<AlertCountAnalytics[]>([])
const isChartLoading = ref<boolean>(true);

onMounted(async () => {
  await getAnalyticsAlertsCount(props.start as ZonedDateTime, props.end as ZonedDateTime, "MONTH", "alerts-count")
    .catch((e) => toast.error(`Error retrieving AlertCountAnalytics data: ${e}`))
    .then((res) => rawAnalyticsData.value = res)
    .finally(() => isChartLoading.value = false)
})

const chartData = computed<ChartDataPoint[]>(() => {
  return rawAnalyticsData.value.map((item) => {
    return {
      date: item.x,
      0: item.severities[0] || 0,
      1: item.severities[1] || 0,
      2: item.severities[2] || 0,
      3: item.severities[3] || 0,
      4: item.severities[4] || 0,
      5: item.severities[5] || 0,
    }
  })
})

const xAxisTickFormat = computed(() => {
  return (timestamp: number) => {
    const date = new Date(timestamp)
    const zonedTime = fromDate(date, props.tz)

    if (props.currentGranularity === 'DAY') {
      return date.toLocaleDateString(props.locale, { timeZone: props.tz, weekday: 'short' })
    }
    if (props.currentGranularity === 'WEEK') {
      return `week ${getWeekNumber(zonedTime)}`
    }
    return date.toLocaleDateString(props.locale, { timeZone: props.tz, month: 'short' })
  }
})


const tooltipLabelFormatter = computed(() => {

  return (d: number | Date) => {
    const date = new Date(d)
    const zonedTime = fromDate(date, props.tz)

    if (props.currentGranularity === 'DAY') {
      return date.toLocaleDateString(props.locale, { timeZone: props.tz, day: 'numeric', month: 'long', year: 'numeric' })
    }
    if (props.currentGranularity === 'WEEK') {
      return `Week ${getWeekNumber(zonedTime)}, ${date.getFullYear()}`
    }
      return date.toLocaleDateString(props.locale, { timeZone: props.tz, month: 'long', year: 'numeric' })
  }

})

const yAxisTickFormat = (value: number) => {
  if (value % 1 !== 0) return ''

  return Intl.NumberFormat('pl-PL', {
    notation: 'compact',
    compactDisplay: 'short',
  }).format(value)
}

</script>

<template>
  <div>
    <h1 :class="`${bigNameLabel} mb-6 text-center`">Alerts amount in period of time</h1>

    <ChartContainer :config="alertCountChartConfig" class="w-full">
      <VisXYContainer :data="chartData">
        <VisGroupedBar
          :x="(d: ChartDataPoint) => d.date"
          :y="[
            (d: ChartDataPoint) => d[0],
            (d: ChartDataPoint) => d[1],
            (d: ChartDataPoint) => d[2],
            (d: ChartDataPoint) => d[3],
            (d: ChartDataPoint) => d[4],
            (d: ChartDataPoint) => d[5]
          ]"
          :color="[
            alertCountChartConfig['0']?.color,
            alertCountChartConfig['1']?.color,
            alertCountChartConfig['2']?.color,
            alertCountChartConfig['3']?.color,
            alertCountChartConfig['4']?.color,
            alertCountChartConfig['5']?.color
          ]"
          :rounded-corners="3"
          bar-padding="0.35"
          group-padding="0.1"
        />

        <VisAxis
          type="x"
          :x="(d: ChartDataPoint) => d.date"
          :tick-format="xAxisTickFormat"
          :tick-values="chartData.map(d => d.date)"
          :tick-line="false"
          :domain-line="true"
          :grid-line="false"
        />

        <VisAxis type="y" :tick-format="yAxisTickFormat" :grid-line="true" :tick-line="true" :domain-line="true" />
        <ChartTooltip />
        <ChartCrosshair

          :template="componentToString(alertCountChartConfig, ChartTooltipContent, {
            labelFormatter: tooltipLabelFormatter

          })"
          :color="[
            alertCountChartConfig['0']?.color,
            alertCountChartConfig['1']?.color,
            alertCountChartConfig['2']?.color,
            alertCountChartConfig['3']?.color,
            alertCountChartConfig['4']?.color,
            alertCountChartConfig['5']?.color
          ]"
        />
      </VisXYContainer>
      <ChartLegendContent />
    </ChartContainer>
  </div>
</template>
