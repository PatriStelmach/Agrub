import {type ComputedRef, type Ref, ref, toRaw} from "vue";

export function useWrapping<T extends object>(
  data: Ref<T[]> | ComputedRef<T[]>,
  key: keyof T = 'id' as keyof T
) {

  const unwrappedItem = ref<T | null>(null)
  const originalItem = ref<T | null>(null)

  const isUnwrapped = (keyValue: string | number | null) => {
    return unwrappedItem.value?.[key] === keyValue
  }

  const wrap = () => {
    originalItem.value = null
    unwrappedItem.value = null
  }

  const hasChanged = () => {
    return JSON.stringify(toRaw(unwrappedItem.value)) !== JSON.stringify(toRaw(originalItem.value))
  }

  const unwrap = (keyValue: string | number) => {
    originalItem.value = data.value.find(p => p?.[key] === keyValue )
    unwrappedItem.value = structuredClone(toRaw(originalItem.value))
  }
  return {hasChanged, unwrap, unwrappedItem, wrap, isUnwrapped, originalItem }
}
