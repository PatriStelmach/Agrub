<script setup lang="ts">
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuIndicator,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  NavigationMenuViewport,
} from '@/components/ui/navigation-menu'
import {
  IconAlertTriangle, IconDatabase,
  IconGizmo,
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
  <NavigationMenu class="w-full">
    <NavigationMenuList class="w-full *:w-1/4 *:min-w-40 **:text-[2vh]">
      <NavigationMenuItem v-for="item in navItems" :key="item.label">
        <NavigationMenuTrigger class="w-full bg-card cursor-pointer"  >{{ item.label }}</NavigationMenuTrigger>
        <NavigationMenuContent class="w-max!">
          <NavigationMenuLink v-for="link in item.links" :key="link.to">
            <div class="flex items-center text-center space-x-2 xl:space-x-4">
              <component class="size-[1vw]" :is="link.icon"/>
              <RouterLink class="text-[1.5vh]" :to="link.to">{{ link.label }}</RouterLink>
            </div>
          </NavigationMenuLink>
        </NavigationMenuContent>
      </NavigationMenuItem>
    </NavigationMenuList>
  </NavigationMenu>
</template>
