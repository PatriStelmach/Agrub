<script setup lang="ts">
import TopH1Div from "@/helpers_components/TopH1Div.vue";
import GridCardTransitionGroup from "@/helpers_components/loaders/GridCardTransitionGroup.vue";
import {computed, onMounted, ref} from "vue";
import {useClientSearchFilter} from "@/composables/useClientSearchFilter.ts";
import {type ApiKey} from "@/types/types.ts";
import {toast} from "vue-sonner";
import ApiKeyCard from "@/pages/settings/api_keys/ApiKeyCard.vue";
import {Button} from "@/components/ui/button";
import {IconLockCode} from "@tabler/icons-vue";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {Search} from "lucide-vue-next";
import {ButtonGroup, ButtonGroupSeparator} from "@/components/ui/button-group";
import CreateApiKey from "@/pages/settings/api_keys/CreateApiKey.vue";
import {useSettingStore} from "@/stores/settingStore.ts";
import BigLoadingBlock from "@/helpers_components/loaders/BigLoadingBlock.vue";

const settingsStore = useSettingStore()
onMounted(async () => {
  if(settingsStore.apiKeys.length === 0 || !settingsStore.apiKeys) {
    await settingsStore.getApiKeysRequest()
      .catch(error => toast.error(`Error retrieving api keys: ${error}`))
      .finally(() =>  isPageLoading.value = false)
  }
  else isPageLoading.value = false
})

const apiKeys = computed(() => settingsStore.apiKeys)
const isPageLoading = ref(true);
const { filteredData, searchFilter } = useClientSearchFilter<ApiKey>( () => apiKeys.value, (key) => key.description);
</script>

<template>
  <div>
    <TopH1Div h1="API Keys">
      <ButtonGroup>
        <CreateApiKey
          action-type="create"
        >
          <Button
            variant="green_outline">
            Generate new key
            <IconLockCode/>
          </Button>
        </CreateApiKey>
        <ButtonGroupSeparator/>
        <InputGroup  >
          <InputGroupInput
            v-model="searchFilter"
            type="search"
            placeholder="Search api keys..."/>
          <InputGroupAddon>
            <Search/>
          </InputGroupAddon>
        </InputGroup>
      </ButtonGroup>
    </TopH1Div>

    <Transition name="fade" mode="out-in">
      <div class="grid grid-cols-3" v-if="isPageLoading">
        <BigLoadingBlock
          class="h-72 lg:h-100 xl:h-120 w-full"
          v-for="(_, index) in [0,1,2,4,5,6,7,8,9]"
          :key="index"/>
      </div>
      <GridCardTransitionGroup
        class=" px-6 py-2 grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3  lg:grid-cols-4 gap-6 max-h-[90vh] overflow-y-auto"
        v-else>
        <ApiKeyCard
          v-for="apiKey in filteredData"
          :key="apiKey.id"
          :propsAPiKey="apiKey"
        />
      </GridCardTransitionGroup>
    </Transition>
  </div>
</template>
