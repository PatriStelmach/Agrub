<script setup lang="ts">

import {IconUserPlus
} from "@tabler/icons-vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import {Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {onMounted, ref} from "vue";
import {useClientSearchFilter} from "@/composables/useClientSearchFilter.js";
import {blankUser, type User} from "@/types/types.ts";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {useUserStore} from "@/stores/userStore.ts";
import GridCardLoader from "@/helpers_components/loaders/GridCardLoader.vue";
import UserCard from './UserCard.vue'
import EditUser from "./EditUser.vue"
import GridCardTransitionGroup from "@/helpers_components/loaders/GridCardTransitionGroup.vue"

const userStore = useUserStore();
const isLoading = ref(true);

const { filteredData,  searchFilter } =
  useClientSearchFilter<User>(() => userStore.allUsers,(user) => `${user.firstname} ${user.surname}` )

onMounted(async () => {
  await Promise.all([
    userStore.getAllUsersRequest(),
    userStore.getAllGroupsRequest()
  ]).finally(() => isLoading.value = false)
})


</script>

<template>
<div>
  <TopH1Div h1="Team">
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
  </TopH1Div>
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
