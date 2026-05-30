<script setup lang="ts">
import {RouterView} from 'vue-router'
import TopRightButtons from "@/pages/globals/TopRightButtons.vue";
import NavBar from "@/pages/globals/navbar/NavBar.vue";
import {Toaster} from "vue-sonner";
import {useSSEstore} from "@/stores/SSEstore.ts";
import {SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";
import {useAuthStore} from "@/stores/authStore.ts";
import LoginPage from "@/pages/login/LoginPage.vue";
import {computed, onMounted, ref, watchEffect} from "vue";
import {Skeleton} from "@/components/ui/skeleton";
import {dateParser} from "@/composables/dateParser.ts";
import {globals} from "@/composables/globals.ts";
import {IconMoon2, IconMoonFilled, IconMoonStars, IconSunFilled} from "@tabler/icons-vue";
import {useColorMode} from "@vueuse/core";
import { getLocalTimeZone, today, ZonedDateTime, CalendarDateTime, CalendarDate,  } from '@internationalized/date'


const locale = navigator.language
const tz = getLocalTimeZone()
const mode = useColorMode()
const authStore = useAuthStore()
const logoutTimeout = computed(() => authStore.currentUser?.autoLogoutMinutes)
const isLoading = ref(true);
onMounted(() => {
  console.log('locale', )
  console.log('tz', tz)
  authStore.refreshToken().finally(() => {
    isLoading.value = false
    if (authStore.isAuthenticated) {
      useSSEstore()
      console.log(authStore.accessToken)
      startLogoutTimer()
    }
  });
})
const {startLogoutTimer } = globals(
  () => Number(logoutTimeout.value),
  () => {
    if (authStore.isAuthenticated) {
      authStore.logout()
    }
  }
)


</script>

<template>
  <div
    v-if="isLoading">
    <Skeleton
      class="w-screen h-screen text-blue-badge"
    />

  </div>
  <div v-else-if="!isLoading && !authStore.isAuthenticated ">
    <LoginPage/>
  </div>
  <div v-else-if="!isLoading && authStore.isAuthenticated" >
    <Toaster
      :duration="60000"
      theme="system"
      richColors
      position="bottom-right"
      :expand="false"
      :close-button="true"
      close-button-position="top-right"
      :gap="10"
      :visibleToasts="15"
    />
    <header class="absolute top-0 right-2 flex w-full h-10 items-center">
        <component stroke="1.5" :is="mode === 'light' ? IconSunFilled : IconMoonStars" @click="mode == 'light' ? mode = 'dark' : mode = 'light' "
                   class="cursor-pointer absolute right-2 size-6 rounded-full  hover:scale-115 duration-100"/>
    </header>
    <SidebarProvider>
      <NavBar  class=" border-none bg-card"/>
      <div class="h-screen w-full flex flex-col  ">
        <main class="bg-card flex flex-1 ">
          <SidebarTrigger class="mt-8 z-9 md:z-10"/>
          <RouterView class="bg-background  border-3 flex-1 overflow-auto  " />
        </main>
        <slot/>
      </div>
    </SidebarProvider>
  </div>
</template>

