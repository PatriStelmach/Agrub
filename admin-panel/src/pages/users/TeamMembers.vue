<script setup lang="ts">

import {Card, CardHeader} from "@/components/ui/card";
import {usersData} from "@/data/usersData.ts";
import {IconPencilCode, IconStatusChange, IconTrash} from "@tabler/icons-vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup} from "@/components/ui/button-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";

// const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage } = use
</script>

<template>
<div>

  <div class="relative">
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
            Edit
            <IconPencilCode/>
          </Button>
          <Button
            @click=""
            variant="orange_outline">
            Change Status
            <IconStatusChange/>
          </Button>
          <Button
            @click=""
            variant="red_outline">
            Delete
            <IconTrash/>
          </Button>
          <InputGroup class=" w-[14vw] border-l-2! " >
            <InputGroupInput
              v-model="searchFilter"
              type="search"
              placeholder="Search for plugin"/>
            <InputGroupAddon>
              <Search/>
            </InputGroupAddon>
          </InputGroup>
        </ButtonGroup>
      </ButtonGroup>
    </div>
  </div>
  <div class=" mx-6 grid grid-cols-5 gap-4">
    <Card v-for="user in usersData">
      <CardHeader class="flex">
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
          ></span>
        </div>

        <div class="grid flex-1 text-left text-sm leading-tight">
          <span class="truncate font-medium">{{ user.firstname + ' ' + user.surname }}</span>
          <span class="text-muted-foreground truncate text-xs">
                {{ user.email }}
              </span>
        </div>
      </CardHeader>
    </Card>
  </div>

  </div>
</template>
