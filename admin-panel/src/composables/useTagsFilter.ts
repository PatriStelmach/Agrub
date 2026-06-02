import {computed, type Ref, ref} from "vue"

export function useTagsFilter(
  // wszystkie dostępne tagi
  allTags: string[],
  // wszystkie tagi obiektu
  tags: Ref<string[] | undefined>,
  // id inputu
  inputId: string,
  // czy można dodać nowe tagi
  canAddNew: boolean
){
  const tagSearch = ref("")
  const tagListOpen = ref(false)
  const existingTag = computed(() => {
      return tags.value?.includes(tagSearch.value)
  })
  const availableTags = computed(() => allTags )

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
    if(canAddNew) addNonexistingTag()
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
    const input = document.getElementById(inputId) as HTMLInputElement
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

  return {
    tagSearch,
    availableTags,
    tagListOpen,
    matchedTags,
    existingTag,
    addTagToList,
    addTagFromBadge,
    addNonexistingTag,
    addTagFromList,
    toggleTagListOpen,
    clearItemTags,
    clearTagsInput,
    handleInputChange,
    removeTagFromList
  }

}


