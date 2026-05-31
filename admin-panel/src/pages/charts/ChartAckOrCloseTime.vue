<script setup lang="ts">
import { VisAxis, VisXYContainer, VisLine } from '@unovis/vue'
import { ChartContainer, ChartCrosshair, ChartLegendContent, ChartTooltip, ChartTooltipContent, componentToString } from '@/components/ui/chart'
import {bigNameLabel, smallNameLabel} from "@/assets/cssFunctions.ts"
import { computed} from "vue"
import { type Granularity, type XYAnalytics } from "@/types/types.ts"
import { fromDate } from '@internationalized/date'
import { getWeekNumber } from "reka-ui/date"
import {useColorMode} from "@vueuse/core";
import {CurveType} from "@unovis/ts";

const mode = useColorMode()
const props = defineProps<{
  header: string
  type: "avg-close-time" | "avg-ack-time"
  locale: string
  tz: string
  currentGranularity: Granularity
  rawAnalyticsData: XYAnalytics[]
  periodLabel: string
}>()

const formatDuration = (seconds: number) => {
  const totalSeconds = Math.round(seconds)

  const minutes = Math.floor(totalSeconds / 60)
  const remainingSeconds = totalSeconds % 60

  return `${minutes} min ${remainingSeconds.toString().padStart(2, '0')} s`
}

const averageValueFormatted = computed(() => {
  const values = props.rawAnalyticsData.map(x => x.y)
  return values ? formatDuration(values.reduce((sum, value) => sum + value, 0) / values.length) : '-'
});

const xAxisTickFormat = computed(() => {
  return (timestamp: number) => {
    const date = new Date(timestamp)
    const zonedTime = fromDate(date, props.tz)

    if (props.currentGranularity === 'DAY') {
      return date.toLocaleDateString(props.locale, { timeZone: props.tz, day: 'numeric', month: 'long', year: 'numeric' })
    }
    if (props.currentGranularity === 'WEEK') {
      return `week ${getWeekNumber(zonedTime)}, ${date.getFullYear()}`
    }
    return date.toLocaleDateString(props.locale, { timeZone: props.tz, month: 'short', year: 'numeric' })
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
      return `week ${getWeekNumber(zonedTime)}, ${date.getFullYear()}`
    }
    return date.toLocaleDateString(props.locale, { timeZone: props.tz, month: 'short', year: 'numeric' })
  }
})

const yAxisTickFormat = (value: number) => {
  return formatDuration(value)
}

const time = computed(()=> ({
  y : {
    label: props.type === 'avg-close-time' ? "Average close time in minutes:" : "Average acknowledge in minutes: ",
    color: props.type === 'avg-close-time' ? '#F40031FF' : mode.value === 'light' ? '#48CF00FF' : '#08A800FF',
    text: averageValueFormatted.value
  },
}))


</script>

<template>
  <div>
    <h1 :class="`${bigNameLabel} mb-6 text-center`">
      {{ `Average ${props.type === 'avg-ack-time' ? 'acknowledge' : 'close'} time in last ${periodLabel}` }}
    </h1>

    <ChartContainer
      :cursor="true"
      :config="time"
      class="w-full" >
      <VisXYContainer
        v-if="rawAnalyticsData.length > 0"
        :y-domain="[Math.min(...rawAnalyticsData.map(d => Math.max(d.y*0.995))), Math.max(...rawAnalyticsData.map(d => Math.max(d.y*1.02)))]"
        :data="rawAnalyticsData" >

        <VisLine
          :fallbackValue="rawAnalyticsData.map(i => i.y).reduce((sum, value) => sum + value, 0) / rawAnalyticsData.length"
          :curveType="CurveType.Linear"
          :highlightOnHover="true"
          :line-width="3"
          :x="(d: any) => d.x"
          :y="(d: any) => d.y"
          :color="time['y'].color"
        />

        <VisAxis
          type="x"
          :x="(d: any) => d.date"
          :tick-format="xAxisTickFormat"
          :tick-values="rawAnalyticsData.map(d => d.x)"
          :tick-line="true"
          :domain-line="true"
          :grid-line="true"
        />

        <VisAxis type="y" :tick-format="yAxisTickFormat" :grid-line="true" :tick-line="true" :domain-line="true" />

        <ChartTooltip className="**:space-x-2" />

        <ChartCrosshair
          :template="componentToString(time, ChartTooltipContent, {
            labelFormatter: tooltipLabelFormatter,
            valueFormatter: formatDuration,
          })"
          :color="time['y'].color"
        />
      </VisXYContainer>
      <div
        class="absolute top-1/2 left-1/2 -translate-y-1/2 -translate-x-1/2 border-b-3 border-red-badge"
        v-else>
        <h1 :class="smallNameLabel">No data to show</h1>
      </div>
      <ChartLegendContent/>
    </ChartContainer>
  </div>
</template>
