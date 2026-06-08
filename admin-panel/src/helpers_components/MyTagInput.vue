<script setup lang="ts">
import {tagsContainer, inputText} from "@/assets/cssFunctions.ts";
import {Badge} from "@/components/ui/badge";
import {
  TagsInput,
  TagsInputInput,
  TagsInputItem,
  TagsInputItemDelete,
  TagsInputItemText
} from "@/components/ui/tags-input";
import DialogLabel from "@/helpers_components/DialogLabel.vue";
import {Button} from "@/components/ui/button";
import {IconEye, IconEyeOff, IconTrash} from "@tabler/icons-vue";
import {computed, type HTMLAttributes, onMounted, ref} from "vue";

const props = defineProps<{
  allTags: string[]
  inputId: string
  canAddNew: boolean
  tagsLabel: string
  class?: HTMLAttributes["class"]
}>()
const tags = defineModel<string[]>('tags')

const tagSearch = ref("")
const tagListOpen = ref(false)
const existingTag = computed(() => {
  return tags.value?.includes(tagSearch.value)
})
const availableTags = computed(() => props.allTags )

const matchedTags = computed(() => {
  return availableTags.value
    .filter(t => t.toLowerCase().includes(tagSearch.value.toLowerCase()))
    .filter(t => !tags.value?.includes(t))
})

const addNonexistingTag = () => {
  if(!existingTag.value && tagSearch.value && tags.value) {
    tags.value.push(tagSearch.value)
  }
}

const addTagFromList = () => {
  if(tagSearch.value ) {
    const matchedBadge = matchedTags.value.find(b => b.toLowerCase() === tagSearch.value.toLowerCase())
    if(matchedBadge) tags.value?.push(matchedBadge)
  }
}

const addTagFromBadge = (tag: string) => {
  if (!tags.value?.includes(tag)) {
    tags.value?.push(tag)
  }
}

const addTagToList = () => {
  if(props.canAddNew) addNonexistingTag()
  else addTagFromList()
  clearTagsInput()
}

const removeTagFromList = (tag: string) => {
  tags.value = tags.value?.filter(t => t !== tag)
}

const clearItemTags = () => {
  tags.value = [];
}

const clearTagsInput = () => {
  const input = document.getElementById(props.inputId) as HTMLInputElement
  if (input) input.value = '';
  tagSearch.value = ''
}

const toggleTagListOpen = () => {
  tagListOpen.value = !tagListOpen.value
  clearTagsInput()
}

const handleInputChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  tagSearch.value = target.value;
};

defineExpose({ clearTagsInput})


onMounted(() => {
  tagListOpen.value = false
})

</script>

<template>
  <div :class="props.class">
    <div class="flex mb-2 space-x-2 relative">

      <DialogLabel :text="tagsLabel" :for="inputId" />
      <Button
        type="button"
        @click="toggleTagListOpen" :variant="tagListOpen ? 'red_outline' : 'green_outline'" size="icon-sm">
        <component :is="tagListOpen ? IconEyeOff : IconEye" />
      </Button>
      <Button
        type="button"
        @click="clearItemTags" size="icon-sm" variant="red_outline">
        <IconTrash />
      </Button>
    </div>
    <Transition name="fade-absolute" mode="out-in">
      <div v-if="!tagListOpen"
           class="w-full">
        <Badge variant="tags" class="mr-1 my-1" v-for="(tag, index) in tags" :key="index">
          {{ tag }}
        </Badge>
      </div>
    </Transition>
    <Transition name="fade" class="flex" mode="out-in">
      <TagsInput
        v-if="tagListOpen"
        v-model="tags"
        :class="tagListOpen ? `${inputText} rounded-[0.5rem_0.5rem_0_0]` : `${inputText}`"
        class="w-full min-h-10">
        <TagsInputItem v-for="(tag, index) in tags" :key="index" :value="tag">
          <TagsInputItemText />
          <TagsInputItemDelete class="cursor-pointer hover:text-red-badge" @click.stop.prevent="removeTagFromList(tag)" />
        </TagsInputItem>
        <TagsInputInput :id="inputId" @input="handleInputChange" @keydown.enter.prevent="addTagToList" :placeholder="`${tagsLabel}...`" />
      </TagsInput>
    </Transition>
    <Transition name="fade" mode="out-in">
      <div v-if="tagListOpen && matchedTags.length > 0" :class="tagsContainer">
        <Badge
          variant="tags" class="mr-1 my-1"
          @click="addTagFromBadge(tag)"
          v-for="(tag, index) in matchedTags" :key="index">
          {{ tag }}
        </Badge>
      </div>
      <div v-else-if="tagListOpen" :class="tagsContainer" class="h-10 text-center text-sm! text-comment">
        No matched tags
      </div>
    </Transition>
  </div>
</template>
