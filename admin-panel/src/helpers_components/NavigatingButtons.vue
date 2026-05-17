<script setup lang="ts">

import {Button} from "@/components/ui/button";
import {ArrowLeftIcon, ArrowRightIcon} from "lucide-vue-next";
import {useRouter} from "vue-router";
import {ButtonGroup} from "@/components/ui/button-group";

const router = useRouter();
</script>

<template>
  <ButtonGroup>
    <Button variant="outline" size="icon" aria-label="Go Back" @click="router.back()">
      <ArrowLeftIcon />
    </Button>
    <Button variant="outline" class="border-l-2!" size="icon" aria-label="Go Back" @click="router.forward()">
      <ArrowRightIcon />
    </Button>
  </ButtonGroup>
</template>

