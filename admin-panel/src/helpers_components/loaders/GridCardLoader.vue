<script setup lang="ts">
import {gridSkeletons} from "@/assets/cssFunctions.js";
import {Skeleton} from "@/components/ui/skeleton";
</script>
<template>
  <div
    :class="gridSkeletons">
    <Skeleton v-for="(_, i) in Array.from({ length: 21})" :key="i" class="w-full h-44"/>
  </div>
</template>
