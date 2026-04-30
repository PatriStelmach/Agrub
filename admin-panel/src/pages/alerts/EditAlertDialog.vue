<script setup lang="ts">
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import {Textarea} from "@/components/ui/textarea";
import { ref} from "vue";
import {IconX, IconCheck, IconSend2} from "@tabler/icons-vue";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {useAlertStore} from "@/stores/alertStore.ts";
import DialogLabel from "@/helpers/DialogLabel.vue";
import type {ActionResponse, ActiveAlert} from "@/types/types.ts";
import {Badge} from "@/components/ui/badge";
import ActionsTable from "@/helpers/ActionsTable.vue";

const props = defineProps<{
  alert: ActiveAlert
}>()

const isLoading = ref(true);

const actions = ref<ActionResponse[]>([])
const getActions = async () => {
    actions.value = await alertStore.getAlertActions(props.alert.id)
    isLoading.value = false
}
const alertStore = useAlertStore()

const newAck = ref(props.alert.acknowledged)
const newSeverity = ref(props.alert.severity)
const newMessage = ref("")

const sentAction = async () => {
  if(newAck.value !== props.alert.acknowledged || newSeverity.value !== props.alert.severity || newMessage.value) {
    await alertStore.updateAlertRequest({
      id: props.alert.id,
      author: 'Błażej chuj',
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
      <DialogTrigger as-child @click="getActions">
        <slot />
      </DialogTrigger>
      <DialogContent :class="` h-fit border-2 shadow-[0_0_1rem_2px] max-md:max-w-4/5! md:max-w-2/5! shadow-severity-${newSeverity}/70 border-severity-${newSeverity}/70 duration-500`" >
        <DialogHeader>
          <DialogTitle :class="`border-b-2 pb-2 border-severity-${newSeverity}/70 duration-500`">Alert actions</DialogTitle>

        </DialogHeader>
        <div class="flex flex-col max-h-[35vh] md:max-h-[50vh]">
          <div class="grid gap-1 [&_p]:text-comment border-b-2 pb-2">
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

          <div class="grid flex-1 gap-1 p-2 overflow-scroll ">
            <div class="grid  h-full">
              <DialogLabel for="my-message " text="Your comment"/>
              <Textarea class="mb-2" id="my-message"  v-model="newMessage"/>
            </div>
            <div class="flex items-center ">
              <DialogLabel for="ack" class="w-15  mb-0 pb-0" :text="newAck ? 'ACK' : 'UNACK' "/>
              <Button  @click="newAck = !newAck" variant="outline"  class="size-8 duration-100">
                <IconCheck
                  v-if="newAck"
                  class="size-6 text-badge1 "/>
                <IconX
                  v-else
                  class="size-6  text-badge2 "/>
              </Button>
            </div>
            <div class="flex items-center ">
              <DialogLabel for="severity" class="w-15 " text="Severity"/>
              <Select
                v-model="newSeverity"
              >
                <SelectTrigger class="cursor-pointer h-8! w-16 ">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem
                    :class='`cursor-pointer hover:bg-severity-${value}/70! `'
                    v-for="value in [0,1,2,3,4,5]" :key="value" :value="value">{{value}}</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>
        <ActionsTable
          :actions="actions"
          :isLoading="isLoading"
          >
          <DialogLabel text="Actions" for="actions-history" />
        </ActionsTable>
        <DialogFooter class=" items-center">
          <DialogClose as-child>
            <Button variant="destructive"
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
              Update <IconSend2 class="text-badge1"/>
            </Button>
          </DialogClose>

        </DialogFooter>
      </DialogContent>
  </Dialog>
</template>
