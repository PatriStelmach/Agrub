<script setup lang="ts">
import {gridSkeletons} from "@/assets/cssFunctions.js";
import {Skeleton} from "@/components/ui/skeleton";
</script>
<template>
  <div
    :class="gridSkeletons">
    <Skeleton v-for="(_, i) in Array.from({ length: 12})" :key="i" class="w-full h-40"/>
  </div>
</template>
