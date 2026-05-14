<script setup lang="ts">
import { useRoute } from "vue-router";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {onMounted, ref} from "vue";

import EditRulesCard from "@/pages/team/groups/EditRulesCard.vue";
import {useUserStore} from "@/stores/userStore.ts";
import {type GroupDetails, InitialGroupDetails} from "@/types/types.ts";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import {IconFilterPlus, IconTrash} from "@tabler/icons-vue";
import {Button} from "@/components/ui/button";
import ShowRuleDiv from "@/pages/team/groups/ShowRuleDiv.vue";

const route = useRoute();
const userStore = useUserStore();
const groupId = route.params.id;
const isEditMode = !!groupId
const isLoading = ref(true)
const data =  ref<GroupDetails | null | undefined>(null)

onMounted(async () => {
  data.value = await userStore.getGroupDataRequest(Number(groupId)).finally(() => isLoading.value = false)
})



</script>

<template>
  <div>
    <TopH1Div :h1="isEditMode ? 'Edit group' : 'Create new group'" />
    <h1 class="font-bold italic mb-6 border-b-3 border-blue-badge/70 w-fit mx-6 text-xl">Group name: {{ data?.name }}</h1>
      <Transition name="fade" mode="out-in">
        <BigLoadingBlock v-if="isLoading"/>
        <div v-else-if="!isLoading && data" class="px-6 max-h-[85vh] w-full">
          <TransitionGroup tag="div"
                           class="flex space-x-20 *:max-lg:w-1/2" name="fade" mode="out-in">
            <div class="w-2/3!">
              <h1 class="pb-1 mb-2 border-b-4  text-center">Group rules settings</h1>
              <div class="mx-auto border-b-4">
                <ul>
                  <li v-if="data.rules.length < 1">
                    <div class="my-4 w-full text-center">
                      <span class=" mx-auto">No rules added yet</span>
                    </div>
                  </li>
                  <li v-else
                      v-for="rule in data.rules" :key="rule.id"
                      class="hover:bg-blue-badge/10 cursor-pointer odd:bg-accent/50"
                  >
                    <ShowRuleDiv
                      :rule-id="rule.id! "
                      :rule="rule"
                    />
                  </li>
                </ul>
              </div>
              <div class="flex w-full my-4 justify-center">
                <Button size="icon-sm" variant="green_outline" class=" w-1/3">
                  Add new group
                  <IconFilterPlus/>
                </Button>
              </div>

            </div>
            <div class="w-1/3!">
              <h1 class="pb-1 mb-2 border-b-4  text-center">Assigned users</h1>
              <ul class="max-h-[50vh] overflow-auto">
                <li v-for="user in data.users" :key="user.id"
                    class="hover:bg-blue-badge/10 odd:bg-accent/50 space-x-2 flex items-center py-2 px-2 pr-6 relative">
                  <Avatar class="size-9 rounded-lg">
                    <AvatarFallback class="text-sm rounded-full grayscale">
                      {{ userStore.avFallback(user)}}
                    </AvatarFallback>
                  </Avatar>
                  <div class="grid">
                    <span class="">{{ userStore.fullName(user) }}</span>
                    <span class="text-comment text-sm">{{ user.email }}</span>
                  </div>
                  <IconTrash
                    class="cursor-pointer hover:scale-102 absolute size-5 right-2 top-1/2 -translate-y-1/2 text-red-badge"/>
                </li>
              </ul>
            </div>


          </TransitionGroup>
        </div>

      </Transition>


  </div>
</template>
