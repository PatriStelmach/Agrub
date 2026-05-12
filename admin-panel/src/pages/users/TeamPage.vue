<script setup lang="ts">

import {usersData} from "@/data/usersData.ts";
import {
  IconUsersGroup, IconUserPlus
} from "@tabler/icons-vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import {Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {useWrapping} from "@/composables/unwrapping.ts";
import {computed, onMounted, ref} from "vue";
import {useClientSearchFilter} from "@/composables/useClientSearchFilter.js";
import {blankUser, type User} from "@/types/types.ts";
import {useTagsFilter} from "@/composables/useTagsFilter.ts";
import {topButtonGroup, topH1} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers_components/GoBackButton.vue";
import UserCard from "@/pages/users/UserCard.vue";
import {useUserStore} from "@/stores/userStore.ts";
import EditUser from "@/pages/users/EditUser.vue";

const userStore = useUserStore();
const isLoading = ref<boolean>(false);

const { filteredData,  searchFilter } =
  useClientSearchFilter<User>(() => userStore.allUsers,(user) => `${user.firstname} ${user.surname}` )

onMounted(async () => {
  await userStore.getAllUsersRequest().finally(() => {
    userStore.getAllGroupsRequest().finally(() => isLoading.value = false)
  })

})




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
          <EditUser
            :user="blankUser as User"
          >
            <Button
              variant="green_outline">
              Add new user
              <IconUserPlus/>
            </Button>
          </EditUser>

          <Button
            class="border-l!"
            @click=""
            variant="blue_outline">
            Groups
            <IconUsersGroup/>
          </Button>
          <ButtonGroupSeparator/>
          <InputGroup  >
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
    class=" px-6 py-2 pr-3 grid sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 max-h-[85vh] overflow-y-auto"
    tag="div"
    name="slide-fade-card"
  >
    <UserCard
      v-for="user in filteredData"
      :key="user.id"
      :user="user"/>
  </TransitionGroup>



  </div>

</template>
