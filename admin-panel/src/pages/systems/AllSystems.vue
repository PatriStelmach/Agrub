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
import AddNew from "@/pages/systems/AddNew.vue";
import {ref} from "vue";
import type {System} from "@/types/types.ts";
import {Field, FieldGroup, FieldLabel} from "@/components/ui/field";
import {Input} from "@/components/ui/input";
import {ButtonGroup} from "@/components/ui/button-group";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";

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
  <div>
    <div class="relative max-h-[10vh] items-center">
      <Button class="absolute left-4" variant="outline" size="icon" aria-label="Go Back">
        <ArrowLeftIcon />
      </Button>
      <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh]    ">Available systems</h1>
    </div>

    <div class="pt-4 grid grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 overflow-auto max-h-[85vh]">
      <div
        v-for="system in systemsLibraryData"
        :key="system.id"
        class=" transition-all duration-300 mx-6 center"
        :class="{'blur-3xl' : isOpen}">
        <Card
          class=" m-4 gap-2 h-[40vh] xl:h-[35vh] hover:shadow-[0_0_20px_1px] border-2 transition-all transition-duration-50 hover:transition-duration-50"
          :class="{' hover:shadow-blue-900 hover:border-blue-900': !system.openSource,
           ' hover:shadow-green-900 hover:border-green-900': system.openSource}">
          <div class=" flex justify-between items-center">
            <CardHeader class=" text-3xl xl:text-4xl 2xl:text-6xl text-shadow-sky-800 text-shadow-sm font-bold ">
              {{system.name}}</CardHeader>
            <img :alt="system.name+'_image'" :src="system.img"  class="rounded-full size-[6vh] mr-4">
          </div>
          <CardAction class="mx-4 text-md xl:text-xl 2xl:text-4xl flex items-center gap-2"
                      v-if="system.openSource">
            <IconBrandOpenSource class="text-green-500  size-8 xl:size-12 2xl:size-16"/>
            Open Source
          </CardAction>
          <CardAction v-else class="mx-4 text-md xl:text-xl 2xl:text-4xl flex items-center gap-1">
            <IconCurrencyDollar class="text-blue-500 size-8 xl:size-12 2xl:size-16"/>
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
      <div v-if="openSystem && isOpen" class="left-0 top-[20vh] absolute w-full z-100 flex mx-auto items-center ">
          <form class="bg-background flex mx-auto p-[2vw] items-center justify-center shadow-[0px_0px_150px_1px] rounded-xl w-1/3 border "
                :class="{' shadow-blue-900 border-blue-900': openSystem.openSource,
           ' shadow-green-900 border-green-900': openSystem.openSource}">
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
                  variant="destructive"
                  @click="closeDialog">
                  Cancel
                </Button>
              </Field>
            </FieldGroup>
          </form>
      </div>
    </div>
  </div>

</template>

