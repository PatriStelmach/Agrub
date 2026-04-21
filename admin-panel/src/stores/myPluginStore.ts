import {defineStore} from "pinia";
import {api_url, type MyPlugin} from "@/types/types.ts";
import {ref} from "vue";
import axios from "axios";

export const useMyPluginStore = defineStore('my-plugins', () => {
  const allMyPlugins = ref<MyPlugin[]>([]);
  const checkedPluginsIds = ref<number[]>([])

  const getAllMyPlugins = async () => {
    // allMyPlugins.value = await axios.get(`${api_url}/local-scripts/list`)
    //   .then((res) => res.data as MyPlugin[])
    console.log(await axios.get(`${api_url}/local-scripts/list`).then((res) => res.data as MyPlugin[]))
  }

  const checkedPlugins = (plugins:number[]) => {
    checkedPluginsIds.value = plugins
  }
  return {
    allMyPlugins,
    checkedPlugins,
    getAllMyPlugins,
    checkedPluginsIds
  }
})
