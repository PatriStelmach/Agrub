<script setup lang="ts">
import {gridSkeletons} from "@/assets/cssFunctions.js";
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
