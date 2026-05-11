<script setup lang="ts">
import {RouterView} from 'vue-router'
import TopRightButtons from "@/pages/globals/TopRightButtons.vue";
import NavBar from "@/pages/globals/navbar/NavBar.vue";
import {Toaster} from "vue-sonner";
import {useSSEstore} from "@/stores/SSEstore.ts";
import {SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";
import {useAuthStore} from "@/stores/authStore.ts";
import LoginPage from "@/pages/login/LoginPage.vue";
import {ref} from "vue";
import {Skeleton} from "@/components/ui/skeleton";



const authStore = useAuthStore()
const isLoading = ref(true);
authStore.refreshToken().finally(() => {
    isLoading.value = false
    if (authStore.isAuthenticated) {
      useSSEstore()
      console.log(authStore.accessToken)
    }
});

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
      theme="system"
      richColors
      position="top-center"
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


</template>

