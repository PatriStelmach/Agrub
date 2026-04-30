<script setup lang="ts">

import DialogLabel from "@/helpers/DialogLabel.vue";
import {Badge} from "@/components/ui/badge";
import type {ActionResponse} from "@/types/types.ts";
import {dateParser} from "@/composables/dateParser.ts";
import {useAlertStore} from "@/stores/alertStore.ts";
import {Skeleton} from "@/components/ui/skeleton";

const props = defineProps<{
  actions: ActionResponse[] | undefined;
  isLoading: boolean;
}>()

</script>

<template>
  <slot/>
    <div class="rounded-md border-2  w-full border-secondary">

      <div class="sticky top-0 z-10 mx-0 w-full flex items-center
            bg-secondary px-2 py-2 text-xs font-semibold uppercase tracking-wider text-comment">
        <span class="w-2/10">Type</span>
        <span class="w-4/10">Comment</span>
        <span class="w-2/10">User</span>
        <span class="w-2/10 text-right">Date</span>
      </div>
      <ul class="overflow-y-auto overflow-x-hidden max-h-[20vh] w-full divide-y text-sm">
        <li v-for="i in [1,2,3,4]" v-if="isLoading"><Skeleton  class=" m-2 h-2 w-full"/></li>

        <li v-else-if=" !isLoading && !actions?.length" class="px-4 py-8 text-center text-slate-400 italic">
          No actions recorded yet.
        </li>

        <li v-else
          v-for="action in actions"
          :key="action.id"
          class="flex items-center px-2 py-3 transition-colors hover:bg-list-hover"
        >
          <div class="w-2/10">
            <Badge variant="ack_type">
              {{ action.actionType }}
            </Badge>
          </div>
          <p class="w-4/10  whitespace-break-spaces text-comment" :title="action.message">
            {{ action.message || '—' }}
          </p>
          <p class="w-2/10 font-medium text-comment">
            {{ action.author }}
          </p>
          <p class="w-2/10 text-right pr-1 text-xs tabular-nums text-date">
            {{ dateParser(action.createdAt).fullDate }}
          </p>
        </li>

      </ul>
    </div>
</template>

