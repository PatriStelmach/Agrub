<script setup lang="ts">
import { ArrowUpRightIcon, FolderCode } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty'
import {topButtonGroup, topDiv, topH1} from "@/assets/cssFunctions.ts";
import {ButtonGroup} from "@/components/ui/button-group";
import GoBackButton from "@/helpers/GoBackButton.vue";
</script>

<template>
  <div>


  <div :class="topDiv">
    <h1 :class="topH1">Connected systems</h1>

    <ButtonGroup :class="topButtonGroup">
      <GoBackButton/>
    </ButtonGroup>
  </div>
    <Empty>
    <EmptyHeader>
      <EmptyMedia variant="icon">
        <FolderCode />
      </EmptyMedia>
      <EmptyTitle>No connected systems yet</EmptyTitle>
      <EmptyDescription>
        You haven't added any systems yet. Get started by connecting your first
        API.
      </EmptyDescription>
    </EmptyHeader>
    <EmptyContent>
      <div class="flex gap-2">
        <RouterLink to="/all_systems"><Button class="cursor-pointer">Add System</Button></RouterLink>
        <RouterLink to="/plugins_library">
          <Button class="cursor-pointer" variant="outline">
            Add plugin
          </Button>
        </RouterLink>
      </div>
    </EmptyContent>
    <Button variant="link" as-child class="text-muted-foreground" size="sm">
      <a href="#">
        Learn More <ArrowUpRightIcon />
      </a>
    </Button>
  </Empty>
  </div>
</template>
