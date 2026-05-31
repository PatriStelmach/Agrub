<script setup lang="ts">

import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {IconForbid, IconExclamationCircle} from "@tabler/icons-vue";
import {useRoute} from "vue-router";

const route = useRoute();
</script>

<template>
  <div>
    <TopH1Div h1="No privileges" />
    <div class="relative w-full h-full">
      <div class="absolute space-y-4 bottom-2/3 left-1/2 -translate-x-1/2">
        <div class="flex w-full space-x-2 ">
          <IconExclamationCircle class="text-red-badge ml-auto"/>
          <h1 :class="`${bigNameLabel} mr-auto`">
            Error navigating to: {{ route.params.prohibited_path }}{{ route.params.prohibited_params ? `/${route.params.prohibited_params}` : '' }}{{ route.params.invalid_data ? `/${route.params.invalid_data}` : '' }}{{ route.params.invalid_path ? `/${route.params.invalid_path}` : '' }}!
          </h1>
        </div>
        <div class="flex w-full space-x-2 ">
          <IconForbid class="ml-auto text-red-badge"/>
          <h1 :class="`${bigNameLabel}   mr-auto`">
            You don't have sufficient permisions to enter this page.
          </h1>
        </div>
      </div>

    </div>
  </div>

</template>
