<script setup lang="ts">
import { useColorMode } from '@vueuse/core'
import {
  IconSunFilled, IconMoonFilled, IconHelpCircleFilled, IconSettingsFilled,
  IconLogout
} from "@tabler/icons-vue";
import {useAuthStore} from "@/stores/authStore.ts";
const mode = useColorMode()
</script>

<template>
  <div class="flex absolute items-center right-[1vh] z-9 gap-x-4">
    <div class="relative">
    </div>
    <IconHelpCircleFilled class="cursor-pointer size-6 hover:animate-pulse hover:animation-duration-[2s] hover:scale-115 duration-100"/>



  </div>
</template>
