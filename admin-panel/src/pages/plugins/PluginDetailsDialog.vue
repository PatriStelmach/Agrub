<script setup lang="ts">
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {Textarea} from "@/components/ui/textarea";

const props = defineProps<{
  description: string,
  code: string
}>()

</script>

<template>
  <Dialog>
    <form>
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent >
        <DialogHeader>
          <DialogTitle>Plugin details</DialogTitle>

        </DialogHeader>
        <div class="grid gap-4">
          <div class="grid gap-3">
            <Label for="my-plugin-description">Description</Label>
            <Input id="my-plugin-description" name="description" :default-value="description" />
          </div>
          <div class="grid gap-3">
            <Label for="my-plugin-code">Code</Label>
            <Textarea id="my-plugin-code" name="code" :default-value="code" />
          </div>
        </div>
        <DialogFooter>
          <DialogClose as-child>
            <Button variant="outline">
              Cancel
            </Button>
          </DialogClose>
          <Button type="submit">
            Save changes
          </Button>
        </DialogFooter>
      </DialogContent>
    </form>
  </Dialog>
</template>
