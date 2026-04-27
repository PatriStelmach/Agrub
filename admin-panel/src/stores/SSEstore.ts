import {useEventSource} from "@vueuse/core";
import {api_url} from "@/types/types.ts";
import {h, watch} from "vue";
import {toast} from "vue-sonner";
import {IconAlertTriangle, IconCircleDashedCheck, IconRefresh} from "@tabler/icons-vue";
import {useAlertStore} from "@/stores/alertStore.ts";
import {useNotificationStore} from "@/stores/notificationStore.ts";

export function useSSEstore()   {
  const alertStore = useAlertStore()
  const notificationStore = useNotificationStore()
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


  watch(status, (newStatus) => {
    toast.info(`SSE STATUS: ${newStatus}`);
    console.log(newStatus)
  })

  watch(data, (nVal) => {
    if (!nVal)
      return
    else {
      if (nVal.status === 'loading') {
        toast.info(nVal.message, { icon: h(IconRefresh, { class:'animate-spin '})})
        console.log(nVal.message)
      }
      if (nVal.status === 'error') {
        toast.error(nVal.message, { icon: h(IconAlertTriangle, { class:'animate-ping duration-100'})})
        console.log(nVal.message)
      }
      if (nVal.status === 'success') {
        switch (nVal.eventType) {
          case 'NEW_ALERT': {
            toast.error(nVal.message.subject, { icon: h(IconAlertTriangle, {class:'animate-pulse duration-100'})})
            alertStore.addCurrentAlert(nVal.message)
            notificationStore.addNotification()
            break;
          }
          case 'ALERT_RESOLVED': {
            toast.success(`Resolved: ${nVal.message.subject}`, { icon: IconCircleDashedCheck})
            alertStore.deleteCurrentAlert(nVal.message.id)
            notificationStore.removeNotification()
            break;
          }


        }

      }
    }
  })
}
