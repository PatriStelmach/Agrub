<script setup lang="ts">
import {topButtonGroup, topH1} from "@/assets/cssFunctions.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import GoBackButton from "@/helpers_components/GoBackButton.vue";

defineProps<{
  h1: string;
  h1Class?: string | Record<string, boolean> | (string | Record<string, boolean>)[];
}>()
</script>

<template>
  <div class="relative">
    <h1 :class="[topH1, h1Class]">{{ h1 }}</h1>
    <ButtonGroup :class="topButtonGroup">
      <ButtonGroup class="flex">
        <GoBackButton/>
      </ButtonGroup>
      <slot/>
    </ButtonGroup>
  </div>
</template>
