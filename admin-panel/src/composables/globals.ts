import {onUnmounted, ref} from "vue";
import {dateParser} from "@/composables/dateParser.ts";

export function globals(timeout?: () => number, onTimeout?: () => void) {
  const date = ref<Date>(new Date())
  const activityEvents = [
    'mousedown',
    'mousemove',
    'keydown',
    'scroll',
    'touchstart'
  ]
  let timer: ReturnType<typeof setTimeout> | null = null

  const time = ref<string>(dateParser(date.value).fullTime);
  const dayMonthYear  = ref<string>(dateParser(date.value).dayMonthYear)
  const weekday = ref<string | undefined>(dateParser(date.value).weekday())

  const changeTime = () => {
    setInterval(() => {
      const newDate = new Date()
      time.value = dateParser(newDate).fullTime
      if(time.value === '00:00:00') {
        dayMonthYear.value = dateParser(newDate).dayMonthYear
        weekday.value = dateParser(newDate).weekday()
      }
    }, 1000)
  }

  const resetLogoutTimer = () => {
    if(timer) clearTimeout(timer)

    if (timeout && onTimeout) {
      const timeoutMinutes = timeout() || 5

      timer = setTimeout(() => {
        onTimeout()
      }, timeoutMinutes * 60 * 1000)
    }
  }

  const startLogoutTimer = () => {
    activityEvents.forEach((event) => {
      window.addEventListener(event, resetLogoutTimer, { passive: true })
    })
    resetLogoutTimer()
  }

  onUnmounted(() => {
    if(timer) clearTimeout(timer)
    activityEvents.forEach((event) => {
      window.removeEventListener(event, resetLogoutTimer)
    })
  })

  return {
    time,
    weekday,
    changeTime,
    dayMonthYear,
    startLogoutTimer,
  }
}
