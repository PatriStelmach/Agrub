<script setup lang="ts">
import {computed, onMounted, ref} from "vue"
import { useUserStore } from "@/stores/userStore.js"

import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import {
  IconKey, IconTool, IconDeviceFloppy, IconLabel, IconLock,
  IconLogout, IconMail, IconCalendarEvent, IconLoader,
  IconEdit, IconUsersGroup, IconEditOff, IconUserKey, IconFilterCog
} from '@tabler/icons-vue'
import {toast} from "vue-sonner";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {dateParser} from "@/helpers_functions/dateParser.js";
import ChangePasswordDialog from "@/pages/team/users/ChangePasswordDialog.vue";
import {useAuthStore} from "@/stores/authStore.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {Badge} from "@/components/ui/badge";
import {
  type ActionResponse, type ActionsOrAlertHistoryFilters, undefinedActionsOrAlertsFilters,
} from "@/types/types.ts";
import UserActionsTable from "@/pages/team/users/UserActionsTable.vue";
import EditMyAccountform from "@/pages/team/users/EditMyAccountform.vue";
import ServerPagination from "@/helpers_components/ServerPagination.vue";
import {useServerSearchFilter} from "@/composables/useServerSearchFilter.ts";
import api from "@/lib/axios.ts";
import ActionsOrAlertsFilters from "@/pages/alerts/history/ActionsOrAlertsFilters.vue";

const authStore = useAuthStore();
const user = computed(() => authStore.currentUser!)
const userStore = useUserStore()

onMounted(async () => {
  if (authStore.isAdmin) {
    await Promise.all([
      getUserActionsRequest(),
      userStore.getAllGroupsRequest()
    ])
      .finally(() => isLoading.value = false)
  }
  else {
    await getUserActionsRequest()
      .finally(() => isLoading.value = false)
  }

})

const isSubmitLoading = ref(false)
const isEditMode = ref<boolean>(false)

const getUserActionsRequest = async () => {
  try {
    const response = await api.get(`/users/${user.value.id}/actions`, {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        sortBy: sortedHead.value.sortKey,
        sortDir: sortedHead.value.sortOrder,
        severity: filters.value.severity,
        message: filters.value.message,
        subject: filters.value.subject,
        source: filters.value.source,
        origin: filters.value.origin,
        ack: filters.value.ack,
        unack: filters.value.unack,
        createdDateFrom: filters.value.createdDateFrom,
        createdDateTo: filters.value.createdDateTo,
        closedDateFrom: filters.value.closedDateFrom,
        closedDateTo: filters.value.closedDateTo,
      },
      paramsSerializer: {
        indexes: null
      }
    })

    if (response.status === 200) {
      actions.value = response.data.content.map((a:any) => ({
      ...a,
      createdAt: new Date(a.createdAt),
      closedAt: new Date(a.closedAt),
      isAcknowledged: a.acknowledged
    }))
      totalElements.value = response.data.totalElements
    }
  } catch (e) {
    toast.error(`Error getting actions history: ${e}`)
  }
}

const {
  currentPage,
  pageSize,
  filters,
  items:actions,
  isLoading,
  sortedHead,
  totalElements,
  updateFilters,
} = useServerSearchFilter<ActionResponse, ActionsOrAlertHistoryFilters>(
  getUserActionsRequest,
  undefinedActionsOrAlertsFilters,
  'createdAt',
  'desc',
)

</script>

<template>
  <div class="w-full">
    <TopH1Div h1="Account details">
      <ButtonGroup>
        <ChangePasswordDialog>
          <Button
            type="button"
            :variant="isEditMode ? 'blue_outline' : 'green_outline'"
          >
            Change password
            <IconLock class="size-5"/>
          </Button>
        </ChangePasswordDialog>
        <Button
          v-if="authStore.isAdmin"
          :variant=" isEditMode ? 'red_outline' : 'blue_outline'"
          type="button"
          @click="isEditMode = !isEditMode"
          class="border-l-2! flex items-center">
          {{ isEditMode ? 'Cancel' : 'Edit' }}
          <component :is="isEditMode ? IconEditOff : IconEdit" class="pointer-events-none"/>
        </Button>
        <Button v-if="isEditMode" type="submit" form="edit-user-form" variant="green_outline" class="border-l-2! flex items-center">
          Submit <IconDeviceFloppy v-if="!isSubmitLoading" />
          <IconLoader v-else class="animate-spin" />
        </Button>
      </ButtonGroup>
    </TopH1Div>
    <div class="w-full flex space-x-10 max-h-[90vh]  overflow-y-auto">
      <div class="w-3/10 ml-6">
        <EditMyAccountform
          v-model:isSubmitLoading="isSubmitLoading"
          v-model:isEditMode="isEditMode"
          v-if="isEditMode"
          :user="user"
        />

        <div v-else class=" w-full space-y-4">
          <Avatar class="size-14 rounded-full">
            <AvatarFallback class="rounded-full text-2xl grayscale">
              {{ authStore.avFallback }}
            </AvatarFallback>
          </Avatar>


          <div class="grid space-y-2 **:flex *:items-center **:gap-2">
            <div>
              <IconLabel class="size-5" />
              <div class="flex items-baseline">
                <h1 :class="bigNameLabel">Firstname: </h1>
                <span class="text-comment">{{ user.firstname }}</span>
              </div>
            </div>

            <div>
              <IconLabel class="size-5" />
              <div class="flex items-baseline">
                <h1 :class="bigNameLabel">Surname: </h1>
                <span class="text-comment">{{ user.surname }}</span>
              </div>
            </div>

            <div>
              <IconMail class="size-5" />
              <div class="flex items-baseline">
                <h1 :class="bigNameLabel">Email: </h1>
                <span class="text-comment">{{ user.email }}</span>
              </div>
            </div>

            <div>
              <IconLogout class="size-5" />
              <div class="flex items-baseline">
                <h1 :class="bigNameLabel">Auto logout: </h1>
                <span class="text-comment">{{ user.autoLogoutMinutes ?? '—' }} min</span>
              </div>
            </div>

            <div>
              <IconUserKey class="size-5"/>
              <div class="flex items-center gap-2">
                <h1 :class="bigNameLabel">User role: </h1>
                <span class="text-comment flex items-center gap-1">
        <component :is="user.role === 'ADMINISTRATOR' ? IconKey : IconTool" class="size-5" :class="{'rotate-90' : user.role === 'TECHNICIAN'}" />
        <span>{{ user.role }}</span>
      </span>
              </div>
            </div>

            <div>
              <IconCalendarEvent class="size-5" />
              <div class="flex items-baseline">
                <h1 :class="bigNameLabel">Last password change: </h1>
                <span class="text-comment">{{ user.lastPasswordChangeDate ? dateParser(user.lastPasswordChangeDate).fullDate : '--------' }}</span>
              </div>
            </div>

            <div>
              <IconUsersGroup class="size-5" />
              <div class="flex items-base gap-2 flex-wrap">
                <h1 :class="bigNameLabel">Users groups:</h1>
                <div v-if="authStore.isAdmin">
                  <RouterLink v-for="g in user.groups" :key="g.id" :to="`/groups/edit_group/${g.id}/${g.name}`">
                    <Badge class="mt-0!" variant="tags">
                      {{ g.name }}
                    </Badge>
                  </RouterLink>
                </div>
                <div v-else>
                  <span v-for="g in user.groups" :key="g.id">
                    <Badge class="mt-0!" variant="tags">
                      {{ g.name }}
                    </Badge>
                  </span>
                </div>

              </div>
            </div>

          </div>
        </div>
      </div>
      <div class="w-7/10 mr-4">
        <div class="flex justify-end">
          <ActionsOrAlertsFilters
            type="actions"
            @update:filters="updateFilters">
            <Button
              class=" ml-auto"
              variant="blue_outline">
              Filters <IconFilterCog />
            </Button>
          </ActionsOrAlertsFilters>
        </div>

        <div class=" pb-2 border-b-4 mt-2">
          <ul>
            <UserActionsTable
              v-model:sorted-head="sortedHead"
              :actions="actions"
              :isLoading="isLoading"
              max-w="65vw"
              max-h="80vh"
            >
              <ServerPagination
                :total="totalElements"
                v-model:pageSize="pageSize"
                v-model:pageIndex="currentPage"
              />
            </UserActionsTable>
          </ul>
        </div>
      </div>
    </div>
  </div>

</template>
