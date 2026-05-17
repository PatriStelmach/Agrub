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
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {gridCard} from "@/assets/cssFunctions.ts";
import GridCardTransitionGroup from "@/helpers_components/loaders/GridCardTransitionGroup.vue";
import GridCardLoader from "@/helpers_components/loaders/GridCardLoader.vue";
import UserCard from "@/pages/team/users/UserCard.vue";

const isOpen = ref(false);
const openSystem = ref<System | null | undefined>(null);
const isLoading = ref<boolean>(false);

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
    <TopH1Div h1="Available systems" :h1Class="['transition-all duration-300', {'blur-3xl' : isOpen}]">
    </TopH1Div>
    <Transition name="fade" mode="out-in">
      <GridCardLoader v-if="isLoading"/>
      <GridCardTransitionGroup
        class="transition-all duration-300"
        :class="{'blur-3xl' : isOpen}"
        v-else>
        <Card
          v-for="system in systemsLibraryData"
          :key="system.id"
          @click="openDialog(system.id)"
          class="cursor-pointer"
          :class="[{ ' hover:shadow-green-badge/50! hover:border-green-badge/50!': system.openSource}, gridCard ]">
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
              <ItemContent class="h-full text-lmd xl:text-lg 2xl:text-xl">
                {{system.description}}
              </ItemContent>
            </Item>
          </CardDescription>
          </Card>
      </GridCardTransitionGroup>
    </Transition>


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

