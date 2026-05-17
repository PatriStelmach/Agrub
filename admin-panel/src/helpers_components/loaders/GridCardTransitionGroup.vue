<script setup lang="ts">
import {gridSkeletons} from "@/assets/cssFunctions.js";
import type {HTMLAttributes} from "vue";
const props = defineProps<{
  class?:HTMLAttributes["class"],
}>()
</script>

<template>
  <TransitionGroup
    :class="[gridSkeletons, props.class]"
    tag="div"
    name="slide-fade-card"
  >
    <slot/>
  </TransitionGroup>
</template>
