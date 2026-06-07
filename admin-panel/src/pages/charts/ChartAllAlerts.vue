<script setup lang="ts">

import { VisAxis, VisStackedBar, VisXYContainer } from '@unovis/vue'
import {
  ChartContainer,
  ChartCrosshair,
  ChartLegendContent,
  ChartTooltip,
  ChartTooltipContent,
  componentToString
} from '@/components/ui/chart'
import {bigNameLabel, smallNameLabel} from "@/assets/cssFunctions.ts";
import {computed} from "vue";
import {
  type Granularity, type XYAnalytics
} from "@/types/types.ts";
import { fromDate} from '@internationalized/date'
import {getWeekNumber} from "reka-ui/date";
import {useColorMode} from "@vueuse/core";

const mode = useColorMode()
const props = defineProps<{
  locale: string
  tz: string
  currentGranularity: Granularity
  rawAnalyticsData: XYAnalytics[]
  periodLabel: string
}>()


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

const totalChartConfig = computed(() => ({
  y: {
    label: Math.round(props.rawAnalyticsData.map(i => i.y).reduce((sum, value) => sum + value ,0)).toString(),
    color: mode.value === 'light' ? '#00A3FFFF' : '#61C8FFFF',
    text: 'alerts'
  }
}))

const meanChartConfig = computed(() => ({
  y: {
    label: Math.round(props.rawAnalyticsData.map(i => i.y).reduce((sum, value) => sum + value ,0)/props.rawAnalyticsData.length).toString(),
    color: mode.value === 'light' ? '#00A3FFFF' : '#61C8FFFF',
    text: 'alerts'
  }
}))


</script>

<template>
  <div>
    <h1 :class="`${bigNameLabel} mb-6 text-center`">
      {{ `Total alerts amount in last ${periodLabel}` }}
    </h1>

    <ChartContainer :cursor="true" :config="meanChartConfig" class="w-full">
      <VisXYContainer
        :y-domain="[0, Math.max(...rawAnalyticsData.map(d => Math.max(d.y *1.1)))]"
        v-if="rawAnalyticsData.length > 0"
        :data="rawAnalyticsData">
        <VisStackedBar
          :x="(d: XYAnalytics) => d.x"
          :y="(d: XYAnalytics) => d.y"
          :color="meanChartConfig['y'].color"
          :rounded-corners="0"
          bar-padding="0.15"
          group-padding="0.2"
        />


        <VisAxis
          type="x"
          :x="(d: XYAnalytics) => d.x"
          :tick-format="xAxisTickFormat"
          :tick-values="rawAnalyticsData.map(d => d.x)"
          :tick-line="false"
          :domain-line="true"
          :grid-line="false"
        />

        <VisAxis type="y" :tick-format="yAxisTickFormat" :grid-line="true" :tick-line="true" :domain-line="true" />
        <ChartTooltip className="**:space-x-2" />
        <ChartCrosshair

          :template="componentToString(meanChartConfig, ChartTooltipContent, {
            labelFormatter: tooltipLabelFormatter

          })"
          :color="meanChartConfig['y'].color"

        />
      </VisXYContainer>
      <div
        class="absolute top-1/2 left-1/2 -translate-y-1/2 -translate-x-1/2 border-b-3 border-red-badge"
        v-else>
        <h1 :class="smallNameLabel">No data to show</h1>
      </div>
      <ChartLegendContent
        :alertsRawData="totalChartConfig"
        :label1="`On average per ${currentGranularity.toLowerCase()}:`"
        label2="Total:" />
    </ChartContainer>
  </div>
</template>
