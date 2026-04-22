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
import type {User} from "@/types/types";
export const sidebarData = {
  loggedUser: {
    id: 40,
    firstname: "Administrator",
    surname: "pjatk",
    email: "admin@pjatk",
    role: "Administrator" as User["role"],
    groups: ["Administration"],
    avatar: "/avatars/shadcn.jpg",
    active: true
  },
  navMain: [
    {
      title: "Alerts",
      url: "/",
      icon: IconDashboard,
    },
    {
      title: "Logs",
      url: "/logs",
      icon: IconClipboardText,
    },
    {
      title: "Connected systems",
      url: "/my_systems",
      icon: IconLayoutGridFilled,
    },
    {
      title: "Add new system",
      url: "/all_systems",
      icon: IconLayoutGridAdd,
    },
    {
      title: "Analytics",
      url: "#",
      icon: IconChartBar,
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
