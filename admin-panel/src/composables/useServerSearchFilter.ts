import {ref, watch} from "vue";

export function useServerSearchFilter<T, F>
(getItemsRequest: () => Promise<void>, emptyFilters: F, sortKey: string, sortOrder: string) {
  const items = ref<T[]>([])
  const searchFilter = ref<string | null>(null);
  const pageSize = ref<number>(100);
  const currentPage = ref<number>(1);
  const totalElements = ref<number>(0);
  const isLoading = ref(true);
  const sortedHead = ref<{ sortKey: string; sortOrder: string }>({
    sortKey: sortKey,
    sortOrder: sortOrder
  })
  const filters = ref<F>(emptyFilters)

  const wrappedRequest = async () => {
    isLoading.value = true
    await getItemsRequest().finally(() => isLoading.value = false)
  }

  const updateFilters = async (data: F) => {
    filters.value = data
    currentPage.value = 1
    await wrappedRequest()
  }

  watch([currentPage, pageSize], async () => {
    await wrappedRequest()
  })

  watch([() => sortedHead.value.sortKey, () => sortedHead.value.sortOrder], async () => {
    currentPage.value = 1
    await wrappedRequest()
  })
  return {
    items,
    searchFilter,
    pageSize,
    currentPage,
    totalElements,
    sortedHead,
    filters,
    isLoading,
    updateFilters,
  }
}

