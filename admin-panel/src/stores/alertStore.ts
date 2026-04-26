import {defineStore} from "pinia";
import {computed, h, ref, watch} from "vue";
import {type AlertObject, api_url} from "@/types/types.ts";
import axios from "axios";
import {toast} from "vue-sonner";
import {dashboardData} from "@/data/dashboardData.ts";

export const useAlertStore = defineStore('useAlertStore', () => {
  const currentAlerts = ref<AlertObject[]>([])
  const currentAlertsIds = ref<number[]>([])

  const getAllCurrentAlerts = computed(() =>
    currentAlerts.value.filter(a => a.status !== "Done"))

  const setCurrentAlerts = (newAlerts: AlertObject[]) => {
    currentAlerts.value = newAlerts;
  }

  const addCurrentAlert  = (newAlert: AlertObject) => {
    currentAlerts.value.push(newAlert);
  }

  const getCurrentAlertsRequest = async () => {
    try {
      const response = await axios.get<AlertObject[]>(`${api_url}/alerts/active`)
      if(response.status === 200 && response.data.length) {
        currentAlerts.value = response.data
        currentAlertsIds.value = response.data.map(a => a.id)
        toast.success('Alerts from db loaded!')
      }
      else {
        currentAlerts.value = dashboardData
        toast.error('Mocked alerts loaded!')
      }
    }
    catch {
      currentAlerts.value = dashboardData
      toast.error('Mocked alerts loaded!')
    }
  }

  const checkCurrentAlertsRequest = async (interval?: number) => {
      if (!currentAlerts.value.length && !currentAlertsIds.value.length) {
        await getCurrentAlertsRequest()
      }
      else {
        const diff = await axios.post(`${api_url}/alerts/check-active`, {
          body: currentAlertsIds.value
        })
        if (diff.status === 204)
          return
        else {
          await getCurrentAlertsRequest()
        }
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
    getCurrentAlertsRequest,
    checkCurrentAlertsRequest
  }
})

