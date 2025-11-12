<template>
  <span :class="tagClasses" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
    {{ categoryName }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  category: string
}

const props = defineProps<Props>()

const categoryConfig = {
  shopping: { name: 'Shopping', class: 'bg-purple-100 text-purple-800' },
  food: { name: 'Food & Dining', class: 'bg-orange-100 text-orange-800' },
  travel: { name: 'Travel', class: 'bg-blue-100 text-blue-800' },
  entertainment: { name: 'Entertainment', class: 'bg-pink-100 text-pink-800' },
  utilities: { name: 'Utilities', class: 'bg-yellow-100 text-yellow-800' },
  healthcare: { name: 'Healthcare', class: 'bg-red-100 text-red-800' },
  education: { name: 'Education', class: 'bg-indigo-100 text-indigo-800' },
  other: { name: 'Other', class: 'bg-gray-100 text-gray-800' }
}

const categoryName = computed(() => {
  return categoryConfig[props.category as keyof typeof categoryConfig]?.name || 'Other'
})

const tagClasses = computed(() => {
  return categoryConfig[props.category as keyof typeof categoryConfig]?.class || 'bg-gray-100 text-gray-800'
})
</script>
