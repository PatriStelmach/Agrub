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
  IconUserKey, IconEdit, IconKey, IconTool, IconUserPlus, IconEditOff, IconDeviceFloppy, IconPlus
} from "@tabler/icons-vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup} from "@/components/ui/button-group";
import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Badge} from "@/components/ui/badge";
import {useWrapping} from "@/composables/unwrapping.ts";
import {computed, onMounted, ref, watch} from "vue";
import {useSearchFilter} from "@/composables/useSearchFilter.ts";
import type {ActiveAlert, User} from "@/types/types.ts";
import {dashboardData} from "@/data/dashboardData.ts";
import {Input} from "@/components/ui/input";
import gsap from "gsap";
import {Label} from "@/components/ui/label";
import {Select} from "@/components/ui/select";
import {RadioGroup, RadioGroupItem} from "@/components/ui/radio-group";
import {useBadgeFilter} from "@/composables/useBadgeFilter.ts";
import {inputText, nameLabel, topButtonGroup, topH1} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers/GoBackButton.vue";

// const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage } = use
const users = computed(() => {
  return usersData;
})

const groups = ["Technicial", "Database",  "System Admins", "Administration", "Servers", "Network"]

const { wrap, unwrap, unwrappedItem, isUnwrapped, originalItem } = useWrapping(users)
const { updatePage, filteredData, tableData, updateData, updateSearchData, currentPage, searchFilter } =
  useSearchFilter<User>(() => users.value,(user) => `${user.firstname} ${user.surname}` )

const { badgeSearch, availableBadges, addBadge, badgeListOpen, matchedBadges, existingBadge } = useBadgeFilter<User>(
  unwrappedItem,
  groups,
  () => unwrappedItem.value?.groups ?? []
)



</script>

<template>
<div >

  <div class="relative ">
    <h1 :class="topH1">Team</h1>
      <ButtonGroup :class="topButtonGroup">
        <ButtonGroup class="flex">
          <GoBackButton/>
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
    <TransitionGroup
      class=" px-6 py-2 pr-3 grid sm:grid-cols-2 md:grid-cols-4  xl:grid-cols-5 gap-6 max-h-[80vh] overflow-y-auto"
      tag="div"
      name="slide-fade-card"
    >
      <Card
        v-for="user in filteredData"
        class="border-2 user-box flex  hover:shadow-lg hover:border-badge shadow-badge/30  duration-50 "
        :class="{'shadow-lg border-badge ' : isUnwrapped(user.id) }"

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
              class="absolute bottom-0 right-0 size-2.5 rounded-full  bg-green-badge"
              :class="{'bg-red-badge' : !user.active}"
            />
          </div>

          <div class="grid flex-1 text-left text-sm leading-tight" :class="{ 'gap-y-2 px-2' : isUnwrapped(user.id) }">
            <span v-if="!isUnwrapped(user.id)" class="truncate font-medium ">{{ `${user.firstname} ${user.surname}` }}</span>
            <div v-else class="space-x-2 flex transition duration-900">
              <div class="grid space-y-2">
                <Label
                  :class="nameLabel"
                  :for="`${user.id}_firstname`">Firstname</Label>
                <Input
                  :class="inputText"
                  :id="`${user.id}_firstname`"
                  :default-value="user.firstname"
                />
              </div>
              <div class="grid space-y-2">
                <Label
                  :class="nameLabel"
                  :for="`${user.id}_surname`">Surname</Label>
                <Input
                  :class="inputText"
                  :id="`${user.id}_surname`"
                  :default-value="user.surname"
                />
              </div>
            </div>
            <span v-if="!isUnwrapped(user.id)" class="text-muted-foreground truncate text-xs">{{ user.email }} </span>
            <div class="grid space-y-2" v-else>
              <Label
                :class="nameLabel"
                :for="`${user.id}_email`">e-mail</Label>
              <Input
                :class="inputText"
                :id="`${user.id}_email`"
                :default-value="user.email"
              />
            </div>

          </div>
          <component :is="isUnwrapped(user.id) ? IconEditOff : IconEdit"
                     @click="isUnwrapped(user.id) ? wrap(false) : unwrap(user.id)"
                     class="text-green-badge cursor-pointer hover:scale-115 " :class="{'text-red-badge' : isUnwrapped(user.id)}" />
        </CardHeader>
        <CardDescription class="px-3 ">
            <RadioGroup v-if="isUnwrapped(user.id)"
              v-model="unwrappedItem!.role"
              :default-value="unwrappedItem!.role"
              class="pb-4 flex **:text-md **:lg:text-lg  **:2xl:text-xl">
              <div class="flex items-center space-x-2 ">
                <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id=radio-admininstrator value="Administrator" />
                <IconKey/><Label class="cursor-pointer " for="radio-admininstrator">Administrator</Label>
              </div>
              <div class="flex items-center space-x-2">
                <RadioGroupItem class="size-4 lg:size-5 xl:size-6 2xl:size-8" id="radio-technician" value="Technician" />
                <IconTool/><Label class="cursor-pointer" for="radio-technician">Technician</Label>
              </div>
            </RadioGroup>
          <div v-else class="flex space-x-1">
            <component :is="user.role === 'Administrator' ? IconKey : IconTool" stroke="2" />
            <h1 class="font-bold mb-2 text-lg xl:text-xl 2xl:text-2xl">{{ user.role }}</h1>
          </div>
          <div class="flex flex-2 items-start whitespace-break-spaces">
            <div class="flex space-x-1 items-center mr-2 mt-1">
              <IconUsersGroup stroke="1.5" />
              <h1 :class="nameLabel">Groups:</h1>
            </div>
            <div>
              <Transition v-if="isUnwrapped(user.id)" class="mb-4" tag="div" name="slide-fade">
                <div  v-if="isUnwrapped(user.id) && unwrappedItem" >
                  <InputGroup
                    class="w-4/5 xl:h-10 2xl:h-12  "
                    :class="{'rounded-br-none rounded-bl-none' : matchedBadges.length || badgeSearch === ''}">
                    <InputGroupInput
                      :class="inputText"
                      v-model="badgeSearch"
                      type="search"
                      @keyup.enter="addBadge"
                      @keyup.esc="badgeListOpen=!badgeListOpen"
                      placeholder="Add new group"/>
                    <InputGroupAddon><IconPlus class="size-4 lg:size-5 xl:size-6 2xl:size-8 cursor-pointer" @click="addBadge"/></InputGroupAddon>
                  </InputGroup>
                  <div class="max-h-20 w-4/5 mb-2  overflow-y-auto border-2 border-t-0! border-input p-2 rounded-b-md" v-if="matchedBadges.length ">
                    <Badge
                      variant="groups"
                      @click="unwrappedItem.groups.push(group); badgeSearch = ''"
                      v-for="(group, index) in matchedBadges" :key="index">{{group}}</Badge>
                  </div>
                  <Transition name="fade" class="w-full">
                    <span
                      v-if="existingBadge"
                      class="text-sm lg:text-md xl:text-xl 2xl:text-3xl text-destructive cursor-text w-full">
                    Tag already exists</span>
                  </Transition>
                </div>
              </Transition>
              <div>
                <Badge variant="groups" v-for="(group, index) in (isUnwrapped(user.id) ? unwrappedItem?.groups : user.groups)" :key="index">
                  {{ group }}
                </Badge>
              </div>
            </div>
          </div>
        </CardDescription>
        <CardFooter class="m-0 flex justify-end" v-if="isUnwrapped(user.id)">
          <Button
            class=" flex items-center"
            variant="green_inside"
          >
            Save
            <IconDeviceFloppy />
          </Button>
        </CardFooter>
      </Card>
    </TransitionGroup>

  </div>

</template>
