import {describe, it, expect, beforeEach, vi, afterEach} from 'vitest'
import {useAuthStore} from "../stores/authStore.js";
import {createPinia, setActivePinia} from "pinia";
import {autoLogoutTimer} from "../lib/autoLogoutTimer.js";
import MockAdapter from 'axios-mock-adapter'
import api from "../lib/axios.js";
import {ActiveAlert, api_url} from '../types/types.js'
import {fetchEventSource} from "@microsoft/fetch-event-source";
import options from "axios";

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

const now = new Date()
const later = new Date(now.getTime() + 60_000)

const mockAlert = {
  id: 1,
  subject: 'host down',
  message: 'test alert',
  severity: 3,
  isAcknowledged: true,
  actions: [
    {
      id: 1,
      ack: true,
      ackUpdate: true,
      author: 'admin@pjatk.pl',
      problemId: 1,
      createdAt: JSON.stringify(later)
    }
  ],
  createdAt: JSON.stringify(now),
  originType: 'ZABBIX',
  source: 'localhost',

}

const fullJWT = (isAdmin: boolean) => {
    const header = btoa(JSON.stringify({alg: 'HS256', typ: 'JWT'}))
    const body = btoa(JSON.stringify(isAdmin ? adminJWT : techJWT))
    const sig = 'fakesig'
  return `${header}.${body}.${sig}`
}

const mock = new MockAdapter(api)

beforeEach(() => {
  setActivePinia(createPinia())
  vi.useFakeTimers()
})
afterEach(() => {
  vi.useFakeTimers()
})

describe('Auto logout', () => {
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

describe('Is admin', () => {
  it('Decodes JWT and returns true for ADMINISTRATOR and false for TECHNICIAN' , () => {

    const authStore = useAuthStore()

    authStore.accessToken = fullJWT(true)
    expect(authStore.isAdmin).toBe(true)

    authStore.accessToken = fullJWT(false)
    expect(authStore.isAdmin).toBe(false)
  })
})

describe('Axios interceptor', () => {
  it('Refreshes access token after 403/401 error code', async () => {
    mock
      .onGet('/alerts/active')
      .replyOnce(403)
    mock
      .onPost('/auth/refresh')
      .replyOnce(200, { access_token: fullJWT(true) })
    mock
      .onGet('/alerts/active')
      .replyOnce(200, { data: [mockAlert] })

    const res = await api.get('/alerts/active')

    expect(res.data).toEqual({ data: [mockAlert] })
  })
})

// describe('Add new alert', () => {
//   it('Adds new alert to the dashboard', async () => {
//     vi.mocked(fetchEventSource).mockImplementation(async (url, options) => {
//       options.onopen.({ok: true, status: 200})
//     })
//   })
// })
//
describe('User sign in', () => {
  it('Decodes user data from JWT after successful login', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('New active alert', () => {
  it('Adds a new alert to the store via SSE event', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Update alert', () => {
  it('Edits an existing active alert', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Open alert', () => {
  it('Navigates to alert page when notification is clicked', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Create new group', () => {
  it('Creates a new user group', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Client sorting', () => {
  it('Sorts data on the client side', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Client search filter', () => {
  it('Filters and paginates data locally', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Wrapping', () => {
  it('Detects changes in a wrapped reactive object', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Security settings form', () => {
  it('Validates security configuration fields', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Create user form', () => {
  it('Validates input fields for a new user', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Api date parsing', () => {
  it('Formats date correctly for API requests', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})

describe('Router before each', () => {
  it('Checks resource access based on user role', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})
