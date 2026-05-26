import {defineStore} from "pinia";
import type {
  MonitoringSystemsConfig,
  NagiosConfig,
  AlertSystemSettings,
  WazuhConfig,
  ZabbixConfig
} from "@/types/types.ts";
import {computed, ref} from "vue";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";

export const useSettingStore = defineStore('setting-store', () => {

  const systemFullSettings = ref<AlertSystemSettings | null>(null)
  const wazuhConfig = computed((): MonitoringSystemsConfig | null => {
    if (!systemFullSettings.value) return null
    return {
      user: systemFullSettings.value.wazuh_user,
      url: systemFullSettings.value.wazuh_url,
      enabled: systemFullSettings.value.wazuh_enabled,
      wazuh_warning_level: systemFullSettings.value.wazuh_warning_level,
      wazuh_critical_level: systemFullSettings.value.wazuh_critical_level,
      wazuh_info_as_alerts: systemFullSettings.value.wazuh_info_as_alerts,
      name: 'WAZUH',
    }
  })

  const nagiosConfig = computed((): MonitoringSystemsConfig | null => {
    if (!systemFullSettings.value) return null
    return {
      url: systemFullSettings.value.nagios_url,
      enabled: systemFullSettings.value.nagios_enabled,
      user: systemFullSettings.value.nagios_user,
      name: 'NAGIOS',
    }
  })

  const zabbixConfig = computed((): MonitoringSystemsConfig | null => {
    if (!systemFullSettings.value) return null
    return {
      url: systemFullSettings.value.zabbix_url,
      enabled: systemFullSettings.value.zabbix_enabled,
      name: 'ZABBIX',
    }
  })
  const systemsConfig = computed(() => {
    if (!systemFullSettings.value) return []
    return [
      zabbixConfig.value as MonitoringSystemsConfig,
      wazuhConfig.value as MonitoringSystemsConfig,
      nagiosConfig.value as MonitoringSystemsConfig,
    ]
  })

  const getSystemFullSettingsRequest = async () => {
    try {
      const res = await api.get('/settings')
      if (res.status === 200) {
        systemFullSettings.value = {
          ...res.data,
          wazuh_enabled: res.data.wazuh_enabled === "true",
          nagios_enabled: res.data.nagios_enabled === "true",
          zabbix_enabled: res.data.zabbix_enabled === "true",
          smtp_enabled: res.data.smtp_enabled === "true",
          wazuh_info_as_alerts: res.data.wazuh_info_as_alerts === "true",
          wazuh_warning_level: Number(res.data.wazuh_warning_level),
          wazuh_critical_level: Number(res.data.wazuh_critical_level),
        }
        return systemFullSettings.value
      }
    } catch (error) {
      toast.error(`Error retrieving system configuration: ${error}`)
    }
  }

  const updateSystemFullSettingsRequest = async (config: Record<string,string>) => {
    try {
      const res = await api.patch('/settings', config)
      if (res.status === 200) {
        toast.success('System configuration updated')
        systemFullSettings.value = {
          ...res.data,
          wazuh_enabled: res.data.wazuh_enabled === "true",
          nagios_enabled: res.data.nagios_enabled === "true",
          zabbix_enabled: res.data.zabbix_enabled === "true",
          smtp_enabled: res.data.smtp_enabled === "true",
          wazuh_warning_level: Number(res.data.wazuh_warning_level),
          wazuh_critical_level: Number(res.data.wazuh_critical_level),
        }
      }
    } catch (error) {
      throw error
    }
  }



  return {
    systemsConfig,
    systemFullSettings,
    wazuhConfig,
    nagiosConfig,
    zabbixConfig,
    getSystemFullSettingsRequest,
    updateSystemFullSettingsRequest,
  }
})
