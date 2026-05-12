<script setup lang="ts">
import {gridSkeletons} from "@/assets/cssFunctions.ts";
</script>

<template>
  <TransitionGroup
    :class="gridSkeletons"
    tag="div"
    name="slide-fade-card"
  >
    <slot/>
  </TransitionGroup>
</template>
