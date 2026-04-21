<script setup lang="ts">
import { RouterView } from 'vue-router'
import TopRightButtons from "@/pages/globals/topRightButtons.vue";
import NavBar from "@/pages/navbar/NavBar.vue";
</script>

<template>

  <div class="h-screen flex flex-col font-mono font-thin ">
    <header class="relative w-full">
      <nav class="absolute top-0 flex w-full h-[5vh] bg-card justify-center items-center">
        <TopRightButtons/>
      </nav>
    </header>

    <main class="bg-card flex flex-1  ">
      <NavBar/>
      <RouterView class="bg-background mt-20 border flex-1 overflow-auto rounded-lg  " />
    </main>
  </div>

</template>

