<script setup lang="ts">

import {systemsLibraryData} from "@/data/systemsLibraryData.ts"
import
{
  CardHeader,
  CardContent,
  Card,
  CardTitle,
  CardFooter,
  CardAction,
  CardDescription
} from "@/components/ui/card";
import
{
  IconBrandOpenSource, IconCurrencyDollar,
  IconPlus
} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import {Item, ItemContent, ItemDescription} from "@/components/ui/item";
import AddNew from "@/pages/systems/AddNew.vue";
import {computed, ref} from "vue";
import type {System} from "@/types/system.ts";

const isOpen = ref(false);
const openSystemId = ref<number | null>(null);

const popUp = (id: number) =>
{
  isOpen.value = true;
  openSystemId.value = id;
  return systemsLibraryData.find(value => value.id == id)
}

</script>


<template>
  <div>
  <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh] font-mono  ">Available systems</h1>
    <div class="pt-4 grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 ">
      <div v-for="system in systemsLibraryData" class=" transition-all duration-300 mx-6 center"
      :class="{'blur-3xl' : isOpen}">
        <Card
          class=" m-4 gap-2 h-[40vh] xl:h-[35vh] hover:shadow-[0_0_20px_1px] border-2 transition-all transition-duration-50 hover:transition-duration-50"
          :class="{' hover:shadow-green-900 hover:border-green-900': !system.openSource,
           ' hover:shadow-blue-900 hover:border-blue-900': system.openSource}">
          <div class=" flex justify-between items-center">
            <CardHeader class=" text-3xl xl:text-4xl 2xl:text-6xl text-shadow-sky-800 text-shadow-sm font-bold ">
              {{system.name}}</CardHeader>
            <img :alt="system.name+'_image'" :src="system.img"  class="rounded-full size-[6vh] mr-4">
          </div>
          <CardAction class="mx-4 text-md xl:text-xl 2xl:text-4xl flex items-center gap-2"
                      v-if="system.openSource">
            <component class="text-blue-500  size-8 xl:size-12 2xl:size-16" :is="IconBrandOpenSource"></component>
            Open Source
          </CardAction>
          <CardAction v-else class="mx-4 text-md xl:text-xl 2xl:text-4xl flex items-center gap-1">
            <component class="text-green-500 size-8 xl:size-12 2xl:size-16" :is="IconCurrencyDollar"></component>
            Commercial
          </CardAction>
          <CardDescription class="h-1/3">
            <Item variant="outline" class="m-4 bg-secondary">
              <ItemContent class="h-full text-xl xl:text-2xl 2xl:text-4xl">
             {{system.description}}
              </ItemContent>
            </Item>
          </CardDescription>

          <CardFooter class="flex mb-0 mt-[8vh] justify-end relative bottom-5 xl:bottom-10 2xl:bottom-15">
            <Button
              class="cursor-pointer hover:shadow-[0_0_10px_3px] size-[5vh]  text-sm xl:text-xl 2xl:text-4xl "
              @click="openSystemId=system.id; isOpen = !isOpen"
            >
              <component :is="IconPlus" class="size-6 xl:size-8 2xl:size-12"></component>
            </Button>
          </CardFooter>
        </Card>

      </div>
      <div class="left-0 top-[20vh] absolute w-full z-100">
        <AddNew
          v-if="isOpen"
          @close="isOpen = false"
          :system="popUp(openSystemId as number) as System"></AddNew>
      </div>
    </div>
  </div>
</template>

