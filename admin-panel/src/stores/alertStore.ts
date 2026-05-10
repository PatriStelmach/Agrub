import {defineStore} from "pinia";
import { ref} from "vue";
import {
  type ActionResponse,
  type Actions,
  type ActiveAlert
} from "@/types/types.ts";
import api from "@/lib/axios";
import {toast} from "vue-sonner";

export const useAlertStore = defineStore('useAlertStore', () => {
  const currentAlerts = ref<ActiveAlert[]>([])

  const addCurrentAlert  = (newAlert: ActiveAlert) => { currentAlerts.value.push(newAlert) }
  const deleteCurrentAlert  = (index: number) => {
    currentAlerts.value = currentAlerts.value.filter(a => a.id !== index)
  }

  const findAlert = (id: number | undefined) => {
    return currentAlerts.value.find(a => a.id === id)
  }

  const getAlertActions = async (id: number) => {
    const response = await api.get<ActionResponse[]>(`/alerts/${id}/actions`)
    console.log(id)
    console.log(response.data)
    if (response.status === 200 && response.data) {
      return response.data
    }
    return []
  }

  const updateAlert = (action: ActionResponse)=> {
    const alert = findAlert(action.alertId)
    console.log(alert)
    if (alert) {
      alert.acknowledged = action.ack ??  alert.acknowledged
      alert.severity = action.newSeverity ?? alert.severity
    }
  }
  const getCurrentAlertsRequest = async (interval?: number) => {
    try {
      const response = await api.get<ActiveAlert[]>('/alerts/active')
      if(response.status === 200) {
        currentAlerts.value = response.data
        console.log(response.data)
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



  const updateAlertRequest = async (action: Actions) => {
    console.log(action)
    try {
      const response = await api.post(`/alerts/${action.id}/ack`, {
          ack: action.ack,
          newSeverity: action.newSeverity,
          message: action.message,
          author: action.author,

      })
      if(response.status === 200) {
        toast.success(`Alert updated \n ${response.data.message}`)
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
    addCurrentAlert,
    deleteCurrentAlert,
    getCurrentAlertsRequest,
    updateAlertRequest,
    updateAlert,
    findAlert,
    getAlertActions,
  }
})

