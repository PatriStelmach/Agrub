// composables/useSort.ts
import { ref, computed } from 'vue'

export function useSort<Paginable>(data: () => Paginable[], defaultSort: keyof Paginable) {
  const sortKey = ref<keyof Paginable>(defaultSort)
  const sortOrder = ref<'asc' | 'desc'>('desc')

  const sortedData = computed(() => {
    const key = sortKey.value as keyof Paginable
    if (!key) return data()
    return [...data()].sort((a , b) => {
      const aVal = a[key]
      const bVal = b[key]
      if (aVal == null) return sortOrder.value === 'asc' ? 1 : -1
      if (bVal == null) return sortOrder.value === 'asc' ? -1 : 1
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
