<script setup lang="ts">
import type {HTMLAttributes} from "vue";
const props = defineProps<{
  class?:HTMLAttributes["class"],
}>()
</script>

<template>
  <TransitionGroup
    :class="props.class"
    tag="div"
    name="slide-fade-card"
  >
    <slot/>
  </TransitionGroup>
</template>
