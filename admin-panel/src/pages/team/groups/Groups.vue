<script setup lang="ts">
import {useUserStore} from "@/stores/userStore.ts";
import {blankUser, type GroupDetails, type User, type UserGroupStats} from "@/types/types.ts";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import EditUser from "@/pages/team/users/EditUser.vue";
import {IconUsersGroup, IconPlus} from "@tabler/icons-vue";
import {Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {defineAsyncComponent, onMounted, ref} from "vue";
import GridCardLoader from "@/helpers_components/loaders/GridCardLoader.vue";

const GroupCard = defineAsyncComponent(() => import("@/pages/team/groups/GroupCard.vue"))
const GridCardTransitionGroup = defineAsyncComponent(() => import("@/helpers_components/loaders/GridCardTransitionGroup.vue"))

const userStore = useUserStore()
const isLoading = ref(true);
const groupsStats = ref<UserGroupStats[]>([]);
onMounted(async () => {
  groupsStats.value = await userStore.getGroupsStatsRequest().finally(() => isLoading.value = false) ?? []
})
const searchFilter = ref("")

</script>

<template>
  <div>
    <TopH1Div h1="Users groups">
      <ButtonGroup class="flex">
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
    </TopH1Div>
    <Transition name="fade" mode="out-in">
      <GridCardLoader
        v-if="isLoading"/>
      <GridCardTransitionGroup v-else>
        <GroupCard
          v-for="group in groupsStats"
          :group="group"
          :key="group.id"/>
      </GridCardTransitionGroup>
    </Transition>

  </div>

</template>
