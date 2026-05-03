<script setup lang="ts">
import {pluginLibraryData} from "@/data/pluginLibrary.ts";
import MyClientPagination from "@/helpers/MyClientPagination.vue";
import PluginsLibraryTable from "@/pages/plugins/PluginsLibraryTable.vue";
import {
  IconMessageCode,
} from "@tabler/icons-vue";

import {ArrowLeftIcon, Search} from "lucide-vue-next";
import {ButtonGroup} from "@/components/ui/button-group";
import {Button} from "@/components/ui/button";
import {api_url, Language, type LibraryPlugin} from "@/types/types.ts"
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {useSearchFilter} from "@/composables/useSearchFilter.ts";
import {topButtonGroup, topDiv, topH1} from "@/assets/cssFunctions.ts";
import GoBackButton from "@/helpers/GoBackButton.vue";
import MyServerPagination from "@/helpers/MyServerPagination.vue";
import axios from "axios";

const getPluginLibraryData = async (page: number, pageSize: number, name: string, creator: string, language: Language) => {
  const response = await axios.get(`${api_url}/plugins/library`, {
    name: name,
    creator: creator,
    page_size: pageSize,

  })
}

</script>

<template>
  <div>
    <div :class="topDiv">
      <h1 :class="topH1">Plugins library</h1>
        <ButtonGroup :class="topButtonGroup">
          <ButtonGroup>
            <GoBackButton/>
            <InputGroup  >
              <InputGroupInput
                v-model="searchFilter"
                type="search"
                placeholder="Search for plugin"/>
              <InputGroupAddon>
                <Search/>
              </InputGroupAddon>
            </InputGroup>
          </ButtonGroup>
        </ButtonGroup>
      <div>
    </div>
<PluginsLibraryTable :data="tableData" />
    <MyServerPagination :total
      />
  </div>
</div>
</template>

