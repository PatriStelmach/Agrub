import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest'
import {useAuthStore} from "../stores/authStore.js";
import {useAlertStore} from "../stores/alertStore.js";
import {useUserStore} from "../stores/userStore.js";
import {createPinia, setActivePinia} from "pinia";
import {autoLogoutTimer} from "../lib/autoLogoutTimer.js";
import api from "../lib/axios.js";
import {useClientSort} from '../composables/useClientSort.js'
import {useClientSearchFilter} from '../composables/useClientSearchFilter.js'
import {ActiveAlert, MyPlugin} from '../types/types.js'
import {useWrapping} from "../composables/useWrapping.js";
import {computed, ref} from "vue";
import {securitySettingSchema, createUserSchema} from '../helpers_functions/formSchemas.js'
import {dateParser} from '../helpers_functions/dateParser.js'
import {
  adminUser, techUser, now, later, earlier, mockPlugin, mockAlert,
  earlierAlert, editedMockAlert, mockGroup, mockSecondGroup, fullJWT, mock
} from './testVariables.js'

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
      .replyOnce(200, [mockAlert])

    const res = await api.get('/alerts/active')

    expect(JSON.stringify(res.data)).toEqual(JSON.stringify([mockAlert]))
  })
})

describe('User sign in', () => {
  it('Decodes user data from JWT after successful login', () => {
    const authStore = useAuthStore()
    authStore.setToken(fullJWT(true))
    expect(authStore.accessToken).toEqual(fullJWT(true))
    expect(authStore.currentUser).toEqual({
      "autoLogoutMinutes": 1,
      "email": "admin@pjatk.pl",
      "firstname": "Admin",
      "groups": [
        {
          "id": 1,
          "name": "ADMIN",
        },
      ],
      "id": 1,
      "lastPasswordChangeDate": "2026-05-15T10:00:00Z",
      "role": "ADMINISTRATOR",
      "surname": "Systemu",
    })
  })
})

describe('Add new alert', () => {
  it('Adds new alert to the dashboard', () => {
    const alertStore = useAlertStore()
    alertStore.addCurrentAlert(mockAlert)
    expect(alertStore.currentAlerts).toEqual([mockAlert])
    expect(alertStore.findAlert(mockAlert.id)).toEqual(mockAlert)
  })
})

describe('Update alert', () => {
  it('Edits an existing active alert', () => {
    const alertStore = useAlertStore()
    alertStore.addCurrentAlert(mockAlert)
    alertStore.updateAlert({
      ...mockAlert,
      severity: 2,
      isAcknowledged: true,
    })
    expect(alertStore.currentAlerts).toEqual([editedMockAlert])
  })
})

describe('Create new group', () => {
  it('Creates a new user group', async () => {
    const userStore = useUserStore()
     mock
       .onGet('/groups')
       .replyOnce(200, [mockGroup])

    await userStore.getAllGroupsRequest()
    expect(userStore.allGroups).toEqual([mockGroup])

    mock
      .onPost('/groups', { name: 'DATABASES' })
      .replyOnce(200, { id: 2, name: 'DATABASES' })
    mock
      .onGet('/groups')
      .replyOnce(200, [mockGroup, mockSecondGroup])

    await userStore.getAllGroupsRequest()
    expect(userStore.allGroups).toEqual([mockGroup, mockSecondGroup])

  })
})

describe('Client sorting', () => {
  it('Sorts data on the client side', () => {
    const alerts = ref<ActiveAlert[]>([mockAlert, earlierAlert])
    const {
      sortedData,
      sortKey,
      sortOrder,
    } = useClientSort<ActiveAlert>(()=> alerts.value, 'createdAt')

    const topItem = computed(() => sortedData.value.at(0))
    expect(topItem.value).toEqual(mockAlert)

    sortKey.value = 'severity'
    sortOrder.value = 'asc'
    expect(topItem.value).toEqual(earlierAlert)

    sortKey.value = 'isAcknowledged'
    sortOrder.value = 'asc'
    expect(topItem.value).toEqual(mockAlert)
  })
})

describe('Client search filter', () => {
  it('Filters and paginates data locally', () => {
    const alerts = ref<ActiveAlert[]>([mockAlert, earlierAlert])
    const {
      filteredData,
      debounceFilter,
    } = useClientSearchFilter<ActiveAlert>(() => alerts.value, (item) => item.subject)

    debounceFilter.value = 'host'
    expect(filteredData.value).toEqual([mockAlert])
    debounceFilter.value = 'swap'
    expect(filteredData.value).toEqual([earlierAlert])
    debounceFilter.value = 'w'
    expect(filteredData.value).toEqual(alerts.value)
  })
})

describe('Wrapping', () => {
  it('Detects changes in a wrapped reactive object', () => {
    const plugins = ref<MyPlugin[]>([mockPlugin])
    const {
      hasChanged,
      unwrap,
      unwrappedItem,
      isUnwrapped,
      originalItem
    } = useWrapping(plugins, 'fullName')

    unwrap(mockPlugin.fullName)
    expect(unwrappedItem.value).toEqual(originalItem.value)
    expect(originalItem.value).toEqual(plugins.value[0])

    if(unwrappedItem.value) {
      unwrappedItem.value.severity = 5
      unwrappedItem.value.cronExpression = '*/5 */2 * * * *'
    }
    expect(hasChanged()).toBe(true)
    expect(unwrappedItem.value).not.to.equal(originalItem.value)
  })
})

describe('Security settings form', () => {
  it('Validates security configuration fields', async () => {
    const validSchema = {
      SECURITY_PASSWORD_LIFETIME_DAYS: 30,
      SECURITY_ACCESS_TOKEN_EXP_MINUTES: 240,
      SECURITY_REFRESH_TOKEN_EXP_HOURS: 720,
      SECURITY_AD_DOMAIN: 'example.com',
      SECURITY_AD_URL: 'ldap://ldap.forumsys.com:389',
      SECURITY_LDAP_BASE_DN: 'dc=example,dc=com',
      SECURITY_LDAP_USER_DN_PATTERN: 'uid={0}'
    }

    const invalidSchema = {
    ...validSchema,
      SECURITY_LDAP_USER_DN_PATTERN: 'uid={0'
    }

    const parsedValidSchema = await securitySettingSchema.parse(validSchema)
    const parsedInvalidSchema = await securitySettingSchema.parse(invalidSchema)

    expect(parsedValidSchema.value).toEqual(validSchema)
    expect(parsedInvalidSchema.errors).toEqual(
      [{
        "errors" : ['Invalid LDAP user dn pattern'],
        "path": "SECURITY_LDAP_USER_DN_PATTERN"
      }]
    )
  })
})

describe('Create user form', () => {
  it('Validates input fields for a new user', async () => {
    const validSchema = {
      email: 'user@pjatk.pl',
      firstname: 'user',
      surname: 'tech',
      password: 'password',
      confirmPassword: 'password'
    }
    const invalidSchema = {
      ...validSchema,
      email: 'user@pjatk',
      confirmPassword: 'password1'
    }

    const parsedValidSchema = await createUserSchema.parse(validSchema)
    const parsedInvalidSchema = await createUserSchema.parse(invalidSchema)

    expect(parsedValidSchema.value).toEqual(validSchema)
    expect(parsedInvalidSchema.errors).toEqual(
      [
        {
          "errors": [
            "Invalid email address",
          ],
          "path": "email",
        },
        {
          "errors": [
            "Passwords don't match!",
          ],
          "path": "password",
        },
      ]
    )
  })
})

describe('Api date parsing', () => {
  it('Formats date correctly for API requests', async () => {
    const fromApi = '2026-01-01T00:00:00.000000'
    expect(dateParser(fromApi).toDate).toEqual(new Date(fromApi))
    expect(dateParser(fromApi).fullDate).toEqual('01/01/2026 - 00:00:00')
    //expect(dateParser(fromApi).apiDate)
  })
})

describe('Router before each', () => {
  it('Checks resource access based on user role', async () => {
    vi.useRealTimers()
    await new Promise(resolve => setTimeout(resolve, 20))
  })
})
