<script setup lang="ts">
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogClose,
  DialogContent, DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import {Textarea} from "@/components/ui/textarea";
import {ref, watch} from "vue";
import {IconX, IconCheck, IconSend2} from "@tabler/icons-vue";
import DialogLabel from "@/helpers_components/DialogLabel.vue";
import type {ActiveAlert} from "@/types/types.js";
import {Badge} from "@/components/ui/badge";
import ActionsTable from "@/helpers_components/ActionsTable.vue";
import {useAuthStore} from "@/stores/authStore.ts";
import SeveritySelect from "@/helpers_components/SeveritySelect.vue";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {toast} from "vue-sonner";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {useRouter} from "vue-router";
import api from "@/lib/axios.ts";

const router = useRouter();


const alert = defineModel<ActiveAlert | null | undefined>("alert")

const authStore = useAuthStore()
const newAck = ref<boolean | null>()
const newSeverity = ref()
const newMessage = ref("")
const isDialogOpen = defineModel<boolean>('isDialogOpen')

const sentAction = async () => {
  if(alert.value && (
      newAck.value !== alert.value.isAcknowledged ||
      newSeverity.value !== alert.value.severity ||
      newMessage.value
  )) {
    try {
      const response = await api.post(`/alerts/${alert.value.id}/ack`, {
        author: authStore.currentUser!.email!,
        ack: newAck.value === alert.value.isAcknowledged ? null : newAck.value,
        message: newMessage.value ?? undefined,
        newSeverity: newSeverity.value === alert.value.severity ? undefined : newSeverity.value,
      })
      if(response.status === 200) {
        return
      }
    }
    catch (error) {
      toast.error(`Error updating alert: ${error}`)
    }
    finally {
      onClose()
    }
  }
}

const onClose = () => {
  if (alert.value) {
    setTimeout(() => {
      newMessage.value = ""
    }, 500)
  }
}

watch(isDialogOpen, (newValue, oldValue) => {
  if (newValue === true && oldValue === false) {
    newSeverity.value = alert.value?.severity
    newAck.value = alert.value?.isAcknowledged
    newMessage.value = ""
  }
  else if (newValue === false && oldValue === true) {
    router.replace({ path: '/active_alerts' })
  }
})

</script>

<template>
  <Dialog v-model:open="isDialogOpen" >
      <DialogTrigger as-child>
        <slot />
      </DialogTrigger>
      <DialogContent
        :show-close-button="false"
        :class=" 'max-w-280! h-180!' +
        ` border-2 shadow-[0_0_1rem_2px] shadow-severity-${newSeverity}/70 border-severity-${newSeverity}/70 duration-500`">
        <Tabs>
          <TabsList class="absolute  right-4">
            <TabsTrigger  value="details">
              Details
            </TabsTrigger>
            <TabsTrigger value="history">
              History
            </TabsTrigger>
          </TabsList>
            <TabsContent  value="details">
              <DialogHeader :class="`border-b-2 pb-2 mb-4 border-severity-${newSeverity}/70 duration-500`">
                <DialogTitle >Alert details</DialogTitle>
                <DialogDescription>Review and add new actions</DialogDescription>
              </DialogHeader>
              <div class="flex flex-col ">
                <div class="grid gap-y-6 *:flex *:space-x-2 *:items-center [&_p]:text-lg [&_p]:text-comment border-b-2 pb-2">
                  <div >
                    <h1 :class="bigNameLabel">Subject: </h1>
                    <p id="subject"  > {{ alert?.subject}}</p>
                  </div>
                  <div class="items-start!" >
                    <h1 :class="bigNameLabel">Message: </h1>
                    <p id="alert-message" > {{ alert?.message}}</p>
                  </div>
                  <div >
                    <h1 :class="bigNameLabel">Source: </h1>
                    <Badge variant="source">{{ alert?.source}}</Badge>
                  </div>
                  <div >
                    <h1 :class="bigNameLabel">Origin: </h1>
                    <RouterLink
                      :to="(alert?.originType === 'ZABBIX' || alert?.originType === 'WAZUH' || alert?.originType === 'NAGIOS') ?
               `/settings/systems/${alert?.originType}` :
                `/my_plugins/${alert?.source}`">
                      <Badge
                        class="whitespace-break-spaces"
                        variant="origin"
                      >{{alert?.originType}}</Badge>
                    </RouterLink>
                  </div>
                </div>
                <div class="grid flex-1 gap-y-3 p-2 overflow-scroll ">
                  <div class="grid  h-full">
                    <DialogLabel for="my-message " text="Your comment"/>
                    <Textarea class="max-h-30 mb-2" id="my-message"  v-model="newMessage"/>
                  </div>
                  <div class="flex items-end ">
                    <DialogLabel for="ack" class="mb-0 pb-0" text="ACK:"/>


                    <div class="flex items-end space-x-2">
                      <Button  @click="newAck = !newAck" variant="outline"  class="size-7 duration-100">
                        <IconCheck
                          v-if="newAck"
                          class="size-5 text-green-badge "/>
                        <IconX
                          v-else
                          class="size-5  text-red-badge "/>
                      </Button>
                      <span class="text-comment">{{ newAck ? 'Acknowledged' : 'Unacknowledged'  }}</span>
                    </div>
                  </div>
                  <div class="flex items-end">
                    <DialogLabel for="severity" class="pb-0 " text="Severity:"/>
                    <SeveritySelect
                      hideUnknown
                      v-model:severity="newSeverity"
                    />
                  </div>
                </div>
              </div>
            </TabsContent>
            <TabsContent  value="history">
              <DialogHeader :class="`border-b-2 pb-2 mb-4 border-severity-${newSeverity}/70 duration-500`">
                <DialogTitle >Actions history</DialogTitle>
                <DialogDescription>All interactions with alert</DialogDescription>
              </DialogHeader>
              <ActionsTable
                :userView="false"
                max-h="32rem"
                max-w="65rem"
                :actions="alert?.actions"
              />
            </TabsContent>
        </Tabs>
        <DialogFooter class="absolute bottom-6 right-6 items-center">
          <DialogClose as-child>
            <Button variant="red_outline"
              @click="onClose">
              Cancel
            </Button>
          </DialogClose>
          <DialogClose>
            <Button
              variant="green_outline"
              type="submit"
              @click="sentAction"
            >
              Update <IconSend2/>
            </Button>
          </DialogClose>
        </DialogFooter>
      </DialogContent>
  </Dialog>
</template>
