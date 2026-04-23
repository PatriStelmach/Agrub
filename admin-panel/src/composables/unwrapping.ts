import {type Ref, ref, toRaw} from "vue";
import type {ApiResponse} from "@/types/types.ts";

export function useWrapping<T extends object>(
  data: Ref<T[]>,
  key: keyof T = 'id' as keyof T
) {

  const unwrappedItem = ref<T | null>(null)
  const originalItem = ref<T | null>(null)


  const isUnwrapped = (keyValue: string | number) => {
    return unwrappedItem.value?.[key] === keyValue
  }

  const wrap = (save: boolean , message?: string) => {
    if (save && unwrappedItem.value && originalItem.value) {
      // tu request zamiast assign
      Object.assign(originalItem.value, unwrappedItem.value)
      return message
    }
    originalItem.value = null
    unwrappedItem.value = null
  }

  const unwrap = (keyValue: string | number) => {
    originalItem.value = data.value.find(p => p?.[key] === keyValue )
    unwrappedItem.value = structuredClone(toRaw(originalItem.value))
  }

  return { unwrap, unwrappedItem, originalItem, wrap, isUnwrapped }
}
