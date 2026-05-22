<script setup lang="ts">
import { useRoute } from "vue-router";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {computed, onMounted, ref} from "vue";
import {useUserStore} from "@/stores/userStore.ts";
import {type GroupDetails, initialGroupDetails, type Rule, type User} from "@/types/types.ts";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import {
  IconX,
  IconEdit,
  IconLoader,
  IconFilterPlus,
  IconTrash,
  IconUserPlus,
  IconCheck,
  IconKey, IconTool
} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import NewRuleDialog from './NewRuleDialog.vue';
import {
  changeGroupNameRequest,
  deleteRuleFromGroupRequest,
  deleteUserFromGroupRequest,
  getGroupDataRequest
} from "@/helpers_functions/requests.ts";
import {hoverListRow} from "@/assets/cssFunctions.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import UsersCombobox from "@/helpers_components/UsersCombobox.vue";
import {toast} from "vue-sonner";
import {Input} from "@/components/ui/input";
import RulesList from "@/pages/team/groups/RulesList.vue";

const route = useRoute();
const userStore = useUserStore();
const groupId = Number(route.params.id)
const isLoading = ref(true)
const isDialogOpen = ref(false)
const groupDetails =  ref<GroupDetails>(initialGroupDetails as GroupDetails)
const loadingUserDelete = ref<number[]>([])
const loadingGroupsDelete = ref<number[]>([])
const editNameOpen = ref(false)
const newName = ref<string>()
const newNameLoading = ref(false)



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
  groupDetails.value = await getGroupDataRequest(groupId).finally(() => isLoading.value = false) ?? initialGroupDetails
  newName.value = groupDetails.value.name
})

const addNewRule = (data: Rule) => {
  groupRules.value = [...groupRules.value, data]
}
const assignUser = (data: User) => {
  groupUsers.value = [...groupUsers.value, data]
}
const deleteUser = async (userId: number) => {
  loadingUserDelete.value.push(userId)
  await deleteUserFromGroupRequest(userId, groupId)
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
  await deleteRuleFromGroupRequest(ruleId)
    .then(() => {
      groupRules.value = groupRules.value.filter((r) => r.id !== ruleId)
    })
    .catch((err) => {
      toast.error(err.message)
    })
    .finally(() => loadingGroupsDelete.value.pop())
}

const changeName = async () => {
  newNameLoading.value = true
  if (newName.value) {
    await changeGroupNameRequest(groupId, newName.value)
      .then((res) => {
        groupDetails.value.name = res.name
      })
      .catch((err) => {
        toast.error(err.message)
      })
      .finally(() => {
        newNameLoading.value = false
        editNameOpen.value = false
      })
  }
  else {
    toast.error('Name cannot be empty!')
    newNameLoading.value = false
  }
}

</script>

<template>
  <div>
    <TopH1Div h1="Edit group" >
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
    <span class="font-bold italic mb-8 items-center border-b-3 h-12 border-blue-badge/70 w-fit mx-6 flex space-x-2 text-xl">
      <span class=" tems-baseline w-full">Group name:</span>
      <span v-if="editNameOpen" class="flex justify-center relative w-full">
        <Input
          :aria-invalid=" newName?.length == 0 || !newName"
          :default-value="groupDetails.name"
          v-model="newName"/>
        <span class="text-xs text-red-badge absolute bottom-10" v-if="newName?.length == 0 || !newName">Name cannot be empty</span>
      </span>

      <span v-else> {{ groupDetails?.name }}</span>

      <Button
        :disabled="newName?.length == 0 || !newName"
        size="icon-sm"
        variant="green_outline"
        @click="editNameOpen ? changeName() : editNameOpen = true"
      >
        <IconCheck v-if="editNameOpen && !newNameLoading"/>
        <IconLoader v-else-if="editNameOpen && newNameLoading" class="animate-spin" />
        <IconEdit v-else />

      </Button>
      <Button
        size="icon-sm"
        variant="red_outline"
        v-if="editNameOpen"
        @click="editNameOpen = false; newName = groupDetails.name"
      >
        <IconX/>
      </Button>
    </span>


      <Transition name="fade" mode="out-in">
        <BigLoadingBlock class="h-[80vh]" v-if="isLoading"/>
        <div v-else-if="!isLoading && groupDetails" class="px-6 max-h-[75vh] overflow-y-auto w-full">
          <TransitionGroup tag="div" class="flex space-x-20 *:max-lg:w-1/2" name="fade" mode="out-in">
            <RulesList
              :rules="groupRules"
              :loading-groups-delete="loadingGroupsDelete"
              @delete-rule="deleteRule"
              key="rules">
            </RulesList>
            <div class="w-3/10! " key="users">
              <h1 class="pb-1 mb-2 border-b-4  text-center">Assigned users: {{ groupUsers.length }}</h1>
              <ul class="mx-auto pb-2 border-b-4 max-h-[70vh] overflow-y-auto">
                <li v-if="groupUsers.length < 1">
                  <div class="my-4 w-full text-center">
                    <span class=" mx-auto">No users assigned yet</span>
                  </div>
                </li>
                <li v-else :class="hoverListRow('px-2')" v-for="user in groupUsers" :key="user.id">
                  <div
                    @click="$router.push(`/team_members/${user.email}`)"
                    v-if="user.id"
                    class="space-x-2 cursor-pointer flex items-center py-2 px-2 pr-6 relative">
                    <Avatar class="size-9 rounded-lg">
                      <AvatarFallback class="text-sm rounded-full grayscale">
                        {{ userStore.avFallback(user)}}
                      </AvatarFallback>
                    </Avatar>
                    <div class="grid mr-5">
                      <span class="whitespace-break-spaces">{{ userStore.fullName(user) }}</span>
                      <span class="text-comment text-sm whitespace-break-spaces">{{ user.email }}</span>
                    </div>
                    <component
                      :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" stroke="2"
                      :class="{ 'rotate-90' : user.role === 'TECHNICIAN' }"
                    />
                    <Button
                      @click="deleteUser(user.id)"
                      variant="red_outline" class="absolute right-2 top-1/2 -translate-y-1/2" size="icon-sm">
                      <IconLoader v-if="loadingUserDelete.some(i => i === user.id)" class="animate-spin "/>
                      <IconTrash v-else />
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
