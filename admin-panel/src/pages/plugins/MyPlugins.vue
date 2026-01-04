<script setup lang="ts">
import MyPluginsTable, {type Plugins} from "@/pages/plugins/MyPluginsTable.vue";
import { ArchiveIcon, ArrowLeftIcon, CalendarPlusIcon, ClockIcon, ListFilterPlusIcon, MailCheckIcon, MoreHorizontalIcon, TagIcon, Trash2Icon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSeparator,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'

import
{
  IconFileImport,
  IconDatabase,
  IconTerminal2,
  IconPencilCode,
} from "@tabler/icons-vue";
import {ref} from "vue";
import {Badge} from "@/components/ui/badge";

const plugins:Plugins[] =
  [
    {
      id: 1,
      name: "bash script",
      runningIntervals: "10 seconds",
      createdAt: new Date(2025, 1, 7, 15, 30),
      updatedAt: new Date(2025, 12, 22, 12, 20),
      on: true,
      tags: ["shell", "scripting"]
    },
    {
      id: 2,
      name: "python monitor",
      runningIntervals: "30 seconds",
      createdAt: new Date(2025, 2, 15, 9, 45),
      updatedAt: new Date(2025, 11, 10, 14, 30),
      on: true,
      tags: ["python", "monitoring", "performance"]
    },
    {
      id: 3,
      name: "node service",
      runningIntervals: "5 minutes",
      createdAt: new Date(2025, 3, 20, 11, 20),
      updatedAt: new Date(2025, 10, 5, 16, 45),
      on: true,
      tags: ["nodejs", "service", "api", "web"]
    },
    {
      id: 4,
      name: "docker watcher",
      runningIntervals: "2 minutes",
      createdAt: new Date(2025, 4, 8, 13, 10),
      updatedAt: new Date(2025, 9, 18, 10, 25),
      on: false,
      tags: ["docker", "containers", "orchestration", "devops", "monitoring"]
    },
    {
      id: 5,
      name: "log analyzer",
      runningIntervals: "1 hour",
      createdAt: new Date(2025, 5, 12, 8, 0),
      updatedAt: new Date(2025, 8, 25, 15, 50),
      on: true,
      tags: ["logs", "analytics", "monitoring", "elasticsearch", "kibana"]
    },
    {
      id: 6,
      name: "cpu monitor",
      runningIntervals: "15 seconds",
      createdAt: new Date(2025, 6, 3, 16, 30),
      updatedAt: new Date(2025, 7, 14, 9, 15),
      on: false,
      tags: ["cpu", "performance", "monitoring", "metrics"]
    },
    {
      id: 7,
      name: "memory checker",
      runningIntervals: "45 seconds",
      createdAt: new Date(2025, 1, 28, 12, 45),
      updatedAt: new Date(2025, 6, 30, 11, 40),
      on: true,
      tags: ["memory", "ram", "monitoring", "performance", "metrics"]
    },
    {
      id: 8,
      name: "network scanner",
      runningIntervals: "10 minutes",
      createdAt: new Date(2025, 7, 22, 14, 20),
      updatedAt: new Date(2025, 12, 1, 8, 30),
      on: true,
      tags: ["network", "security", "scanning", "ports"]
    },
    {
      id: 9,
      name: "disk monitor",
      runningIntervals: "30 minutes",
      createdAt: new Date(2025, 8, 10, 7, 15),
      updatedAt: new Date(2025, 11, 20, 17, 25),
      on: true,
      tags: ["disk", "storage", "monitoring", "performance", "filesystem"]
    },
    {
      id: 10,
      name: "process watcher",
      runningIntervals: "20 seconds",
      createdAt: new Date(2025, 9, 5, 10, 30),
      updatedAt: new Date(2025, 12, 15, 13, 10),
      on: false,
      tags: ["process", "system", "monitoring", "performance", "psutil"]
    },
    {
      id: 11,
      name: "file auditor",
      runningIntervals: "4 hours",
      createdAt: new Date(2025, 2, 14, 9, 0),
      updatedAt: new Date(2025, 10, 12, 14, 45),
      on: false,
      tags: ["security", "audit", "filesystem", "compliance", "integrity"]
    }
  ]

const label = ref('personal')
</script>

<template>
  <div>
    <ButtonGroup class="ml-[2vw] mt-[2vh] ">
      <ButtonGroup class="hidden sm:flex">
        <Button variant="outline" size="icon" aria-label="Go Back">
          <ArrowLeftIcon />
        </Button>
      </ButtonGroup>
      <ButtonGroup>
        <Button variant="outline">Edit</Button>
        <Button variant="outline">Turn off</Button>
        <Button variant="outline">Delete</Button>
      </ButtonGroup>
      <ButtonGroup>
        <Badge>Add</Badge>
        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <Button variant="outline" size="icon" aria-label="More Options"><MoreHorizontalIcon/></Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" class="w-52">
            <DropdownMenuLabel class="border-b">Plugins</DropdownMenuLabel>
            <DropdownMenuGroup>
              <DropdownMenuItem>
                <component :is="IconFileImport"/>Import</DropdownMenuItem>
              <DropdownMenuItem>
                <component :is="IconTerminal2"/>Create</DropdownMenuItem>
              <DropdownMenuItem>
                <component :is="IconDatabase"/>Search for plugins</DropdownMenuItem>
            </DropdownMenuGroup>
            <DropdownMenuSeparator/>
            <DropdownMenuGroup>
              <DropdownMenuLabel class=" border-b">Tags</DropdownMenuLabel>
              <DropdownMenuItem>
                <component :is="IconPencilCode"/>Create</DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
      </ButtonGroup>
    </ButtonGroup>

    <MyPluginsTable :data="plugins"/>
  </div>

</template>
