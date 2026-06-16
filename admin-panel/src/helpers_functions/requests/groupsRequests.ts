import api from "@/lib/axios.ts";
import type {GroupDetails, Rule} from "@/types/types.ts";

export const getGroupDataRequest = async (id: number) => {
  try {
    const res = await api.get(`/groups/${id}/details`)
    if (res.status === 200) {
      return res.data as GroupDetails
    }
  }
  catch (error) {
    throw error
  }
}

export const getGroupsStatsRequest = async () => {
  try {
    const res = await api.get(`/groups/stats`)
    if (res.status === 200) {
      return res.data
    }
  }
  catch (error) {
    throw error
  }
}

export const assignUserToGroupRequest = async (userId: number, groupId: number) => {
  try {
    const res = await api.post(`/groups/${groupId}/users/${userId}`)
    if (res.status === 200) {
      return res.data.email
    }
  } catch (error) {
    throw (error)
  }
}

export const deleteUserFromGroupRequest = async (userId: number, groupId: number) => {
  try {
    const res = await api.delete(`/groups/${groupId}/users/${userId}`)
    if (res.status === 200) {
      return res.data.email
    }
  } catch (error) {
    throw (error)
  }
}

export const updateRuleRequest = async (rule: Rule) => {
  try {
    const res = await api.put(`/rules/${rule.id}`, rule)
    if (res.status === 200) {
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
      return true
    }
  } catch (error) {
    throw (error)
  }
}

export const changeGroupNameRequest = async (groupId: number, name: string) => {
  try {
    const res = await api.put(`/groups/${groupId}/change-name`, {name: name})
    if (res.status === 200) {
      return res.data.name
    }
  } catch (error) {
    throw (error)
  }
}

export const createGroupRequest = async (name: string) => {
  try {
    const res = await api.post(`/groups`, {name: name} )
    if (res.status === 200) {
      return res.data
    }
  } catch (error) {
    throw (error)
  }
}

export const addNewRuleRequest = async (rule: Rule) => {
  try {
    const res = await api.post(`/rules`, rule)
    if (res.status === 200) {
      return res.data
    }
  } catch (error) {
    throw (error)
  }
}

