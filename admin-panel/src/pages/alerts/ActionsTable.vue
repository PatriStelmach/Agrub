<script setup lang="ts">
import {Badge} from "@/components/ui/badge";
import type {ActionResponse} from "@/types/types.ts";
import {dateParser} from "@/composables/dateParser.ts";
import {hoverListRow} from "@/assets/cssFunctions.ts";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";

defineProps<{
  actions: ActionResponse[] | undefined;
}>()

</script>

<template>
  <slot/>
    <div class="rounded-md border-2  w-full border-secondary">

      <div class="sticky top-0 z-10 mx-0 w-full flex
            bg-secondary px-2 py-2 space-x-2 text-xs font-semibold uppercase  text-comment">
        <span class="w-15/100">Ack update</span>
        <span class="w-10/100">Old sev</span>
        <span class="w-10/100">New sev</span>
        <span class="w-30/100">Comment</span>
        <span class="w-20/100">User</span>
        <span class="w-15/100 text-right pr-2">Date</span>
      </div>
      <ul class="overflow-y-auto overflow-x-hidden  max-h-[20vh] w-full divide-y text-sm">

        <li v-if=" !actions?.length" class="px-4 py-8  text-center text-slate-400 italic">
          No actions recorded yet.
        </li>

        <li v-else
          v-for="action in actions"
          :key="action.id"
          :class="hoverListRow('flex items-center px-2 space-x-2 py-3 transition-colors')"
        >
          <div class="w-15/100">
            <Badge :variant="action.ackUpdate ? 'ack_type' : 'null'">
              {{ action.ackUpdate ?? 'false' }}
            </Badge>
          </div>
          <SeverityDiv class="w-10/100" :severity="action.previousSeverity"/>
          <SeverityDiv class="w-10/100" :severity="action.newSeverity"/>
          <p class="w-30/100 whitespace-break-spaces text-comment" :title="action.message">
            {{ action.message || '—' }}
          </p>
          <p class="w-20/100 font-medium text-comment text-xs">
            {{ action.author }}
          </p>
          <p class="w-15/100 text-right pr-2 text-xs tabular-nums text-date">
            {{ dateParser(action.createdAt).fullDate }}
          </p>
        </li>

      </ul>
    </div>
</template>

