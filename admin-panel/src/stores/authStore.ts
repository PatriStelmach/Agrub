import { defineStore } from 'pinia'
import { ref, computed } from "vue"
import { type MyJWTPayload} from "@/types/types.ts"
import { jwtDecode } from "jwt-decode"
import api from "@/lib/axios"

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref<boolean>(false)
  const accessToken = ref<string | null>(null)
  const fullName = computed(() => `${userFirstname.value} ${userSurname.value}`)
  const avFallback = computed(() =>  `${userFirstname.value?.slice(0,1)}${userSurname.value?.slice(0,1)}`)
  const userEmail = computed(() => userPayload.value?.sub)
  const userFirstname = computed(() => userPayload.value?.firstname)
  const userSurname = computed(() => userPayload.value?.surname)
  const userRole = computed(() => userPayload.value?.role)
  const userPayload = computed(() => {
    if (!accessToken.value) return null
    try {
      return jwtDecode<MyJWTPayload>(accessToken.value)
    } catch {
      return null
    }
  })


  async function login(credentials: { email: string; password: string }) {
    try {
      const response = await api.post('/auth/login', credentials)

      if (response.status === 200 && response.data.access_token) {
        accessToken.value = response.data.access_token
        isAuthenticated.value = true
        console.log(response.data.access_token)
        return true
      }

    } catch (error) {
      console.error('Login error:', error)
      return false
    }
  }

  async function logout() {
    try {
       await api.post(`/auth/logout`,  { withCredentials: true });
    } catch (error) {
      console.error('Logout error context:', error)
    } finally {
      isAuthenticated.value = false
      accessToken.value = null
    }
  }

  async function refreshToken() {
    try {
      const response = await api.post(`/auth/refresh`, { withCredentials: true });
      if (response.status === 200 && response.data.access_token) {
        accessToken.value = response.data.access_token
        isAuthenticated.value = true
      }
      else isAuthenticated.value = false
    } catch  {
      isAuthenticated.value = false
    }
  }

  return {
    userPayload,
    userRole,
    fullName,
    userEmail,
    userFirstname,
    userSurname,
    accessToken,
    isAuthenticated,
    avFallback,
    login,
    logout,
    refreshToken
  }
})
