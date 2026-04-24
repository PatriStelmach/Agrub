import {defineStore} from "pinia";
import {h, ref, watch} from "vue";
import {type AlertObject, api_url} from "@/types/types.ts";
import axios from "axios";
import {useEventSource} from "@vueuse/core";
import {IconAlertTriangle, IconCircleDashedCheck, IconRefresh} from "@tabler/icons-vue";
import {toast} from "vue-sonner";

export const useAlertStore = defineStore('useAlertStore', () => {
  const currentAlerts = ref<AlertObject[]>([])
  const getCurrentAlerts = async () => {
    currentAlerts.value = await axios.get<AlertObject[]>(`${api_url}/alerts/active`)
      .then((res) => res.data as AlertObject[]);
  }

  const { status, data, error, close } = useEventSource(
    `${api_url}/alerts/stream`,
    [],
    {
      autoReconnect:{
        retries: 5,
        delay: 1000,
        onFailed() {
          alert(`Error retrieving data from API: ${error}`);
        },

      }
    })

  watch(status, () => {
    console.log(status)
  })

  watch(data, (nVal) => {
    if (!nVal)
      return
    else {
      if (nVal.status === 'loading') {
        toast.info(nVal.msg, { icon: h(IconRefresh, { class:'animate-spin '})})}
      if (nVal.status === 'error')
        toast.error(nVal.msg, { icon: h(IconAlertTriangle, { class:'animate-ping duration-100'})})
      if (nVal.status === 'success')
        toast.success(nVal.msg, { icon: h(IconCircleDashedCheck, {class:'animate-pulse duration-100'})})
    }
  })
  return {

  }
})
