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
import { ref} from "vue";
import {IconX, IconCheck, IconSend2} from "@tabler/icons-vue";


import {useAlertStore} from "@/stores/alertStore.js";
import DialogLabel from "@/helpers_components/DialogLabel.vue";
import type { ActiveAlert} from "@/types/types.js";
import {Badge} from "@/components/ui/badge";
import ActionsTable from "@/pages/alerts/ActionsTable.vue";
import {useAuthStore} from "@/stores/authStore.ts";
import SeveritySelect from "@/helpers_components/SeveritySelect.vue";

const props = defineProps<{
  alert: ActiveAlert
}>()

const alertStore = useAlertStore()
const authStore = useAuthStore()

const newAck = ref(props.alert.acknowledged)
const newSeverity = ref(props.alert.severity)
const newMessage = ref("")

const sentAction = async () => {
  if(newAck.value !== props.alert.acknowledged || newSeverity.value !== props.alert.severity || newMessage.value) {
    await alertStore.updateAlertRequest({
      id: props.alert.id,
      author: authStore.userEmail!,
      ack: newAck.value === props.alert.acknowledged ? undefined : newAck.value,
      message: newMessage.value ?? undefined,
      newSeverity: newSeverity.value === props.alert.severity ? undefined : newSeverity.value,
    })
  } onClose()
}

const onClose = () => {
  setTimeout(() => {
    newAck.value = props.alert.acknowledged
    newSeverity.value = props.alert.severity
    newMessage.value = ""
  }, 500)
}

</script>

<template>
  <Dialog >
      <DialogTrigger as-child>
        <slot />
      </DialogTrigger>
      <DialogContent
        :show-close-button="false"
        :class="` h-fit border-2 shadow-[0_0_1rem_2px] max-md:max-w-4/5! md:max-w-2/5! shadow-severity-${newSeverity}/70 border-severity-${newSeverity}/70 duration-500`" >
        <DialogHeader :class="`border-b-2 pb-2 border-severity-${newSeverity}/70 duration-500`">
          <DialogTitle >Alert details</DialogTitle>
          <DialogDescription>Review and add new actions</DialogDescription>
        </DialogHeader>
        <div class="flex flex-col max-h-[35vh] md:max-h-[50vh]">
          <div class="grid gap-y-2 [&_p]:text-comment border-b-2 pb-2">
            <div class="grid  ">
              <DialogLabel for="alert-subject" text="Subject"/>
              <p id="subject"  class="text-sm"> {{ props.alert.subject}}</p>
            </div>
            <div class="grid   ">
              <DialogLabel text="Message" for="alert-message"/>
              <p id="alert-message" class="text-sm"> {{ props.alert.message}}</p>
            </div>
            <div class="flex space-x-2 ">
              <div>
                <DialogLabel text="Source" for="alert-source" />
                <Badge variant="source" >{{ props.alert.source }}</Badge>
              </div>
              <div>
                <DialogLabel text="Origin"  for="alert-origin"/>
                <Badge variant="origin">{{ props.alert.originType}}</Badge>
              </div>
            </div>
          </div>

          <div class="grid flex-1 gap-y-3 p-2 overflow-scroll ">
            <div class="grid  h-full">
              <DialogLabel for="my-message " text="Your comment"/>
              <Textarea class="mb-2" id="my-message"  v-model="newMessage"/>
            </div>
            <div class="flex items-end ">
              <DialogLabel for="ack" class="w-36  mb-0 pb-0" :text="newAck ? 'Acknowledged' : 'Unacknowledged' "/>
              <Button  @click="newAck = !newAck" variant="outline"  class="size-7 duration-100">
                <IconCheck
                  v-if="newAck"
                  class="size-5 text-green-badge "/>
                <IconX
                  v-else
                  class="size-5  text-red-badge "/>
              </Button>
            </div>
            <div class="flex items-end">
              <DialogLabel for="severity" class="w-36 pb-0 " text="Severity level"/>
              <SeveritySelect
                v-model:severity="newSeverity"
              />
            </div>
          </div>
        </div>
        <!-- zmienić na tabs z shadcn dla actions -->
        <ActionsTable
          :actions="alert.actions"
          >
          <DialogLabel text="Actions" for="actions-history" />
        </ActionsTable>
        <DialogFooter class=" items-center">
          <DialogClose as-child>
            <Button variant="red_outline"
              @click="onClose">
              Cancel
            </Button>
          </DialogClose>
          <DialogClose>
            <Button
              variant="outline"
              type="submit"
              @click="sentAction"
            >
              Update <IconSend2 class="text-green-badge"/>
            </Button>
          </DialogClose>

        </DialogFooter>
      </DialogContent>
  </Dialog>
</template>
