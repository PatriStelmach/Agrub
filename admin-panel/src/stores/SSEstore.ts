import { api_url } from "@/types/types.ts";
import { h, ref } from "vue";
import { toast } from "vue-sonner";
import { IconAlertTriangle, IconCircleDashedCheck, IconRefresh } from "@tabler/icons-vue";
import { useAlertStore } from "@/stores/alertStore.ts";
import { useNotificationStore } from "@/stores/notificationStore.ts";
import { defineStore } from "pinia";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import {useAuthStore} from "@/stores/authStore.ts";

export const useSSEstore = defineStore('SSE', () => {
  const alertStore = useAlertStore()
  const notificationStore = useNotificationStore()
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
          toast.error(event.message.subject, { icon: h(IconAlertTriangle, { class: 'animate-pulse duration-100' }) });
          alertStore.addCurrentAlert(event.message);
          notificationStore.addNotification();
          break;
        }
        case 'ALERT_RESOLVED': {
          toast.success(`Resolved: ${event.message.subject}`, { icon: IconCircleDashedCheck });
          alertStore.deleteCurrentAlert(event.message.id);
          notificationStore.removeNotification();
          break;
        }
        case 'ALERT_UPDATE': {
          toast.success(`Updated: ${event.message.alertId}`);
          alertStore.updateAlert(event.message);
          break;
        }
      }
    }
  };

  const connectToSSE = async () => {
    const token = authStore.accessToken

    await fetchEventSource(`${api_url}/alerts/stream`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'text/event-stream',
      },
      async onopen(response) {
        if (response.ok && response.headers.get('content-type')?.includes('text/event-stream')) {
          isConnected.value = true;
          console.log('connected to sse')
        } else if (response.status >= 400 && response.status < 500 && response.status !== 429) {
          throw new Error(`Fatal connection error: ${response.status}`);
        }
      },
      onmessage(msg) {
        console.log(`data: ${msg.data}`);
          const parsedData = JSON.parse(msg.data);
          handleSSEMessage(parsedData);
          console.log(parsedData);
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
