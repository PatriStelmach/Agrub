import {defineStore} from "pinia";
import { ref} from "vue";
import {
  type ActionResponse, type Actions,
  type ActiveAlert, SeverityRecord
} from "@/types/types.ts";
import api from "@/lib/axios";
import {toast} from "vue-sonner";
import {useRouter} from "vue-router";

export const useAlertStore = defineStore('alert-store', () => {
  const currentAlerts = ref<ActiveAlert[]>([])
  const router = useRouter();

  const addCurrentAlert  = (newAlert: ActiveAlert) => { currentAlerts.value.push(newAlert) }
  const deleteCurrentAlert  = (index: number) => {
    currentAlerts.value = currentAlerts.value.filter(a => a.id !== index)
  }

  const findAlert = (id: number | undefined) => {
    return currentAlerts.value.find(a => a.id === id)
  }

  const findAlertIndex = (id: number | undefined) => {
    return currentAlerts.value.findIndex(a => a.id === id)
  }

  const updateAlertActions = (action: Actions) => {
    const alert = findAlert(action.alertId)
    if (alert) {
      const changes: string[] = []

      if (action.ack !== null && action.ack !== undefined) {
        const ackText = action.ack ? 'Acknowledged' : 'Unacknowledged'
        changes.push(ackText)
        alert.isAcknowledged = action.ack
      }
      if (action.newSeverity !== undefined && action.newSeverity !== alert.severity) {
        changes.push(`Severity changed to ${SeverityRecord[action.newSeverity]}`)
        alert.severity = action.newSeverity
      }

      if (action.message && action.message.trim() !== "") {
        changes.push(`message: ${action.message}`)
      }

      alert.actions.push({
        id: action.id,
        author: action.author,
        ackUpdate: action.ack,
        newSeverity: action.newSeverity,
        previousSeverity: action.previousSeverity,
        message: action.message,
        alertId: action.alertId,
        createdAt: action.createdAt
      } as ActionResponse)

      const changesText = changes.length > 0 ? changes.join(' | ') : 'Updated action'

      toast.success(`${alert.subject} updated by: ${action.author} --> ${changesText}` , {
        action: {
          label: "Open",
          onClick: async () => {
            await router.push(`/active_alerts/${alert.id}`)
          }
        }
      })
    }
  }

  const updateAlert = (updatedAlert : ActiveAlert) => {
    const index = findAlertIndex(updatedAlert.id)
    if (index !== -1) {
      currentAlerts.value[index] = updatedAlert;
    }
  }

  const getCurrentAlertsRequest = async () => {
    try {
      const response = await api.get<ActiveAlert[]>('/alerts/active')
      if(response.status === 200) {
        currentAlerts.value = response.data.map((a :any) => ({
          ...a,
          isAcknowledged: a.acknowledged
        }))
      }
      else {
        toast.error('Error while fetching current alerts')
      }
    }
    catch {
      toast.error('Error getting current alerts')
    }
  }




  return {
    currentAlerts,
    addCurrentAlert,
    deleteCurrentAlert,
    getCurrentAlertsRequest,
    updateAlert,
    updateAlertActions,
    findAlert,
  }
})

