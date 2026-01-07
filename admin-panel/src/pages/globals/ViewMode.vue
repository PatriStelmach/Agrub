<script setup lang="ts">
import { Icon } from '@iconify/vue'
import { useColorMode } from '@vueuse/core'
import { Button } from '@/components/ui/button'
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu'
import {IconSunFilled, IconMoon, IconMoonFilled} from "@tabler/icons-vue";
import {ref} from "vue";

const mode = useColorMode()

</script>

<template>
  <div class="fixed top-4 right-4 z-9999">
    <DropdownMenu>
      <DropdownMenuTrigger as-child>
        <Button class="cursor-pointer relative size-[3vh]">
          <component :is="mode === 'light' ? IconSunFilled :IconMoonFilled" class="size-[1vw] rotate-0 scale-100 transition-all" />
          <span class="sr-only">Toggle theme</span>
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent class="w-2.5" align="end">
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
