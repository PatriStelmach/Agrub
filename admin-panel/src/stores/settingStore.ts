import {defineStore} from "pinia";
import type {
  MonitoringSystemsConfig,
  AlertSystemSettings, ApiKey
} from "@/types/types.ts";
import {computed, ref} from "vue";
import api from "@/lib/axios.ts";

export const useSettingStore = defineStore('setting-store', () => {
  const apiKeys = ref<ApiKey[]>([])
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
          // systemy
          wazuh_enabled: res.data.wazuh_enabled === "true",
          nagios_enabled: res.data.nagios_enabled === "true",
          zabbix_enabled: res.data.zabbix_enabled === "true",
          smtp_enabled: res.data.smtp_enabled === "true",
          wazuh_info_as_alerts: res.data.wazuh_info_as_alerts === "true",
          wazuh_warning_level: Number(res.data.wazuh_warning_level),
          wazuh_critical_level: Number(res.data.wazuh_critical_level),
          // Timeouty i timery (number)
          scripts_execution_timeout_seconds: Number(res.data.scripts_execution_timeout_seconds),
          external_system_sync_timer: Number(res.data.external_system_sync_timer),
          // S SECURITY
          SECURITY_PASSWORD_LIFETIME_DAYS: Number(res.data.SECURITY_PASSWORD_LIFETIME_DAYS),
          SECURITY_ACCESS_TOKEN_EXP_MINUTES: Number(res.data.SECURITY_ACCESS_TOKEN_EXP_MINUTES),
          SECURITY_REFRESH_TOKEN_EXP_HOURS: Number(res.data.SECURITY_REFRESH_TOKEN_EXP_HOURS),
        }
      }
    } catch (error) {
      throw error
    }
  }

  const updateSystemFullSettingsRequest = async (config: Record<string,string>) => {
    try {
      const res = await api.patch('/settings', config)
      if (res.status === 200) {
        systemFullSettings.value = {
          ...res.data,
          // systemy
          wazuh_enabled: res.data.wazuh_enabled === "true",
          nagios_enabled: res.data.nagios_enabled === "true",
          zabbix_enabled: res.data.zabbix_enabled === "true",
          smtp_enabled: res.data.smtp_enabled === "true",
          wazuh_info_as_alerts: res.data.wazuh_info_as_alerts === "true",
          wazuh_warning_level: Number(res.data.wazuh_warning_level),
          wazuh_critical_level: Number(res.data.wazuh_critical_level),
          // Timeouty i timery
          scripts_execution_timeout_seconds: Number(res.data.scripts_execution_timeout_seconds),
          external_system_sync_timer: Number(res.data.external_system_sync_timer),
          //  SECURITY
          SECURITY_PASSWORD_LIFETIME_DAYS: Number(res.data.SECURITY_PASSWORD_LIFETIME_DAYS),
          SECURITY_ACCESS_TOKEN_EXP_MINUTES: Number(res.data.SECURITY_ACCESS_TOKEN_EXP_MINUTES),
          SECURITY_REFRESH_TOKEN_EXP_HOURS: Number(res.data.SECURITY_REFRESH_TOKEN_EXP_HOURS),
        }
      }
    } catch (error) {
      throw error
    }
  }

  const getApiKeysRequest = async () => {
    try {
      const res = await api.get(`/admin/keys`)
      if (res.status === 200) {
        apiKeys.value = res.data
        return
      }
    } catch (error) {
      throw (error)
    }
  }

  const revokeApiKeyRequest = async (id: number) => {
    try {
      const res = await api.put(`/admin/keys/${id}/revoke`)
      if (res.status === 200) {
        const key = apiKeys.value.find((k) => k.id === id)
        if(key)
          key.active = !key.active
        return res.data
      }
    } catch (error) {
      throw (error)
    }
  }

  const deleteApiKeyRequest = async (id: number) => {
    try {
      const res = await api.delete(`/admin/keys/${id}`)
      if (res.status === 200) {
        apiKeys.value = apiKeys.value.filter((k) => k.id !== id)
        return
      }
    } catch (error) {
      throw (error)
    }
  }

  const generateApiKeyRequest = async (description: string) => {
    try {
      const res = await api.post('/admin/keys/generate', {description: description})
      if (res.status === 200) {
        apiKeys.value.push(res.data)
        return
      }
    } catch (error) {
      throw (error)
    }
  }




  return {
    systemsConfig,
    systemFullSettings,
    wazuhConfig,
    nagiosConfig,
    zabbixConfig,
    apiKeys,
    getSystemFullSettingsRequest,
    updateSystemFullSettingsRequest,
    revokeApiKeyRequest,
    getApiKeysRequest,
    deleteApiKeyRequest,
    generateApiKeyRequest,
  }
})
