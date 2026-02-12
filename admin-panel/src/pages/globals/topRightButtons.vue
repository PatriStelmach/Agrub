<script setup lang="ts">
import { useColorMode } from '@vueuse/core'
import { Button } from '@/components/ui/button'
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu'
import {IconSunFilled, IconMoonFilled, IconHelpCircleFilled, IconSettingsFilled} from "@tabler/icons-vue";

const mode = useColorMode()

</script>

<template>
  <div class="flex absolute items-center right-[1vh] z-9999 gap-x-4">
    <IconHelpCircleFilled class="cursor-pointer size-[2vh] hover:animate-pulse hover:animation-duration-[2s] hover:scale-115 duration-100"/>
    <IconSettingsFilled class="rounded-full cursor-pointer size-[2vh] hover:animate-spin hover:animation-duration-[6s] hover:scale-115 duration-100"/>
    <DropdownMenu>
      <DropdownMenuTrigger as-child>
        <Button
          class="cursor-pointer relative size-[2vh] p-0! rounded-full bg-card hover:bg-card hover:scale-115 duration-100"
        >
          <component :is="mode === 'light' ? IconSunFilled :IconMoonFilled" class="size-[2vh] text-primary " />
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent class="w-2.5">
        <DropdownMenuItem class="cursor-pointer data-highlighted:bg-secondary" @click="mode = 'light'">
          Light
        </DropdownMenuItem>
        <DropdownMenuItem class="cursor-pointer data-highlighted:bg-secondary" @click="mode = 'dark'">
          Dark
        </DropdownMenuItem>
        <DropdownMenuItem class="cursor-pointer data-highlighted:bg-secondary" @click="mode = 'auto'">
          System
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>

  </div>
</template>
