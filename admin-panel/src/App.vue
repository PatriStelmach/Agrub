<script setup lang="ts">
import {RouterView} from 'vue-router'
import NavBar from "@/pages/globals/navbar/NavBar.vue";
import {Toaster} from "vue-sonner";
import {useSSEstore} from "@/stores/SSEstore.ts";
import {SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";
import {useAuthStore} from "@/stores/authStore.ts";
import LoginPage from "@/pages/login/LoginPage.vue";
import {computed, onMounted, watch} from "vue";
import {autoLogoutTimer} from "@/lib/autoLogoutTimer";
import {IconMoonStars, IconSunFilled} from "@tabler/icons-vue";
import {useColorMode} from "@vueuse/core";

const mode = useColorMode()
const authStore = useAuthStore()
const sseStore = useSSEstore()
const logoutTimeout = computed(() => authStore.currentUser?.autoLogoutMinutes)

onMounted(() => {
  if (authStore.isAuthenticated && !sseStore.isConnected) {
    sseStore.connectToSSE()
  }
  startLogoutTimer()
})

const {startLogoutTimer, stopLogoutTimer} = autoLogoutTimer(
  () => Number(logoutTimeout.value),
  () => {
    if (authStore.isAuthenticated) authStore.logout()
  }
)

watch(() => authStore.isAuthenticated,(newValue) => {
  if (newValue) {
    startLogoutTimer()
    if(!sseStore.isConnected)
      sseStore.connectToSSE()
  }
  else if (!newValue) {
    stopLogoutTimer()
  }
})

</script>

<template>
  <div v-if="!authStore.isAuthenticated ">
    <LoginPage/>
  </div>
  <div v-else>
    <Toaster
      :duration="3000"
      theme="system"
      richColors
      position="top-center"
      :expand="false"
      close-button-position="top-right"
      :gap="5"
      :visibleToasts="16"
    />
    <header class="absolute top-0 right-2 flex w-full h-10 items-center">
        <component
          stroke="1.5" :is="mode === 'light' ? IconSunFilled : IconMoonStars"
          @click="mode == 'light' ? mode = 'dark' : mode = 'light' "
          class="cursor-pointer absolute right-2 size-6 rounded-full  hover:scale-115 duration-100"
        />
    </header>
    <SidebarProvider>
      <NavBar  class=" border-none bg-card"/>
      <div class="h-screen w-full flex flex-col">
        <main class="bg-card flex flex-1 ">
          <SidebarTrigger class="mt-8 z-9 md:z-10"/>
          <RouterView class="bg-background  border-3 flex-1 overflow-auto  " />
        </main>
      </div>
    </SidebarProvider>
  </div>
</template>

