<script setup lang="ts">

import {bigNameLabel, gridCard} from "@/assets/cssFunctions.ts";
import {IconTrash, IconStatusChange, IconSquareKey, IconClipboardText, IconCalendarDue, IconCopy, IconLoader} from "@tabler/icons-vue";
import {Card, CardHeader} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import type {ApiKey} from "@/types/types.ts";
import {dateParser} from "@/composables/dateParser.ts";
import {Button} from "@/components/ui/button";
import {useClipboard} from "@vueuse/core";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'
import {toast} from "vue-sonner";
import {ref} from "vue";
import {useSettingStore} from "@/stores/settingStore.ts";

const props = defineProps<{
  propsAPiKey: ApiKey
}>()
const settingsStore = useSettingStore()
const apiKey = ref<ApiKey>(props.propsAPiKey)
const { copy } = useClipboard()
const isRevoking = ref<boolean>(false)
const isDeleting = ref<boolean>(false)

const revoke = async(id: number) => {
  isRevoking.value = true
  setTimeout(async() => {
    await settingsStore.revokeApiKeyRequest(id)
      .then((res) => toast.success(res))
      .catch((e) => toast.error(`API key could not be revoked: ${e}`))
      .finally(async () => isRevoking.value = false)
  },200)
}

const deleteApiKey = async(id: number) => {
  isDeleting.value = true
  setTimeout(async() => {
    await settingsStore.deleteApiKeyRequest(id)
      .then(() => toast.success('API key deleted successfully.'))
      .catch((e) => toast.error(`API key could not be deleted: ${e}`))
      .finally(async () => isDeleting.value = false)
  },200)
}

</script>

<template>
  <Card :class="gridCard">
    <CardHeader class="flex  justify-between">
      <Badge variant="ack_type">ID: {{ apiKey.id }}</Badge>
      <Button
        @click="deleteApiKey(apiKey.id)"
        size="icon-sm"
        variant="red_outline"
      >
        <IconLoader v-if="isDeleting" class="size-5 animate-spin" />
        <IconTrash v-else class="size-5"/>
      </Button>
    </CardHeader>

    <CardHeader class="px-4 gap-y-2 *:flex *:space-x-2 *:items-start [&_p]:pt-0.5 [&_p]:text-md [&_p]:text-comment">
      <div >
          <IconStatusChange class="size-5"/>
        <h1 :class="bigNameLabel">{{ apiKey.active ? 'Active:' : 'Inactive:'}}</h1>
        <Button
          @click="revoke(apiKey.id)"
          class="w-26 items-center max-md:*:mt-1 lg:*:mt-0"
          size="sm"
          :variant="!apiKey.active ? 'green_outline' : 'red_outline'"
        >
          <IconLoader v-if="isRevoking" class="animate-spin size-5"/>
          <span  v-else-if="!isRevoking && apiKey.active">deactivate</span>
          <span v-else>activate</span>
        </Button>
      </div>
      <div>
        <IconClipboardText class="size-5 mb-1"/>
        <h1 :class="bigNameLabel">Description:</h1>
        <p >{{ apiKey.description }}</p>
      </div>
      <div>
        <IconCalendarDue class="size-5"/>
        <h1 :class="bigNameLabel">Created at:</h1>
        <p >{{ dateParser(apiKey.createdAt).fullDate }}</p>
      </div>
      <div>
        <IconSquareKey class="size-5"/>
        <h1 :class="bigNameLabel">Token:</h1>
        <div class="flex ">
          <p class="wrap-break-word w-66" >{{ apiKey.token }}</p>
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger>
                <IconCopy
                  @click="copy(apiKey.token!); toast.message(`Copied to clipboard`)"
                  class="ml-2 mb-6 size-5 cursor-pointer hover:scale-115 text-label"/>
              </TooltipTrigger>
              <TooltipContent>
                <p>Copy</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

        </div>


      </div>
    </CardHeader>
  </Card>
</template>
