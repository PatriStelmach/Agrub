import {defineStore} from "pinia";
import type {User, UserGroup} from "@/types/types.ts";
import {ref} from "vue";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";
import {sidebarData} from "@/data/sidebarData.ts";

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
    // try {
    //   const res = await api.get(`/users/${id}`)
    //   if (res.status === 200) return res.data
    //   else toast.error(`Error retrieving user: ${id}`)
    // }
    // catch (error) {
    //   toast.error(`Error retrieving user: ${error}`)
    // }

    return sidebarData.loggedUser
  }

  const editUserRequest = async (user: User) => {
    try {
      const res = await api.put(`/users/${user.id}`, user)
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

  return {
    allUsers,
    allGroups,
    getAllUsersRequest,
    editUserRequest,
    getUserByIdRequest,
    getAllGroupsRequest

  }
})
