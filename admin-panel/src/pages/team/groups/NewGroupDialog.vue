<script setup lang="ts">
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTrigger
} from "@/components/ui/dialog";
import { IconUsersGroup, IconPencilPlus} from "@tabler/icons-vue";
import FormInput from "@/helpers_components/form/FormInput.vue";
import {Button} from "@/components/ui/button";
import {useForm} from "vee-validate";
import {groupSchema} from "@/helpers_functions/formSchemas.ts";
import {ref} from "vue";
import {createGroupRequest} from "@/helpers_functions/requests.ts";
import {toast} from "vue-sonner";
import {useRouter} from "vue-router";

const isLoading = ref(false);
const { handleSubmit } = useForm({
  validationSchema: groupSchema,
  initialValues: { groupName: ''}
});
const router = useRouter();

const onSubmit = handleSubmit(async (data) => {
  isLoading.value = true
  await createGroupRequest(data.groupName)
    .then((res) => router.push({ path: `/groups/edit_group/${res.id}/${res.name}`}))
    .catch((e) => toast.error(`Error creating new group: ${e}`))
    .finally(() => isLoading.value = false)
})

</script>

<template>
  <Dialog>
    <DialogTrigger as-child>
      <slot/>
    </DialogTrigger>
    <DialogContent>
      <DialogHeader>
        New group
      </DialogHeader>
      <DialogDescription>
        Create a new user group
      </DialogDescription>
        <form class="grid items-center gap-y-4" id="new-group" @submit="onSubmit">
          <FormInput class=" text-comment mr-1" name="groupName" label="Group name" orientation="vertical">
            <IconUsersGroup />
          </FormInput>
          <Button
            variant="blue_outline"
            class="w-full"
            type="submit"
            form="new-group"
          >
            Create
            <IconPencilPlus/>
          </Button>
        </form>
    </DialogContent>
  </Dialog>
</template>
