<script setup lang="ts">
import {
  Sheet,
  SheetContent,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
  SheetClose, SheetDescription,
} from '@/components/ui/sheet'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Button } from '@/components/ui/button'
import {IconKey, IconTool, IconDeviceFloppy, IconLoader} from '@tabler/icons-vue'
import { inputText, nameLabel } from '@/assets/cssFunctions'
import { useUserStore } from "@/stores/userStore.ts"
import { ref } from "vue"
import type { User } from "@/types/types"
import MyTagInput from "@/helpers/MyTagInput.vue"
import {Field, FieldError, FieldGroup} from "@/components/ui/field";
import {toTypedSchema} from "@vee-validate/zod";
import z from "zod"
import { useForm, Field as VeeField } from 'vee-validate'
import MyFieldLabel from "@/helpers/MyFieldLabel.vue";
import DialogLabel from "@/helpers/DialogLabel.vue";

const userStore = useUserStore()
const props = defineProps<{
  user: User
}>()
const isLoading = ref<boolean>(false)
const updatedUser = ref<User>({...props.user})

const formSchema = toTypedSchema(
  z.object({
    email: z
      .string()
      .email('Invalid email address'),
    firstname: z
      .string()
      .min(2, 'Firstname must be at least 2 characters.'),
    surname: z
      .string()
      .min(2, 'Firstname must be at least 2 characters.')
  }),
)

const { handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    email: '',
    firstname: '',
    surname: '',
  },
})
// const onSubmit = handleSubmit(async (data) => {
//   isLoading.value = true
//   try {
//     if(!await userStore.
//       showAlert.value = true
//   }
//   catch { showAlert.value = true }
//   finally { isLoading.value = false }
// })


</script>

<template>
  <Sheet >
    <SheetTrigger as-child>
      <slot  />
    </SheetTrigger>
    <SheetContent side="left" >
        <SheetHeader>
          <SheetTitle>Edit user</SheetTitle>
          <SheetDescription>Change privileges and user data</SheetDescription>
        </SheetHeader>
        <form
          id="create-user-form" @submit="onSubmit"
          class="px-4 space-y-4">
          <div class="grid space-y-2 w-3/4">
            <Avatar class="size-12 rounded-lg">
              <AvatarFallback class="rounded-full text-xl grayscale">
                {{
                  updatedUser.firstname.slice(0, 1).toUpperCase() + updatedUser.surname.slice(0, 1).toUpperCase()
                }}
              </AvatarFallback>
            </Avatar>
            <FieldGroup class=" space-x-2">

              <VeeField v-slot="{ field, errors }" name="firstname">
                <Field
                  :data-invalid="!!errors.length"
                  class="w-fit">
                  <MyFieldLabel text="Firstname" :class="nameLabel" for="firstname"/>
                  <Input
                    v-bind="field"
                    placeholder="Firstname..."
                    autocomplete="firstname"
                    :class="inputText"
                    id="firstname"
                    v-model="updatedUser.firstname"
                  />
                  <FieldError v-if="errors.length" :errors="errors" />
                </Field>
              </VeeField>
              <VeeField v-slot="{ field, errors }" name="surname">
                <Field
                  :data-invalid="!!errors.length"
                  class="w-fit ">
                  <MyFieldLabel text="Surname" :class="nameLabel" for="surname">Surname</MyFieldLabel>
                  <Input
                    v-bind="field"
                    placeholder="Surname..."
                    autocomplete="surname"
                    :class="inputText"
                    id="surname"
                    v-model="updatedUser.surname"
                  />
                  <FieldError v-if="errors.length" :errors="errors" />
                </Field>
              </VeeField>

              <VeeField v-slot="{ field, errors }" name="email">
                <Field
                  class="w-fit "
                  :data-invalid="!!errors.length">
                  <MyFieldLabel text="e-mail" :class="nameLabel" for="email">e-mail</MyFieldLabel>
                  <Input
                    v-bind="field"
                    placeholder="user@domain.com"
                    :aria-invalid="!!errors.length"
                    autocomplete="email"
                    :class="inputText"
                    id="email"
                    v-model="updatedUser.email"
                  />
                  <FieldError v-if="errors.length" :errors="errors" />
                </Field>
              </VeeField>
            </FieldGroup>
            <div class="grid mt-4 space-y-6">
              <div>
                <DialogLabel text="Role" for="role" class="mb-2"/>
                <RadioGroup id="role" v-model="updatedUser.role" :default-value="updatedUser.role" class="grid **:text-md **:xl:text-lg **:text-label">
                  <div class="flex items-center space-x-2">
                    <RadioGroupItem id="radio-administrator" value="ADMINISTRATOR" />
                    <IconKey /><Label class="cursor-pointer" for="radio-administrator">ADMINISTRATOR</Label>
                  </div>
                  <div class="flex items-center space-x-2">
                    <RadioGroupItem id="radio-technician" value="TECHNICIAN" />
                    <IconTool class="rotate-90" /><Label class="cursor-pointer" for="radio-technician">TECHNICIAN</Label>
                  </div>
                </RadioGroup>
              </div>
              <MyTagInput
                v-model:tags="updatedUser.groups"
                :all-tags="userStore.allGroups.map(g => g.name)"
                input-id="user-groups-input"
                :can-add-new="false"
                tags-label="Groups"
              />
            </div>
          </div>


        </form>
        <SheetFooter>
          <SheetClose as-child>
            <Field>
              <Button
                v-if="!isLoading"
                type="submit" form="edit-user-form"
                class="flex items-center"
                variant="green_outline">
                Save <IconDeviceFloppy />
              </Button>
              <IconLoader v-else class="animate-spin duration-75"/>
            </Field>
          </SheetClose>
          <SheetClose as-child>
            <Button variant="red_outline">Cancel</Button>
          </SheetClose>
        </SheetFooter>
    </SheetContent>

  </Sheet>
</template>
