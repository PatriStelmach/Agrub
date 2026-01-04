<script setup lang="ts">

import AppSidebar from "@/components/AppSidebar.vue";
import {SidebarProvider} from "@/components/ui/sidebar";
import zabbix_logo from "@/components/icons/zabbix_logo.jpeg"
import wazuh_logo from "@/components/icons/wazuh_logo.png"
import nagios_logo from "@/components/icons/nagios_logo.webp"
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

const systems =
  [
    {
      name: "Zabbix",
      img: zabbix_logo,
      openSource: true,
      description: "Open-source software tool to monitor IT infrastructure such as networks, servers, virtual machines, and cloud services."
    },
    {
      name: "Wazuh",
      img: wazuh_logo,
      openSource: true,
      description: "Analyzes security data across endpoints, clouds, and networks to detect threats, respond to incidents, and ensure compliance."
    },
    {
      name: "Nagios",
      img: nagios_logo,
      openSource: false,
      description: "Event monitoring system that offers monitoring and alerting services for servers, switches, applications and services."
    }
  ]
</script>


<template>
    <div class="pt-4 grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 ">
      <div v-for="system in systems" class=" mx-6 center">
        <Card
          class="m-4 h-4/5 lg:h-3/8 xl:h-1/3 2xl:h-3/11 hover:shadow-[0_0_20px_1px] transition-all hover:transition-duration-100"
          :class="{' hover:shadow-green-900 hover:border-green-900': !system.openSource,
           ' hover:shadow-blue-900 hover:border-blue-900': system.openSource}">
          <div class="flex justify-between items-center">
            <CardHeader class="text-3xl xl:text-4xl 2xl:text-6xl text-shadow-sky-800 text-shadow-sm font-bold ">
              {{system.name}}</CardHeader>
            <img alt="system_image" :src="system.img" width="60" height="60" class="rounded-4xl mr-4">
          </div>
          <CardDescription class="h-1/3 mb-4">
            <Item variant="outline" class="m-4 bg-slate-800">
              <ItemContent class="h-full text-lg xl:text-2xl 2xl:text-3xl">
             {{system.description}}
              </ItemContent>
            </Item>
          </CardDescription>
          <CardAction class="mx-4 mt-[2vh] text-md xl:text-xl 2xl:text-4xl flex items-center gap-2"
          v-if="system.openSource">
            <component class="text-blue-500  size-8 xl:size-12 2xl:size-16" :is="IconBrandOpenSource"></component>
            Open Source
          </CardAction>
          <CardAction v-else class="mx-4 mt-[2vh] text-md xl:text-xl 2xl:text-4xl flex items-center gap-1">
            <component class="text-green-500 size-8 xl:size-12 2xl:size-16" :is="IconCurrencyDollar"></component>
            Commercial
          </CardAction>
          <CardFooter class="flex justify-end relative bottom-5 xl:bottom-10 2xl:bottom-15">
            <Button class="cursor-pointer hover:shadow-[0_0_10px_3px] w-12 xl:size-16 2xl:size-24 text-sm xl:text-xl 2xl:text-4xl ">
              <component :is="IconPlus" class="size-6 xl:size-8 2xl:size-12"></component>
            </Button>
          </CardFooter>
        </Card>
      </div>
    </div>
</template>

