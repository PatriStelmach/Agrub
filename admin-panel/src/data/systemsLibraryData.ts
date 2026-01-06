import zabbix_logo from "@/components/icons/zabbix_logo.jpeg"
import wazuh_logo from "@/components/icons/wazuh_logo.png"
import nagios_logo from "@/components/icons/nagios_logo.webp"
import type {System} from "@/types/system.ts";

export const systemsLibraryData:  System[] = [
  {
    id:1,
    name: "Zabbix",
    img: zabbix_logo,
    openSource: true,
    description: "Open-source software tool to monitor IT infrastructure such as networks, servers, virtual machines, and cloud services."
  },
  {
    id:2,
    name: "Wazuh",
    img: wazuh_logo,
    openSource: true,
    description: "Analyzes security data across endpoints, clouds, and networks to detect threats, respond to incidents, and ensure compliance."
  },
  {
    id:3,
    name: "Nagios",
    img: nagios_logo,
    openSource: false,
    description: "Event monitoring system that offers monitoring and alerting services for servers, switches, applications and services."
  }
  ]

