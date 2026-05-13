<script setup lang="ts">
import { useColorMode } from '@vueuse/core'
import {
  IconSunFilled, IconMoonFilled, IconHelpCircleFilled, IconSettingsFilled,
  IconLogout
} from "@tabler/icons-vue";
import {useAuthStore} from "@/stores/authStore.ts";
const authStore = useAuthStore();
const mode = useColorMode()
</script>

<template>
  <div class="flex absolute items-center right-[1vh] z-9 gap-x-4">
    <div class="relative">
    </div>
    <IconHelpCircleFilled class="cursor-pointer size-6 hover:animate-pulse hover:animation-duration-[2s] hover:scale-115 duration-100"/>
    <IconSettingsFilled class="rounded-full cursor-pointer size-6 hover:animate-spin hover:animation-duration-[6s] hover:scale-115 duration-100"/>

          <component :is="mode === 'light' ? IconSunFilled :IconMoonFilled" @click="mode == 'light' ? mode = 'dark' : mode = 'light' "
                     class="cursor-pointer relative size-6 p-0! rounded-full  hover:scale-115 duration-100"/>
    <component
      class="cursor-pointer relative size-6 p-0! rounded-full  hover:scale-115 duration-100"
      :is="IconLogout"
      @click="authStore.logout()"
    />
  </div>
</template>
