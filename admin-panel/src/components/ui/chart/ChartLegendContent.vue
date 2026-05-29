<script setup lang="ts">
import type { HTMLAttributes } from "vue"
import { computed, onMounted, ref } from "vue"
import { cn } from "@/lib/utils"
import {type ChartConfig, useChart} from "."
import type {Severity} from "@/types/types.ts";

const props = withDefaults(defineProps<{
  hideIcon?: boolean
  nameKey?: string
  label1?: string
  label2?: string
  alertsRawData?: ChartConfig
  verticalAlign?: "bottom" | "top"
  // payload?: any[]
  class?: HTMLAttributes["class"]
}>(), {
  verticalAlign: "bottom",
})

const { id, config } = useChart()

const payload = computed(() => Object.entries(config.value).map(([key]) => {
  return {
    key: props.nameKey || key,
    itemConfig: config.value[key],
  }
}))

const totalPayload = computed(() => props.alertsRawData ? Object.entries(props.alertsRawData).map(([key]) => ({
    key: props.nameKey || key,
    itemConfig: props.alertsRawData![key],
  })) : []
)

const containerSelector = ref("")
onMounted(() => {
  containerSelector.value = `[data-chart="chart-${id}"]>[data-vis-xy-container]`
})
</script>

<template>

  <div

    v-if="containerSelector"
    :class="cn(
      'flex items-center justify-center gap-4',
      verticalAlign === 'top' ? 'pb-3' : 'pt-3',
      props.class,
    )"
  >

    <span class="text-[0.9rem] font-extralight italic">
       {{ label1 }}
    </span>
    <div
      v-for="{ key, itemConfig } in payload"
      :key="key"
      :class="cn(
        '[&>svg]:text-muted-foreground flex items-center gap-1.5 [&>svg]:h-3 [&>svg]:w-3',
      )"
    >
      <component :is="itemConfig.icon" v-if="itemConfig?.icon" />
      <div
        v-else
        class="h-2 w-2 shrink-0 rounded-[2px]"
        :style="{
          backgroundColor: itemConfig!.color,
        }"
      />
      <span class="grid grid-cols-1 text-[0.9rem] font-extralight italic">
              {{ itemConfig?.label }} {{ itemConfig?.text}}
      </span>

    </div>
  </div>

  <div

    v-if="alertsRawData"
    :class="cn(
      'flex items-center justify-center gap-4',
      verticalAlign === 'top' ? 'pb-3' : 'pt-3',
      props.class,
    )"
  >

    <span class="text-[0.9rem] font-extralight italic">
       {{ label2 }}
    </span>
    <div
      v-for="{ key, itemConfig } in totalPayload"
      :key="key"
      :class="cn(
        '[&>svg]:text-muted-foreground flex items-center gap-1.5 [&>svg]:h-3 [&>svg]:w-3',
      )"
    >
      <component :is="itemConfig.icon" v-if="itemConfig?.icon" />
      <div
        v-else
        class="h-2 w-2 shrink-0 rounded-[2px]"
        :style="{
          backgroundColor: itemConfig!.color,
        }"
      />
      <span class="grid grid-cols-1 text-[0.9rem] font-extralight italic">
              {{ itemConfig?.label }} {{ itemConfig?.text}}
      </span>

    </div>
  </div>

</template>
