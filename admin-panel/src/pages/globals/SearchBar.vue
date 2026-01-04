<script setup lang="ts">
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {Search} from 'lucide-vue-next'

</script>

<template>
  <InputGroup  class="w-1/4 h-2/3">
    <InputGroupInput type="search" placeholder="Search" class="h-full text-lg! xl:text-xl! 2xl:text-2xl!"/>
      <InputGroupAddon>
        <Search/>
      </InputGroupAddon>
  </InputGroup>
</template>
