<script setup lang="ts">
import {RouterView, useRouter} from 'vue-router'
import TopRightButtons from "@/pages/globals/TopRightButtons.vue";
import NavBar from "@/pages/navbar/NavBar.vue";
import {Toaster} from "vue-sonner";
import {useSSEstore} from "@/stores/SSEstore.ts";
import {SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";
import {useAuthStore} from "@/stores/authStore.ts";
import LoginPage from "@/pages/login/LoginPage.vue";
import {computed, watchEffect} from "vue";

const router = useRouter();


const authStore = useAuthStore()
const loggedIn = computed(() => authStore.getIsAuthenticated)
watchEffect(async () => {
  if(loggedIn.value){
    useSSEstore()
  }
})



</script>

<template>
  <div v-if="loggedIn">
    <Toaster
      theme="system"
      richColors
      position="top-right"
      :expand="true"
    />
    <header class="relative w-full">
      <nav class="absolute top-0 flex w-full h-10 bg-card justify-center items-center">
        <TopRightButtons/>
      </nav>
    </header>
    <SidebarProvider>
      <NavBar class=" border-none bg-card"/>
      <div class="h-screen w-full flex flex-col  ">
        <main class="bg-card flex flex-1 ">
          <SidebarTrigger class="mt-4 z-9"/>
          <RouterView class="bg-background mt-10 border-3 flex-1 overflow-auto rounded-[1rem_0_0_0] " />
        </main>
        <slot/>
      </div>
    </SidebarProvider>
  </div>
  <div v-else>
    <LoginPage/>
  </div>

</template>

