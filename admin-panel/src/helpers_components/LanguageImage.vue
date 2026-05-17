<script setup lang="ts">
import bash_icon from "@/components/icons/bash_icon.png"
import python_icon from "@/components/icons/python_icon.png"
import powershell_icon from "@/components/icons/powershell_icon.png"
import { isPython, isBash, isPowerShell } from "@/types/types.js"

withDefaults(defineProps<{
  language: string
  sizeClass?: string
}>(), {
  sizeClass: 'size-7'
})

function iconSrc(lang: string): string | undefined {
  if (isPython(lang)) return python_icon
  if (isBash(lang)) return bash_icon
  if (isPowerShell(lang)) return powershell_icon
  return undefined
}
</script>

<template>
  <img
    v-if="iconSrc(language)"
    :alt="language + '_icon'"
    :src="iconSrc(language)"
    :class="sizeClass"
  />
</template>
