<script setup lang="ts">

import {systemsLibraryData} from "@/data/systemsLibraryData.ts"
import
{
  CardHeader,
  Card,
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
import {Item, ItemContent} from "@/components/ui/item";
import {ref} from "vue";
import type {System} from "@/types/types.ts";
import {Field, FieldGroup, FieldLabel} from "@/components/ui/field";
import {Input} from "@/components/ui/input";
import {ButtonGroup} from "@/components/ui/button-group";
import {tableDiv, topButtonGroup, topDiv, topH1} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers_components/GoBackButton.vue";

const isOpen = ref(false);
const openSystem = ref<System | null | undefined>(null);

const openDialog = (id: number) => {
  isOpen.value = true
  openSystem.value = systemsLibraryData.find(s => s.id == id)
}

const closeDialog = () => {
  isOpen.value = false
  openSystem.value = null
}

</script>


<template>
  <div class="relative">
    <div :class="topDiv">
      <h1
        class="transition-all duration-300"
        :class="[topH1, {'blur-3xl' : isOpen}]">Available systems</h1>
      <ButtonGroup :class="topButtonGroup">
      <GoBackButton/>
      </ButtonGroup>
    </div>

      <div
        class="transition-all duration-300 pt-4 grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 overflow-auto max-h-[85vh]"
        :class="{'blur-3xl' : isOpen}"
      >
        <div
          v-for="system in systemsLibraryData"
          :key="system.id"
          class="  mx-6 center">
          <Card
            class=" m-4 gap-2 h-[40vh] xl:h-[35vh] hover:shadow-[0_0_20px_1px] border-2 w-full duration-200"
            :class="{' hover:shadow-blue-badge hover:border-blue-badge': !system.openSource,
           ' hover:shadow-green-badge hover:border-green-badge': system.openSource}">
            <div class=" flex justify-between items-center">
              <CardHeader class=" text-3xl xl:text-4xl 2xl:text-6xl text-shadow-sky-800 text-shadow-sm font-bold ">
                {{system.name}}</CardHeader>
              <img :alt="system.name+'_image'" :src="system.img"  class="rounded-full size-[6vh] mr-4">
            </div>
            <CardAction class="mx-4 text-md lg:text-lg xl:text-xl 2xl:text:3xl flex items-center gap-2"
                        v-if="system.openSource">
              <IconBrandOpenSource class="text-green-badge  size-8 xl:size-12 2xl:size-16"/>
              Open Source
            </CardAction>
            <CardAction v-else class="mx-4 text-md lg:text-lg xl:text-xl 2xl:text:3xl flex items-center gap-1">
              <IconCurrencyDollar class="text-blue-badge size-8 xl:size-12 2xl:size-16"/>
              Commercial
            </CardAction>
            <CardDescription class="h-1/3">
              <Item variant="outline" class="m-4 bg-secondary">
                <ItemContent class="h-full text-lg xl:text-xl 2xl:text-4xl">
                  {{system.description}}
                </ItemContent>
              </Item>
            </CardDescription>

            <CardFooter class="flex mb-0 mt-[9vh] xl:mt-[7vh] 2xl:mt-[10vh] justify-end relative bottom-5 xl:bottom-10 2xl:bottom-15">
              <Button
                class="cursor-pointer hover:shadow-[0_0_10px_3px] size-[4vh]  text-sm xl:text-xl 2xl:text-4xl "
                @click="openDialog(system.id)"
              >
                <component :is="IconPlus" class="size-6 xl:size-8 2xl:size-12"></component>
              </Button>
            </CardFooter>
          </Card>

        </div>

      </div>


    <Transition name="fade" tag="div"
         class="left-1/2 -translate-x-1/2 top-[20vh] absolute z-100 flex  items-center fade-in">
      <form v-if="openSystem && isOpen" class="bg-background flex mx-auto p-[2vw] items-center justify-center shadow-[0px_10px_50px_1px] rounded-xl w-1/3 border "
            :class="{' shadow-blue-badge border-blue-badge': !openSystem.openSource,
           ' shadow-green-badge/50 border-green-badge': openSystem.openSource}">
        <FieldGroup>
          <FieldLabel class="flex w-full text-4xl justify-between">{{openSystem.name}}
            <img :alt="openSystem.name+'_image'" :src="openSystem.img" class=" rounded-full size-[6vh]"/>
          </FieldLabel>
          <FieldGroup>
            <Field>
              <FieldLabel for="api-key">Api Key</FieldLabel>
              <Input
                id="api-key"
                placeholder="Enter API Key"
                required
              />
            </Field>
            <Field>
              <FieldLabel for="login">Login</FieldLabel>
              <Input
                id="login"
                placeholder="Enter Login"
                required
              />
            </Field>
            <Field>
              <FieldLabel for="password">Password</FieldLabel>
              <Input
                id="password"
                placeholder="Enter Password"
                required
              />
            </Field>
          </FieldGroup>
          <Field>
            <Button>
              Submit
            </Button>
            <Button
              variant="red_outline"
              @click="closeDialog">
              Cancel
            </Button>
          </Field>
        </FieldGroup>
      </form>
    </Transition>
  </div>

</template>

