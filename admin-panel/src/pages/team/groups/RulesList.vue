<script setup lang="ts">
import {hoverListRow} from "@/assets/cssFunctions.ts";
import ShowRuleDiv from "@/pages/team/groups/ShowRuleDiv.vue";
import type {Rule} from "@/types/types.ts";
import {useWrapping} from "@/composables/unwrapping.ts";
import {updateRuleRequest} from "@/helpers_functions/requests.ts";
import EditRuleDiv from "@/pages/team/groups/EditRuleDiv.vue";

const loadingGroupsDelete = defineModel<number[]>('loadingGroupsDelete', {required: true})
const rules = defineModel<Rule[]>('rules', {required: true})

const emit = defineEmits<{
  'delete-rule': [number]
}>()

const {unwrap, isUnwrapped, wrap, save, unwrappedItem, originalItem} = useWrapping<Rule>(rules)

const onEditSave = (data: Rule) => {
  Object.assign(unwrappedItem.value!, data)
  save(async () => await updateRuleRequest(unwrappedItem.value!))
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
            class=""
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
