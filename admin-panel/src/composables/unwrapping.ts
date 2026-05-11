import {type Ref, ref, toRaw} from "vue";
import type {ApiResponse} from "@/types/types.ts";

export function useWrapping<T extends object>(
  data: Ref<T[]>,
  key: keyof T = 'id' as keyof T
) {

  const unwrappedItem = ref<T | null>(null)
  const originalItem = ref<T | null>(null)


  const isUnwrapped = (keyValue: string | number | null) => {
    return unwrappedItem.value?.[key] === keyValue
  }

  const wrap = () => {
    if (unwrappedItem.value && originalItem.value) {
      Object.assign(originalItem.value, toRaw(unwrappedItem.value))  // cofnij zmiany
    }
    originalItem.value = null
    unwrappedItem.value = null
  }

  const save = (request: () => void) => {
    request()
    wrap()
  }

  const unwrap = (keyValue: string | number) => {
    originalItem.value = data.value.find(p => p?.[key] === keyValue )
    unwrappedItem.value = structuredClone(toRaw(originalItem.value))
  }

  return { unwrap, unwrappedItem, originalItem, wrap, isUnwrapped, save }
}
