<script setup lang="ts">
import {useUserStore} from "@/stores/userStore.ts";
import {topButtonGroup, topH1} from "@/assets/cssFunctions.ts";
import {blankUser, type User} from "@/types/types.ts";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import EditUser from "@/pages/users/EditUser.vue";
import {IconUsersGroup, IconPlus} from "@tabler/icons-vue";
import {Search} from "lucide-vue-next";
import GoBackButton from "@/helpers_components/GoBackButton.vue";
import {Button} from "@/components/ui/button";
import {defineAsyncComponent, onMounted, ref} from "vue";
import GridCardLoader from "@/helpers_components/GridCardLoader.vue";

const GroupCard = defineAsyncComponent(() => import("@/pages/users/GroupCard.vue"))
const GridCardTransitionGroup = defineAsyncComponent(() => import("@/helpers_components/GridCardTransitionGroup.vue"))

const userStore = useUserStore()
const isLoading = ref(true);
onMounted(async () => {
  if(userStore.allGroups.length === 0)
    await userStore.getAllGroupsRequest().then(() => isLoading.value = false)
  else isLoading.value = false
})
const searchFilter = ref("")

</script>

<template>
  <div>
    <div class="relative ">
      <h1 :class="topH1">Users groups</h1>
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
              Create new group
              <div class="flex space-x-0">
                <IconPlus/>
                <IconUsersGroup/>
              </div>
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
      <GridCardLoader
        v-if="isLoading">
      </GridCardLoader>
      <GridCardTransitionGroup v-else>
        <GroupCard
          v-for="group in userStore.allGroups"
          :key="group.id"/>
      </GridCardTransitionGroup>
    </Transition>

  </div>

</template>
