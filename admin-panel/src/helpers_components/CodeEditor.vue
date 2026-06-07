<script setup lang="ts">
import {ref} from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue: string
    /** Ile spacji ma robić jeden Tab. 0, żeby wstawiać prawdziwy znak \t */
    tabSize?: number
  }>(),
  {tabSize: 2},
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const textareaRef = ref<HTMLTextAreaElement | null>(null)

const indentUnit = () => (props.tabSize > 0 ? ' '.repeat(props.tabSize) : '\t')

function setValue(newValue: string, selStart: number, selEnd: number) {
  emit('update:modelValue', newValue)
  // przywróć zaznaczenie po aktualizacji modelu
  requestAnimationFrame(() => {
    const el = textareaRef.value
    if (!el) return
    el.selectionStart = selStart
    el.selectionEnd = selEnd
  })
}

function handleKeydown(e: KeyboardEvent) {
  const el = e.currentTarget as HTMLTextAreaElement
  const value = el.value
  const start = el.selectionStart
  const end = el.selectionEnd
  const indent = indentUnit()

  // --- TAB / SHIFT+TAB ---
  if (e.key === 'Tab') {
    e.preventDefault()

    const selectsMultipleLines = start !== end && value.slice(start, end).includes('\n')

    if (selectsMultipleLines) {
      // znajdź początek pierwszej zaznaczonej linii
      const lineStart = value.lastIndexOf('\n', start - 1) + 1
      const before = value.slice(0, lineStart)
      const block = value.slice(lineStart, end)
      const after = value.slice(end)

      let newBlock: string
      let delta = 0
      let firstLineDelta = 0

      if (e.shiftKey) {
        // usuń wcięcie z początku każdej linii (jeśli jest)
        newBlock = block
          .split('\n')
          .map((line, i) => {
            if (line.startsWith(indent)) {
              const removed = indent.length
              if (i === 0) firstLineDelta = -removed
              delta -= removed
              return line.slice(indent.length)
            }
            // fallback: usuń pojedynczą spację/tab, jeśli jest
            if (line.startsWith('\t') || line.startsWith(' ')) {
              if (i === 0) firstLineDelta = -1
              delta -= 1
              return line.slice(1)
            }
            return line
          })
          .join('\n')
      } else {
        // dodaj wcięcie na początku każdej linii
        const lines = block.split('\n')
        newBlock = lines.map((line) => indent + line).join('\n')
        firstLineDelta = indent.length
        delta = indent.length * lines.length
      }

      setValue(before + newBlock + after, start + firstLineDelta, end + delta)
      return
    }

    // pojedyncza linia / brak zaznaczenia
    if (e.shiftKey) {
      // usuń wcięcie z początku bieżącej linii
      const lineStart = value.lastIndexOf('\n', start - 1) + 1
      const lineHead = value.slice(lineStart, lineStart + indent.length)
      if (lineHead === indent) {
        const newValue = value.slice(0, lineStart) + value.slice(lineStart + indent.length)
        setValue(newValue, Math.max(start - indent.length, lineStart), Math.max(end - indent.length, lineStart))
      }
      return
    }

    // zwykły Tab — wstaw wcięcie w miejscu kursora
    const newValue = value.slice(0, start) + indent + value.slice(end)
    setValue(newValue, start + indent.length, start + indent.length)
    return
  }

  // --- ENTER: kontynuuj wcięcie poprzedniej linii ---
  if (e.key === 'Enter') {
    const lineStart = value.lastIndexOf('\n', start - 1) + 1
    const currentLine = value.slice(lineStart, start)
    const match = currentLine.match(/^[ \t]*/)
    const leading = match ? match[0] : ''

    // bonus: jeśli linia kończy się na { [ ( — dorzuć dodatkowe wcięcie
    const trimmed = value.slice(lineStart, start).trimEnd()
    const extra = /[{[(]$/.test(trimmed) ? indent : ''

    if (leading.length === 0 && extra.length === 0) return

    e.preventDefault()
    const insert = '\n' + leading + extra
    const newValue = value.slice(0, start) + insert + value.slice(end)
    const caret = start + insert.length
    setValue(newValue, caret, caret)
    return
  }
}

function onInput(e: Event) {
  emit('update:modelValue', (e.target as HTMLTextAreaElement).value)
}
</script>

<template>
  <textarea
    ref="textareaRef"
    class="code-area min-h-96 h-full m-2 w-95/100 blue-badge-focus border-input"
    spellcheck="false"
    autocomplete="off"
    autocapitalize="off"
    :value="modelValue"
    @input="onInput"
    @keydown="handleKeydown"
  />
</template>
