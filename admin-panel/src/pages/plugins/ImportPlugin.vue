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
import {computed, ref, watch} from "vue";
import {
  IconUpload
} from "@tabler/icons-vue";
import {ButtonGroup} from "@/components/ui/button-group";
import {
  IconTrash,
  IconFileImport,
  IconListCheck
} from "@tabler/icons-vue";
import {ArrowLeftIcon} from "lucide-vue-next";

const idCounter = ref(0);
const files = ref<{ id:number, file:File }[]>([]);
const checkedFiles = ref<number[]>([]);
const checkText = ref<'Check all' | 'Uncheck all'>('Check all');

function handleFileUpload(e: Event)
{
  const input = e.target as HTMLInputElement;
  const filesArray= Array.from(input?.files || []);

  const newFiles = filesArray
    .filter(file => !files.value.some(item => item.file.name === file.name))
    .map( file => ({
      file: file,
        id: idCounter.value++
    }));

  files.value = files.value.concat(newFiles);
  input.value = '';
}

function removeFiles(ids:number[])
{
  files.value = files.value.filter(item => !ids.includes(item.id));
  checkedFiles.value = [];
}

const isAllChecked = computed(() =>
{
  return files.value.length > 0 && checkedFiles.value.length === files.value.length
})


watch(isAllChecked, () =>
{
  return isAllChecked.value ? checkText.value= 'Uncheck all' : checkText.value= 'Check all'
})

function toggleAllChecked()
{
  if(!isAllChecked.value)
  {
    checkedFiles.value = files.value.map((file) => file.id)
    checkText.value = 'Uncheck all'
  }
  else
  {
    checkedFiles.value = []
    checkText.value = 'Check all'
  }
}

</script>

<template>
  <div>
    <h1 class="text-center my-[2vh] text-[3vh] border-b pb-[2vh]   md ">Importing Plugins</h1>
    <div class="mx-auto w-full">

  <ButtonGroup class="ml-[2vw]">
    <ButtonGroup>
      <Button variant="outline">
      <ArrowLeftIcon/>
    </Button>
    </ButtonGroup>
<ButtonGroup>
  <Button
    @click="toggleAllChecked"
    variant="yellow_outline">
    {{ checkText }}
    <IconListCheck/>
  </Button>
  <Button variant="green_outline">
    Add to your plugins
    <IconFileImport/>
  </Button>
  <Button
    @click="removeFiles(checkedFiles)"
    variant="red_outline">
    Remove
    <IconTrash/>
  </Button>
</ButtonGroup>

  </ButtonGroup>

  <Empty class=" ">
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
              cursor-pointer border-2 border-green-500 rounded-lg text-green-500
              hover:shadow-[0_0_20px_5px] hover:shadow-green-500 transition-all duration-100  mb-[2vh]"
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
        <li class="list-none p-2 w-[40vw]"
            v-for="(item) in files"
            :key="item.id">

          <div
            class="flex items-center justify-between py-3 px-4 rounded-md bg-linear-to-bl w-full"
            :class=
              "{ 'from-[#3475A8]/60 to-[#F9CA3A]/60 hover:from-[#3475A8]/80 hover:to-[#F9CA3A]/80'
                 : item.file.type.includes('python'),
                 'from-[#4374CC]/60 to-[#4476CF]/60 hover:from-[#4476CF]/80 hover:to-[#4374CC]/80'
                 : item.file.type.includes('powershell'),
                 'from-[#222B32]/60 to-[#4CA825]/50 hover:from-[#222B32]/60 hover:to-[#4CA825]/80'
                 : item.file.type.includes('x-sh')
                  || item.file.type.includes('x-bash') || item.file.type.includes('x-shellscript')}"
          >
            <input
              type="checkbox"
              :id="item.file.name"
              :value="item.id"
              v-model="checkedFiles"
              class="border-foreground mr-[1vw] cursor-pointer size-5 rounded-full checkbox"
            />
            <span class="flex justify-start   w-[15vw]">{{item.file.name}} </span>
            <span class="lex justify-start   w-[5vw]">{{item.file.size < 1000 ? item.file.size + ' KB' : (item.file.size/1000).toFixed(2) + ' MB'}}</span>
            <img
              v-if="item.file.type.includes('python')"
              alt="python_icon"
              src="@/components/icons/python_icon.png"
              class="size-6"
            />
            <img
              v-if="item.file.type.includes('x-sh') || item.file.type.includes('x-bash') || item.file.type.includes('x-shellscript')"
              alt="bash_icon"
              src="@/components/icons/bash_icon.png"
              class="size-6 "
            />
            <img
              v-if="item.file.type.includes('powershell')"
              alt="powershell_icon"
              src="@/components/icons/powershell_icon.png"
              class="size-6"
            />
          </div>
        </li>
      </ul>
    </EmptyContent>
    <Button variant="link" as-child class="text-muted-foreground" size="sm">
      <a href="#">
        Learn More <ArrowUpRightIcon />
      </a>
    </Button>
  </Empty>
  </div>
  </div>
</template>
