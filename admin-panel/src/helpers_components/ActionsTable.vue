<script setup lang="ts">
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from '@/components/ui/table'
import type {ActionResponse} from "@/types/types.js";
import {dateParser} from "@/composables/dateParser.js";
import {dataTable, hoverListRow, tableHeaders} from "@/assets/cssFunctions.js";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {IconCircleCheck, IconCircleX} from "@tabler/icons-vue";
import SortableHead from "@/helpers_components/SortableHead.vue";
import {useSort} from "@/composables/sorting.js";
import {type HTMLAttributes, onMounted, ref} from "vue";
import {useAuthStore} from "@/stores/authStore.ts";
import {useUserStore} from "@/stores/userStore.ts";
import LoadingTable from "@/helpers_components/LoadingTable.vue";

const props = defineProps<{
  actions: ActionResponse[] | undefined;
  maxH: string
  maxW?: string
  class?: HTMLAttributes["class"]
  userId?: number
}>()
const authStore = useAuthStore()
const userStore = useUserStore()
const areActionsLoading = ref(true);


const { sortedData, sortKey, sortOrder, toggleSort } = useSort(
  () => props.actions ?? [],
  'createdAt'
)
onMounted(async () => {
  await userStore.getAllUsersRequest()
    .finally(() => areActionsLoading.value = false)
})

const givenUser = (author: string, withId: boolean) => {
  const user = userStore.allUsers.find(u => u.email === author)
  return withId ? `${user?.id}/${user?.firstname} ${user?.surname}` : `${user?.firstname} ${user?.surname}`
}

const link = (action: ActionResponse) => {
  if (authStore.currentUser?.email === action.author)
    return '/team_members/my_account'
  else if (!authStore.isAdmin)
    return '/team_members'
  else
    return `/team_members/${givenUser(action.author, true)}`
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
          <TableHead class="px-2 w-18/100 ">
          <span
            class=" items-center h-full truncate mx-0  font-bold text-sm lg:text-md xl:text-lg 2xl:text:xl">
            Severity
          </span>
          </TableHead>
          <SortableHead keyName="message" label="Comment" class="w-37/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="author" label="User" class="w-18/100" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
          <SortableHead keyName="createdAt" label="Date" class="pr-2 'w-20/100'" :sort-key="sortKey as string" :sort-order="sortOrder" @update:toggle-sort="toggleSort"/>
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
              <IconCircleCheck v-if="action.ackUpdate" class="text-green-badge"/>
              <IconCircleX v-else-if="!action.ackUpdate  && action.ackUpdate !== null" class="text-red-badge" />
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
                >{{  `${givenUser(action.author, false)}` }}</span>
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
