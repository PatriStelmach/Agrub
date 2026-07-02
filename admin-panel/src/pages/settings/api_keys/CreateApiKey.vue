<script setup lang="ts">

import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {smallNameLabel} from "@/assets/cssFunctions.ts";
import {Button} from "@/components/ui/button";
import {IconCodeDots, IconLoader, IconSend2} from "@tabler/icons-vue";
import {Input} from "@/components/ui/input";
import {ref} from "vue";
import {useSettingStore} from "@/stores/settingStore.ts";
import {toast} from "vue-sonner";

const settingsStore = useSettingStore();
const description = ref<string>("");
const isLoading = ref(false);
const onSubmit = async () => {
  isLoading.value = true;
  await settingsStore.generateApiKeyRequest(description.value)
    .then(() => toast.success(`New API key: "${description.value}" has been created successfully.`))
    .catch((err) => toast.error(`Error generating key: ${err.message}`))
    .finally(() => {
      description.value = "";
      isLoading.value = false
    })
}
</script>

<template>
  <Popover>
    <PopoverTrigger as-child>
      <slot/>
    </PopoverTrigger>
    <PopoverContent>
      <div class="space-y-2">
        <div class="flex items-center space-x-2">
          <IconCodeDots class="text-comment"/>
          <h1 :class="smallNameLabel">Description</h1>
        </div>
        <h2 class="text-xs text-comment">Set description for your key</h2>
        <h2 class="text-xs text-comment">For example "ZABBIX token"</h2>
        <div class="flex items-center space-x-2">
          <Input
            v-model="description"
            name="description"
            class="h-8"
          />
          <Button
            @click="onSubmit"
            class="h-8"
            variant="green_outline"
          >
            <IconSend2 v-if="!isLoading"/>
            <IconLoader v-else class="animate-spin"/>
          </Button>
        </div>
      </div>
    </PopoverContent>
  </Popover>
</template>
