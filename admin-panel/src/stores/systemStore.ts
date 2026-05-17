import {defineStore} from "pinia";
import type {NagiosConfig, SystemConfig, SystemsStatus, WazuhConfig, ZabbixConfig} from "@/types/types.ts";
import {computed, ref} from "vue";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";

export const useSystemStore = defineStore('system-store', () => {
  const systemsStatus = ref<SystemsStatus>({
    wazuh_enabled: false,
    zabbix_enabled: false,
    nagios_enabled: false
  })
  const systemConfig = ref<SystemConfig | null>(null)

  const getAllStatusesRequest = async () => {
    try {
      const res = await api.get('/settings/enabled')
      if (res.status === 200) {
        console.log(res.data)
        systemsStatus.value = res.data
        return res.data;
      }
    } catch (error) {
      toast.error(`Error retrieving systems settings: ${error}`)
    }
  }

  const getSystemConfigRequest = async () => {
    try {
      const res = await api.get('/settings')
      if (res.status === 200) {
        systemConfig.value = res.data
        return res.data as SystemConfig
      }
    } catch (error) {
      toast.error(`Error retrieving system config: ${error}`)
    }
  }

  const updateSystemConfigRequest = async (config: WazuhConfig | NagiosConfig | ZabbixConfig) => {
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

  const wazuhConfig = computed((): WazuhConfig | null => {
    if (!systemConfig.value) return null
    return {
      wazuh_user: systemConfig.value.wazuh_user,
      wazuh_password: systemConfig.value.wazuh_password,
      wazuh_url: systemConfig.value.wazuh_url,
      wazuh_enabled: systemConfig.value.wazuh_enabled,
      wazuh_min_active_level: systemConfig.value.wazuh_min_active_level,
    }
  })

  const nagiosConfig = computed((): NagiosConfig | null => {
    if (!systemConfig.value) return null
    return {
      nagios_url: systemConfig.value.nagios_url,
      nagios_enabled: systemConfig.value.nagios_enabled,
      nagios_user: systemConfig.value.nagios_user,
      nagios_pass: systemConfig.value.nagios_pass,
    }
  })

  const zabbixConfig = computed((): ZabbixConfig | null => {
    if (!systemConfig.value) return null
    return {
      zabbix_url: systemConfig.value.zabbix_url,
      zabbix_enabled: systemConfig.value.zabbix_enabled,
      zabbix_api_token: systemConfig.value.zabbix_api_token,
    }
  })

  return {
    systemsStatus,
    systemConfig,
    wazuhConfig,
    nagiosConfig,
    zabbixConfig,
    getAllStatusesRequest,
    getSystemConfigRequest,
    updateSystemConfigRequest,
  }
})
