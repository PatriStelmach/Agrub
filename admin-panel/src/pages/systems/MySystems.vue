<script setup lang="ts">
import wazuh_logo_black from "/icons/wazuh_logo_black.svg"
import wazuh_logo_white from "/icons/wazuh_logo_white.svg"
import zabbix_logo from "/icons/zabbix_logo.svg"
import nagios_logo from "/icons/nagios_logo.svg"
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {useSettingStore} from "@/stores/settingStore.js";
import {computed, onMounted, ref} from "vue";
import GridCardTransitionGroup from "@/helpers_components/loaders/GridCardTransitionGroup.vue";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import type {
  MonitoringSystemsConfig,
} from "@/types/types.ts";
import {toast} from "vue-sonner";
import {bigNameLabel, gridCard} from "@/assets/cssFunctions.ts";
import {useWrapping} from "@/composables/unwrapping.ts";
import {IconDeviceFloppy, IconLock, IconCheck, IconEdit, IconX} from "@tabler/icons-vue";
import {Card, CardDescription, CardFooter, CardHeader} from "@/components/ui/card";
import {useColorMode} from "@vueuse/core";
import {Button} from "@/components/ui/button";
import FormInput from "@/helpers_components/form/FormInput.vue";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {ButtonGroup} from "@/components/ui/button-group";

const isPageLoading = ref(true);
const changePassword = ref(false);
const settingStore = useSettingStore()
const mode = useColorMode()

const systemLogo = (system: MonitoringSystemsConfig) => {
  switch (system.name) {
    case 'Zabbix':
      return zabbix_logo
    case 'Nagios':
      return nagios_logo
    case 'Wazuh':
      return mode.value === 'light' ? wazuh_logo_black : wazuh_logo_white
  }
}
onMounted(async () => {
  await settingStore.getSystemFullSettingsRequest()
    .catch((err) => toast.error(err.message))
    .finally(() => isPageLoading.value = false)
})

const { isUnwrapped, unwrap, unwrappedItem, save, originalItem, wrap} = useWrapping<MonitoringSystemsConfig>(ref(settingStore.systemsConfig), 'name')

const cancelEdit = () => {
  wrap()
  if(changePassword.value)  changePassword.value = false
}

const editSystemButton = (name: string) => {
  if (isUnwrapped(name))  {
    save(() => {
      return
    })
  } else  {
    unwrap(name)
  }
  if(changePassword.value)  changePassword.value = false
}


</script>

<template>
  <div>
    <TopH1Div h1="Connected systems"/>
    <Transition name="fade" mode="out-in">
      <div class="grid grid-cols-3" v-if="isPageLoading">
        <BigLoadingBlock
          class="h-90 w-full"
          v-for="(_, index) in [0,1,2]"
          :key="index"/>
      </div>
      <GridCardTransitionGroup
        class=" px-6 py-2 pr-3 grid sm:grid-cols-2 md:grid-cols-3 gap-6 max-h-[85vh] overflow-y-auto"
        v-else>
        <div
          v-for="system in settingStore.systemsConfig"
          :key="system.name"
        >
          <Card
            v-if="isUnwrapped(system.name)"
            class=" cursor-auto! relative min-h-100! border-blue-badge/50  shadow-blue-badge/50!"
            :class="gridCard">
            <CardHeader class="left-1/2 -translate-x-1/2  items-center relative">
              <div class="w-48 h-20 mx-auto">
                <img
                  :src="systemLogo(system)"
                  :alt="system.name"
                  :class="{'-mt-3': system.name === 'Wazuh', '-mt-4 ' : system.name === 'Nagios'}"  />
              </div>
            </CardHeader>
            <CardDescription class="px-3 space-y-3 *:flex *:space-x-1 *:items-center *:mr-2 -mt-4">
              <form>
                <h1 :class="bigNameLabel">Connected:</h1>
                <component :is="system.enabled  ? IconCheck : IconX" :class="system.enabled ? 'text-green-badge' : 'text-red-badge'"/>
              </form>
              <div>
                <FormInput
                  :name="`${system.name.toLowerCase()}_url`"
                  label="URL"/>
              </div>
              <div v-if="system.user">
                <FormInput
                  :name="`${system.name.toLowerCase()}_user`"
                  label="User"/>
              </div>
              <div class="grid! grid-cols-1! space-y-3" v-if="changePassword">
                <div>
                  <FormInput
                    :name="`${system.name.toLowerCase()}_password_old`"
                    label="Old password"/>
                </div>
                <div>
                  <FormInput
                    :name="`${system.name.toLowerCase()}_password_new`"
                    label="New password"/>
                </div>
                <div>
                  <FormInput
                    :name="`${system.name.toLowerCase()}_password_new_confirmation`"
                    label="Confirm Password"/>
                </div>
              </div>

              <div v-if="system.min_active_level">
                <FormNumberInput
                  :step="1"
                  :min="0"
                  :max="16"
                  label="Minimal severity level: "
                  name="wazuh_min_active_level"
                  class="text-lg text-comment flex"/>
              </div>
            </CardDescription>
            <CardFooter class="mt-auto w-full">
              <ButtonGroup class="mx-auto">
                <Button
                  @click="cancelEdit"
                  variant="red_outline"
                  class=" *:size-5!"
                >
                  Cancel <IconX />
                </Button>
                <Button
                  class="border-l-2!"
                  variant="blue_outline"
                  @click="changePassword = !changePassword"
                >
                  Change {{ system.name === 'Zabbix' ? 'API token' : 'password' }}<IconLock/>
                </Button>
                <Button
                  class="border-l-2!"
                  @click="editSystemButton(system.name)"
                  variant="green_outline"
                >
                  Save <IconDeviceFloppy/>
                </Button>
              </ButtonGroup>
            </CardFooter>
          </Card>
          <Card
            v-else
            class=" cursor-auto! relative h-90!"
            :class="gridCard">
            <CardHeader class="left-1/2 -translate-x-1/2  items-center relative">
              <div class="w-48 h-20 mx-auto">
                <img
                  :src="systemLogo(system)"
                  :alt="system.name"
                  :class="{'-mt-3': system.name === 'Wazuh', '-mt-4 ' : system.name === 'Nagios'}"  />
              </div>
            </CardHeader>
            <CardDescription class="px-3 space-y-2 *:flex *:space-x-1 *:items-center *:mr-2 -mt-4">
              <div>
                <h1 :class="bigNameLabel">Connected:</h1>
                <component :is="system.enabled  ? IconCheck : IconX" :class="system.enabled ? 'text-green-badge' : 'text-red-badge'"/>
              </div>
              <div>
                <h1 :class="bigNameLabel">URL:</h1>
                <a
                  class="cursor-pointer truncate text-comment italic  text-lg hover:text-blue-badge/50 transition duration-100"
                  :href="system.url">
                  {{ system.url}}
                </a>
              </div>
              <div v-if="system.user">
                <h1 :class="bigNameLabel">User: </h1>
                <p class="text-lg text-comment"> {{ system.user}}</p>
              </div>
              <div v-if="system.min_active_level">
                <h1 :class="bigNameLabel">Minimal severity level: </h1>
                <p class="text-lg text-comment"> {{ system.min_active_level}}</p>
              </div>
            </CardDescription>
            <CardFooter
              class="bottom-4 absolute w-full"
            >
              <Button
                @click="editSystemButton(system.name)"
                variant="green_outline"
                class=" w-full *:size-5!"
              >
               Edit configuration <IconEdit/>
              </Button>
            </CardFooter>
          </Card>
        </div>
      </GridCardTransitionGroup>
    </Transition>

  </div>
</template>
