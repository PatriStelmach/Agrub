import {type ComputedRef, type Ref, ref, toRaw} from "vue";
import {toast} from "vue-sonner";

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

// Dodajemy async przed definicją funkcji oraz zmieniamy typ requestu na Promise
  const save = async (request: () => Promise<string | number |void> | void) => {
    const hasChanged = JSON.stringify(toRaw(unwrappedItem.value)) !== JSON.stringify(toRaw(originalItem.value))
    if (hasChanged) {
      await request()
    } else {
      toast.message('No changes have been made')
    }
    wrap()
  }

  const unwrap = (keyValue: string | number) => {
    originalItem.value = data.value.find(p => p?.[key] === keyValue )
    unwrappedItem.value = structuredClone(toRaw(originalItem.value))
  }

  return { unwrap, unwrappedItem, originalItem, wrap, isUnwrapped, save }
}
