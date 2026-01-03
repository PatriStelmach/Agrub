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
      isAdded: true,
      openSource: true,
      description: "Open-source software tool to monitor IT infrastructure such as networks, servers, virtual machines, and cloud services."
    },
    {
      name: "Wazuh",
      img: wazuh_logo,
      isAdded: false,
      openSource: true,
      description: "Analyzes security data across endpoints, clouds, and networks to detect threats, respond to incidents, and ensure compliance."
    },
    {
      name: "Nagios",
      img: nagios_logo,
      isAdded: false,
      openSource: false,
      description: "Event monitoring system that offers monitoring and alerting services for servers, switches, applications and services."
    }
  ]

const lol =
  [
    {
      id: 1,
      name: "lol"
    },
    {
      id: 2,
      name: "belosan"
    }
  ]
</script>


<template>
  <SidebarProvider
    :style=" {
      '--sidebar-width': 'calc(var(--spacing) * 72)',
      '--header-height': 'calc(var(--spacing) * 12)',
    }"
  >
    <AppSidebar variant="inset"/>
    <div class="bg-background rounded-xl w-full my-4 p-4 flex">
      <div v-for="system in systems" class="w-6/7 center">
        <Card class="m-4 h-1/2">
          <div class="flex justify-between items-center">
            <CardHeader class="text-2xl">{{system.name}}</CardHeader>
            <img alt="system_image" :src="system.img" width="60" height="60" class="rounded-4xl mr-4">
          </div>
          <CardDescription class="h-1/3 mb-4">
            <Item variant="outline" class="m-4">
              <ItemContent class="h-full!">
             {{system.description}}
              </ItemContent>
            </Item>
          </CardDescription>
          <CardAction class="mx-4 text-sm flex items-center gap-1"
          v-if="system.openSource">
            <component class="text-green-600" :is="IconBrandOpenSource"></component>
            Open Source
          </CardAction>
          <CardAction v-else class="mx-4 text-sm flex items-center gap-1">
            <component class="text-red-600" :is="IconCurrencyDollar"></component>
            Commercial
          </CardAction>
          <CardFooter class="flex justify-end">
            <Button class="cursor-pointer">
              <component v-if="system.isAdded" :is="IconPlus" ></component>
              <component v-else>Open</component>
            </Button>
          </CardFooter>
        </Card>
      </div>

    </div>

    </SidebarProvider>
</template>

