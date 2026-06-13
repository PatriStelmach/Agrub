<script setup lang="ts">
import { Button } from '@/components/ui/button'
import { IconCheck} from "@tabler/icons-vue";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {ref, watchEffect} from "vue";
import CodeEditor from "@/helpers_components/CodeEditor.vue";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";

const props = defineProps<{
  description: string,
  code: string
  isLoading?: boolean
  editable: boolean
}>()

const isCodeDialogOpen = defineModel<boolean>('isCodeDialogOpen')

const newCode = ref<string>(props.code)
const newDescription = ref<string>(props.description)

watchEffect(() => {
  newCode.value = props.code;
  newDescription.value = props.description;
})

const cancel = () => {
  setTimeout(() => {
    newCode.value = props.code;
    newDescription.value = props.description;
  }, 200)
}

const emits = defineEmits<{
  'update:save-changes': [code: string, description: string]
}>()

</script>

<template>
  <Dialog v-model:open="isCodeDialogOpen">
    <form id="plugin_details_form">
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent :show-close-button="false" class="max-w-[60vw]! min-h-1/4 max-h-4/5">
        <DialogHeader>
          <DialogTitle>Plugin details</DialogTitle>

        </DialogHeader>
        <div class="grid gap-4">
          <div class="grid gap-3">
            <Label class="text-lg" for="my-plugin-description">Description</Label>
            <Transition name="fade" mode="out-in">
              <BigLoadingBlock class="h-10" v-if="isLoading" />
              <Input
                v-else-if="editable && !isLoading" class="m-2 badge-focus max-w-95/100 blue-badge-focus"
                id="my_plugin_description"
                name="description"
                v-model="newDescription"
                :default-value="description" />
              <h1 class="border-b-2 border-accent pl-2 pr-8 w-fit" id="show_plugin_description" v-else>{{ description}}</h1>
            </Transition>
          </div>
          <div class="grid gap-3  h-full">
            <Label class="text-lg" for="my-plugin-code">Code</Label>
            <Transition name="fade" mode="out-in">
              <BigLoadingBlock class="h-100" v-if="isLoading"/>
              <CodeEditor
                v-else-if="editable && !isLoading"
                id="my_plugin_code"
                name="code"
                v-model="newCode"
                :default-value="code" />
              <div v-else class="code-area min-h-96 max-h-[50vh] m-2 w-95/100 !">
                <code id="show_plugin_code">
                  {{ code }}
                </code>
              </div>
            </Transition>
          </div>
        </div>
        <DialogFooter>
          <DialogClose as-child>
            <Button
              id="close_plugin_details_dialog"
              @click="cancel"
              variant="red_outline">
              {{ editable ? 'Cancel' : 'Exit' }}
            </Button>
          </DialogClose>
          <DialogClose as-child v-if="editable">
            <Button
              variant="green_outline"
              type="submit"
              @click="emits('update:save-changes', newCode, newDescription)"
            >
              Ok
              <IconCheck />
            </Button>
          </DialogClose>

        </DialogFooter>
      </DialogContent>
    </form>
  </Dialog>
</template>
