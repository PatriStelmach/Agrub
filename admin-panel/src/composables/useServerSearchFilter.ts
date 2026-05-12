import {ref, watch} from "vue";

export function useServerSearchFilter<T, F>
(getItemsRequest: () => void, emptyFilters: F, sortKey: string, sortOrder: string) {
  const items = ref<T[]>([])
  const searchFilter = ref<string | null>(null);
  const pageSize = ref<number>(50);
  const currentPage = ref<number>(1);
  const totalElements = ref<number>(0);
  const sortedHead = ref<{ sortKey: string; sortOrder: string }>({
    sortKey: sortKey,
    sortOrder: sortOrder
  })
  const filters = ref<F>(emptyFilters)

  const updateFilters = async (data: F) => {
    filters.value = data
    currentPage.value = 1
    getItemsRequest()
  }
  watch([currentPage, pageSize], async () => {
    getItemsRequest()
  })

  watch([() => sortedHead.value.sortKey, () => sortedHead.value.sortOrder], async () => {
    currentPage.value = 1
    getItemsRequest()
  })

  return {
    items,
    searchFilter,
    pageSize,
    currentPage,
    totalElements,
    sortedHead,
    filters,
    updateFilters,
  }
}

