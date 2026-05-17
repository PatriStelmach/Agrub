<script setup lang="ts">
import { ArrowUpRightIcon, FolderCode } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty'
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import {useSystemStore} from "@/stores/systemStore.ts";
import {computed, onMounted, ref} from "vue";
import GridCardTransitionGroup from "@/helpers_components/loaders/GridCardTransitionGroup.vue";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";
import NagiosConfigCard from "@/pages/systems/NagiosConfigCard.vue";
import WazuhConfigCard from "@/pages/systems/WazuhConfigCard.vue";
import ZabbixConfigCard from "@/pages/systems/ZabbixConfigCard.vue";
import type {NagiosConfig, WazuhConfig, ZabbixConfig} from "@/types/types.ts";
import {toast} from "vue-sonner";

const isPageLoading = ref(true);
const systemsStore = useSystemStore()

onMounted(async () => {
  await Promise.all([
    systemsStore.getAllStatusesRequest(),
    systemsStore.getSystemConfigRequest()
  ])
    .catch((err) => toast.error(err.message))
    .finally(() => isPageLoading.value = false)
})

const isAnySystemConnected = computed(() =>
  systemsStore.systemsStatus.nagios_enabled ||
  systemsStore.systemsStatus.wazuh_enabled ||
  systemsStore.systemsStatus.zabbix_enabled
)

function updateNagios(config: NagiosConfig) {
  systemsStore.updateSystemConfigRequest(config)
}

function updateWazuh(config: WazuhConfig) {
  systemsStore.updateSystemConfigRequest(config)
}

function updateZabbix(config: ZabbixConfig) {
  systemsStore.updateSystemConfigRequest(config)
}
</script>

<template>
  <div>
    <TopH1Div h1="Connected systems"/>
    <Transition name="fade" mode="out-in">
      <div class="grid grid-cols-3" v-if="isPageLoading">
        <BigLoadingBlock
          class="h-120 w-full"
          v-for="(_, index) in [0,1,2]"
          :key="index"/>
      </div>
      <GridCardTransitionGroup v-else-if="!isPageLoading && isAnySystemConnected">
        <NagiosConfigCard
          v-if="systemsStore.systemsStatus.nagios_enabled && systemsStore.nagiosConfig"
          :config="systemsStore.nagiosConfig"
          @save="updateNagios"
        />
        <WazuhConfigCard
          v-if="systemsStore.systemsStatus.wazuh_enabled && systemsStore.wazuhConfig"
          :config="systemsStore.wazuhConfig"
          @save="updateWazuh"
        />
        <ZabbixConfigCard
          v-if="systemsStore.systemsStatus.zabbix_enabled && systemsStore.zabbixConfig"
          :config="systemsStore.zabbixConfig!"
          @save="updateZabbix"
        />
      </GridCardTransitionGroup>
      <Empty v-else>
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <FolderCode />
          </EmptyMedia>
          <EmptyTitle>No connected systems yet</EmptyTitle>
          <EmptyDescription>
            You haven't added any systems yet. Get started by connecting your first
            API.
          </EmptyDescription>
        </EmptyHeader>
        <EmptyContent>
          <div class="flex gap-2">
            <RouterLink to="/available_systems"><Button class="cursor-pointer">Add System</Button></RouterLink>
            <RouterLink to="/plugins_library">
              <Button class="cursor-pointer" variant="outline">
                Add plugin
              </Button>
            </RouterLink>
          </div>
        </EmptyContent>
        <Button variant="link" as-child class="text-muted-foreground" size="sm">
          <a href="#">
            Learn More <ArrowUpRightIcon />
          </a>
        </Button>
      </Empty>
    </Transition>

  </div>
</template>
