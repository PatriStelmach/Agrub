<script setup lang="ts">
import { useColorMode } from '@vueuse/core'
import { Button } from '@/components/ui/button'
import {IconSunFilled, IconMoonFilled, IconHelpCircleFilled, IconSettingsFilled, IconBellFilled} from "@tabler/icons-vue";

const mode = useColorMode()

</script>

<template>
  <div class="flex absolute items-center right-[1vh] z-9999 gap-x-4">
    <IconBellFilled class="cursor-pointer size-[2vh] hover:animate-pulse hover:animation-duration-[2s] hover:scale-115 duration-100"/>
    <IconHelpCircleFilled class="cursor-pointer size-[2vh] hover:animate-pulse hover:animation-duration-[2s] hover:scale-115 duration-100"/>
    <IconSettingsFilled class="rounded-full cursor-pointer size-[2vh] hover:animate-spin hover:animation-duration-[6s] hover:scale-115 duration-100"/>
        <Button
          @click="mode == 'light' ? mode = 'dark' : mode = 'light' "
          class="cursor-pointer relative size-[2vh] p-0! rounded-full bg-card hover:bg-card hover:scale-115 duration-100"
        >
          <component :is="mode === 'light' ? IconSunFilled :IconMoonFilled" class="size-[2vh] text-primary " />
        </Button>
  </div>
</template>
