<script setup lang="ts">
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import type { ActionResponse } from "@/types/types.js";
import { dateParser } from "@/composables/dateParser.js";
import {dataTable, hoverListRow, tableHeaders} from "@/assets/cssFunctions.js";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {
  IconCircleCheck,
  IconCircleX
} from "@tabler/icons-vue";
import SortableHead from "@/helpers_components/SortableHead.vue";
import { useSort } from "@/composables/sorting.js";
import {type HTMLAttributes, onMounted, ref} from "vue";
import {useAuthStore} from "@/stores/authStore.ts";
import {useUserStore} from "@/stores/userStore.ts";
import {getUserActionsRequest} from "@/helpers_functions/requests.ts";
import { toast } from "vue-sonner";
import LoadingTable from "@/helpers_components/LoadingTable.vue";

const props = defineProps<{
  userView: boolean
  actions?: ActionResponse[] | undefined;
  maxH: string
  maxW?: string
  class?: HTMLAttributes["class"]
  userId?: number
}>()
const authStore = useAuthStore()
const userStore = useUserStore()
const areActionsLoading = ref(true);
const userActions = ref<ActionResponse[]>([])


const { sortedData, sortKey, sortOrder, toggleSort } = useSort(
  () => props.actions ? props.actions : userActions.value,
  'createdAt'
)
onMounted(async () => {
  if (props.userView && props.userId) {
    await getUserActionsRequest(props.userId)
      .then(res => userActions.value = res )
      .catch(error => toast.error(`Error retrieving actions: ${error}`))
  }
  else if(userStore.allUsers.length === 0 || !userStore.allUsers) {
    await userStore.getAllUsersRequest()
  }
  areActionsLoading.value = false
})

const givenUser = (author: string, withId: boolean) => {
  const user = userStore.allUsers.find(u => u.email === author)
  return withId ? `${user?.id}/${user?.firstname} ${user?.surname}` : `${user?.firstname} ${user?.surname}`
}

const link = (action: ActionResponse) => {
  if (props.userView) {
    return !!action.closedAt ? `/alerts_history/${action.alertId}`: `/active_alerts/${action.alertId}`;
  }
  else {
    if (authStore.currentUser?.email === action.author)
      return '/team_members/my_account'
    else
      return `/team_members/${givenUser(action.author, true)}`
  }

}

</script>
<template>
  <div
    :style="` max-height: ${maxH}`"
    :class="`mx-auto overflow-auto ${props.class} `">
    <Table :class="`${dataTable}  overflow-x-scroll max-w-[${maxW}] `">
      <TableHeader class="h-8">
        <TableRow v-if="!sortedData.length || areActionsLoading">
          <TableHead colspan="6" class="px-4 py-8 text-center text-slate-400 italic">
            No actions recorded yet.
          </TableHead>
        </TableRow>
        <TableRow v-else :class="tableHeaders">
          <SortableHead keyName="ackUpdate" label="ACK" class="w-7/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <TableHead class="px-2 max-md:w-18/100 w-14/100">
          <span
            class=" items-center h-full truncate mx-0  font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">
            Severity
          </span>
          </TableHead>
          <SortableHead keyName="message" label="Comment" class="w-31/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead v-if="userView" key-name="subject" class="w-30/100" label="Alert" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead v-else keyName="author" label="User" class="w-15/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Date" :class="userView ? 'w-18/100 ' : 'w-15/100' " class="pr-2" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
        </TableRow>
      </TableHeader>
      <Transition name="fade" mode="out-in">
        <LoadingTable :colspan="6" v-if="areActionsLoading"/>
        <TableBody v-else>
          <TableRow
            v-for="action in sortedData"
            :key="action.id"
            :class="hoverListRow('transition-colors')"
          >
            <TableCell>
              <IconCircleCheck v-if="!action.ackUpdate && action.ackUpdate !== null" class="text-green-badge"/>
              <IconCircleX v-else-if="action.ackUpdate" class="text-red-badge" />
              <p class="text-comment ml-2" v-else-if="action.ackUpdate === null">
                —
              </p>
            </TableCell>
            <TableCell >
              <div class="flex space-x-1 items-center" v-if="action.newSeverity">
                <SeverityDiv :severity="action.previousSeverity"/>
                <span :class="`bg-linear-to-r from-severity-${action.previousSeverity} to-severity-${action.newSeverity} bg-clip-text text-md text-transparent font-extrabold `">
                  ➠
              </span>
                <SeverityDiv :severity="action.newSeverity"/>
              </div>
              <div class="text-comment" v-else>
                —
              </div>
            </TableCell>
            <TableCell class="whitespace-break-spaces text-comment" :title="action.message">
              {{ action.message || '—' }}
            </TableCell>
            <TableCell>
              <RouterLink
                :to="link(action)">
                <span
                  class="hover:border-b font-semibold text-blue-badge/80 hover:border-b-blue-600 hover:text-blue-600 text-xs whitespace-break-spaces"
                >{{ userView ? action.subject : `${givenUser(action.author, false)}` }}</span>
              </RouterLink>
            </TableCell>
            <TableCell class=" pr-2 tabular-nums text-comment whitespace-break-spaces">
              {{ dateParser(action.createdAt!).fullDate }}
            </TableCell>
          </TableRow>

        </TableBody>
      </Transition>
    </Table>
  </div>

</template>
