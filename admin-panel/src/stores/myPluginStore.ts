import {defineStore} from "pinia";
import {
  type MyPlugin,
  type MyPluginsFromApi
} from "@/types/types.ts";
import { ref} from "vue";
import api from "@/lib/axios";
import {toast} from "vue-sonner";

export const useMyPluginStore = defineStore('my-plugins', () => {
  const allMyPlugins = ref<MyPlugin[]>([]);

  const editMyPlugin = async (plugin: MyPlugin) => {
    console.log(plugin)
    try {
      const response = await api.put(`/local-scripts/${plugin.fullName}/edit`, {
        name: plugin.name,
        code: plugin.code,
        description: plugin.description,
        severity: plugin.severity,
        cronExpression: plugin.cronExpression,
        tags: plugin.tags,
        active: plugin.active
      })
      if(response.status === 200)
        getAllMyPluginsRequest().finally(() => toast.success(`Plugin ${plugin.name} successfully updated`))
      else
        toast.error(`Plugin ${plugin.name} failed with status ${response.status}`);
    }
    catch (error) {
      toast.error(`Plugin ${plugin.name} failed with error ${error}`);
    }
  }

  const getAllMyPluginsRequest = async () => {
    try {
      const response= await api.get('/local-scripts/list')
      if(response.status === 200) {
        console.log(response)
        allMyPlugins.value = response.data.map((item: MyPluginsFromApi) => ({
          active: item.active,
          creator: item.creator,
          severity: item.severity,
          name: item.fileName,
          fullName: item.fileName + item.language,
          language: item.language,
          updatedAt: new Date(item.updatedAt),
          weight: item.weight,
          tags: item.tags,
          cronExpression: item.cronExpression,
        }))
      }
      else
        toast.error(`Unable to fetch all plugins`)
    }
    catch (error) {
      toast.error(`Error fetching all plugins: ${error}`)
    }

  }

  const changeStatus = async (fileNames: string[]) => {
    try {
      const response = await api.post('/local-scripts/change-status', fileNames)
      if (response.status === 200)
        toast.success(`Status changed successfully`)
      else
        toast.error(`Failed to change status with status ${response.status}`)
    } catch (error) {
      toast.error(`Error changing status: ${error}`)
    }
    finally {
      await getAllMyPluginsRequest()
    }
  }

  const deleteMyPlugins = async (fileNames: string[]) => {
    try {
      const response = await api.delete('/local-scripts/delete', { params: { fileNames: fileNames } })
      if (response.status === 200)
        toast.success('Plugins deleted successfully')
      else
        toast.error(`Failed to delete plugins with status ${response.status}`)
    } catch (error) {
      toast.error(`Error deleting plugins: ${error}`)
    }
    finally {
      await getAllMyPluginsRequest()
    }
  }

  return {
    allMyPlugins,
    getAllMyPluginsRequest,
    changeStatus,
    deleteMyPlugins,
    editMyPlugin,
  }
})
