import './assets/style.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'vue-sonner/style.css'
import App from './App.vue'
import router from './router'
import {useAuthStore} from "@/stores/authStore.ts";
const app = createApp(App)

app.use(createPinia())
const authStore = useAuthStore()
await authStore.refreshToken()
app.use(router)
app.mount('#app')
