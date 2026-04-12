import {computed, type Ref, ref, toRaw} from "vue";

export function useWrapping<Paginable extends {id: number}>(data: Ref<Paginable[]>) {

  const unwrappedItem = ref<Paginable | null>(null)
  const originalItem = ref<Paginable | null>(null)
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

  const unwrap = (item: Paginable) => {
    originalItem.value = item
    unwrappedItem.value = structuredClone(toRaw(item))
  }

  return { unwrap, unwrappedItem, originalItem, wrap, isUnwrapped, items, indexUnwrapped }
}
