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
import DialogLabel from "@/helpers/DialogLabel.vue";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import { reactive } from "vue";
import {Language, type LibraryPluginFilters} from "@/types/types.ts";

const emit = defineEmits<{
  'update:filters': [LibraryPluginFilters]
}>()

const filters = reactive({
  name: undefined as string | undefined,
  language: undefined as Language | undefined,
  creator: undefined as string | undefined,
})

const clearFilters = () => {
  filters.name = undefined
  filters.language = undefined
  filters.creator = undefined
}

const onCancel = () => {
  setTimeout(() => {
    clearFilters()
  }, 200)
}

const onSubmit = () => {
  emit("update:filters", {
    name: filters.name,
    language: filters.language,
    creator: filters.creator,
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
  return styles[lang.toLowerCase()] || 'from-gray-500 to-gray-700';
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
                  class="bg-linear-to-bl rounded-none cursor-pointer  py-3 my-1"
                  :class="getLangStyles(lang)"
                >
                  {{ lang }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      <SheetFooter>
        <SheetClose as-child>
          <Button
            @click.stop="onSubmit"
            class="text-md" type="button" variant="outline">
            Submit <IconFilterShare class="text-green-badge size-5" />
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
