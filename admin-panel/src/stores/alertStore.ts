import {defineStore} from "pinia";
import {ref} from "vue";
import {type AlertObject, api_url} from "@/types/types.ts";
import axios from "axios";

export const alertStore = defineStore('alertStore', () => {
  const currentAlerts = ref<AlertObject[]>([])
  const getCurrentAlerts = async () => {
    currentAlerts.value = await axios.get<AlertObject[]>(`${api_url}/alerts/current`)
      .then((res) => res.data as AlertObject[]);
  }

  return {

  }
})
