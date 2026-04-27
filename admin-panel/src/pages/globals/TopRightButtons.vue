<script setup lang="ts">
import { useColorMode } from '@vueuse/core'
import { Button } from '@/components/ui/button'
import {IconSunFilled, IconMoonFilled, IconHelpCircleFilled, IconSettingsFilled, IconBellFilled} from "@tabler/icons-vue";
import {useNotificationStore} from "@/stores/notificationStore.ts";
import {Popover} from "@/components/ui/popover";

const mode = useColorMode()
const store = useNotificationStore()
</script>

<template>
  <div class="flex absolute items-center right-[1vh] z-9999 gap-x-4">
    <div class="relative">
      <Popover>
        <PopoverTrigger as-child>
          <div
            v-show="store.notificationAmount > 0 "
            class="absolute -top-2 -right-1 rounded-full bg-badge2 px-1 text-[0.65rem] xl:text-sm">{{ store.notificationAmount}}</div>
        </PopoverTrigger>
        <PopoverContent>
          <ul>
            <li v-for="notif in store.notificationAmount">
              <div>
                <h1>{{ notif}}</h1>
              </div>
            </li>
          </ul>
        </PopoverContent>
      </Popover>
      <IconBellFilled class="cursor-pointer size-6 lg:size-7 xl:size-8  hover:animate-pulse hover:animation-duration-[2s] hover:scale-115 duration-100"/>

    </div>
    <IconHelpCircleFilled class="cursor-pointer size-6 lg:size-7 xl:size-8  hover:animate-pulse hover:animation-duration-[2s] hover:scale-115 duration-100"/>
    <IconSettingsFilled class="rounded-full cursor-pointer size-6 lg:size-7 xl:size-8  hover:animate-spin hover:animation-duration-[6s] hover:scale-115 duration-100"/>
        <Button
          @click="mode == 'light' ? mode = 'dark' : mode = 'light' "
          class="cursor-pointer relative size-6 lg:size-7 xl:size-8  p-0! rounded-full bg-card hover:bg-card hover:scale-115 duration-100"
        >
          <component :is="mode === 'light' ? IconSunFilled :IconMoonFilled" class="size-6 lg:size-7 xl:size-8  text-primary " />
        </Button>
  </div>
</template>
