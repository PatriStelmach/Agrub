import {ref, computed, type Ref} from 'vue'
import { defineStore } from 'pinia'

export const useNotificationStore = defineStore('notification', () => {
  const notificationAmount = ref<number>(0)

  const getNotificationAmount = computed(() => notificationAmount.value)
  const addNotification = () => { notificationAmount.value++ }
  const removeNotification = () => { notificationAmount.value-- }



  return {
    notificationAmount,
    getNotificationAmount,
    addNotification,
    removeNotification,
  }
})
