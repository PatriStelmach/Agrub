<script setup lang="ts">
import { VisAxis, VisXYContainer, VisLine } from '@unovis/vue'
import { ChartContainer, ChartCrosshair, ChartLegendContent, ChartTooltip, ChartTooltipContent, componentToString } from '@/components/ui/chart'
import {bigNameLabel} from "@/assets/cssFunctions.ts"
import { computed } from "vue"
import { type Granularity, type XYAnalytics } from "@/types/types.ts"
import { fromDate} from '@internationalized/date'
import { getWeekNumber } from "reka-ui/date"
import { useColorMode } from "@vueuse/core"
import {CurveType} from "@unovis/ts";

const props = defineProps<{
  header: string
  locale: string
  tz: string
  currentGranularity: Granularity
  periodLabel: string
  closedAndAcked: {
    ack: XYAnalytics[],
    close: XYAnalytics[]
  }
}>()

const mode = useColorMode()

const formatDuration = (seconds: number) => {
  const totalSeconds = Math.round(seconds)

  const minutes = Math.floor(totalSeconds / 60)
  const remainingSeconds = totalSeconds % 60

  return `${minutes} min ${remainingSeconds.toString().padStart(2, '0')} s`
}

const averageValueFormatted = computed(() => {
  const acks = props.closedAndAcked?.ack.map(x => x.y) ?? [];
  const closed = props.closedAndAcked?.close.map(x => x.y) ?? [];

  if (acks.length === 0 || closed.length === 0) return { ack: '-', closed: '-' };

  return  {
    ack: formatDuration(acks.reduce((sum, value) => sum + value, 0) / acks.length),
    closed: formatDuration(closed.reduce((sum, value) => sum + value, 0) / closed.length) }
});

const timeConfig = computed(() => ({
  close: {
    label: "Average closing time:",
    color: '#F40031FF',
    text: averageValueFormatted.value.closed
  },
  ack: {
    label: "Average acknowledge time:",
    color: mode.value === 'light' ? '#48CF00FF' : '#08A800FF',
    text: averageValueFormatted.value.ack
  }
}))

const chartData = computed(() => {
  const ackArr = props.closedAndAcked?.ack || []
  const closeArr = props.closedAndAcked?.close|| []

  const mergedMap = new Map<number, { date: number; ack: number; close: number }>()

  ackArr.forEach(item => {
    mergedMap.set(item.x, { date: item.x, ack: item.y, close: 0 })
  })

  closeArr.forEach(item => {
    if (mergedMap.has(item.x)) {
      mergedMap.get(item.x)!.close = item.y
    } else {
      mergedMap.set(item.x, { date: item.x, ack: 0, close: item.y })
    }
  })

  return Array.from(mergedMap.values()).sort((a, b) => a.date - b.date)
})

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

</script>

<template>
  <div class="mb-40!">
    <h1 :class="`${bigNameLabel} mb-6 text-center`">
      {{ `Comparison of average acknowledge and close time in last ${periodLabel}` }}
    </h1>

    <ChartContainer
      :cursor="true"
      :config="timeConfig"
      class="w-full">
      <VisXYContainer
        v-if="chartData.length > 0"
        :data="chartData"
        :y-domain="[0, Math.max(...chartData.map(d => Math.max(d.ack,d.close*1.05)))]">

        <VisLine
          :curveType="CurveType.Linear"
          :highlightOnHover="true"
          :line-width="3"
          :x="(d: any) => d.date"
          :y="(d: any) => d.close"
          :color="timeConfig.close.color"
        />

        <VisLine
          :curveType="CurveType.Linear"
          :highlightOnHover="true"
          :line-width="3"
          :x="(d: any) => d.date"
          :y="(d: any) => d.ack"
          :color="timeConfig.ack.color"/>

        <VisAxis
          type="x"
          :x="(d: any) => d.date"
          :tick-format="xAxisTickFormat"
          :tick-values="chartData.map(d => d.date)"
          :tick-line="true"
          :domain-line="true"
          :grid-line="true"
        />

        <VisAxis type="y" :tick-format="yAxisTickFormat" :grid-line="true" :tick-line="true" :domain-line="true" />

        <ChartTooltip className="**:space-x-2" />

        <ChartCrosshair
          :template="componentToString(timeConfig, ChartTooltipContent, {
            labelFormatter: tooltipLabelFormatter,
            valueFormatter: formatDuration,
          })"
          :color="[timeConfig.close.color, timeConfig.ack.color]"
        />
      </VisXYContainer>

      <ChartLegendContent />
    </ChartContainer>
  </div>
</template>
