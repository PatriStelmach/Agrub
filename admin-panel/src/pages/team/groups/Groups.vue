<script setup lang="ts">
import {useUserStore} from "@/stores/userStore.ts";
import {blankUser, type User, type UserGroupStats} from "@/types/types.ts";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import EditUser from "@/pages/team/users/EditUser.vue";
import {IconUsersGroup, IconPlus} from "@tabler/icons-vue";
import {Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {onMounted, ref} from "vue";
import GridCardLoader from "@/helpers_components/loaders/GridCardLoader.vue";
import GroupCard from "@/pages/team/groups/GroupCard.vue";
import GridCardTransitionGroup from "@/helpers_components/loaders/GridCardTransitionGroup.vue"
import {getGroupsStatsRequest} from "@/helpers_functions/requests.ts";
import {gridSkeletons} from "@/assets/cssFunctions.ts";

const isLoading = ref(true);
const groupsStats = ref<UserGroupStats[]>([]);
onMounted(async () => {
  groupsStats.value = await getGroupsStatsRequest().finally(() => isLoading.value = false) ?? []
})
const searchFilter = ref("")

</script>

<template>
  <div>
    <TopH1Div h1="Users groups">
      <ButtonGroup>
          <Button
            variant="green_outline">
            New group
            <div class="flex space-x-0">
              <IconPlus/>
              <IconUsersGroup/>
            </div>
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
    </TopH1Div>
    <Transition name="fade" mode="out-in">
      <GridCardLoader
        v-if="isLoading"/>
      <GridCardTransitionGroup :class="gridSkeletons" v-else>
        <GroupCard
          v-for="group in groupsStats"
          :group="group"
          :key="group.id"/>
      </GridCardTransitionGroup>
    </Transition>

  </div>

</template>
