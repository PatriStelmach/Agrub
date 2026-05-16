import {defineStore} from "pinia";
import type {User, UserGroup} from "@/types/types.ts";
import {ref} from "vue";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";

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
  const editUserRequest = async (user: User) => {
    try {
      const res = await api.put(`/users/${user.id}`, user )
      if (res.status === 200){
        await getAllUsersRequest()
        return res.data.email
      }
    }
    catch (error) {
      throw error;
    }
  }

  const getUserByIdRequest = async (id: number) => {
    try {
      const res = await api.get(`/users/${id}`)
      if (res.status === 200) return res.data
    }
    catch (error) {
      toast.error(`Error retrieving user: ${error}`)
    }
  }

  const createUserRequest = async (user: User) => {
    try {
      const res = await api.post('/users', user)
      if (res.status === 200) {
        toast.success(`User ${user.email} created successfully.`)
        await getAllUsersRequest()
      }
    } catch (error) {
      toast.error(`Error creating user: ${error}`)
      throw error
    }
  }


  const getAllGroupsRequest = async () => {
    try {
      const res = await api.get('/groups')
      if (res.status === 200 && res.data.length > 0) {
        allGroups.value =  res.data
      }
      else return  []
    }
    catch (error) {
      toast.error(`Error retrieving groups: ${error}`)
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
    getUserByIdRequest,
    fullName,
    avFallback,
    editUserRequest,
    getAllGroupsRequest,
    createUserRequest,
  }
})
