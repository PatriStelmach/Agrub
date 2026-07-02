import { ref, computed } from 'vue'

export function useClientSort<T>(data: () => T[], defaultSort: keyof T) {
  const sortKey = ref<keyof T>(defaultSort)
  const sortOrder = ref<'asc' | 'desc'>('desc')


  const sortedData = computed(() => {
    const key = sortKey.value as keyof T
    if (!key) return data()
    return [...data()].sort((a , b) => {
      const aVal = a[key]
      const bVal = b[key]
      if (aVal == null) return sortOrder.value === 'asc' ? 1 : -1
      if (bVal == null) return sortOrder.value === 'asc' ? -1 : 1
      if (typeof bVal === 'boolean' && typeof aVal === 'boolean') {
        return sortOrder.value === 'asc' ?
          Number(aVal) - Number(bVal) :
          Number(bVal) - Number(aVal)
      }
      if (aVal < bVal) return sortOrder.value === 'asc' ? -1 : 1
      if (aVal > bVal) return sortOrder.value === 'asc' ? 1 : -1

      return 0
    })
  })

  const toggleSort = (key: string) => {
    if (sortKey.value === key)
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    else {
      sortKey.value = key
      sortOrder.value = 'asc'
    }
  }

  return { sortedData, sortKey, sortOrder, toggleSort }
}
