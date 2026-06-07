import { api_url } from "@/types/types.ts";
import { h, ref } from "vue";
import { toast } from "vue-sonner";
import { IconAlertCircle, IconCircleCheck, IconRefresh } from "@tabler/icons-vue";
import { useAlertStore } from "@/stores/alertStore.ts";
import { defineStore } from "pinia";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import {useAuthStore} from "@/stores/authStore.ts";
import {useRouter} from "vue-router";

export const useSSEstore = defineStore('SSE', () => {
  const alertStore = useAlertStore()
  const authStore = useAuthStore()
  const router = useRouter()
  const isConnected = ref(false)

  const handleSSEMessage = (event: any) => {
    if (!event) return;

    if (event.status === 'loading') {
      toast.info(event.message, { icon: h(IconRefresh, { class: 'animate-spin' }) });
    }

    if (event.status === 'error') {
      toast.error(event.message, { icon: h(IconAlertCircle, { class: 'animate-ping duration-100' }) });
    }

    if (event.status === 'success') {
      switch (event.eventType) {
        case 'NEW_ALERT': {
          toast.error(`New alert: ${event.message.subject}`, {
            duration: 120000,
            position: "bottom-right",
            closeButton: true,
            action: {
              label: "Open" ,
              onClick: async () => {
                await router.push(`/active_alerts/${event.message.id}`);
              }
            },
            class: '[&_div]:text-severity-5 bg-background! border-severity-5/50! border-2! ' +
              '[&_button]:text-severity-5! [&_button]:bg-background! [&_button]:border! ' +
              '[&_button]:hover:border-severity-5! ' +
              '[&_button]:hover:bg-severity-5/50! [&_button]:hover:text-primary! ',
            icon: h(IconAlertCircle, {
              class: 'size-5 animate-pulse'
            })
          });
          alertStore.addCurrentAlert(event.message);
          break;
        }
        case 'ALERT_RESOLVED': {
          toast.success(`Alert resolved: ${event.message.subject}`, {
            duration: 120000,
            position: "bottom-right",
            closeButton: true,
            action: {
              label: "Open",
              onClick: async () => {
                await router.push({ path: `/alerts_history/${event.message.id}` });
              },
            },
            class: '[&_div]:text-severity-2! bg-background! border-severity-2/50! border-2! ' +
              '[&_button]:text-severity-2! [&_button]:bg-background! [&_button]:border! ' +
              '[&_button]:hover:border-severity-2! ' +
              '[&_button]:hover:bg-severity-2/50! [&_button]:hover:text-primary! ',
            icon: h(IconCircleCheck, {
              class: 'size-5'
            })
          });
          alertStore.deleteCurrentAlert(event.message.id);
          break;
        }
        case 'ALERT_UPDATE_ONLY': {
          alertStore.updateAlertActions(event.message);
          break;
        }
        case 'ALERT_UPDATE' : {
          if(event.message)
          toast.info(`Alert updated: ${event.message.subject}`, {
            duration: 120000,
            position: "bottom-right",
            closeButton: true,
            action: {
              label: "Open",
              onClick: async () => {
                await router.push({ path: `/active_alerts/${event.message.alertId}` });
              }
            },
            class: '[&_div]:text-severity-1! bg-background! border-severity-1/50! border-2! ' +
              '[&_button]:text-severity-1! [&_button]:bg-background! [&_button]:border! ' +
              '[&_button]:hover:border-severity-1! ' +
              '[&_button]:hover:bg-severity-1/50! [&_button]:hover:text-primary! ',
          });
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
      },
      onerror(err) {
        toast.error(`${err}`)
      },
    });
  };


  return {
    isConnected,
    connectToSSE
  };
});
