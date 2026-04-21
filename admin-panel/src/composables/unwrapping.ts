import {computed, type Ref, ref, toRaw} from "vue";

export function useWrapping<T>(data: Ref<T[]>) {

  const unwrappedItem = ref<T | null>(null)
  const originalItem = ref<T | null>(null)
  const items = computed(() =>  data.value)


  const isUnwrapped = (i: T) => {
    return unwrappedItem.value === i
  }

  const wrap = (save: boolean) => {
    if (save && unwrappedItem.value && originalItem.value) {
      // tu request zamiast assign
      Object.assign(originalItem.value, unwrappedItem.value)
    }
    originalItem.value = null
    unwrappedItem.value = null
  }

  const unwrap = (i: T) => {
    originalItem.value = items.value.find(p => p === i )
    unwrappedItem.value = structuredClone(toRaw(originalItem.value))
  }

  return { unwrap, unwrappedItem, originalItem, wrap, isUnwrapped, items }
}
