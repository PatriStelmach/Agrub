import {defineStore} from "pinia";
import type {User} from "@/types/types.ts";
import {ref} from "vue";
import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";

export const useUserStore = defineStore('user-store',() => {
  const allUsers = ref<User[]>([])

  const getAllUsersRequest = async () => {
    try {
      const response = await api.get<User[]>('/api/users')
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
      const res = await api.put(`/api/users/${user.id}`, user)
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
    getAllUsersRequest,
    editUserRequest,

  }
})
