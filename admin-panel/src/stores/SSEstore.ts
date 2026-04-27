import {useEventSource} from "@vueuse/core";
import {api_url} from "@/types/types.ts";
import {h, watch} from "vue";
import {toast} from "vue-sonner";
import {IconAlertTriangle, IconCircleDashedCheck, IconRefresh} from "@tabler/icons-vue";
import {useAlertStore} from "@/stores/alertStore.ts";

export function useSSEstore()   {
  const alertStore = useAlertStore()
  const { status, data, error, close } = useEventSource(
    `${api_url}/alerts/stream`,
    [],
    {
      serializer: { read: rawData => JSON.parse(rawData!) },
      autoReconnect:{
        retries: 5,
        delay: 1000,
        onFailed() {
          alert(`Error connecting to API: ${error}`);
        },

      }
    })
  setTimeout(() =>{

    toast.info(`SSE STATUS: ${status.value}`)
  }, 1000)

  watch(status, (newStatus) => {
    toast.info(`SSE STATUS: ${newStatus}`);
  })

  watch(data, (nVal) => {
    if (!nVal)
      return
    else {
      if (nVal.status === 'loading') {
        toast.info(nVal.msg, { icon: h(IconRefresh, { class:'animate-spin '})})}
      if (nVal.status === 'error')
        toast.error(nVal.msg, { icon: h(IconAlertTriangle, { class:'animate-ping duration-100'})})
      if (nVal.status === 'success'){
        toast.success(nVal.message, { icon: h(IconCircleDashedCheck, {class:'animate-pulse duration-100'})})
          alertStore.addCurrentAlert(nVal.message)
      }
    }
  })
}
