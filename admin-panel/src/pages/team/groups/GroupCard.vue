<script setup lang="ts">
import {Card, CardDescription, CardHeader} from "@/components/ui/card";
import type {UserGroupStats} from "@/types/types.ts";
import {gridCard} from "@/assets/cssFunctions.ts";
import {IconFilterCog, IconUserCog} from "@tabler/icons-vue";
import {Badge} from "@/components/ui/badge";

defineProps<{
  group: UserGroupStats,
}>()

</script>
<template>
  <RouterLink :to="`/groups/edit_group/${group.id}/${group.name}`">
    <Card
      :id="`group_${group.name}_card`"
      class="cursor-pointer"
      :class="gridCard"
    >
      <CardHeader class="font-bold mx-8 text-center border-b-2 border-blue-badge/50 italic">
        {{ group.name }}
      </CardHeader>
      <CardDescription class="*:flex *:gap-x-2 *:items-center *:justify-center space-y-2 px-3">
      <span>
        <IconFilterCog />
        <span class="max-lg:text-md lg:text-lg 2xl:text-xl">Rules configurations:</span>
        <Badge variant="number">{{ group.ruleCount}}</Badge>
      </span>
        <span>
        <IconUserCog />
        <span class="max-lg:text-md lg:text-lg 2xl:text-xl">Assigned users:</span>
        <Badge variant="number">{{ group.userCount}}</Badge>
      </span>
      </CardDescription>
    </Card>
  </RouterLink>
</template>

