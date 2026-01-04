<script lang="ts">
export const iframeHeight = "800px"
export const description = "A dashboard with sidebar, data table, and analytics cards."
</script>

<script setup lang="ts">
import AppSidebar from "@/components/AppSidebar.vue"
import DataTable from "@/components/DataTable.vue"
import SiteHeader from "@/components/SiteHeader.vue"
import {
  SidebarInset,
  SidebarProvider,
} from "@/components/ui/sidebar"


const data = [
  {
    id: 1,
    header: "CPU usage over 80%",
    type: "Zabbix",
    status: "In Process",
    target: "high",
    reviewer: "Morgan Freeman",
  },
  {
    id: 2,
    header: "Server is down",
    type: "Wazuh",
    status: "Done",
    target: "extreme",
    reviewer: "Wily Wonka",
  },
  {
    id: 3,
    header: "HDD usage over 60%",
    type: "Zabbix",
    status: "Done",
    target: "medium",
    reviewer: "Eren Jaeger",
  },
  {
    id: 4,
    header: "LAN connection interrupted",
    type: "Custom_plugin_v1",
    status: "Done",
    target: "low",
    reviewer: "Donald Duck",
  },
  {
    id: 5,
    header: "No free ports on switch",
    type: "My_switch_plugin",
    status: "In Process",
    target: "low",
    reviewer: "Lara Croft",
  },
  {
    id: 6,
    header: "CPU usage over 80%",
    type: "Zabbix",
    status: "In Process",
    target: "high",
    reviewer: "Morgan Freeman",
  },
  {
    id: 7,
    header: "Server is down",
    type: "Wazuh",
    status: "Done",
    target: "extreme",
    reviewer: "Wily Wonka",
  },
  {
    id: 8,
    header: "HDD usage over 60%",
    type: "Zabbix",
    status: "Done",
    target: "medium",
    reviewer: "Eren Jaeger",
  },
  {
    id: 9,
    header: "LAN connection interrupted",
    type: "Custom_plugin_v1",
    status: "Done",
    target: "low",
    reviewer: "Donald Duck",
  },
  {
    id: 10,
    header: "No free ports on switch",
    type: "My_switch_plugin",
    status: "In Process",
    target: "low",
    reviewer: "Lara Croft",
  },
]
</script>

<template>

  <div>
      <SiteHeader class="py-2" />
      <div class="flex flex-col  my-4">
            <div class="px-4 lg:px-6">
              <DataTable :data="data" />
        </div>
      </div>
  </div>
</template>
