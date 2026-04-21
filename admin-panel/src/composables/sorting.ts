import { ref, computed } from 'vue'

export function useSort<T>(data: () => T[], defaultSort: keyof T) {
  const sortKey = ref<keyof T>(defaultSort)
  const sortOrder = ref<'asc' | 'desc'>('desc')

  const severityOrder:  Record<string, number> = {
    'unknown': 0,
    'not classified': 1,
    'ok': 2,
    'information': 3,
    'low': 4,
    'warning': 5,
    'medium': 6,
    'average': 7,
    'high': 8,
    'disaster': 9,
    'critical': 10,
  }
  const sortedData = computed(() => {
    const key = sortKey.value as keyof T
    if (!key) return data()
    return [...data()].sort((a , b) => {
      const aVal = (key === 'severity' && typeof a[key] === 'string') ? severityOrder[a[key].toLowerCase()] : a[key]
      const bVal = (key === 'severity' && typeof b[key] === 'string') ? severityOrder[b[key].toLowerCase()] : b[key]
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
