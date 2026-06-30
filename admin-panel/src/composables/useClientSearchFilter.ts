import { ref, computed, watch } from "vue"
import {useDebounceFn} from "@vueuse/core";

export function useClientSearchFilter<T>(data: () => T[], filter: (item: T) => string) {
  const searchFilter = ref<string>('')
  const debounceFilter = ref<string>('')
  const currentPage = ref(1)
  const tableData = ref<T[]>(data())
  const pageSize = ref<number>(100)

  const filteredData = computed(() => {
    if(!debounceFilter.value) {
      return data();
    }
    return data().filter((item) => filter(item).toLowerCase().includes(debounceFilter.value.toLowerCase()))
  })

  const updateFilter = useDebounceFn((filter: string)=> {
    debounceFilter.value = filter
  }, 30)

  const updateData = (data:T[]) => {
    tableData.value = data
  }
  const updateSearchData = (data: string) => {
    searchFilter.value = data.trim()
  }

  watch(searchFilter, (val) => {
    currentPage.value = 1
    updateFilter(val)
  })

  watch(pageSize, () => {
    currentPage.value = 1
  })

  return {
    searchFilter,
    currentPage,
    tableData,
    filteredData,
    debounceFilter,
    updateData,
    updateSearchData,
    pageSize,
  }
}
