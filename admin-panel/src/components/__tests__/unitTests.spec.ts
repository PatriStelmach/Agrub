import {describe, it, expect, beforeEach, vi, afterEach} from 'vitest'
import {useAuthStore} from "../../stores/authStore";
import {createPinia, setActivePinia} from "pinia";
import {autoLogoutTimer} from "../../lib/autoLogoutTimer";

const adminJWT = {
  id: 1,
  firstname: 'Admin',
  surname: 'Systemu',
  groups: [{id: 1, name:'ADMIN'}],
  autoLogoutMinutes: 1,
  lastPasswordChangeDate: '2026-05-15T10:00:00Z',
  sub: 'admin@pjatk.pl',
  role: 'ADMINISTRATOR'
}

const techJWT = {
  id: 2,
  firstname: 'Tech',
  surname: 'Systemu',
  groups: [{id: 2, name:'DATABASE'}],
  autoLogoutMinutes: 5,
  lastPasswordChangeDate: '2026-05-16T10:00:00Z',
  sub: 'tech@pjatk.pl',
  role: 'TECHNICIAN'
}

const fullJWT = (isAdmin: boolean) => {
    const header = btoa(JSON.stringify({alg: 'HS256', typ: 'JWT'}))
    const body = btoa(JSON.stringify(isAdmin ? adminJWT : techJWT))
    const sig = 'fakesig'
  return `${header}.${body}.${sig}`
}

beforeEach(() => {
  setActivePinia(createPinia())
  vi.useFakeTimers()
})
afterEach(() => {
  vi.useFakeTimers()
})

describe('AutoLogout', () => {
  it('Logs user out after time period given in JWT', () => {

    const authStore = useAuthStore()
    authStore.accessToken = fullJWT(true)

    const { startLogoutTimer } = autoLogoutTimer(
      () => Number(authStore.currentUser?.autoLogoutMinutes),
      () => {
        if (authStore.isAuthenticated)
          authStore.setToken(null)
      }
    )
    startLogoutTimer()

    expect(authStore.isAuthenticated).toBe(true)
    vi.advanceTimersByTime(60_000)

    expect(authStore.isAuthenticated).toBe(false)
    vi.useRealTimers()
  })
})

describe('IsAdmin', () => {
  it('Decodes JWT and returns true for ADMINISTRATOR and false for TECHNICIAN' , () => {

    const authStore = useAuthStore()

    authStore.accessToken = fullJWT(true)
    expect(authStore.isAdmin).toBe(true)

    authStore.accessToken = fullJWT(false)
    expect(authStore.isAdmin).toBe(false)
  })
})

describe('Login', () => {
  it('Is ')
})
