<template>
  <div :class="cardClasses">
    <div v-if="$slots.header" class="px-6 py-4 border-b border-gray-200">
      <slot name="header" />
    </div>
    <div :class="contentClasses">
      <slot />
    </div>
    <div v-if="$slots.footer" class="px-6 py-4 border-t border-gray-200">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'default' | 'elevated' | 'outlined' | 'gradient'
  padding?: 'none' | 'sm' | 'md' | 'lg'
  rounded?: boolean
  hover?: boolean
  shadow?: 'none' | 'sm' | 'md' | 'lg' | 'xl'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  padding: 'md',
  rounded: true,
  hover: false,
  shadow: 'md'
})

const cardClasses = computed(() => {
  const baseClasses = [
    'bg-white',
    'transition-all',
    'duration-300'
  ]

  // Variant classes
  const variantClasses = {
    default: ['border', 'border-gray-200'],
    elevated: ['shadow-lg'],
    outlined: ['border-2', 'border-gray-300'],
    gradient: ['bg-gradient-to-br', 'from-white', 'to-gray-50', 'border', 'border-gray-100']
  }

  // Shadow classes
  const shadowClasses = {
    none: [],
    sm: ['shadow-sm'],
    md: ['shadow-md'],
    lg: ['shadow-lg'],
    xl: ['shadow-xl']
  }

  // Rounded classes
  const roundedClasses = props.rounded ? ['rounded-2xl'] : ['rounded-lg']

  // Hover classes
  const hoverClasses = props.hover ? ['hover:shadow-xl', 'hover:scale-105', 'transform'] : []

  return [
    ...baseClasses,
    ...variantClasses[props.variant],
    ...shadowClasses[props.shadow],
    ...roundedClasses,
    ...hoverClasses
  ].join(' ')
})

const contentClasses = computed(() => {
  const paddingClasses = {
    none: [],
    sm: ['p-4'],
    md: ['p-6'],
    lg: ['p-8']
  }

  return paddingClasses[props.padding]
})
</script>
