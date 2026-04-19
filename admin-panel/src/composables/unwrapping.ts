import {computed, type Ref, ref, toRaw} from "vue";

export function useWrapping<T extends {id: number}>(data: Ref<T[]>) {

  const unwrappedItem = ref<T | null>(null)
  const originalItem = ref<T | null>(null)
  const items = computed(() =>  data.value)

  const indexUnwrapped = computed(() => {
    return data.value.findIndex(i => i.id === originalItem.value.id)
  })

  const isUnwrapped = (id: number) => {
    return unwrappedItem.value?.id === id
  }

  const wrap = (save: boolean) => {
    if (save && unwrappedItem.value && originalItem.value) {
      // tu request zamiast assign
      Object.assign(originalItem.value, unwrappedItem.value)
    }
    originalItem.value = null
    unwrappedItem.value = null
  }

  const unwrap = (id: number) => {
    originalItem.value = items.value.find(p => p.id === id)
    unwrappedItem.value = structuredClone(toRaw(originalItem.value))
  }

  return { unwrap, unwrappedItem, originalItem, wrap, isUnwrapped, items, indexUnwrapped }
}
