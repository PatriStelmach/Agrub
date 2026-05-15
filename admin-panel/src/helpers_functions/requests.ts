import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";
import type {
  Actions,
  GroupDetails,
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


export const getMyPluginDetails = async (fileName: string) => {
  try {
    const response = await api.get(`/local-scripts/${fileName}/details`)
    if (response.status === 200)
      return response.data as PluginDetails
  } catch (error) {
    toast.error(`Error fetching plugin details: ${error}`)
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
      toast.success(`Alert updated \n ${response.data.message}`)
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

export const assignUserToGroup = async (userId: number, groupId: number) => {
  try {
    const res = await api.post(`/groups/${groupId}/users/${userId}`)
    if (res.status === 200) {
      toast.success(`Successfully assigned user: ${res.data.email}`)
    }
  } catch (error) {
    throw (error)
  }
}

export const deleteUserFromGroup = async (userId: number, groupId: number) => {
  try {
    const res = await api.delete(`/groups/${groupId}/users/${userId}`)
    if (res.status === 200) {
      toast.success(`Successfully deleted user: ${res.data.email} from group`)
    }
  } catch (error) {
    throw (error)
  }
}

export const deleteRuleFromGroup = async (ruleId: number) => {
  try {
   const res = await api.delete(`/rules/${ruleId}`)
    if (res.status === 200) {
      toast.success(`Successfully deleted rule`)
    }
  } catch (error) {
    throw (error)
  }
}
