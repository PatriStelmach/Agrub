import axios from "axios";
import { api_url } from "@/types/types.ts";
import { useAuthStore } from "../stores/authStore.ts";

const api = axios.create({
  baseURL: api_url,
  withCredentials: true,

});

api.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  const token = authStore.getAccessToken

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

api.interceptors.response.use(
  response => response,
  async error => {
    const authStore = useAuthStore();
    const originalRequest = error.config;

    if (originalRequest.url?.includes(['refresh', 'login', 'logout']) ) {
      authStore.setAccessToken(null)
      authStore.setIsAuthenticated(false)
      return Promise.reject(error);
    }

    if (error.response?.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        await authStore.refreshToken()
        const newToken = authStore.getAccessToken;
        if (newToken) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
        }
        return api(originalRequest);
      }
      catch (refreshError) {
        if(authStore.getIsAuthenticated)
          await authStore.logout()
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error);
  }
)

export default api
