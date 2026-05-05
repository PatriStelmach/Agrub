import {computed, type Ref, ref} from "vue"

export function useBadgeFilter<T>(
  item: Ref<T | null>,
  badges: string[],
  filter: (i: T) => string[]
){
  const badgeSearch = ref("")
  const badgeListOpen = ref(false)
  const existingBadge = computed(() => {
    if(item.value)
      return filter(item.value).includes(badgeSearch.value)
    else return []
  })
  const availableBadges = computed(() => badges )

  const matchedBadges = computed(() => {
    if(item.value)
      return availableBadges.value.filter(t => t.toLowerCase().includes(badgeSearch.value.toLowerCase()))
      .filter(t => !filter(item.value!).includes(t))
    else return []
  })
  const addNonExistingBadge = () => {
    if(!existingBadge.value && badgeSearch.value && item.value) {
      filter(item.value).push(badgeSearch.value)
      badgeSearch.value = ''
    }
  }

  const addExistingBadge = () => {
    if(badgeSearch.value && item.value) {
      const matchedBadge = matchedBadges.value.find(b => b.toLowerCase() === badgeSearch.value.toLowerCase())
      if(matchedBadge) filter(item.value).push(matchedBadge)
      badgeSearch.value = ''
    }
  }

  return {
    badgeSearch,
    availableBadges,
    badgeListOpen,
    matchedBadges,
    existingBadge,
    addNonExistingBadge,
    addExistingBadge
  }

}


