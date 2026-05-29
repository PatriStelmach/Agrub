<script setup lang="ts">
import { Badge } from "@/components/ui/badge";
import {IconEdit, IconLoader, IconTrash} from "@tabler/icons-vue";
import { type Rule } from "@/types/types.ts";
import {Button} from "@/components/ui/button";

const props = defineProps<{
  rule: Rule
  isRuleDeleting: boolean
}>()


</script>

<template>
  <div class="border">
    <div class="p-4">
      <div class="grid grid-cols-2 gap-x-6 gap-y-2 text-sm">

        <div class="flex items-center gap-2">
          <span class="text-comment w-34 shrink-0">Source pattern:</span>
          <span class=" font-medium truncate">{{ rule.sourcePattern || '—' }}</span>
          <Badge v-if="rule.sourcePattern" variant="ack_type" class="ml-auto text-xs">{{ rule.sourceType }}</Badge>
          <Badge v-else variant="null" class="ml-auto text-xs">EMPTY</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-34 shrink-0">Content pattern:</span>
          <span class=" font-medium truncate">{{ rule.contentPattern || '—' }}</span>
          <Badge v-if="rule.contentPattern" variant="ack_type" class="ml-auto text-xs">{{ rule.contentType }}</Badge>
          <Badge v-else variant="null" class="ml-auto text-xs">EMPTY</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-34 shrink-0">Subject pattern:</span>
          <span class=" font-medium truncate">{{ rule.subjectPattern || '—' }}</span>
          <Badge v-if="rule.subjectPattern" variant="ack_type" class="ml-auto text-xs">{{ rule.subjectMatchType }}</Badge>
          <Badge v-else variant="null" class="ml-auto text-xs">EMPTY</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-34 shrink-0">Origin pattern:</span>
          <span class=" font-medium truncate">{{ rule.originPattern || '—' }}</span>
          <Badge v-if="rule.originPattern" variant="ack_type" class="ml-auto text-xs">{{ rule.originMatchType }}</Badge>
          <Badge v-else variant="null" class="ml-auto text-xs">EMPTY</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-34 shrink-0">Min severity:</span>
          <span class="font-medium">{{ rule.minSeverity }}</span>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-34 shrink-0">Play sound:</span>
          <span class="font-medium">{{ rule.playSound ? 'Yes' : 'No' }}</span>
          <Button
            @click="$emit('unwrap', rule.id)"
            class="ml-auto"
            variant="green_outline"
            size="icon-sm"
          >
            <IconEdit />
          </Button>
          <Button @click="$emit('delete-rule', rule.id)" variant="red_outline" size="icon-sm">
            <IconLoader v-if="isRuleDeleting" class="animate-spin"/>
            <IconTrash v-else />
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>
