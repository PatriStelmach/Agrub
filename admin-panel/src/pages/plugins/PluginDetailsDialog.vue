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
  arguments: string
  isLoading?: boolean
  editable: boolean
}>()

const isCodeDialogOpen = defineModel<boolean>('isCodeDialogOpen')

const newCode = ref<string>(props.code)
const newDescription = ref<string>(props.description)
const newArguments = ref<string>(props.arguments)

watchEffect(() => {
  newCode.value = props.code
  newDescription.value = props.description
  newArguments.value = props.arguments
})

const cancel = () => {
  setTimeout(() => {
    newCode.value = props.code
    newDescription.value = props.description
    newArguments.value = props.arguments
  }, 200)
}

const emits = defineEmits<{
  'update:save-changes': [code: string, description: string, arguments: string]
}>()

</script>

<template>
  <Dialog v-model:open="isCodeDialogOpen">
    <div>
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent :show-close-button="false" class="max-w-[60vw]! min-h-1/4 max-h-[90vh]">
        <DialogHeader>
          <DialogTitle>Plugin details</DialogTitle>

        </DialogHeader>
        <div class="grid gap-4">
          <div class="grid grid-cols-3">
            <div class="col-span-1 gap-3">
              <Label class="text-lg" for="my-plugin-description">Description</Label>
              <h2 class="text-sm text-comment">Description of your plugin</h2>
              <Transition name="fade" mode="out-in">
                <BigLoadingBlock class="h-10" v-if="isLoading" />
                <Input
                  v-else-if="editable && !isLoading" class="my-2 text-xs! badge-focus max-w-95/100 blue-badge-focus"
                  id="my_plugin_description"
                  name="description"
                  v-model="newDescription"
                  :default-value="description" />
                <h1 class="border-b-2 border-accent pl-2 pr-8 w-fit" id="show_plugin_description" v-else>{{ description}}</h1>
              </Transition>
            </div>
            <div class="col-span-2 gap-3">
              <Label class="text-lg" for="my_plugin_arguments">Arguments</Label>
              <h2 class="text-sm text-comment">Set execute arguments for script
                Use space between arguments: "arg1 arg2 arg3"
              </h2>
              <Transition name="fade" mode="out-in">
                <BigLoadingBlock class="h-10" v-if="isLoading"/>
                <Input
                  v-else-if="editable && !isLoading" class="my-2 text-xs! badge-focus max-w-95/100 blue-badge-focus"
                  id="my_plugin_arguments"
                  name="arguments"
                  v-model="newArguments"
                  :default-value="arguments" />
              </Transition>
            </div>
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
              <div v-else class="code-area min-h-96 max-h-[50vh] my-2 w-95/100 !">
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
              id="save_plugin_details"
              variant="green_outline"
              type="submit"
              @click="emits('update:save-changes', newCode, newDescription, newArguments)"
            >
              Ok
              <IconCheck />
            </Button>
          </DialogClose>

        </DialogFooter>
      </DialogContent>
    </div>
  </Dialog>
</template>
