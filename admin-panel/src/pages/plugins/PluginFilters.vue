<script setup lang="ts">
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet'
import { Button } from "@/components/ui/button";
import {
  IconFilterOff,
  IconFilterShare,
  IconFilterX
} from "@tabler/icons-vue";
import DialogLabel from "@/helpers_components/DialogLabel.vue";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {onMounted, reactive, ref, useTemplateRef} from "vue";
import {Language, type LibraryPluginFilters} from "@/types/types.ts";
import MyTagInput from "@/helpers_components/MyTagInput.vue";
import {
  NumberField,
  NumberFieldContent,
  NumberFieldDecrement, NumberFieldIncrement,
  NumberFieldInput
} from "@/components/ui/number-field";
import api from "@/lib/axios.ts";
import { toast } from "vue-sonner";

const emit = defineEmits<{
  'update:filters': [LibraryPluginFilters]
}>()

const tagsRef = useTemplateRef<InstanceType<typeof MyTagInput>>('tagsRef')
const availableTags = ref<string[]>([])
const filters = reactive({
  name: undefined as string | undefined,
  language: undefined as Language | undefined,
  creator: undefined as string | undefined,
  tags: [] as string[],
  maxWeight: undefined as number | undefined,
})

onMounted(async () => {
  try {
    const res = await api.get('/plugins/tags')
    if (res.status === 200) {
      availableTags.value = res.data
    }
    else {
      toast.error(res.data)
    }
  }
  catch (err) {
    toast.error(`Error getting tags:${err}`)
  }
})

const clearFilters = () => {
  filters.name = undefined
  filters.language = undefined
  filters.creator = undefined
  filters.maxWeight = undefined
  filters.tags = []
  tagsRef.value?.clearTagsInput()

}

const onCancel = () => {
  setTimeout(() => {
    clearFilters()
    onSubmit()
  }, 200)
}

const onSubmit = () => {
  emit("update:filters", {
    name: filters.name,
    language: filters.language,
    creator: filters.creator,
    tags: filters.tags,
    maxWeight: filters.maxWeight
  })
}

const getLangStyles = (lang: string) => {
  const styles: Record<string, string> = {
    python: 'from-[#3475A8]/60 to-[#F9CA3A]/60 focus:from-[#3475A8]/80 focus:to-[#F9CA3A]/80',
    powershell: 'from-[#4374CC]/60 to-[#4476CF]/60 focus:from-[#4476CF]/80 focus:to-[#4374CC]/80',
    powershell_module: 'from-[#4374CC]/60 to-[#4476CF]/60 focus:from-[#4476CF]/80 focus:to-[#4374CC]/80',
    bash: 'from-[#222B32]/60 to-[#4CA825]/50 focus:from-[#222B32]/60 focus:to-[#4CA825]/80',
    sh: 'from-[#222B32]/60 to-[#4CA825]/50 focus:from-[#222B32]/60 focus:to-[#4CA825]/80',
  };
  return styles[lang.toLowerCase()]
};
</script>

<template>
  <Sheet>
    <SheetTrigger as-child>
      <slot />
    </SheetTrigger>
    <SheetContent
      side="left">
      <div class="w-full">
        <SheetHeader>
          <SheetTitle>Filters</SheetTitle>
          <SheetDescription>Set search filters for plugins library</SheetDescription>
          <Button @click="clearFilters" variant="red_outline" class="text-md *:size-5!">
            Clear Filters <IconFilterOff />
          </Button>
        </SheetHeader>

        <div class="grid space-y-3 pl-4">
          <div>
            <DialogLabel text="Name" for="name-input" />
            <Input placeholder="Name contains..." v-model="filters.name" type="text" class="w-3/4" id="name-input" />
          </div>

          <div>
            <DialogLabel text="Creator" for="creator-input" />
            <Input placeholder="Creator contains..." v-model="filters.creator" type="text" class="w-3/4" id="creator-input" />
          </div>

          <div>
            <DialogLabel text="Language" for="language-select" />
            <Select v-model="filters.language">
              <SelectTrigger id="language-select" class="cursor-pointer w-2/5">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem
                  v-for="lang in Object.keys(Language)"
                  :key="lang"
                  :value="Language[lang as keyof typeof Language]"
                  class="bg-linear-to-bl rounded-none cursor-pointer  py-2 my-1"
                  :class="getLangStyles(lang)"
                >
                  {{ lang }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <MyTagInput
            ref="tagsRef"
            v-model:tags="filters.tags"
            class="w-3/4"
            :all-tags="availableTags"
            input-id="tags-input"
            :can-add-new="false"
            tags-label="Tags"
          />
          <div class="w-3/4 flex space-x-2">
            <NumberField
              id="max-weight"
              :default-value="0"
              :min="0"
              v-model="filters.maxWeight"
            >
              <DialogLabel text="Maximum Weight" for="max-weight" />
              <NumberFieldContent>
                <NumberFieldDecrement/>
                <NumberFieldInput/>
                <NumberFieldIncrement/>
              </NumberFieldContent>
            </NumberField>
            <span class="text-comment text-sm mb-2 mt-auto">KB</span>
          </div>
        </div>
      </div>

      <SheetFooter>
        <SheetClose as-child>
          <Button
            @click.stop="onSubmit"
            class="text-md" type="button" variant="green_outline">
            Submit <IconFilterShare class=" size-5" />
          </Button>
        </SheetClose>
        <SheetClose as-child>
          <Button class="text-md" @click="onCancel" variant="red_outline">
            Cancel <IconFilterX class="size-5" />
          </Button>
        </SheetClose>
      </SheetFooter>
    </SheetContent>
  </Sheet>
</template>
