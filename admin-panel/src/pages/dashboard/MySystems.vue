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
import {SidebarProvider} from "@/components/ui/sidebar";
import AppSidebar from "@/components/AppSidebar.vue";
</script>

<template>
  <SidebarProvider
    :style=" {
      '--sidebar-width': 'calc(var(--spacing) * 72)',
      '--header-height': 'calc(var(--spacing) * 12)',
    }"
  >
    <AppSidebar variant="inset" />
    <Empty class="bg-background my-4">
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
        <RouterLink to="/add_system"><Button class="cursor-pointer">Add System</Button></RouterLink>
        <Button class="cursor-pointer" variant="outline">
          Add plugin
        </Button>
      </div>
    </EmptyContent>
    <Button variant="link" as-child class="text-muted-foreground" size="sm">
      <a href="#">
        Learn More <ArrowUpRightIcon />
      </a>
    </Button>
  </Empty>
  </SidebarProvider>
</template>
