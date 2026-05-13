import {defineStore} from "pinia";
import type {GroupDetails, User, UserGroup, UserGroupStats} from "@/types/types.ts";
import {ref} from "vue";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";
import {groupsData} from "@/data/groupsData.ts";

export const useUserStore = defineStore('user-store',() => {
  const allUsers = ref<User[]>([])
  const allGroups = ref<UserGroup[]>([])

  const getAllUsersRequest = async () => {
    try {
      const response = await api.get<User[]>('/users')
      if (response.status === 200) {
        allUsers.value = response.data
      }
    }
    catch (error) {
      toast.error(`Error retrieving users: ${error}`)
    }
  }

  const getGroupDataRequest = async (id: number) => {
    try {
      const res = await api.get(`/groups/${id}/details`)
      if (res.status === 200 && res.data.length > 0) {
        console.log(res.data)
        return res.data as GroupDetails
      }
      else {
        toast.error(`Error retrieving groups: ${res.data}`)
      }

    }
    catch (error) {
      toast.error(`Error retrieving groups: ${error}`)
    }
  }

  const getGroupsStatsRequest = async () => {
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

  const getAllGroupsRequest = async () => {
    try {
      const res = await api.get('/groups')
      if (res.status === 200 && res.data.length > 0) {
        allGroups.value = res.data
      }
      else allGroups.value = []
    }
    catch (error) {
      toast.error(`Error retrieving groups: ${error}`)
    }
    return allGroups.value
  }

  const getUserByIdRequest = async (id: number) => {
    try {
      const res = await api.get(`/users/${id}`)
      if (res.status === 200) return res.data
      else toast.error(`Error retrieving user: ${id}`)
    }
    catch (error) {
      toast.error(`Error retrieving user: ${error}`)
    }
  }

  const editUserRequest = async (user: User) => {
    try {
      const res = await api.put(`/users/${user.id}`, user, )
      if (res.status === 200){
        toast.success(`User ${user.email} updated successfully.`)
        await getAllUsersRequest()
      }
    else
      toast.error(`Error updating user: ${user.email}`)
    }
    catch (error) {
      toast.error(`Error updating user: ${error}`)
    }
  }

  const avFallback = (user: User) =>  {
    return `${user.firstname.slice(0, 1).toUpperCase()}${user.surname.slice(0,1).toUpperCase()}`
  }
  const fullName = (user: User) =>  {
   return `${user.firstname} ${user.surname}`
  }

  return {
    allUsers,
    allGroups,
    getAllUsersRequest,
    editUserRequest,
    getUserByIdRequest,
    getAllGroupsRequest,
    fullName,
    avFallback,
    getGroupDataRequest,
    getGroupsStatsRequest

  }
})
