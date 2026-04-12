<script setup lang="ts">

import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {
  IconBellCog,
  IconChevronDown,
  IconEyeCog,
  IconLock,
  IconStatusChange,
  IconUser
} from "@tabler/icons-vue";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {ref, watch} from "vue";

const searchFilter = ref<string>("")


const emit = defineEmits<{
  'update:search-data': [data:string]
}>()


watch(searchFilter, () => {
  emit('update:search-data', searchFilter.value)
}, {immediate: true})
</script>

<template>
  <div>
    <div class="flex ml-[1vw] my-[2vh] ">
      <ButtonGroup>
        <ButtonGroup>
          <Button variant="outline" size="icon" aria-label="Go Back">
            <ArrowLeftIcon />
          </Button>
        </ButtonGroup>

        <ButtonGroup>
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button variant="green_outline">
                <IconBellCog/>
                Alert actions
                <IconChevronDown/>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent class="w-full z-99999">
              <DropdownMenuItem>
                <IconEyeCog/>Change priority
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconUser/>Change technician
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconStatusChange/>Change status
              </DropdownMenuItem>
              <DropdownMenuItem>
                <IconLock/>Close
              </DropdownMenuItem>

            </DropdownMenuContent>
          </DropdownMenu>
          <InputGroup class="relative l-[30vw] w-[20vw]  " >
            <InputGroupInput
              v-model="searchFilter"
              type="search"
              placeholder="Search for alert"/>
            <InputGroupAddon>
              <Search/>
            </InputGroupAddon>
          </InputGroup>

        </ButtonGroup>
      </ButtonGroup>
    </div>
  </div>

</template>

