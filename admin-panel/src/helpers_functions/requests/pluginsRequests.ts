import api from "@/lib/axios.ts";
import type {MyPlugin, MyPluginsFromApi, PluginDetails} from "@/types/types.ts";

export const editMyPluginRequest = async (plugin: MyPlugin) => {
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
      return await getAllMyPluginsRequest()
  }
  catch (error) {
    throw error;
  }
}

export const getAllMyPluginsRequest = async () => {
  try {
    const response= await api.get('/local-scripts/list')
    if(response.status === 200) {
      return response.data.map((item: MyPluginsFromApi) => ({
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
  }
  catch (error) {
    throw error
  }
}

export const changeMyPluginStatusRequest = async (fileNames: string[]) => {
  try {
    const response = await api.post('/local-scripts/change-status', fileNames)
    if (response.status === 200){
      return await getAllMyPluginsRequest()
    }
  } catch (error) {
    throw error
  }
}

export const deleteMyPluginsRequest = async (fileNames: string[]) => {
  try {
    const response = await api.delete('/local-scripts/delete', { data: fileNames  })
    if (response.status === 200) {
      return await getAllMyPluginsRequest()
    }
  } catch (error) {
    throw error
  }
}

export const getPluginTagsRequest = async () => {
  try {
    const res = await api.get('/plugins/tags')
    if (res.status === 200) {
      return res.data
    }
  }
  catch (err) {
    throw err
  }
}

export const getPluginDetailsRequest = async (key: string | number, type: 'local-scripts' | 'plugins') => {
  try {
    const response = await api.get(`/${type}/${key}/details`)
    if (response.status === 200)
      return response.data as PluginDetails
  } catch (error) {
    throw error
  }
}

export const downloadPluginRequest = async (id: number) => {
  try {
    const res = await api.post(`/plugins/download/${id}`)
    if (res.status === 200) {
      return true
    }
  } catch (error) {
    throw error
  }
}

export const runPluginRequest = async (fileName: string | undefined, args: string) => {
  try {
    const res = await api.post(`/local-scripts/${fileName}/run` , {arguments: args})
    if (res.status === 200) {
      return res.data
    }
  } catch (error) {
    throw (error)
  }
}
