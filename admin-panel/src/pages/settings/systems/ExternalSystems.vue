<script setup lang="ts">
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {useSettingStore} from "@/stores/settingStore.js";
import {computed, onMounted, ref, watchEffect} from "vue";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import type {
  MonitoringSystemsConfig,
} from "@/types/types.js";
import {toast} from "vue-sonner";
import NagiosCard from "@/pages/settings/systems/NagiosCard.vue";
import {useWrapping} from "@/composables/useWrapping";
import WazuhCard from "@/pages/settings/systems/WazuhCard.vue";
import ZabbixCard from "@/pages/settings/systems/ZabbixCard.vue";
import {useRoute, useRouter} from "vue-router";

const isPageLoading = ref(true);
const settingStore = useSettingStore()
const route = useRoute()
const router = useRouter()
const systemsConfig = computed(() => settingStore.systemsConfig)
onMounted(async () => {
  if (!settingStore.systemFullSettings){
    await settingStore.getSystemFullSettingsRequest()
      .catch((err) => toast.error(err.message))
      .finally(() => isPageLoading.value = false)
  }
  else isPageLoading.value = false
})

const {
  isUnwrapped,
  unwrap,
  unwrappedItem,
} = useWrapping<MonitoringSystemsConfig>(systemsConfig, 'name')

watchEffect(() => {
  if(route.params.system) {
    unwrap(String(route.params.system))
  }
  else {
    unwrappedItem.value = null
  }
})

const onCloseAndSave = () => {
  router.replace({path: '/settings/systems'})
}

const onEdit = (system: string) => {
  router.replace({path: `/settings/systems/${system}`})
}

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
        class=" px-6 py-2 grid sm:grid-cols-2 md:grid-cols-3 gap-6 max-h-[90vh] overflow-y-auto"
        v-else>
        <NagiosCard
          v-if="systemsConfig[2]"
          :system="systemsConfig[2]"
          :is-unwrapped="isUnwrapped('NAGIOS')"
          @edit="onEdit('NAGIOS')"
          @cancel="onCloseAndSave"
          @save="onCloseAndSave"
        />
        <WazuhCard
          v-if="systemsConfig[1]"
          :system="systemsConfig[1]"
          :is-unwrapped="isUnwrapped('WAZUH')"
          @edit="onEdit('WAZUH')"
          @cancel="onCloseAndSave"
          @save="onCloseAndSave"
        />
        <ZabbixCard
          v-if="systemsConfig[0]"
          :system="systemsConfig[0]"
          :is-unwrapped="isUnwrapped('ZABBIX')"
          @edit="onEdit('ZABBIX')"
          @cancel="onCloseAndSave"
          @save="onCloseAndSave"
        />
      </div>
    </Transition>
  </div>
</template>
