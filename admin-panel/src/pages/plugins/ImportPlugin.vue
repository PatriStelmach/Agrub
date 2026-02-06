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
import {Input} from "@/components/ui/input";
import {ref} from "vue";
import {
  IconUpload
} from "@tabler/icons-vue";
import {ButtonGroup} from "@/components/ui/button-group";

const files = ref<File[]>([]);

function handleFileUpload(e: Event)
{
  const input = e.target as HTMLInputElement;
  const filesArray= Array.from(input?.files || [])
  files.value = files.value.concat(filesArray)
}

function removeFile(index:number)
{
  files.value.splice(index,1);
}


</script>

<template>

  <Empty>
    <EmptyHeader>
      <EmptyMedia variant="icon">
        <FolderCode />
      </EmptyMedia>
      <EmptyTitle>Import a plugin</EmptyTitle>
      <EmptyDescription>
        Choose your script to upload.
      </EmptyDescription>
    </EmptyHeader>
    <EmptyContent>
      <Label for="uploadedPlugin"
             class="text-[2vh] flex justify-between py-[1vh] px-[2vw] items-center
              cursor-pointer bg-blue-600 rounded-lg
              hover:bg-blue-500 transition-all duration-100 text-white"
      >
        Upload
        <IconUpload class="ml-[1vh]" size="1vw"/>
      </Label>
      <Input
        type="file"
        id="uploadedPlugin"
        @change="handleFileUpload"
        accept=".py, .sh, .bash, .ps1, .psm1"
        multiple
        hidden
      />
      <ul>
        <li class="list-none my-[1vh] items-center flex justify-between border-b border-blue-200 p-2"
            v-for="(file, index) in files"
            :key="file.name">
          <span class="flex justify-start w-[15vw]">{{file.name}} </span>
          <span class="lex justify-start  w-[5vw]">{{file.size}} KB</span>
          <ButtonGroup>
            <Button @click="removeFile(index)" variant="destructive" class="h-[3vh] ml-[1vw]">Remove</Button>
            <Button variant="green_outline" class="h-[3vh]">Add</Button>
          </ButtonGroup>
        </li>
      </ul>
    </EmptyContent>
    <Button variant="link" as-child class="text-muted-foreground" size="sm">
      <a href="#">
        Learn More <ArrowUpRightIcon />
      </a>
    </Button>
  </Empty>

</template>
