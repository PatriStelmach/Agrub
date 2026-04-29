<script setup lang="ts">
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {Textarea} from "@/components/ui/textarea";
import {onUnmounted, ref, watch} from "vue";
import {IconCircleDashedX, IconCircleDashedCheck, IconSend2} from "@tabler/icons-vue";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {useAlertStore} from "@/stores/alertStore.ts";
import DialogLabel from "@/helpers/DialogLabel.vue";
import {Alert} from "@/components/ui/alert";
import type {ActiveAlert} from "@/types/types.ts";
import {Badge} from "@/components/ui/badge";
import {Card, CardHeader, CardTitle} from "@/components/ui/card";

const props = defineProps<{
  alert: ActiveAlert
}>()

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
  newAck.value = props.alert.acknowledged
  newSeverity.value = props.alert.severity
  newMessage.value = ""
}

</script>

<template>
  <Dialog >
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent class=" h-fit border-2 shadow-[0_0_1rem_2px]  shadow-badge2 border-badge2 duration-500" :class="{'border-badge1 shadow-badge1' : newAck}">
        <DialogHeader>
          <DialogTitle class="border-b-2 pb-2 border-badge2 duration-500" :class="{'border-badge1 ' : newAck}">Alert actions</DialogTitle>

        </DialogHeader>
        <div class="flex flex-col px-2 max-h-[70vh] overflow-hidden">
          <div class="grid gap-2  shrink-0 border-b-2 pb-2 ">
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

          <div class="grid flex-1 gap-2 p-2 h-1/2 overflow-y-auto">
            <div class="grid  h-full">
              <DialogLabel for="my-message " text="Your comment"/>
              <Textarea class="mb-2" id="my-message"  v-model="newMessage"/>
            </div>
            <div class="flex items-center ">
              <DialogLabel for="ack" class="w-14 mb-0 pb-0" :text="newAck ? 'ACK' : 'UNACK' "/>
              <IconCircleDashedCheck
                @click="newAck = !newAck"
                v-if="newAck"
                class="size-8 cursor-pointer mb-0 pb-0  text-badge1 hover:scale-120 duration-300"/>
              <IconCircleDashedX
                @click="newAck = !newAck"
                v-else
                class="size-8 cursor-pointer text-badge2 hover:scale-120 duration-300"/>
            </div>
            <div class="flex items-center ">
              <DialogLabel for="severity" class="w-14 " text="Severity"/>
              <Select
                v-model="newSeverity"
              >
                <SelectTrigger class="cursor-pointer w-1/3 ">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem
                    :class='`cursor-pointer hover:bg-severity-${value}/50! `'
                    v-for="value in [0,1,2,3,4,5]" :key="value" :value="value">{{value}}</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>

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
