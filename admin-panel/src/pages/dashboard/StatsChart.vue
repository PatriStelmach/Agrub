<script setup lang="ts">
import type { ChartConfig } from '@/components/ui/chart'
import { VisAxis, VisGroupedBar, VisXYContainer } from '@unovis/vue'
import { ChartContainer, ChartCrosshair, ChartLegendContent, ChartTooltip, ChartTooltipContent, componentToString } from '@/components/ui/chart'

const chartData = [
  { date: new Date('2024-01-01'), low: 186, medium: 80, high: 13 },
  { date: new Date('2024-02-01'), low: 250, medium: 200, high: 25 },
  { date: new Date('2024-03-01'), low: 137, medium: 120, high: 10 },
  { date: new Date('2024-04-01'), low: 73, medium: 190, high: 30 },
  { date: new Date('2024-05-01'), low: 209, medium: 130, high: 45},
  { date: new Date('2024-06-01'), low: 214, medium: 140, high: 120 },
  { date: new Date('2024-07-01'), low: 244, medium: 110, high: 20 }
]

type Data = typeof chartData[number]

const chartConfig = {
  low: {
    label: 'low',
    color: '#22c55e',
  },
  medium: {
    label: 'medium',
    color: '#eab308',
  },

  high : {
    label: 'high',
    color: '#dc2626'
  }
} satisfies ChartConfig
</script>

<template>
<div class="my-[4vh]">
  <ChartContainer :config="chartConfig" class="min-h-[200px] w-full">
    <Label class="text-2xl mx-auto my-[4vh] ">Alerts this week</Label>
    <VisXYContainer :data="chartData">
      <VisGroupedBar
        :x="(d: Data) => d.date"
        :y="[(d: Data) => d.low, (d: Data) => d.medium, (d: Data) => d.high]"
        :color="[chartConfig.low.color, chartConfig.medium.color, chartConfig.high.color]"
        :rounded-corners="4"
        bar-padding="0.1"
        group-padding="0"
      />
      <VisAxis
        type="x"
        :x="(d: Data) => d.date"
        :tick-line="false"
        :domain-line="false"
        :grid-line="false"
        :tick-format="(d: number) => {
          const date = new Date(d)
          return date.toLocaleDateString('en-US', {
            weekday: 'short',
          })
        }"
        :tick-values="chartData.map(d => d.date)"
      />
      <VisAxis
        type="y"
        :tick-format="(d: number) => ''"
        :tick-line="false"
        :domain-line="false"
        :grid-line="true"
      />
      <ChartTooltip />
      <ChartCrosshair
        :template="componentToString(chartConfig, ChartTooltipContent, {
          labelFormatter(d) {
            return new Date(d).toLocaleDateString('en-US', {
              month: 'long',
            })
          },
        })"
        :color="[chartConfig.low.color, chartConfig.medium.color, chartConfig.high.color]"
      />
    </VisXYContainer>

    <ChartLegendContent />
  </ChartContainer>
</div>
</template>
