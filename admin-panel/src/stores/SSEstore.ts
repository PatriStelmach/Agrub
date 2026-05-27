import { api_url } from "@/types/types.ts";
import { h, ref } from "vue";
import { toast } from "vue-sonner";
import { IconAlertTriangle, IconCircleCheck, IconRefresh } from "@tabler/icons-vue";
import { useAlertStore } from "@/stores/alertStore.ts";
import { defineStore } from "pinia";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import {useAuthStore} from "@/stores/authStore.ts";

export const useSSEstore = defineStore('SSE', () => {
  const alertStore = useAlertStore()
  const authStore = useAuthStore()
  const isConnected = ref(false)

  const handleSSEMessage = (event: any) => {
    if (!event) return;

    if (event.status === 'loading') {
      toast.info(event.message, { icon: h(IconRefresh, { class: 'animate-spin' }) });
    }

    if (event.status === 'error') {
      toast.error(event.message, { icon: h(IconAlertTriangle, { class: 'animate-ping duration-100' }) });
    }

    if (event.status === 'success') {
      switch (event.eventType) {
        case 'NEW_ALERT': {
          toast.error(`New alert: ${event.message.subject}`,
            { icon: h(IconAlertTriangle, { class: 'animate-pulse duration-100' }) });
          alertStore.addCurrentAlert(event.message);
          break;
        }
        case 'ALERT_RESOLVED': {
          toast.success(`Alert resolved: ${event.message.subject}`, { icon: IconCircleCheck });
          alertStore.deleteCurrentAlert(event.message.id);
          break;
        }
        case 'ALERT_UPDATE_ONLY': {
          alertStore.updateAlertActions(event.message);
          break;
        }
        case 'ALERT_UPDATE' : {
          if(event.message)
          toast.success(`Alert updated: ${event.message.subject}`);
          alertStore.updateAlert(event.message);
          break;
        }
      }
    }
  };

  const connectToSSE = async () => {


    await fetchEventSource(`${api_url}/alerts/stream`, {
      headers: {
        'Authorization': `Bearer ${authStore.accessToken}`,
        'Accept': 'text/event-stream',
      },
      credentials: 'include',
      openWhenHidden: true,
      async onopen(response) {
        if (response.ok && response.headers.get('content-type')?.includes('text/event-stream')) {
          isConnected.value = true;
          console.log('connected to sse')
        } else if (response.status >= 400 && response.status < 500 && response.status !== 429) {
          throw new Error(`Fatal connection error: ${response.status}`);
        }
      },
      onmessage(event) {
        if(event.event !== 'INIT') {
          handleSSEMessage(JSON.parse(event.data));
        }
      },

      onclose() {
        isConnected.value = false;
        console.log("SSE connection closed by server");
      },
      onerror(err) {
        console.error("SSE Error:", err);
        throw err;
      },
    });
  };

  connectToSSE();

  return {
    isConnected
  };
});
