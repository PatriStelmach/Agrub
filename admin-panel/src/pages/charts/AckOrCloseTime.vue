<script setup lang="ts">
import { VisAxis, VisXYContainer, VisLine } from '@unovis/vue'
import { ChartContainer, ChartCrosshair, ChartLegendContent, ChartTooltip, ChartTooltipContent, componentToString } from '@/components/ui/chart'
import { bigNameLabel } from "@/assets/cssFunctions.ts"
import { computed, onMounted, ref } from "vue"
import { type Granularity, type XYAnalytics } from "@/types/types.ts"
import { getAnalyticsAlertsCount } from "@/helpers_functions/requests.ts"
import { fromDate, ZonedDateTime, CalendarDateTime, CalendarDate } from '@internationalized/date'
import { toast } from "vue-sonner"
import { getWeekNumber } from "reka-ui/date"
import {useColorMode} from "@vueuse/core";

const props = defineProps<{
  header: string
  type: "avg-close-time" | "avg-ack-time"
  start: CalendarDate | CalendarDateTime | ZonedDateTime
  end: CalendarDate | CalendarDateTime | ZonedDateTime
  locale: string
  tz: string
  currentGranularity: Granularity
}>()

const emits = defineEmits<{
  'update:values': [XYAnalytics[]]
}>()

const mode = useColorMode()
const rawAnalyticsData = ref<XYAnalytics[]>([])
const isChartLoading = ref<boolean>(true)

onMounted(async () => {
  await getAnalyticsAlertsCount(props.start as ZonedDateTime, props.end as ZonedDateTime, "MONTH", props.type)
    .then((res) => rawAnalyticsData.value = res)
    .catch((e) => toast.error(`Error retrieving AlertCountAnalytics data: ${e}`))
    .finally(() => {
      isChartLoading.value = false
      emits('update:values', rawAnalyticsData.value)
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
  if (value/60 % 1 !== 0) return ''
  const formattedMinutes = Intl.NumberFormat(props.locale, {
    notation: 'compact',
    compactDisplay: 'long',
  }).format(value/60)
  return `${formattedMinutes} min`
}

const time = {
  x : {
    label: props.type === 'avg-close-time' ? "Close time" : "Acknowledge time",
    color: props.type === 'avg-close-time' ? '#F40031FF' : mode.value === 'light' ? '#48CF00FF' : '#08A800FF'
  },
}


</script>

<template>
  <div>
    <h1 :class="`${bigNameLabel} mb-6 text-center`">{{ header }}</h1>

    <ChartContainer :config="time" class="w-full">
      <VisXYContainer :data="rawAnalyticsData" :y-domain="[0, Math.max(...rawAnalyticsData.map(d => Math.max(d.y)))]">

        <VisLine
          :x="(d: any) => d.x"
          :y="(d: any) => d.y"
          :color="props.type === 'avg-close-time' ? '#F40031FF' : mode === 'light' ? '#48CF00FF' : '#08A800FF'"
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

        <ChartTooltip />

        <ChartCrosshair
          :template="componentToString(time, ChartTooltipContent, {
            labelFormatter: tooltipLabelFormatter,
          })"
          :color="props.type === 'avg-ack-time' ? '#F40031FF' : mode === 'light' ? '#48CF00FF' : '#08A800FF'"
        />
      </VisXYContainer>

      <ChartLegendContent />
    </ChartContainer>
  </div>
</template>
