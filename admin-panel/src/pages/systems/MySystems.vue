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
import {IconDeviceFloppy, IconLock, IconLockOff, IconCheck, IconEdit, IconX} from "@tabler/icons-vue";
import {Card, CardDescription, CardFooter, CardHeader} from "@/components/ui/card";
import {useColorMode} from "@vueuse/core";
import {Button} from "@/components/ui/button";
import FormInput from "@/helpers_components/form/FormInput.vue";
import FormNumberInput from "@/helpers_components/form/FormNumberInput.vue";
import {ButtonGroup} from "@/components/ui/button-group";
import {useForm} from "vee-validate";
import {loginSchema, useSystemForm} from "@/helpers_functions/formSchemas.ts";
import SystemConfigCard from "@/pages/systems/SystemConfigCard.vue";

const isPageLoading = ref(true);
const changePassword = ref(false);
const settingStore = useSettingStore()
const mode = useColorMode()

const systemsConfig = computed(() => settingStore.systemsConfig)

const systemLogo = (system: MonitoringSystemsConfig) => {
  switch (system.name) {
    case 'zabbix':
      return zabbix_logo
    case 'nagios':
      return nagios_logo
    case 'wazuh':
      return mode.value === 'light' ? wazuh_logo_black : wazuh_logo_white
  }
}
onMounted(async () => {
  await settingStore.getSystemFullSettingsRequest()
    .catch((err) => toast.error(err.message))
    .finally(() => isPageLoading.value = false)
})


const {
  isUnwrapped,
  unwrap,
  unwrappedItem,
  save,
  originalItem,
  wrap
} = useWrapping<MonitoringSystemsConfig>(systemsConfig, 'name')


const onSave = (data: MonitoringSystemsConfig) => {

}


</script>

<template>
  <div>
    <TopH1Div h1="Systems configuration"/>
    <Transition name="fade" mode="out-in">
      <div class="grid grid-cols-3" v-if="isPageLoading">
        <BigLoadingBlock
          class="h-90 w-full"
          v-for="(_, index) in [0,1,2]"
          :key="index"/>
      </div>
      <div
        class=" px-6 py-2 pr-3 grid sm:grid-cols-2 md:grid-cols-3 gap-6 max-h-[85vh] overflow-y-auto"
        v-else>
          <SystemConfigCard
            v-for="system in settingStore.systemsConfig"
            :key="system.name"
            :system="system"
            :is-unwrapped="isUnwrapped(system.name)"
            @edit="unwrap(system.name)"
            @cancel="wrap()"
            @save="onSave"
          />
      </div>
    </Transition>
  </div>
</template>
