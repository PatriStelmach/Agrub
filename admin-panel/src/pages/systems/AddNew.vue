<script setup lang="ts">
import type {System} from "@/types/system.ts";
import { Button } from '@/components/ui/button'
import {
  Field,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'

const data = defineProps<{
  system: System
}>()


</script>

<template class="flex w-full mx-auto bg-background ">
  <div class="flex mx-auto items-center  ">
<form class="bg-background flex mx-auto p-[2vw] items-center justify-center shadow-[0px_0px_150px_1px] rounded-xl w-1/3 border "
      :class="{' shadow-blue-900 border-blue-900': !data.system.openSource,
           ' shadow-green-900 border-green-900': data.system.openSource}">
  <FieldGroup>
      <FieldLabel class="flex w-full text-4xl justify-between">{{data.system.name}}
      <img :alt="data.system.name+'_image'" :src="data.system.img" class=" rounded-full size-[6vh]"/>
      </FieldLabel>
    <FieldGroup>
      <Field>
        <FieldLabel for="api-key">Api Key</FieldLabel>
        <Input
        id="api-key"
        placeholder="Enter API Key"
        required
        />
      </Field>
      <Field>
        <FieldLabel for="login">Login</FieldLabel>
        <Input
        id="login"
        placeholder="Enter Login"
        required
        />
      </Field>
      <Field>
        <FieldLabel for="password">Password</FieldLabel>
        <Input
        id="password"
        placeholder="Enter Password"
        required
        />
      </Field>
    </FieldGroup>
    <Field>
      <Button>
        Submit
      </Button>
      <Button
      variant="destructive"
      @click="$emit('close')">
        Cancel
      </Button>
    </Field>
  </FieldGroup>
</form>
  </div>
</template>

