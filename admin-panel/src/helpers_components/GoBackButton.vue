<script setup lang="ts">

import {Button} from "@/components/ui/button";
import {ArrowLeftIcon} from "lucide-vue-next";
import {useRouter} from "vue-router";

const router = useRouter();
</script>

<template>
  <Button variant="outline" size="icon" aria-label="Go Back" @click="router.back()">
    <ArrowLeftIcon />
  </Button>
</template>

