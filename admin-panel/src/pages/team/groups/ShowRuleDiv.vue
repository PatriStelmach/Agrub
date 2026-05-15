<script setup lang="ts">
import { Badge } from "@/components/ui/badge";
import {IconLoader, IconTrash} from "@tabler/icons-vue";
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
          <span class="text-comment w-32 shrink-0">Source pattern</span>
          <span class=" font-medium truncate">{{ rule.sourcePattern || '—' }}</span>
          <Badge variant="ack_type" class="ml-auto text-xs">{{ rule.sourceType }}</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-32 shrink-0">Content pattern</span>
          <span class=" font-medium truncate">{{ rule.contentPattern || '—' }}</span>
          <Badge variant="ack_type" class="ml-auto text-xs">{{ rule.contentType }}</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-32 shrink-0">Subject pattern</span>
          <span class=" font-medium truncate">{{ rule.subjectPattern || '—' }}</span>
          <Badge variant="ack_type" class="ml-auto text-xs">{{ rule.subjectMatchType }}</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-32 shrink-0">Origin pattern</span>
          <span class=" font-medium truncate">{{ rule.originPattern || '—' }}</span>
          <Badge variant="ack_type" class="ml-auto text-xs">{{ rule.originMatchType }}</Badge>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-32 shrink-0">Min severity</span>
          <span class="font-medium">{{ rule.minSeverity }}</span>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-comment w-32 shrink-0">Play sound</span>
          <span class="font-medium">{{ rule.playSound ? 'Yes' : 'No' }}</span>
          <Button @click="$emit('delete-rule', rule.id)" variant="red_outline" class="ml-auto" size="icon-sm">
            <IconLoader v-if="isRuleDeleting" class="animate-spin"/>
            <IconTrash v-else />
          </Button>

        </div>

      </div>
    </div>

  </div>
</template>
