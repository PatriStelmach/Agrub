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
import { useTagsFilter} from "@/composables/useTagsFilter.ts";
import DialogLabel from "@/helpers/DialogLabel.vue";
import {Button} from "@/components/ui/button";
import {IconEye, IconEyeOff, IconTrash} from "@tabler/icons-vue";
import {type HTMLAttributes, onMounted} from "vue";

const props = defineProps<{
  allTags: string[]
  inputId: string
  canAddNew: boolean
  tagsLabel: string
  class?: HTMLAttributes["class"]
}>()
const tags = defineModel<string[]>('tags')

const {
  tagListOpen,
  matchedTags,
  addTagToList,
  clearTagsInput,
  toggleTagListOpen,
  clearItemTags,
  handleInputChange,
  removeTagFromList,
  addTagFromBadge
} = useTagsFilter(
  props.allTags,
  tags,
  props.inputId,
  props.canAddNew
)
defineExpose({ clearTagsInput})


onMounted(() => {
  tagListOpen.value = false
})

</script>

<template>
  <div class="flex mb-2 space-x-2 relative" :class="props.class">

    <DialogLabel :text="tagsLabel" :for="inputId" />
    <Button @click="toggleTagListOpen" :variant="tagListOpen ? 'red_outline' : 'green_outline'" size="icon-sm">
      <component :is="tagListOpen ? IconEyeOff : IconEye" />
    </Button>
    <Button @click="clearItemTags" size="icon-sm" variant="red_outline">
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
          <TagsInputInput :id="inputId" @input="handleInputChange" @keydown.enter="addTagToList" :placeholder="`${tagsLabel}...`" />
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

</template>

<style scoped>

</style>
