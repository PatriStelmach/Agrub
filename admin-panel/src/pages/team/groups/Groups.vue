<script setup lang="ts">
import {type UserGroupStats} from "@/types/types.ts";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import {IconUsersGroup, IconPlus} from "@tabler/icons-vue";
import {Search} from "lucide-vue-next";
import {Button} from "@/components/ui/button";
import {computed, onMounted, ref} from "vue";
import GridCardLoader from "@/helpers_components/loaders/GridCardLoader.vue";
import GroupCard from "@/pages/team/groups/GroupCard.vue";
import GridCardTransitionGroup from "@/helpers_components/loaders/GridCardTransitionGroup.vue"
import {getGroupsStatsRequest} from "@/helpers_functions/requests.ts";
import {gridSkeletons} from "@/assets/cssFunctions.ts";
import NewGroupDialog from "@/pages/team/groups/NewGroupDialog.vue";

const isLoading = ref(true);
const groupsStats = ref<UserGroupStats[]>([]);
onMounted(async () => {
  groupsStats.value = await getGroupsStatsRequest().finally(() => isLoading.value = false) ?? []
})
const searchFilter = ref("")

const groups = computed(() => {
  return groupsStats.value.filter((g) => g.name.toLowerCase().includes(searchFilter.value.toLowerCase()));
})

</script>

<template>
  <div>
    <TopH1Div h1="Users groups">
      <ButtonGroup>
        <NewGroupDialog>
          <Button
            variant="green_outline">
            New group
            <div class="flex space-x-0">
              <IconPlus/>
              <IconUsersGroup/>
            </div>
          </Button>
        </NewGroupDialog>
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
          v-for="group in groups"
          :group="group"
          :key="group.id"/>
      </GridCardTransitionGroup>
    </Transition>

  </div>

</template>
