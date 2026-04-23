import {defineStore} from "pinia";
import {
  api_url,
  type ApiResponse,
  type MyPlugin,
  type MyPluginsFromApi,
  type PluginDetails
} from "@/types/types.ts";
import {computed, ref} from "vue";
import axios from "axios";

export const useMyPluginStore = defineStore('my-plugins', () => {
  const allMyPlugins = ref<MyPlugin[]>([]);

  const editMyPlugin = async (plugin: MyPlugin) => {
    console.log(plugin)
    const response = await axios.put(`${api_url}/local-scripts/${plugin.fileName}/edit`, {
      fileName: plugin.fileName,
      code: plugin.code,
      description: plugin.description,
      severity: plugin.severity,
      cronExpression: plugin.cronExpression,
      tags: plugin.tags,
      active: plugin.active
    })
    return response.data as ApiResponse
  }
  const getMyPluginDetails = async (fileName: string) => {
    const response = await axios.get(`${api_url}/local-scripts/${fileName}/details`)
    return response.data as PluginDetails
  }

  const getAllMyPlugins = async () => {
    const response= await axios.get(`${api_url}/local-scripts/list`)
    allMyPlugins.value = response.data.map((item: MyPluginsFromApi) => ({
        active: item.active,
        creator: item.creator,
        severity: item.severity,
        name: item.fileName,
        fileName: item.fileName + item.language,
        language: item.language,
        updatedAt: new Date(item.updatedAt),
        weight: item.weight,
        tags: item.tags,
        cronExpression: item.cronExpression,
      }))
  }

  const changeStatus = async (fileNames: string[]) => {
    const response = await axios.post(`${api_url}/local-scripts/change-status`, fileNames)
    return response.data as ApiResponse
  }

  const deleteMyPlugins = async (fileNames: string[]) => {
    const response = await axios.delete(`${api_url}/local-scripts/delete`, {params: {fileNames: fileNames}})
    return response.data as ApiResponse
  }

  return {
    allMyPlugins,
    getAllMyPlugins,
    getMyPluginDetails,
    changeStatus,
    deleteMyPlugins,
    editMyPlugin,
  }
})
