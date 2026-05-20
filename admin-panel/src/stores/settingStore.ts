import {defineStore} from "pinia";
import type {
  MonitoringSystemsConfig,
  NagiosConfig,
  systemFullSettings,
  WazuhConfig,
  ZabbixConfig
} from "@/types/types.ts";
import {computed, ref, watchEffect} from "vue";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";

export const useSettingStore = defineStore('setting-store', () => {

  const systemFullSettings = ref<systemFullSettings | null>(null)
  const wazuhConfig = computed((): MonitoringSystemsConfig | null => {
    if (!systemFullSettings.value) return null
    return {
      user: systemFullSettings.value.wazuh_user,
      url: systemFullSettings.value.wazuh_url,
      enabled: systemFullSettings.value.wazuh_enabled,
      min_active_level: systemFullSettings.value.wazuh_min_active_level,
      name: 'Wazuh',
    }
  })

  const nagiosConfig = computed((): MonitoringSystemsConfig | null => {
    if (!systemFullSettings.value) return null
    return {
      url: systemFullSettings.value.nagios_url,
      enabled: systemFullSettings.value.nagios_enabled,
      user: systemFullSettings.value.nagios_user,
      name: 'Nagios',
    }
  })

  const zabbixConfig = computed((): MonitoringSystemsConfig | null => {
    if (!systemFullSettings.value) return null
    return {
      url: systemFullSettings.value.zabbix_url,
      enabled: systemFullSettings.value.zabbix_enabled,
      name: 'Zabbix',
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
          wazuh_min_active_level: Number(res.data.wazuh_min_active_level),
        }
        return systemFullSettings.value
      }
    } catch (error) {
      toast.error(`Error retrieving system config: ${error}`)
    }
  }

  const updatesystemFullSettingsRequest = async (config: WazuhConfig | NagiosConfig | ZabbixConfig) => {
    try {
      const res = await api.put('/settings', config)
      if (res.status === 200) {
        toast.success('System config updated')
        return res.data
      }
    } catch (error) {
      toast.error(`Error updating system config: ${error}`)
    }
  }



  return {
    systemsConfig,
    systemFullSettings,
    wazuhConfig,
    nagiosConfig,
    zabbixConfig,
    getSystemFullSettingsRequest,
    updatesystemFullSettingsRequest,
  }
})
