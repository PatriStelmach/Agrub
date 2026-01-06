import {
  IconChartBar,
  IconDashboard,
  IconDatabase,
  IconFolder,
  IconHelp,
  IconLayoutGridAdd,
  IconLayoutGridFilled,
  IconSettings,
  IconUsers,
  IconClipboardText, IconTerminal2, IconFileImport
} from "@tabler/icons-vue"

export const sidebarData = {
  user: {
    name: "admin",
    email: "admin@pjatk",
    avatar: "/avatars/shadcn.jpg",
  },
  navMain: [
    {
      title: "Dashboard",
      url: "/",
      icon: IconDashboard,
    },
    {
      title: "Connected systems",
      url: "/my_systems",
      icon: IconLayoutGridFilled,
    },
    {
      title: "Add new system",
      url: "/systems_library",
      icon: IconLayoutGridAdd,
    },
    {
      title: "Analytics",
      url: "#",
      icon: IconChartBar,
    },
    {
      title: "Log files",
      url: "#",
      icon: IconClipboardText,
    },
    {
      title: "Team",
      url: "#",
      icon: IconUsers,
    },
  ],
  navSecondary: [
    {
      title: "Settings",
      url: "#",
      icon: IconSettings,
    },
    {
      title: "Get Help",
      url: "#",
      icon: IconHelp,
    },
  ],
  documents: [
    {
      name: "Plugins library",
      url: "/plugins_library",
      icon: IconDatabase,
    },
    {
      name: "My plugins",
      url: "/my_plugins",
      icon: IconFolder,
    },
    {
      name: "Create plugin",
      url: "#",
      icon: IconTerminal2,
    },
    {
      name: "Import plugin",
      url: "#",
      icon: IconFileImport,
    },
  ],
}
