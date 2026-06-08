<script setup lang="ts">
import {hoverListRow} from "@/assets/cssFunctions.ts";
import ShowRuleDiv from "@/pages/team/groups/ShowRuleDiv.vue";
import type {Rule} from "@/types/types.ts";
import {useWrapping} from "@/composables/useWrapping.js";
import {updateRuleRequest} from "@/helpers_functions/requests/groupsRequests.ts";
import EditRuleDiv from "@/pages/team/groups/EditRuleDiv.vue";
import {toast} from "vue-sonner";

const loadingGroupsDelete = defineModel<number[]>('loadingGroupsDelete', {required: true})
const rules = defineModel<Rule[]>('rules', {required: true})

const emit = defineEmits<{
  'delete-rule': [number]
}>()

const {hasChanged, unwrap, isUnwrapped, wrap, unwrappedItem} = useWrapping<Rule>(rules)

const onEditSave = async (data: Rule) => {
  unwrappedItem.value = data
  if (unwrappedItem.value && hasChanged()) {
    await updateRuleRequest(data)
      .then((res: Rule) => {
        toast.success('Rule updated successfully!')
        rules.value = rules.value.map(r => r.id === res.id ? res : r)
      })
      .catch(err => toast.error(`Error updating rule: ${err}`))
  }
  else {
    toast.info('No changes have been made')
  }
  wrap()
}

</script>

<template>
  <div class="w-7/10!">
    <h1 class="pb-1 mb-2 border-b-4  text-center">Group rules configuration: {{ rules.length }}</h1>
    <div class="mx-auto pb-2 border-b-4 max-h-[70vh] overflow-y-auto">
      <ul>
        <li v-if="rules.length < 1">
          <div class="my-4 w-full text-center">
            <span class=" mx-auto">No rules added yet</span>
          </div>
        </li>
        <li v-else
            v-for="rule in rules" :key="rule.id"
            :class="hoverListRow('cursor-pointer')"
        >
          <EditRuleDiv
            v-if="isUnwrapped(rule.id!)"
            :rule="unwrappedItem!"
            @save="onEditSave"
            @cancel="wrap"
          />
          <ShowRuleDiv
            v-else
            @unwrap="unwrap"
            :is-rule-deleting="loadingGroupsDelete.some(r => r === rule.id)"
            :rule="rule"
            @delete-rule="emit('delete-rule', rule.id!)"
          />
        </li>
      </ul>
    </div>
  </div>
</template>
