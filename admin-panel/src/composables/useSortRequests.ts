import { ref } from 'vue'

export function useSortRequests<T>(data: () => T[], defaultSort: keyof T) {
  const sortKey = ref<keyof T>(defaultSort)
  const sortOrder = ref<'asc' | 'desc'>('desc')


  const toggleSort = (key: string) => {
    if (sortKey.value === key)
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    else {
      sortKey.value = key
      sortOrder.value = 'asc'
    }
  }

  return { sortKey, sortOrder, toggleSort }
}
