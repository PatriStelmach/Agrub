<script setup lang="ts">
import { ScrollArea } from '@/components/ui/scroll-area'
import { Separator } from '@/components/ui/separator'
import { usersData } from "@/data/usersData.ts";


</script>

<template>
<ScrollArea class=" border-y-3 py-4 px-2 h-[25vh]">
  <div>
    <div
      v-for="user in usersData"
      :key="user.id"
      class="inline "
    >
      <div class="flex mt-2 h-8 items-end ">
        <img
          :alt="user.name + ' _avatar'"
          :src="user.avatar as string"
          class="size-6 mb-2"
        />
        <span class="ml-2 text-start">
          {{ user.name }}
        </span>
      </div>
      <Separator class="mx-auto w-9/10"/>
    </div>
  </div>
</ScrollArea>
</template>
