import {defineStore} from "pinia";
import {computed, h, ref, watch} from "vue";
import {type OpenAlert, api_url} from "@/types/types.ts";
import axios from "axios";
import {toast} from "vue-sonner";
import {dashboardData} from "@/data/dashboardData.ts";

export const useAlertStore = defineStore('useAlertStore', () => {
  const currentAlerts = ref<OpenAlert[]>([])
  const currentAlertsIds = ref<number[]>([])

  const getAllCurrentAlerts = computed(() =>currentAlerts.value)

  const setCurrentAlerts = (newAlerts: OpenAlert[]) => { currentAlerts.value = newAlerts }
  const addCurrentAlert  = (newAlert: OpenAlert) => { currentAlerts.value.push(newAlert) }
  const deleteCurrentAlert  = (index: number) => {
    currentAlerts.value = currentAlerts.value.filter(a => a.id !== index)
  }
  const getCurrentAlertsRequest = async (interval?: number) => {
    try {
      const response = await axios.get<OpenAlert[]>(`${api_url}/alerts/active`)
      if(response.status === 200) {
        currentAlerts.value = response.data
        currentAlertsIds.value = response.data.map(a => a.id)
      }
      else {
        toast.error('Error while fetching current alerts')
      }
    }
    catch {
      toast.error('Error getting current alerts')
    }
    if(interval) {
      setTimeout(() => {
        getCurrentAlertsRequest(interval)
      }, interval)
    }
  }


  const ackAlertRequest = async (alertId: number) => {
    try {
      const response = await axios.post(`${api_url}/alerts/${alertId}/ack`)
      if(response.status === 200) {
        await getCurrentAlertsRequest()
        toast.success(response.data.message)
      }
      else {
        toast.error(response.data.message)
      }
    }
    catch {
      toast.error('Failed to send ack!')
    }
  }


  return {
    currentAlerts,
    getAllCurrentAlerts,
    setCurrentAlerts,
    addCurrentAlert,
    deleteCurrentAlert,
    getCurrentAlertsRequest,
    ackAlertRequest,
  }
})

