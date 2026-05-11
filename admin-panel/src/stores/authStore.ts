import { defineStore } from 'pinia'
import { ref, computed } from "vue"
import {api_url, type MyJWTPayload} from "@/types/types.ts"
import { jwtDecode } from "jwt-decode"
import api from "@/lib/axios"
import axios from "axios";

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref<boolean>(false)
  const accessToken = ref<string | null>(null)
  const user = computed(() => userPayload.value?.sub || null)
  const userRoles = computed(() => userPayload.value?.authorities || [])
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
      const response = await api.post(`/auth/refresh`, {}, { withCredentials: true });
      accessToken.value = response.data.access_token
      isAuthenticated.value = true

      return true;
    } catch (err) {
      throw err;
    }
  }

  return {
    userPayload,
    userRoles,
    user,
    accessToken,
    isAuthenticated,
    login,
    logout,
    refreshToken
  }
})
