import {onMounted, onUnmounted, ref, watchEffect} from "vue";
import {dateParser} from "@/composables/dateParser.ts";

export function globals(timeout: () => number, onTimeout: () => void) {
  const date = new Date();
  const weekDay = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
  const activityEvents = [
    'mousedown',
    'mousemove',
    'keydown',
    'scroll',
    'touchstart'
  ]
  let timer: ReturnType<typeof setTimeout> | null = null

  watchEffect(() => console.log(timeout()))
  const time = ref<{hour: string; minute: string, second: string}>
  ({hour: dateParser(date).hours, minute: dateParser(date).minutes, second: dateParser(date).seconds});

  const changeTime = () => {
    setInterval(() => {
      const newDate = new Date()
      time.value.hour = dateParser(newDate).hours
      time.value.minute = dateParser(newDate).minutes
      time.value.second = dateParser(newDate).seconds
    }, 1000)
  }

  const resetTimer = () => {
    if(timer) clearTimeout(timer)

    const timeoutMinutes = timeout() || 1

    timer = setTimeout(() => {
      onTimeout()
    }, timeoutMinutes * 60 * 1000)
  }

  onMounted(() => {
    activityEvents.forEach((event) => {
      window.addEventListener(event, resetTimer, { passive: true })
    })
    resetTimer()
  })

  onUnmounted(() => {
    if(timer) clearTimeout(timer)
    activityEvents.forEach((event) => {
      window.removeEventListener(event, resetTimer)
    })
  })

  return {
    time,
    changeTime
  }
}
