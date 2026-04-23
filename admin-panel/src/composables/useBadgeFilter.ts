import {computed, ref} from "vue";

export function useBadgeFilter<T>(item: T, badges: string[], filter: (item: T) => string[]){
  const unwrappedItem = computed(() => item )
  const badgeSearch = ref("")
  const badgeListOpen = ref(false)
  const existingBadge = computed(() =>  filter(item).includes(badgeSearch.value))
  const availableBadges = computed(() => badges )
  const matchedBadges = computed(() => availableBadges.value.filter(t => t.includes(badgeSearch.value))
    .filter(t => filter(item).includes(t)))
  const addBadge = () => {
    if(unwrappedItem.value && !existingBadge.value && badgeSearch.value) {
      filter(item).push(badgeSearch.value)
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

