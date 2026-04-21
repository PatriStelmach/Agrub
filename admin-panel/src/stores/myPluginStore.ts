import {defineStore} from "pinia";
import {api_url, type MyPlugin, type MyPluginsFromApi, type PluginDetails} from "@/types/types.ts";
import {computed, ref} from "vue";
import axios from "axios";

export const useMyPluginStore = defineStore('my-plugins', () => {
  const allMyPlugins = ref<MyPlugin[]>([]);
  // const changePluginStatus = async (fileName: string) => {
  //   await axios.put(`${api_url}/local-scripts/${fileName}/`)
  // }
  const getMyPluginDetails = async (fileName: string) => {
    return await axios.get(`${api_url}/local-scripts/${fileName}/details`)
      .then((res) => res.data as PluginDetails )
  }

  const getAllMyPlugins = async () => {
    allMyPlugins.value = await axios.get(`${api_url}/local-scripts/list`)
      .then((res) => res.data.map((item: MyPluginsFromApi) => ({
        active: item.active,
        creator: item.creator,
        log: item.isLog,
        name: item.fileName,
        fileName: item.fileName + item.language,
        language: item.language,
        updatedAt: new Date(item.updatedAt),
        weight: item.weight,
        tags: item.tags,
        cronExpression: item.cronExpression,
      })))
  }

  // const getAllMyPlugins = async () => {
  //   allMyPlugins.value = await axios.get(`${api_url}/local-scripts/list`)
  //     .then((res) => res.data.map((item: MyPluginsFromApi) => ({
  //       active: item.active,
  //       creator: item.creator,
  //       log: item.isLog,
  //       name: item.name,
  //       fileName: item.fileName,
  //       language: item.language,
  //       updatedAt: new Date(item.updatedAt),
  //       weight: item.weight,
  //       tags: item.tags,
  //       cronExpression: item.cronExpression,
  //     })))
  // }

  const changeStatus = async (fileNames: string[]) => {
    return await axios.post(`${api_url}/local-scripts/change-status`, fileNames).then((res) => res.data)
  }

  const deleteMyPlugins = async (fileNames: string[]) => {
    return await axios.delete(`${api_url}/local-scripts/delete`, {params: {fileNames: fileNames}}).then((res) => res.data)
  }

  return {
    allMyPlugins,
    getAllMyPlugins,
    getMyPluginDetails,
    changeStatus,
    deleteMyPlugins
  }
})
