<script setup lang="ts">
import {Card, CardDescription, CardHeader} from "@/components/ui/card";
import type {UserGroup, UserGroupStats} from "@/types/types.ts";
import {gridCard} from "@/assets/cssFunctions.ts";
import {IconFilterCog, IconUserCog} from "@tabler/icons-vue";
import {Badge} from "@/components/ui/badge";

defineProps<{
  group: UserGroupStats,
}>()

</script>
<template>
  <Card
    @click="$router.push(`/groups/edit_group/${group.id}`)"
    class="cursor-pointer"
    :class="gridCard"
  >
    <CardHeader class="font-bold mx-8 text-center border-b-2 border-blue-badge/50 italic">
      {{ group.name }}
    </CardHeader>
    <CardDescription class="*:flex *:gap-x-2 *:items-center flex justify-between px-3">
      <span>
        <IconFilterCog />
        <span class="text-lg 2xl:text-xl">Rules:</span>
        <Badge variant="number">{{ group.ruleCount}}</Badge>
      </span>
      <span>
        <IconUserCog />
        <span class="text-lg 2xl:text-xl">Assigned users:</span>
        <Badge variant="number">{{ group.userCount}}</Badge>
      </span>
    </CardDescription>
  </Card>
</template>

