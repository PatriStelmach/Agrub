<script setup lang="ts">
import { RouterView } from 'vue-router'
import TopRightButtons from "@/pages/globals/TopRightButtons.vue";
import NavBar from "@/pages/navbar/NavBar.vue";
import {Toaster} from "vue-sonner";
import {useAlertStore} from "@/stores/alertStore.ts";
import {useSSEstore} from "@/stores/SSEstore.ts";
import {SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";

useSSEstore()
</script>

<template>
  <Toaster
    theme="system"
    richColors
    position="top-right"
    :expand="true"
  />
  <header class="relative w-full">
    <nav class="absolute top-0 flex w-full h-[5vh] bg-card justify-center items-center">
      <TopRightButtons/>
    </nav>
  </header>
  <SidebarProvider>
    <NavBar class="border-none bg-card"/>
    <div class="h-screen w-full flex flex-col  ">


      <main class="bg-card flex flex-1 ">
        <SidebarTrigger class="mt-4 z-99999"/>
        <RouterView class="bg-background mt-[5vh] border-3 flex-1 overflow-auto rounded-[1rem_0_0_0] " />

      </main>

      <slot/>
    </div>
  </SidebarProvider>



</template>

