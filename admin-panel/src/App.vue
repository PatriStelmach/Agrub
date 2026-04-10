<script setup lang="ts">
import { RouterView } from 'vue-router'
import SearchBar from "@/pages/globals/SearchBar.vue";
import {SidebarProvider} from "@/components/ui/sidebar";
import AppSidebar from "@/pages/navbar/AppSidebar.vue";
import TopRightButtons from "@/pages/globals/topRightButtons.vue";
</script>

<template>

  <div class="h-screen flex flex-col font-mono font-thin">
    <header>

      <nav class="flex h-[5vh] bg-card justify-center items-center">
        <SearchBar/>
        <TopRightButtons/>
      </nav>
    </header>

    <main class="bg-card flex flex-1 min-h-0 ">
      <SidebarProvider class="w-auto min-h-0">
        <AppSidebar class="bg-card" variant="inset" />
      </SidebarProvider>
      <RouterView class="bg-background  border rounded-2xl flex-1 overflow-auto scroll-style " />
    </main>
  </div>

</template>

