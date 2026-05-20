<script setup lang="ts">
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {useSettingStore} from "@/stores/settingStore.js";
import {computed, onMounted, ref} from "vue";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import type {
  MonitoringSystemsConfig,
} from "@/types/types.ts";
import {toast} from "vue-sonner";
import NagiosCard from "@/pages/systems/NagiosCard.vue";
import {useWrapping} from "@/composables/unwrapping.ts";
import WazuhCard from "@/pages/systems/WazuhCard.vue";
import ZabbixCard from "@/pages/systems/ZabbixCard.vue";

const isPageLoading = ref(true);
const settingStore = useSettingStore()
const systemsConfig = computed(() => settingStore.systemsConfig)
onMounted(async () => {
  await settingStore.getSystemFullSettingsRequest()
    .catch((err) => toast.error(err.message))
    .finally(() => isPageLoading.value = false)
})

const {
  isUnwrapped,
  unwrap,
  wrap
} = useWrapping<MonitoringSystemsConfig>(systemsConfig, 'name')

</script>

<template>
  <div>
    <TopH1Div h1="Systems configuration"/>
    <Transition name="fade" mode="out-in">
      <div class="grid grid-cols-3" v-if="isPageLoading">
        <BigLoadingBlock
          class="h-90 lg:h-100 xl:h-120 w-full"
          v-for="(_, index) in [0,1,2]"
          :key="index"/>
      </div>
      <div
        class=" px-6 py-2 pr-3 grid sm:grid-cols-2 md:grid-cols-3 gap-6 max-h-[85vh] overflow-y-auto"
        v-else>
        <NagiosCard
          :system="systemsConfig[2]!"
          :is-unwrapped="isUnwrapped('nagios')"
          @edit="unwrap('nagios')"
          @cancel="wrap"
          @save="wrap"
        />
        <WazuhCard
          :system="systemsConfig[1]!"
          :is-unwrapped="isUnwrapped('wazuh')"
          @edit="unwrap('wazuh')"
          @cancel="wrap"
          @save="wrap"
        />
        <ZabbixCard
          :system="systemsConfig[0]!"
          :is-unwrapped="isUnwrapped('zabbix')"
          @edit="unwrap('zabbix')"
          @cancel="wrap"
          @save="wrap"
        />
      </div>
    </Transition>
  </div>
</template>
