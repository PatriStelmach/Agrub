import {
  IconChartBar,
  IconDashboard,
  IconDatabase,
  IconFolder,
  IconLayoutGridAdd,
  IconLayoutGridFilled,
  IconUsers,
  IconClipboardText, IconTerminal2, IconFileImport
} from "@tabler/icons-vue"
import type {User} from "@/types/user.ts";
export const sidebarData = {
  loggedUser: {
    id: 40,
    firstname: "admin",
    surname: "pjatk",
    email: "admin@pjatk",
    role: "admin" as User["role"],
    groups: ["Administration"],
    avatar: "/avatars/shadcn.jpg",
    active: true
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
      name: "Import plugins",
      url: "/import_plugins",
      icon: IconFileImport,
    },
  ],
}
