import {ref} from "vue";

export function useWrapping() {

  const unwrappedItem = ref()
  const isUnwrapped = (id: number) => {
    return unwrappedItem.value === id
  }

  const wrap = () => {
    unwrappedItem.value = undefined
  }

  const unwrap = (id: number) => {
    console.log('unwrap', id)
    unwrappedItem.value = id
  }



  return { unwrap, unwrappedItem, wrap, isUnwrapped }
}
