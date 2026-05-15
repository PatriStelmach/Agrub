<script setup lang="ts">
import { useRoute } from "vue-router";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {computed, onMounted, ref} from "vue";
import {useUserStore} from "@/stores/userStore.ts";
import {type GroupDetails, type Rule, type User} from "@/types/types.ts";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import {IconEdit, IconLoader, IconFilterPlus, IconTrash, IconUserPlus} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import ShowRuleDiv from "@/pages/team/groups/ShowRuleDiv.vue";
import NewRuleDialog from './NewRuleDialog.vue';
import {
  deleteRuleFromGroup,
  deleteUserFromGroup,
  getGroupDataRequest
} from "@/helpers_functions/requests.ts";
import {hoverListRow} from "@/assets/cssFunctions.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import UsersCombobox from "@/helpers_components/UsersCombobox.vue";
import {toast} from "vue-sonner";

const route = useRoute();
const userStore = useUserStore();
const groupId = Number(route.params.id)
const isEditMode = !!groupId
const isLoading = ref(true)
const isDialogOpen = ref(false)
const groupDetails =  ref<GroupDetails>()
const loadingUserDelete = ref<number[]>([])
const loadingGroupsDelete = ref<number[]>([])
const editNameOpen = ref(false)

const groupUsers = computed({
  get() { return groupDetails.value?.users ?? [] },
  set(val) {
    if (groupDetails.value) {
      groupDetails.value.users = val ?? []
    }
  }
})

const groupRules = computed({
  get() { return groupDetails.value?.rules ?? [] },
  set(val) {
    if (groupDetails.value) {
      groupDetails.value.rules = val ?? []
    }
  }
})

onMounted(async () => {
  groupDetails.value = await getGroupDataRequest(groupId).finally(() => isLoading.value = false)
})

const addNewRule = (data: Rule) => {
  groupRules.value = [...groupRules.value, data]
}
const assignUser = (data: User) => {
  groupUsers.value = [...groupUsers.value, data]
}
const deleteUser = async (userId: number) => {
  loadingUserDelete.value.push(userId)
  await deleteUserFromGroup(userId, groupId)
    .then(() => {
      groupUsers.value = groupUsers.value.filter((u) => u.id !== userId)
    })
    .catch((err) => {
      toast.error(err.message)
    })
    .finally(() => loadingUserDelete.value.pop())
}

const deleteRule = async (ruleId: number) => {
  loadingGroupsDelete.value.push(ruleId)
  await deleteRuleFromGroup(ruleId)
    .then(() => {
      groupRules.value = groupRules.value.filter((r) => r.id !== ruleId)
    })
    .catch((err) => {
      toast.error(err.message)
    })
    .finally(() => loadingGroupsDelete.value.pop())
}

</script>

<template>
  <div>
    <TopH1Div :h1="isEditMode ? 'Edit group' : 'Create new group'" >
      <ButtonGroup>
        <NewRuleDialog
          @update:rule="addNewRule"
          v-model:open="isDialogOpen"
        >
          <Button
            @click="isDialogOpen = true" variant="green_outline" >
            Add rules
            <IconFilterPlus/>
          </Button>
        </NewRuleDialog>
        <UsersCombobox
          @update:add-user="assignUser"
          :groupId="groupId"
          :current-users="groupDetails?.users ?? []">
          <Button
            class="border-l-2!"
            variant="blue_outline"
          >
            Assign users
            <IconUserPlus/>
          </Button>
        </UsersCombobox>
      </ButtonGroup>
    </TopH1Div>
    <div class="flex">
      <h1 class="font-bold italic mb-6 border-b-3 border-blue-badge/70 w-fit mx-6 text-xl">
        Group name: {{ groupDetails?.name }}
      </h1>
      <Button
        size="icon-sm"
        variant="green_outline"
        @click="editNameOpen = !editNameOpen"
      ><IconEdit/>
      </Button>
    </div>
      <Transition name="fade" mode="out-in">
        <BigLoadingBlock v-if="isLoading"/>
        <div v-else-if="!isLoading && groupDetails" class="px-6 max-h-[75vh] overflow-y-auto w-full">
          <TransitionGroup tag="div" class="flex space-x-20 *:max-lg:w-1/2" name="fade" mode="out-in">
            <div class="w-7/10!" key="rules">
              <h1 class="pb-1 mb-2 border-b-4  text-center">Group rules configuration: {{groupDetails.rules.length }}</h1>
              <div class="mx-auto pb-2 border-b-4 max-h-[70vh] overflow-y-auto">
                <ul>
                  <li v-if="groupRules.length < 1">
                    <div class="my-4 w-full text-center">
                      <span class=" mx-auto">No rules added yet</span>
                    </div>
                  </li>
                  <li v-else
                      v-for="rule in groupRules" :key="rule.id"
                      :class="hoverListRow('cursor-pointer')"
                  >
                    <ShowRuleDiv
                      :is-rule-deleting="loadingGroupsDelete.some(r => r === rule.id)"
                      :rule="rule"
                      @delete-rule="deleteRule"
                    />
                  </li>
                </ul>
              </div>
            </div>
            <div class="w-3/10! " key="users">
              <h1 class="pb-1 mb-2 border-b-4  text-center">Assigned users: {{ groupUsers.length }}</h1>
              <ul class="mx-auto pb-2 border-b-4 max-h-[70vh] overflow-y-auto">
                <li v-if="groupUsers.length < 1">
                  <div class="my-4 w-full text-center">
                    <span class=" mx-auto">No users assigned yet</span>
                  </div>
                </li>
                <li v-else :class="hoverListRow('px-2')" v-for="user in groupUsers" :key="user.id">
                  <div class="space-x-2 flex items-center py-2 px-2 pr-6 relative">
                    <Avatar class="size-9 rounded-lg">
                      <AvatarFallback class="text-sm rounded-full grayscale">
                        {{ userStore.avFallback(user)}}
                      </AvatarFallback>
                    </Avatar>
                    <div class="grid mr-5">
                      <span class="whitespace-break-spaces">{{ userStore.fullName(user) }}</span>
                      <span class="text-comment text-sm whitespace-break-spaces">{{ user.email }}</span>
                    </div>
                    <Button variant="red_outline" class="absolute right-2 top-1/2 -translate-y-1/2" size="icon-sm">
                      <IconLoader v-if="loadingUserDelete.some(i => i === user.id)" class="animate-spin "/>
                      <IconTrash v-else @click="deleteUser(user.id!)"/>
                    </Button>

                  </div>
                  </li>
              </ul>
            </div>
          </TransitionGroup>
        </div>
      </Transition>
  </div>
</template>
