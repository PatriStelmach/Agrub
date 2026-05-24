import { defineStore } from 'pinia'
import {ref, computed, watch, watchEffect} from "vue"
import {type MyJWTPayload, type User} from "@/types/types.ts"
import { jwtDecode } from "jwt-decode"
import api from "@/lib/axios"
import {toast} from "vue-sonner";

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref<boolean>(false)
  const accessToken = ref<string | null>(null)
  const fullName = computed(() => `${currentUser.value?.firstname} ${currentUser.value?.surname}`)
  const avFallback = computed(() =>  `${currentUser.value?.firstname.slice(0,1)}${currentUser.value?.surname.slice(0,1)}`)
  const currentUser = computed(() => {
    if (!accessToken.value) return null
    try {
      const payload = jwtDecode<MyJWTPayload>(accessToken.value)
      return {
        id: payload.id,
        firstname: payload.firstname,
        surname: payload.surname,
        groups: payload.groups,
        autoLogoutMinutes: payload.autoLogoutMinutes,
        lastPasswordChangeDate: payload.lastPasswordChangeDate,
        email: payload.sub,
        role: payload.role,
      } as User
    } catch {
      return null
    }
  })


  async function alertLogin(credentials: { email: string; password: string }) {
    try {
      const response = await api.post('/auth/login', credentials)
      if (response.status === 200 && response.data.access_token) {
        accessToken.value = response.data.access_token
        isAuthenticated.value = true
        console.log(response.data.access_token)
        return true
      }

    } catch {
      return false
    }
  }

  async function ADLogin(credentials: { email: string; password: string }) {
    try {
      const response = await api.post('/auth/login/ad', credentials)
      if (response.status === 200 && response.data.access_token) {
        accessToken.value = response.data.access_token
        isAuthenticated.value = true
        console.log(response.data.access_token)
        return true
      }

    } catch {
      return false
    }
  }

  async function logout() {
    try {
       await api.post(`/auth/logout`,  { withCredentials: true });
    } catch (error) {
      toast.error(`Error during logout: ${error}`)
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
    currentUser,
    fullName,
    accessToken,
    isAuthenticated,
    avFallback,
    alertLogin,
    ADLogin,
    logout,
    refreshToken
  }
})
