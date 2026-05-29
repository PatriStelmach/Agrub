<script setup lang="ts">

import { VisAxis, VisGroupedBar, VisXYContainer } from '@unovis/vue'
import {
  type ChartConfig,
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
  type AlertCountAnalytics,
  type ChartDataPoint,
  type Granularity,
  type Severity
} from "@/types/types.ts";
import { fromDate} from '@internationalized/date'
import {getWeekNumber} from "reka-ui/date";
import {useColorMode} from "@vueuse/core";

const mode = useColorMode()
const props = defineProps<{
  locale: string
  tz: string
  currentGranularity: Granularity
  rawAnalyticsData: AlertCountAnalytics[]
  periodLabel: string
}>()


const chartData = computed<ChartDataPoint[]>(() => {
    return props.rawAnalyticsData.map((item) => {
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

const severities = computed<Record<Severity, number[]>>(() => ({
  UNKNOWN: chartData.value.map(x => x['0']),
  INFO: chartData.value.map(x => x['1']),
  LOW: chartData.value.map(x => x['2']),
  MEDIUM: chartData.value.map(x => x['3']),
  HIGH: chartData.value.map(x => x['4']),
  CRITICAL: chartData.value.map(x => x['5']),
}))

const averageAlertsValueFormatted = computed<Record<Severity, string>>(() => {
  return Object.fromEntries(
    Object.entries(severities.value).map(([key, values]) => [
      key,
      values.length === 0
        ? '-'
        : (values.reduce((sum, value) => sum + value, 0) / values.length).toFixed(),
    ])
  ) as Record<Severity, string>;
});

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

const alertCountChartConfig = computed(() => ({
  0: {
    label: 'UNKNOWN',
    color: mode.value === 'light' ? '#314158FF' : '#E2E8F0FF',
    text: averageAlertsValueFormatted.value['UNKNOWN']
  },
  1: {
    label: 'INFO',
    color: mode.value === 'light' ? '#00A3FFFF' : '#61C8FFFF',
    text: averageAlertsValueFormatted.value['INFO']
  },
  2: {
    label: 'LOW',
    color: mode.value === 'light' ? '#48CF00FF' : '#08A800FF',
    text: averageAlertsValueFormatted.value['LOW']
  },
  3: {
    label: 'MEDIUM',
    color: mode.value === 'light' ? '#ECCC00FF' : '#FFDF20FF',
    text: averageAlertsValueFormatted.value['MEDIUM']
  },
  4: {
    label: 'HIGH',
    color: mode.value === 'light' ? '#FE9A00FF' : '#E98600FF',
    text: averageAlertsValueFormatted.value['HIGH']
  },
  5: {
    label: 'CRITICAL',
    color: '#F40031FF',
    text: averageAlertsValueFormatted.value['CRITICAL']
  },
}))

const totalChartLegendContent = computed(() => ({
  0: {
    label: 'UNKNOWN',
    color: mode.value === 'light' ? '#314158FF' : '#E2E8F0FF',
    text: severities.value['UNKNOWN'].reduce((sum, value) => sum + value, 0).toString()
  },
  1: {
    label: 'INFO',
    color: mode.value === 'light' ? '#00A3FFFF' : '#61C8FFFF',
    text: severities.value['INFO'].reduce((sum, value) => sum + value, 0).toString()
  },
  2: {
    label: 'LOW',
    color: mode.value === 'light' ? '#48CF00FF' : '#08A800FF',
    text: severities.value['LOW'].reduce((sum, value) => sum + value, 0).toString()
  },
  3: {
    label: 'MEDIUM',
    color: mode.value === 'light' ? '#D6B900FF' : '#FFDF20FF',
    text: severities.value['MEDIUM'].reduce((sum, value) => sum + value, 0).toString()
  },
  4: {
    label: 'HIGH',
    color: mode.value === 'light' ? '#FE9A00FF' : '#E98600FF',
    text: severities.value['HIGH'].reduce((sum, value) => sum + value, 0).toString()
  },
  5: {
    label: 'CRITICAL',
    color: '#F40031FF',
    text: severities.value['CRITICAL'].reduce((sum, value) => sum + value, 0).toString()
  },
}))

</script>

<template>
  <div>
    <h1 :class="`${bigNameLabel} mb-6 text-center`">
      {{ `Total alerts amount in last ${periodLabel}, grouped by severity` }}
    </h1>

    <ChartContainer :config="alertCountChartConfig" class="w-full">
      <VisXYContainer
        :y-domain="[0, Math.max(...chartData.map(d => Math.max(d['0'],d['1'],d['2'],d['3'],d['4'],d['5'] *1.1)))]"
        v-if="rawAnalyticsData.length > 0"
        :data="chartData">
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
          :rounded-corners="0"
          bar-padding="0.15"
          group-padding="0.2"
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
        <ChartTooltip className="**:space-x-2" />
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
      <div
        class="absolute top-1/2 left-1/2 -translate-y-1/2 -translate-x-1/2 border-b-3 border-red-badge"
        v-else>
        <h1 :class="smallNameLabel">No data to show</h1>
      </div>
      <ChartLegendContent
        :alertsRawData="totalChartLegendContent as ChartConfig"
        :label1="`On average per ${currentGranularity.toLowerCase()}:`"
        label2="Total:" />
    </ChartContainer>
  </div>
</template>
