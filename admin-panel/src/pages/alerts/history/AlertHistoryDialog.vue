<script setup lang="ts">

import {
  Dialog, DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle, DialogTrigger
} from "@/components/ui/dialog";
import {Badge} from "@/components/ui/badge";
import DialogLabel from "@/helpers/DialogLabel.vue";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {Textarea} from "@/components/ui/textarea";
import ActionsTable from "@/pages/alerts/ActionsTable.vue";
import {Button} from "@/components/ui/button";
import {IconCheck, IconSend2, IconX} from "@tabler/icons-vue";
import type {ActionResponse, ActiveAlert, HistoryAlert} from "@/types/types.ts";
import {ref} from "vue";
import {useAlertStore} from "@/stores/alertStore.ts";
import SeverityDiv from "@/helpers/SeverityDiv.vue";

const props = defineProps<{
  alert: HistoryAlert
}>()
const alertStore = useAlertStore()
const isLoading = ref(true);

const actions = ref<ActionResponse[]>([])
const getActions = async () => {
  actions.value = await alertStore.getAlertActions(props.alert.id)
  isLoading.value = false
}


</script>

<template>
  <Dialog >
    <DialogTrigger as-child @click="getActions">
      <slot />
    </DialogTrigger>
    <DialogContent :class="` h-fit border-2 shadow-[0_0_1rem_2px] max-md:max-w-4/5! md:max-w-2/5! shadow-severity-${alert.severity}/70 border-severity-${alert.severity}/70 duration-500`" >
      <DialogHeader>
        <DialogTitle :class="`border-b-2 pb-2 border-severity-${alert.severity}/70 duration-500`">Alert actions</DialogTitle>

      </DialogHeader>
      <div class="flex flex-col max-h-[35vh] md:max-h-[60vh]  border-b-2 pb-2 ">
        <div class="grid gap-y-3 [&_p]:text-comment ">
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

          <div class="flex items-end ">
            <DialogLabel for="ack" class="w-36  mb-0 pb-0" :text="alert.acknowledged ? 'Acknowledged' : 'Unacknowledged' "/>
            <Button variant="outline" class="size-7 cursor-default">
              <IconCheck
                v-if="alert.acknowledged"
                class="size-5 text-green-badge "/>
              <IconX
                v-else
                class="size-5  text-red-badge "/>
            </Button>

          </div>
          <div class="flex items-end">
            <DialogLabel for="severity" class="w-36 pb-0 " text="Severity level"/>
            <SeverityDiv
              :severity="alert.severity"
            />
          </div>
        </div>
      </div>
      <!-- zmienić na tabs z shadcn dla actions -->
      <ActionsTable
        :actions="actions"
        :isLoading="isLoading"
      >
        <DialogLabel text="Actions" for="actions-history" />
      </ActionsTable>
      <DialogFooter class=" items-center">
        <DialogClose as-child>
          <Button variant="red_outline">
            Exit
          </Button>
        </DialogClose>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
