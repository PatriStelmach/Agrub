import { ref, computed, watch } from "vue"

export function useSearchFilter<T>(data: () => T[], filter: (item: T) => string) {
  const searchFilter = ref<string>('')
  const currentPage = ref(1)
  const tableData = ref<T[]>(data())

  const filteredData = computed(() => {
    if(!searchFilter.value) {
      return data();
    }
    return data().filter((item) => filter(item).toLowerCase().includes(searchFilter.value.toLowerCase()))
  })

  watch(searchFilter, () => {
    currentPage.value = 1
  })

  const updateData = (data:T[]) => {
    tableData.value = data as T[]
  }
  const updateSearchData = (data: string) => {
    searchFilter.value = data.trim()
  }

  const updatePage = (page: number) => {
    currentPage.value = page
  }

  return {
    searchFilter,
    currentPage,
    tableData,
    filteredData,
    updateData,
    updateSearchData,
    updatePage,
  }
}
