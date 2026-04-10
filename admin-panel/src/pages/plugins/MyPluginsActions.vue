<script setup lang="ts">

import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator, DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {
  IconDatabase,
  IconFileImport,
  IconPencilCode,
  IconPlayerPause, IconPlus,
  IconTerminal2, IconTrash
} from "@tabler/icons-vue";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {Badge} from "@/components/ui/badge";
import {Button} from "@/components/ui/button";
import {ref, watch} from "vue";

const searchFilter = ref<string>("");

const emit = defineEmits<{
  'update:search-data': [data:string]
}>()

watch(searchFilter, () => {
  emit('update:search-data', searchFilter.value)
}, {immediate: true})

</script>

<template>
  <div class="flex ml-[1vw] my-[2vh] ">
    <ButtonGroup>
      <ButtonGroup class="hidden sm:flex">
        <Button variant="outline" size="icon" aria-label="Go Back">
          <ArrowLeftIcon />
        </Button>
      </ButtonGroup>
      <ButtonGroup>
        <Button variant="cyan_outline">
          Edit
          <IconPencilCode/>
        </Button>
        <Button variant="yellow_outline">
          Turn off
          <IconPlayerPause/>
        </Button>
        <Button variant="red_outline">
          Delete
          <IconTrash/>
        </Button>
        <InputGroup class="relative l-[30vw] w-[20vw]  " >
          <InputGroupInput
            v-model="searchFilter"
            type="search"
            placeholder="Search for plugin"/>
          <InputGroupAddon>
            <Search/>
          </InputGroupAddon>
        </InputGroup>
      </ButtonGroup>
      <ButtonGroup>

        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <Button variant="outline" size="icon" aria-label="More Options"><IconPlus/></Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" class="w-52">
            <DropdownMenuLabel class="border-b">Plugins</DropdownMenuLabel>
            <DropdownMenuGroup>
              <DropdownMenuItem>
                <RouterLink class="flex w-full gap-x-2" to="/import_plugins">
                  <IconFileImport/>Import
                </RouterLink>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconTerminal2/>Create</DropdownMenuItem>
              <DropdownMenuItem>
                <IconDatabase/>Search for plugins
              </DropdownMenuItem>
            </DropdownMenuGroup>
            <DropdownMenuSeparator/>
            <DropdownMenuGroup>
              <DropdownMenuLabel class=" border-b">Tags</DropdownMenuLabel>
              <DropdownMenuItem>
                <component :is="IconPencilCode"/>Create</DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
        <Badge>Add</Badge>
      </ButtonGroup>
    </ButtonGroup>
  </div>
</template>

<style scoped>

</style>
