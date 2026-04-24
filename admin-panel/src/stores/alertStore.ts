import {defineStore} from "pinia";
import {computed, h, ref, watch} from "vue";
import {type AlertObject, api_url} from "@/types/types.ts";
import axios from "axios";
import {toast} from "vue-sonner";
import {dashboardData} from "@/data/dashboardData.ts";

export const useAlertStore = defineStore('useAlertStore', () => {
  const currentAlerts = ref<AlertObject[]>([])

  const getAllCurrentAlerts = computed(() =>
    currentAlerts.value.filter(a => a.status !== "Done"))

  const setCurrentAlerts = (newAlerts: AlertObject[]) => {
    currentAlerts.value = newAlerts;
  }

  const addCurrentAlert  = (newAlert: AlertObject) => {
    currentAlerts.value.push(newAlert);
  }

  const getCurrentAlertsRequest = async () => {
    const response = await axios.get<AlertObject[]>(`${api_url}/alerts/active`)
    if (response.status === 200 && response.data.length) {
      currentAlerts.value = response.data
      toast.success('Alerts from db loaded!')
    }
    else {
      currentAlerts.value = dashboardData
      toast.error('Mocked alerts loaded!')
    }
  }


  return {
    currentAlerts,
    getAllCurrentAlerts,
    setCurrentAlerts,
    addCurrentAlert,
    getCurrentAlertsRequest
  }
})

