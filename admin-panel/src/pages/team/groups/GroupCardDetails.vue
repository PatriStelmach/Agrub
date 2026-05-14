<script setup lang="ts">
import { useRoute } from "vue-router";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {onMounted, ref} from "vue";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious
} from "@/components/ui/carousel";
import RulesCard from "@/pages/team/groups/RulesCard.vue";
import {useUserStore} from "@/stores/userStore.ts";
import {type GroupDetails, InitialGroupDetails} from "@/types/types.ts";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import {IconTrash} from "@tabler/icons-vue";

const route = useRoute();
const userStore = useUserStore();
const groupId = route.params.id;
const isEditMode = !!groupId
const isLoading = ref(true)
const data =  isEditMode ? ref<GroupDetails | null | undefined>(null) : ref<GroupDetails>(InitialGroupDetails)

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
            <div class="w-3/4">
              <h1 class="w-fit mx-auto pb-1 mb-2 border-b-4 text-center">Group rules settings</h1>
              <Carousel  v-if="data" class="mx-auto w-4/5">
                <CarouselContent>
                  <CarouselItem v-for="rule in data.rules" :key="rule.id">
                    <div class="p-1">
                      <RulesCard
                        :rule-id="rule.id!"
                        :is-loading="isLoading"
                        :rule="rule"
                      />
                    </div>
                  </CarouselItem>
                </CarouselContent>
                <CarouselPrevious />
                <CarouselNext />
              </Carousel>
            </div>
            <div class="w-1/4">
              <h1 class="pb-1 mb-2 border-b-4  text-center">Assigned users</h1>
              <ul class="max-h-[50vh] overflow-auto">
                <li v-for="user in data.users" :key="user.id"
                    class="hover:bg-blue-badge/20 odd:bg-accent/50 space-x-2 flex items-center py-2 px-2 pr-6 relative">
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
