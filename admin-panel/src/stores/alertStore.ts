import {defineStore} from "pinia";
import {computed, h, ref, watch} from "vue";
import {type ActionResponse, type Actions, type ActiveAlert, api_url} from "@/types/types.ts";
import axios from "axios";
import {toast} from "vue-sonner";
import {dashboardData} from "@/data/dashboardData.ts";

export const useAlertStore = defineStore('useAlertStore', () => {
  const currentAlerts = ref<ActiveAlert[]>([])

  const getAllCurrentAlerts = computed(() => currentAlerts.value)

  const setCurrentAlerts = (newAlerts: ActiveAlert[]) => { currentAlerts.value = newAlerts }
  const addCurrentAlert  = (newAlert: ActiveAlert) => { currentAlerts.value.push(newAlert) }
  // zmienic na splice, bo wolne
  const deleteCurrentAlert  = (index: number) => {
    currentAlerts.value = currentAlerts.value.filter(a => a.id !== index)
  }

  const findAlert = (id: number | undefined) => {
    return currentAlerts.value.find(a => a.id === id)
  }

  const getAlertActions = async (id: number) => {
    const response = await axios.get<ActionResponse[]>(`${api_url}/alerts/${id}/actions`)
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
      //alert.actions.push(action)
      alert.acknowledged = action.ack ??  alert.acknowledged
      alert.severity = action.newSeverity ?? alert.severity
    }
  }
  const getCurrentAlertsRequest = async (interval?: number) => {
    try {
      const response = await axios.get<ActiveAlert[]>(`${api_url}/alerts/active`)
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
      const response = await axios.post(`${api_url}/alerts/${action.id}/ack`, {
          ack: action.ack,
          newSeverity: action.newSeverity,
          message: action.message,
          author: action.author,

      })
      if(response.status === 200) {
        //await getCurrentAlertsRequest()
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

  const getAlertsHistory =
    async (pageSize: number, sortBy: string = 'createdAt', descending: boolean = true, firstId?: number, pagesBehind?:number, lastId?: number, pagesAhead?: number,
           searchQuery?: string, systems?: string, origin?: string, dateFrom?: Date, dateTo?: Date) => {
    try {
      const response = await axios.get(`${api_url}/alerts/history`, {
        params: {
          pageSize: pageSize,
          sortBy: sortBy,
          descending: descending,
          firstId: firstId,
          pagesBehind: pagesBehind,
          lastId: lastId,
          pagesAhead: pagesAhead,
          searchQuery: searchQuery,
          systems: systems,
          origin: origin,
          dateFrom: dateFrom,
          dateTo: dateTo,
        }
      })
      if (response.status === 200) {
        toast.info('Alerts history fetched')
        console.log(response.data)
        return response.data;
      }
    }
    catch {
      toast.error('Error getting alerts history');
    }
  }

  return {
    currentAlerts,
    getAllCurrentAlerts,
    setCurrentAlerts,
    addCurrentAlert,
    deleteCurrentAlert,
    getCurrentAlertsRequest,
    updateAlertRequest,
    updateAlert,
    findAlert,
    getAlertActions,
    getAlertsHistory
  }
})

