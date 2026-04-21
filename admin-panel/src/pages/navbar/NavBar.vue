<script setup lang="ts">
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import {
  IconAlertTriangle, IconDatabase,
  IconHistory,
  IconLogs,
  IconScript, IconSitemap, IconSitemapOff,
  IconUpload
} from "@tabler/icons-vue";

const navItems = [
  {
    label: 'Alerts',
    links: [
      { label: 'Active alerts', to: 'alerts', icon: IconAlertTriangle },
      { label: 'History', to: 'alerts_history', icon: IconHistory },
    ]
  },
  {
    label: 'Logs',
    links: [
      { label: 'Active logs', to: 'logs', icon: IconLogs },
      { label: 'History', to: 'logs_history', icon: IconHistory },
    ]
  },
  {
    label: 'Plugins',
    links: [
      { label: 'My plugins', to: 'my_plugins', icon: IconScript },
      { label: 'Library', to: 'plugins_library', icon: IconDatabase },
      { label: 'Import', to: 'import_plugins', icon: IconUpload },
    ]
  },
  {
    label: 'Systems',
    links: [
      { label: 'My systems', to: 'my_systems', icon: IconSitemap },
      { label: 'All systems', to: 'all_systems', icon: IconSitemapOff },
    ]
  },
]
</script>

<template>

  <Accordion class="w-40 xl:w-50 mx-4" type="single" collapsible>
    <AccordionItem class="" v-for="item in navItems" :key="item.label" :value="item.label">
      <AccordionTrigger class=" cursor-pointer lg:text-lg xl:text-xl">
        <div class="flex items-center text-center space-x-2 xl:space-x-4">
          {{ item.label }}
        </div>
      </AccordionTrigger>
      <AccordionContent class="grid gap-y-2 ">
        <RouterLink class="hover:bg-input py-2 px-1 rounded-md xl:text-lg" v-for="link in item.links" :key="link.to" :to="link.to">
          <div class="flex items-center gap-x-2"><component class="size-4  xl:size-6" :is="link.icon"/> {{link.label}} </div></RouterLink>
      </AccordionContent>
    </AccordionItem>
  </Accordion>

</template>
