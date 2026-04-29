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

const props = defineProps<{
  id: number,
  subject: string,
  alertMessage: string,
  ack: boolean,
  severity: 0 | 1 | 2 | 3 | 4 | 5 ,
}>()

const alertStore = useAlertStore()

const newAck = ref(props.ack)
const newSeverity = ref(props.severity)
const newMessage = ref("")

const sentAction = async () => {
  if(newAck.value !== props.ack || newSeverity.value !== props.severity || newMessage.value) {
    await alertStore.updateAlertRequest({
      id: props.id,
      author: 'Błażej chuj',
      ack: newAck.value === props.ack ? undefined : newAck.value,
      message: newMessage.value ?? undefined,
      newSeverity: newSeverity.value === props.severity ? undefined : newSeverity.value,
    })
  } onClose()
}

const onClose = () => {
  newAck.value = props.ack
  newSeverity.value = props.severity
  newMessage.value = ""
}

</script>

<template>
  <Dialog>
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent class=" min-h-1/4 max-h-4/5 border-2 shadow-[0_0_1rem_2px]  shadow-badge2 border-badge2 duration-500" :class="{'border-badge1 shadow-badge1' : newAck}">
        <DialogHeader>
          <DialogTitle class="border-b-2 pb-2 border-badge2 duration-500" :class="{'border-badge1 ' : newAck}">Alert actions</DialogTitle>

        </DialogHeader>
        <div class="grid gap-3">
          <div class="grid gap-2  h-full">
            <Label for="alertMessage" class="text-md">Alert</Label>
            <p id="alertMessage"  class="text-sm"> {{ props.subject}}</p>
          </div>
        <div class="grid gap-2  h-full">
          <Label class="text-md" for="alertMessage">Alert message</Label>
          <p id="alertMessage" class="text-sm"> {{ props.alertMessage}}</p>
        </div>

          <div class="grid gap-3  h-full">
            <Label for="my-message">Your message</Label>
            <Textarea id="my-message" v-model="newMessage"/>
          </div>
          <div class="grid grid-cols-8 items-center space-x-2">
            <Label for="ack" class="col-span-1" >{{ newAck ? 'ACK' : 'UNACK' }}</Label>
            <IconCircleDashedCheck
              @click="newAck = !newAck"
              v-if="newAck"
              class="size-8 cursor-pointer text-badge1 hover:scale-120 duration-300"/>
            <IconCircleDashedX
                @click="newAck = !newAck"
                v-else
                class="size-8 cursor-pointer text-badge2 hover:scale-120 duration-300"/>
          </div>
          <div class="flex items-center space-x-2">
            <Label for="severity">Severity</Label>
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
        <DialogFooter>
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
              Update <IconSend2/>
            </Button>
          </DialogClose>

        </DialogFooter>
      </DialogContent>
  </Dialog>
</template>
