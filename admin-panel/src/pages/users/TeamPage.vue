<script setup lang="ts">

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle
} from "@/components/ui/card";
import {usersData} from "@/data/usersData.ts";
import {
  IconPencilCode,
  IconStatusChange,
  IconTrash,
  IconUsersGroup,
  IconUserKey, IconEdit, IconKey, IconTool, IconUserPlus, IconEditOff, IconDeviceFloppy
} from "@tabler/icons-vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup} from "@/components/ui/button-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Badge} from "@/components/ui/badge";
import {useWrapping} from "@/composables/unwrapping.ts";
import {computed, ref} from "vue";
import {useSearchFilter} from "@/composables/useSearchFilter.ts";
import type {AlertObject, User} from "@/types/types.ts";
import {dashboardData} from "@/data/dashboardData.ts";
import {Input} from "@/components/ui/input";

// const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage } = use
const users = computed(() => {
  return usersData;
})

const { wrap, unwrap, unwrappedItem, isUnwrapped, originalItem } = useWrapping(users)
const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage, searchFilter } =
  useSearchFilter<User>(() => users.value,(user) => user.firstname+user.surname )


</script>

<template>
<div class="">

  <div class="relative ">
    <h1 class="text-center my-[2vh] text-xl xl:text-2xl 2xl:text-4xl border-b pb-[2vh]">Team</h1>
    <div class="flex absolute top-0 left-4">
      <ButtonGroup>
        <ButtonGroup class="flex">
          <Button variant="outline" size="icon" aria-label="Go Back">
            <ArrowLeftIcon />
          </Button>
        </ButtonGroup>
        <ButtonGroup>
          <Button
            @click=""
            variant="green_outline">
            Add new user
            <IconUserPlus/>
          </Button>
          <InputGroup class=" w-[14vw] border-l-2! " >
            <InputGroupInput
              v-model="searchFilter"
              type="search"
              placeholder="Search for users"/>
            <InputGroupAddon>
              <Search/>
            </InputGroupAddon>
          </InputGroup>
        </ButtonGroup>
      </ButtonGroup>
    </div>
  </div>
  <div class=" px-6 py-2 pr-3 grid grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6 max-h-[80vh] overflow-y-auto">
    <Card
      class="flex  hover:shadow-lg hover:border-badge shadow-badge/30 transition duration-50"
      :class="{'shadow-lg border-badge' : isUnwrapped(user.id) }"
      v-for="user in filteredData"
      :key="user.id">
      <CardHeader class="px-3 flex space-x-1 items-center" :class="{'items-start' : isUnwrapped(user.id)}">
        <div class="relative">
          <Avatar class="size-9 rounded-lg ">
            <AvatarImage class="size-9" :src="user.avatar ? user.avatar : ''" :alt="`${user.surname}_${user.surname}`"/>
            <AvatarFallback class="rounded-full grayscale">
              {{user.firstname.slice(0,1) + user.surname.slice(0,1)}}
            </AvatarFallback>
          </Avatar>
          <span
            class="absolute bottom-0 right-0 size-2.5 rounded-full  bg-green-500"
            :class="{'bg-red-500' : !user.active}"
          />
        </div>

        <div class="grid flex-1 text-left text-sm leading-tight" :class="{ 'gap-y-2 px-2' : isUnwrapped(user.id) }">
          <span v-if="!isUnwrapped(user.id)" class="truncate font-medium">{{ user.firstname + ' ' + user.surname }}</span>
          <div v-else class="space-x-2 flex">
            <div class="grid space-y-2">
              <Label :for="`${user.id}_firstname`">Firstname</Label>
              <Input
                :id="`${user.id}_firstname`"
                :default-value="user.firstname"
              />
            </div>
            <div class="grid space-y-2">
              <Label :for="`${user.id}_surname`">Surname</Label>
              <Input
                :id="`${user.id}_surname`"
                :default-value="user.surname"
              />
            </div>
          </div>
          <span v-if="!isUnwrapped(user.id)" class="text-muted-foreground truncate text-xs">{{ user.email }} </span>
          <div class="grid space-y-2" v-else>
            <Label :for="`${user.id}_email`">e-mail</Label>
            <Input
              :id="`${user.id}_email`"
              :default-value="user.email"
            />
          </div>

        </div>
          <component :is="isUnwrapped(user.id) ? IconEditOff : IconEdit"
            @click="isUnwrapped(user.id) ? wrap(false) : unwrap(user.id)"
            class="text-badge1 cursor-pointer hover:scale-115 " :class="{'text-badge2' : isUnwrapped(user.id)}" />
      </CardHeader>
      <CardDescription class="px-3 ">
        <div class="flex space-x-1">
          <component :is="user.role === 'Administrator' ? IconKey : IconTool" stroke="2" />
          <h1 class="font-bold mb-2 text-lg">{{ user.role }}</h1>
        </div>
        <div class="flex flex-2 items-start whitespace-break-spaces">
          <div class="flex space-x-1 items-center mr-1 mt-1">
            <IconUsersGroup stroke="1.5" />
            <h1>Groups:</h1>
          </div>
          <div>
            <Badge variant="groups" v-for="(group, index) in user.groups" :key="index">
              {{ group }}
            </Badge>
          </div>
        </div>
      </CardDescription>
      <CardFooter class="relative" v-if="isUnwrapped(user.id)">
        <IconDeviceFloppy class="absolute right-3 bottom-2 text-badge1 cursor-pointer hover:scale-115 "/>
      </CardFooter>
    </Card>
  </div>

  </div>
</template>
