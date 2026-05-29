<script setup lang="ts">
import { VisAxis, VisXYContainer, VisLine } from '@unovis/vue'
import { ChartContainer, ChartCrosshair, ChartLegendContent, ChartTooltip, ChartTooltipContent, componentToString } from '@/components/ui/chart'
import { bigNameLabel } from "@/assets/cssFunctions.ts"
import { computed } from "vue"
import { type Granularity, type XYAnalytics } from "@/types/types.ts"
import { fromDate, ZonedDateTime, CalendarDateTime, CalendarDate } from '@internationalized/date'
import { getWeekNumber } from "reka-ui/date"
import { useColorMode } from "@vueuse/core"

const props = defineProps<{
  header: string
  start: CalendarDate | CalendarDateTime | ZonedDateTime
  end: CalendarDate | CalendarDateTime | ZonedDateTime
  locale: string
  tz: string
  currentGranularity: Granularity
  closedAndAcked: {
    ack: XYAnalytics[],
    close: XYAnalytics[]
  }
}>()

const mode = useColorMode()
const isLoading = defineModel<boolean>('isLoading')

const timeConfig = computed(() => ({
  close: {
    label: "Close time",
    color: '#F40031FF'
  },
  ack: {
    label: "Acknowledge time",
    color: mode.value === 'light' ? '#48CF00FF' : '#08A800FF'
  }
}))

const chartData = computed(() => {
  const ackArr = props.closedAndAcked?.ack || []
  const closeArr = props.closedAndAcked?.close || []

  const mergedMap = new Map<number, { date: number; ack: number; close: number }>()

  ackArr.forEach(item => {
    const timestamp = item.x
    mergedMap.set(timestamp, { date: timestamp, ack: item.y, close: 0 })
  })

  closeArr.forEach(item => {
    const timestamp = item.x
    if (mergedMap.has(timestamp)) {
      mergedMap.get(timestamp)!.close = item.y
    } else {
      mergedMap.set(timestamp, { date: timestamp, ack: 0, close: item.y })
    }
  })

  return Array.from(mergedMap.values()).sort((a, b) => a.date - b.date)
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
  if (value/60 % 1 !== 0) return ''
  const formattedMinutes = Intl.NumberFormat(props.locale, {
    notation: 'compact',
    compactDisplay: 'short',
  }).format(value / 60)
  return `${formattedMinutes} min`
}
</script>

<template>
  <div>
    <h1 :class="`${bigNameLabel} mb-6 text-center`">{{ header }}</h1>

    <ChartContainer :config="timeConfig" class="w-full">
      <VisXYContainer :data="chartData" :y-domain="[0, Math.max(...chartData.map(d => Math.max(d.ack, d.close)))]">

        <VisLine
          :x="(d: any) => d.date"
          :y="(d: any) => d.close"
          :color="timeConfig.close.color"
        />

        <VisLine
          :x="(d: any) => d.date"
          :y="(d: any) => d.ack"
          :color="timeConfig.ack.color"
        />

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

        <ChartTooltip />

        <ChartCrosshair
          :template="componentToString(timeConfig, ChartTooltipContent, {
            labelFormatter: tooltipLabelFormatter
          })"
          :color="[timeConfig.close.color, timeConfig.ack.color]"
        />
      </VisXYContainer>

      <ChartLegendContent />
    </ChartContainer>
  </div>
</template>
