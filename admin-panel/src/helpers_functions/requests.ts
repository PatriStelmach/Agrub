import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";
import type {
  Actions,
  GroupDetails, LibraryPlugin,
  PluginDetails,
  Rule,
  UserGroupStats
} from "@/types/types.ts";

export const getPluginTagsResponse = async () => {
  try {
    const res = await api.get('/plugins/tags')
    if (res.status === 200) {
      return res.data
    }
  }
  catch (err) {
    toast.error(`Error getting tags:${err}`)
  }
}


export const getGroupDataRequest = async (id: number) => {
  try {
    const res = await api.get(`/groups/${id}/details`)
    if (res.status === 200) {
      console.log(res.data)
      return res.data as GroupDetails
    }
  }
  catch (error) {
    toast.error(`Error retrieving groups: ${error}`)
  }
}

export const getGroupsStatsRequest = async () => {
  try {
    const res = await api.get(`/groups/stats`)
    if (res.status === 200) {
      return res.data as UserGroupStats[]
    }
    else {
      toast.error(`Error retrieving stats: ${res.data}`)
      return []
    }
  }
  catch (error) {
    toast.error(`Error retrieving stats: ${error}`)
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

export const updateAlertRequest = async (action: Actions) => {
  try {
    const response = await api.post(`/alerts/${action.id}/ack`, {
      ack: action.ack,
      newSeverity: action.newSeverity,
      message: action.message,
      author: action.author,
    })
    if(response.status === 200) {
      return
    }
  }
  catch {
    toast.error('Failed to send ack!')
  }
}

export const addNewRuleRequest = async (rule: Rule) => {
  try {
    const res = await api.post(`/rules`, rule)
    if (res.status === 200) {
      toast.success('New rule added successfully!')
      return res.data
    }
  } catch (error) {
    throw (error)
  }
}

export const assignUserToGroupRequest = async (userId: number, groupId: number) => {
  try {
    const res = await api.post(`/groups/${groupId}/users/${userId}`)
    if (res.status === 200) {
      toast.success(`Successfully assigned user: ${res.data.email}`)
    }
  } catch (error) {
    throw (error)
  }
}

export const deleteUserFromGroupRequest = async (userId: number, groupId: number) => {
  try {
    const res = await api.delete(`/groups/${groupId}/users/${userId}`)
    if (res.status === 200) {
      toast.success(`Successfully deleted user: ${res.data.email} from group`)
    }
  } catch (error) {
    throw (error)
  }
}

export const updateRuleRequest = async (rule: Rule) => {
  try {
    const res = await api.put(`/rules/${rule.id}`, rule)
    if (res.status === 200) {
      console.log(rule)
      toast.success('Rule updated successfully!')
      return res.data
    }
  } catch (error) {
    throw (error)
  }
}

export const deleteRuleFromGroupRequest = async (ruleId: number) => {
  try {
   const res = await api.delete(`/rules/${ruleId}`)
    if (res.status === 200) {
      toast.success(`Successfully deleted rule`)
    }
  } catch (error) {
    throw (error)
  }
}

export const changeGroupNameRequest = async (groupId: number, name: string) => {
  try {
    const res = await api.put(`/groups/${groupId}/change-name`, {name: name})
    if (res.status === 200) {
      toast.success(`Successfully updated group name`)
      return res.data
    }
  } catch (error) {
    throw (error)
  }
}

export const runScriptRequest = async (fileName: string | undefined, args: string) => {
  try {
    const res = await api.post(`/local-scripts/${fileName}/run` , {arguments: args})
    if (res.status === 200) {
      return res.data
    }
  } catch (error) {
    throw (error)
  }
}

export const downloadPluginRequest = async (plugin: LibraryPlugin) => {
  try {
    const res = await api.post(`/plugins/download/${plugin.id}`)
    if (res.status === 200) {
      toast.success(`Successfully downloaded "${plugin.fileName}"`);
    }
  }
  catch (error) {
    toast.error(`Error downloading plugin: ${error}`);
  }
}

export const createGroupRequest = async (name: string) => {
  try {
    const res = await api.post(`/groups`, {name: name} )
    if (res.status === 200) {
      return res.data
    }
  }
  catch (error) {
    throw (error)
  }
}

export const getUserDetailsRequest = async () => {
  try {
    const res = await api.get('users/me/settings')
    if (res.status === 200) {
      return res.data
    }
  }
  catch (error) {
    throw (error)
  }
}

export const autoLogoutRequest = async (minutes: number | null) => {
  if(minutes) {
    try {
      const res = await api.patch('users/me/auto-logout', {minutes: minutes})
      if (res.status === 200) {
        return true
      }
    } catch (error) {
      throw (error)
    }
  }}

export const changeUserPasswordRequest = async (oldPassword: string, newPassword: string) => {
    try {
      const res = await api.patch('users/me/password', { oldPassword: oldPassword, newPassword: newPassword })
      if (res.status === 200) {
        toast.success('Password changed successfully!')
        return true
      }
    } catch (error) {
      throw (error)
    }
  }

export const getUserActions = async (id: number) => {
  try {
    const res = await api.get(`/users/${id}/actions`)
    if (res.status === 200) {
      return res.data ?? []
    }
  }
  catch (error) {
    toast.error( `Error retrieving user actions: ${error}`)
  }
}






