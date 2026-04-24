import { ref, computed, watch } from "vue"
import {useDebounceFn} from "@vueuse/core";

export function useSearchFilter<T>(data: () => T[], filter: (item: T) => string) {
  const searchFilter = ref<string>('')
  const debounceFilter = ref<string>('')
  const currentPage = ref(1)
  const tableData = ref<T[]>(data())

  const filteredData = computed(() => {
    if(!debounceFilter.value) {
      return data();
    }
    return data().filter((item) => filter(item).toLowerCase().includes(debounceFilter.value.toLowerCase()))
  })

  const updateFilter = useDebounceFn((filter: string)=> {
    debounceFilter.value = filter
  }, 300)

  watch(searchFilter, (val) => {
    currentPage.value = 1
    updateFilter(val)
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
