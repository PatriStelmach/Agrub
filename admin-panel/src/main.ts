import './assets/style.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'vue-sonner/style.css'
import 'primeicons/primeicons.css'
import App from './App.vue'
import router from './router'
const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
