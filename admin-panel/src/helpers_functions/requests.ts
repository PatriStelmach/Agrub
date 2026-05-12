import api from "@/lib/axios.ts";
import {toast} from "vue-sonner";

export const getPluginTagsResponse = async () => {
  try {
    const res = await api.get('/plugins/tags')
    if (res.status === 200) {
      return res.data
    }
    else {
      toast.error(res.data)
    }
  }
  catch (err) {
    toast.error(`Error getting tags:${err}`)
  }
}
