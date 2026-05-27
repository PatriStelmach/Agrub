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
    name="fade"
  >
    <slot/>
  </TransitionGroup>
</template>
