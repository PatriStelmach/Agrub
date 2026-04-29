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
import {ref, watch} from "vue";
import CodeEditor from "@/helpers/CodeEditor.vue";

const props = defineProps<{
  description: string,
  code: string
}>()

const newCode = ref<string>(props.code)
const newDescription = ref<string>(props.description)

watch(() => [props.code, props.description], ([nextCode, nextDesc]) => {
  newCode.value = nextCode;
  newDescription.value = nextDesc;
});


const emits = defineEmits<{
  'update:save-changes': [code: string, description: string]
}>()

</script>

<template>
  <Dialog>
    <form>
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent class="max-w-[60vw]! min-h-1/4 max-h-4/5">
        <DialogHeader>
          <DialogTitle>Plugin details</DialogTitle>

        </DialogHeader>
        <div class="grid gap-4">
          <div class="grid gap-3">
            <Label for="my-plugin-description">Description</Label>
            <Input class="m-2 badge-focus max-w-95/100" id="my-plugin-description" name="description" v-model="newDescription" :default-value="description" />
          </div>
          <div class="grid gap-3  h-full">
            <Label for="my-plugin-code">Code</Label>
            <CodeEditor  id="my-plugin-code" name="code" v-model="newCode" :default-value="code" />
          </div>
        </div>
        <DialogFooter>
          <DialogClose as-child>
            <Button variant="outline">
              Cancel
            </Button>
          </DialogClose>
          <DialogClose>
            <Button
              type="submit"
              @click="$emit('update:save-changes', newCode, newDescription)"
            >
              Save changes
            </Button>
          </DialogClose>

        </DialogFooter>
      </DialogContent>
    </form>
  </Dialog>
</template>
