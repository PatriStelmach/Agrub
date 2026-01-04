<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import SearchBar from "@/pages/globals/SearchBar.vue";
import {SidebarProvider} from "@/components/ui/sidebar";
import AppSidebar from "@/components/AppSidebar.vue";
</script>

<template>

  <div class="h-screen flex flex-col">
    <header>
      <nav class="flex h-[6vh] bg-card justify-center items-center">
        <SearchBar/>
      </nav>
    </header>

    <main class="flex flex-1 min-h-0">
      <SidebarProvider class="w-auto min-h-0!">
        <AppSidebar variant="inset" />
      </SidebarProvider>
      <RouterView class=" flex-1 overflow-auto" />
    </main>
  </div>

</template>

