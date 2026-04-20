<script setup lang="ts">
import { RouterView } from 'vue-router'
import {SidebarProvider} from "@/components/ui/sidebar";
import AppSidebar from "@/pages/navbar/AppSidebar.vue";
import TopRightButtons from "@/pages/globals/topRightButtons.vue";
import NavBar from "@/pages/navbar/NavBar.vue";
</script>

<template>

  <div class="h-screen flex flex-col font-mono font-thin">
    <header class="w-screen">

      <nav class="flex w-full h-[5vh] bg-card justify-center items-center">
        <NavBar/>
        <TopRightButtons/>
      </nav>
    </header>

    <main class="bg-card flex flex-1 min-h-0 ">
      <RouterView class="bg-background  border rounded-2xl flex-1 overflow-auto   " />
    </main>
  </div>

</template>

