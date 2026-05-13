<script setup lang="ts">

import {IconUserPlus
} from "@tabler/icons-vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import {Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {defineAsyncComponent, onMounted, ref} from "vue";
import {useClientSearchFilter} from "@/composables/useClientSearchFilter.js";
import {blankUser, type User} from "@/types/types.ts";
import {topButtonGroup, topH1} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers_components/GoBackButton.vue";
import UserCard from "@/pages/users/UserCard.vue";
import {useUserStore} from "@/stores/userStore.ts";
import GridCardLoader from "@/helpers_components/GridCardLoader.vue";
const EditUser = defineAsyncComponent(() => import("@/pages/users/EditUser.vue"))
const GridCardTransitionGroup = defineAsyncComponent(() => import("@/helpers_components/GridCardTransitionGroup.vue"))

const userStore = useUserStore();
const isLoading = ref(true);

const { filteredData,  searchFilter } =
  useClientSearchFilter<User>(() => userStore.allUsers,(user) => `${user.firstname} ${user.surname}` )

onMounted(async () => {
  await userStore.getAllUsersRequest().finally(() => {
    userStore.getAllGroupsRequest().finally(() => isLoading.value = false)
  })
})

</script>

<template>
<div>
  <div class="relative ">
    <h1 :class="topH1">Team</h1>
      <ButtonGroup :class="topButtonGroup">
        <ButtonGroup class="flex">
          <GoBackButton/>
        </ButtonGroup>
        <ButtonGroup>
          <EditUser
            action-type="create"
            :user="blankUser as User"
          >
            <Button
              variant="green_outline">
              Add new user
              <IconUserPlus/>
            </Button>
          </EditUser>
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
  <Transition name="fade" mode="out-in">
    <GridCardLoader v-if="isLoading"/>
    <GridCardTransitionGroup v-else>
      <UserCard
        v-for="user in filteredData"
        :key="user.id"
        :user="user"
      />
    </GridCardTransitionGroup>
  </Transition>

  </div>
</template>
