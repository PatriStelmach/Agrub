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
import TopH1Div from "@/helpers_components/TopH1Div.vue";
</script>

<template>
  <div>


  <TopH1Div h1="Connected systems"/>
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
