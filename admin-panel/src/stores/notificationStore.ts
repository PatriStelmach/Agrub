import {ref} from 'vue'
import { defineStore } from 'pinia'

export const useNotificationStore = defineStore('notification', () => {
  const notificationAmount = ref<number>(0)

  const addNotification = () => { notificationAmount.value++ }
  const removeNotification = () => { notificationAmount.value-- }



  return {
    notificationAmount,
    addNotification,
    removeNotification,
  }
})
