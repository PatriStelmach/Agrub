export function autoLogoutTimer(timeout?: () => number, onTimeout?: () => void) {

  let lastBroadcast = 0
  const BROADCAST_INTERVAL = 1000
  let logoutChannel: BroadcastChannel | null = null
  const activityEvents = ['mousedown', 'keydown', 'scroll']
  let timer: ReturnType<typeof setTimeout> | null = null

  const resetLogoutTimer = () => {
    if(timer) clearTimeout(timer)

    if (timeout && onTimeout) {
      const timeoutMinutes = timeout() || 5

      timer = setTimeout(() => {
        onTimeout()
      }, timeoutMinutes * 60 * 1000)
    }
  }

  const activityHandler = () => {
    resetLogoutTimer()
    const now = Date.now()
    if (now - lastBroadcast > BROADCAST_INTERVAL) {
      lastBroadcast = now
      logoutChannel?.postMessage('reset-logout-timer')
    }
  }

  const startLogoutTimer = () => {
    logoutChannel = new BroadcastChannel('alert-logout-timer')
    logoutChannel.onmessage = (event) => {
      if (event.data === 'reset-logout-timer') resetLogoutTimer()
    }
    activityEvents.forEach((event) => {
      window.addEventListener(event, activityHandler, { passive: true })})
    resetLogoutTimer()
  }

  const stopLogoutTimer = () => {
    if (timer) clearTimeout(timer)
    activityEvents.forEach((event) => {
      window.removeEventListener(event, activityHandler)
    })
    logoutChannel?.close()
    logoutChannel = null
  }

  return { startLogoutTimer, stopLogoutTimer }
}
