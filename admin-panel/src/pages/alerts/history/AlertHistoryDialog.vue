<script setup lang="ts">

import {
  Dialog, DialogClose,
  DialogContent, DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle, DialogTrigger
} from "@/components/ui/dialog";
import {Badge} from "@/components/ui/badge";
import ActionsList from "@/helpers_components/ActionsList.vue";
import {Button} from "@/components/ui/button";
import {IconCheck, IconX} from "@tabler/icons-vue";
import type {ActionResponse, HistoryAlert} from "@/types/types.ts";
import {ref, watch, watchEffect} from "vue";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import {useRoute} from "vue-router";
import router from "@/router";

const props = defineProps<{
  alert: HistoryAlert
}>()
const route = useRoute()
const isLoading = ref(true);
const isDialogOpen = defineModel<boolean>('isDialogOpen')

const actions = ref<ActionResponse[]>([])
const getActions = async () => {
  actions.value = props.alert.actions
  isLoading.value = false
}

watchEffect( () => {
  if(route.params.alert === String(props.alert.id)) {
    isDialogOpen.value = true
  }
})

watch(isDialogOpen, (newValue, oldValue) => {
  if (newValue === false && oldValue === true) {
    router.replace({ path: '/alerts_history' })
  }
})

</script>

<template>
  <Dialog v-model:open="isDialogOpen">
    <DialogTrigger as-child @click="getActions">
      <slot />
    </DialogTrigger>
    <DialogContent
      :show-close-button="false"
      :class=" 'max-w-280! h-180!' +
        ` border-2 shadow-[0_0_1rem_2px] shadow-severity-${alert.severity}/70 border-severity-${alert.severity}/70 duration-500`">
      <Tabs>
        <TabsList class="absolute  right-4">
          <TabsTrigger  value="details">
            Details
          </TabsTrigger>
          <TabsTrigger value="history">
            History
          </TabsTrigger>
        </TabsList>
        <TabsContent value="details">
          <DialogHeader :class="`border-b-2 pb-2 mb-4 border-severity-${alert.severity}/70 duration-500`">
            <DialogTitle>Alert actions</DialogTitle>
            <DialogDescription>
              Review alert details
            </DialogDescription>
          </DialogHeader>
            <div class="grid gap-y-6 *:flex *:space-x-2 *:items-start [&_p]:text-md! [&_p]:text-comment  pb-2">
              <div >
                <h1 :class="bigNameLabel">Subject: </h1>
                <p id="subject"  > {{ props.alert.subject}}</p>
              </div>
              <div >
                <h1 :class="bigNameLabel">Message: </h1>
                <p id="alert-message" > {{ props.alert.message}}</p>
              </div>
              <div >
                <h1 :class="bigNameLabel">Source: </h1>
                <Badge variant="source">{{ props.alert.source}}</Badge>
              </div>
              <div >
                <h1 :class="bigNameLabel">Origin: </h1>
                <Badge variant="origin">{{ props.alert.originType}}</Badge>
              </div>
              <div class="flex items-end!">
                <h1 :class="bigNameLabel">ACK: </h1>
                <div class="flex items-end space-x-2">
                  <Button variant="outline" class="size-7 cursor-default">
                    <IconCheck
                      v-if="alert.isAcknowledged"
                      class="size-5 text-green-badge "/>
                    <IconX
                      v-else
                      class="size-5  text-red-badge "/>
                  </Button>
                  <span class="text-comment">{{ alert.isAcknowledged ? 'Acknowledged' : 'Unacknowledged'  }}</span>
                </div>

              </div>
              <div>
                <h1 :class="bigNameLabel">Severity: </h1>
                <SeverityDiv
                  :severity="alert.severity"
                />
              </div>
             </div>
        </TabsContent>

        <TabsContent value="history">
          <DialogHeader :class="`border-b-2 pb-2 mb-4 border-severity-${alert.severity}/70 duration-500`">
            <DialogTitle >Actions history</DialogTitle>
            <DialogDescription>All interactions with alert</DialogDescription>
          </DialogHeader>
          <ActionsList
            :userView="false"
            max-h="32rem"
            max-w="65rem"
            :actions="alert.actions"
          />
        </TabsContent>
      </Tabs>
      <DialogFooter class="absolute bottom-6 right-6 items-center">
        <DialogClose as-child>
          <Button variant="red_outline">
            Exit
          </Button>
        </DialogClose>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
