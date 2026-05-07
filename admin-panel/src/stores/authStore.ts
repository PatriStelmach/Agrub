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
    if (!accessToken.value || !getAccessToken.value) return null
    try {
      return jwtDecode<MyJWTPayload>(getAccessToken.value)
    } catch {
      return null
    }
  })
  const getAccessToken =  computed(() => accessToken.value)
  const getIsAuthenticated = computed(() => isAuthenticated.value)

  const setAccessToken = (token: string | null) => {
    accessToken.value = token
  }
  const setIsAuthenticated = (val: boolean) => {
    isAuthenticated.value = val
  }


  async function login(credentials: { email: string; password: string }) {
    try {
      const response = await api.post('/auth/login', credentials)

      if (response.status === 200 && response.data.token) {
        accessToken.value = response.data.token as string
        isAuthenticated.value = true
        return true
      }

    } catch (error) {
      console.error('Login error:', error)
      return false
    }
  }

  async function logout() {
    try {
      await axios.post(`${api_url}/auth/logout`, {}, { withCredentials: true });
    } catch (error) {
      console.error('Logout error context:', error)
    } finally {
      isAuthenticated.value = false
      accessToken.value = null
    }
  }

  async function refreshToken() {
    try {
      const response = await axios.post(`${api_url}/auth/refresh`, {}, { withCredentials: true });
      accessToken.value = response.data.token;
      return true;
    } catch (err) {
      throw err;
    }
  }

  return {
    userPayload,
    userRoles,
    user,
    getAccessToken,
    getIsAuthenticated,
    setAccessToken,
    setIsAuthenticated,
    login,
    logout,
    refreshToken
  }
})
