<script setup lang="ts">
import {
  IconAlertTriangle,
  IconDatabase,
  IconHistory,
  IconPhoneRinging,
  IconDeviceDesktopAnalytics,
  IconClock,
  IconCalendar,
  IconUsers,
  IconUsersGroup,
  IconSettings,
  IconLockCode,
  IconCode,
  IconHelp,
  IconChartBar,
} from "@tabler/icons-vue";
import {
  Sidebar, SidebarContent,
  SidebarFooter, SidebarGroup, SidebarGroupContent, SidebarGroupLabel, SidebarHeader, SidebarMenu,
  SidebarMenuButton, SidebarMenuItem
} from "@/components/ui/sidebar";
import NavUser from "@/pages/globals/navbar/NavUser.vue";
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import {useAuthStore} from "@/stores/authStore.ts";
import {dateParser} from "@/helpers_functions/dateParser.ts";

const date = ref<Date>(new Date())
const time = ref<string>(dateParser(date.value).fullTime);
const dayMonthYear  = ref<string>(dateParser(date.value).dayMonthYear)
const weekday = ref<string | undefined>(dateParser(date.value).weekday())

const changeTime = () => {
  setInterval(() => {
    const newDate = new Date()
    time.value = dateParser(newDate).fullTime
    if(time.value === '00:00:00') {
      dayMonthYear.value = dateParser(newDate).dayMonthYear
      weekday.value = dateParser(newDate).weekday()
    }
  }, 1000)
}
const route = useRoute()
const authStore = useAuthStore()
onMounted(() => changeTime())

const navItems = authStore.isAdmin ? [
  {
    label: 'Alerts',
    links: [
      { label: 'Active', to: 'active_alerts', icon: IconAlertTriangle },
      { label: 'History', to: 'alerts_history', icon: IconHistory },
    ]
  },
  {
    label: 'Plugins',
    links: [
      { label: 'My plugins', to: 'my_plugins', icon: IconCode },
      { label: 'Library', to: 'plugins_library', icon: IconDatabase },
    ]
  },
  {
    label: 'Team',
    links: [
      { label: 'Team members', to: 'team_members', icon: IconUsers },
      { label: 'Users groups', to: 'groups', icon: IconUsersGroup },
    ],
  },
  {
    label: 'Settings',
    links: [
      { label: 'External systems', to: 'settings/systems', icon: IconDeviceDesktopAnalytics },
      { label: 'Configuration', to: 'settings/configuration', icon: IconSettings },
      { label: 'API keys', to:  'settings/api_keys', icon: IconLockCode}
    ]
  },
  {
    label: 'Analytics',
    links: [
      { label: 'Charts', to: 'charts', icon: IconChartBar },
    ]
  },
] :
  [
    {
      label: 'Alerts',
      links: [
        { label: 'Active', to: 'active_alerts', icon: IconAlertTriangle },
        { label: 'History', to: 'alerts_history', icon: IconHistory },
      ]
    },
    {
      label: 'Plugins',
      links: [
        { label: 'My plugins', to: 'my_plugins', icon: IconCode },
        { label: 'Library', to: 'plugins_library', icon: IconDatabase },
      ]
    },
    {
      label: 'Team',
      links: [
        { label: 'Team members', to: 'team_members', icon: IconUsers },
      ],
    }
  ]

</script>

<template>

  <Sidebar>
    <SidebarHeader>
      <div class="text-xl space-x-2 flex pt-4"><IconPhoneRinging/>
        <span>Alert</span>
      </div>

    </SidebarHeader>
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupContent class="w-fit">
          <SidebarMenu>
            <SidebarMenuItem  v-for="item in navItems" :key="item.label" :value="item.label">
              <SidebarMenuButton as-child>
                <SidebarGroupLabel class="hover:bg-transparent pl-1 flex items-center text-center" >
                  {{ item.label }}
                </SidebarGroupLabel>
                  <RouterLink
                    :class="{'bg-blue-badge/50 border-blue-badge' : route.name?.toString().includes(link.to.toString())}"
                    class="flex text-sm items-center xl:text-md ml-3 border-l-4 w-full gap-x-2 p-2 hover:bg-input rounded-[0_0.5rem_0.5rem_0]"
                    v-for="link in item.links" :key="link.to" :to="{ name: link.to}">
                      <component class="size-5  xl:size-6" :is="link.icon"/>
                    <span>{{link.label}}</span>
                  </RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
            <SidebarMenuItem>
              <SidebarMenuButton as-child>
                <SidebarGroupLabel class="hover:bg-transparent pl-1 flex items-center text-center">
                  Help
                </SidebarGroupLabel>
                <a
                  target="_blank"
                  href="https://github.com/PatriStelmach/AlertVIP/tree/main"
                  class="flex text-sm items-center xl:text-md ml-3 border-l-4 w-full gap-x-2 p-2 hover:bg-input rounded-[0_0.5rem_0.5rem_0]">
                  <IconHelp class="size-5  xl:size-6"/>
                  <span >
                    Documentation
                  </span>
                </a>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
    <SidebarFooter>
      <div class="grid space-y-2 **:text-comment justify-between *:items-center  border-b-3 pb-2">
        <div class="flex space-x-1 ">
          <IconClock class="size-4"/>
          <span class="text-xs mt-1 ">
        {{ time }}
        </span>
        </div>
        <div class="flex space-x-1">
          <IconCalendar class="size-4"/>
          <span class="text-xs mt-1">
            {{ dayMonthYear }}
          </span>
          <span class="text-xs mt-1">
            {{ weekday }}
          </span>
        </div>
      </div>

      <NavUser/>
    </SidebarFooter>
  </Sidebar>
</template>
