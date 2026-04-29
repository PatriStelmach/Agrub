import {computed, type Ref, ref} from "vue"

export function useBadgeFilter<T>(
  item: Ref<T | null>,
  badges: string[],
  filter: (i: T) => string[]
){
  const unwrappedItem = computed(() => item);
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
      return availableBadges.value.filter(t => t.includes(badgeSearch.value.toLowerCase()))
      .filter(t => !filter(item.value!).includes(t))
    else return []
  })
  const addBadge = () => {
    if(unwrappedItem.value && !existingBadge.value && badgeSearch.value && item.value) {
      filter(item.value).push(badgeSearch.value)
      badgeSearch.value = ''
    }
  }

  return {
    badgeSearch,
    availableBadges,
    badgeListOpen,
    matchedBadges,
    existingBadge,
    addBadge,
  }

}


