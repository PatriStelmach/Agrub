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
import type {HistoryAlert} from "@/types/types.ts";
import {watch} from "vue";
import SeverityDiv from "@/helpers_components/SeverityDiv.vue";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {bigNameLabel} from "@/assets/cssFunctions.ts";
import router from "@/router";

defineProps<{
  alert: HistoryAlert | null
}>()
const isDialogOpen = defineModel<boolean>('isDialogOpen')

watch(isDialogOpen, (newValue, oldValue) => {
  if (newValue === false && oldValue === true) {
    router.replace({ path: '/alerts_history' })
  }
})

</script>

<template>
  <Dialog v-model:open="isDialogOpen">
    <DialogTrigger as-child>
      <slot />
    </DialogTrigger>
    <DialogContent
      v-if="alert"
      :show-close-button="false"
      :class=" 'max-w-280! h-140!' +
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
          <div class="grid gap-y-4  *:flex *:space-x-2 *:items-center [&_p]:text-lg! [&_p]:text-comment  pb-2">
            <div >
              <h1 :class="bigNameLabel">Subject: </h1>
              <p id="subject"  > {{ alert.subject}}</p>
            </div>
            <div class="items-start!" >
              <h1 :class="bigNameLabel">Message: </h1>
              <p id="alert-message" > {{ alert.message}}</p>
            </div>
            <div >
              <h1 :class="bigNameLabel">Source: </h1>
              <Badge variant="source">{{ alert.source}}</Badge>
            </div>
            <div >
              <h1 :class="bigNameLabel">Origin: </h1>
              <Badge variant="origin">{{ alert.originType}}</Badge>
            </div>
            <div class="flex items-end">
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
                <span class="text-comment text-lg">{{ alert.isAcknowledged ? 'Acknowledged' : 'Unacknowledged'  }}</span>
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
            max-h="24rem"
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
