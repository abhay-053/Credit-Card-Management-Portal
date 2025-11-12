<template>
  <button
    :class="buttonClasses"
    :disabled="disabled || loading"
    @click="$emit('click', $event)"
  >
    <div v-if="loading" class="flex items-center justify-center">
      <svg class="animate-spin -ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      <span>{{ loadingText || 'Loading...' }}</span>
    </div>
    <div v-else class="flex items-center justify-center">
      <svg v-if="icon" :class="iconClasses" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="icon"></path>
      </svg>
      <span v-if="$slots.default"><slot /></span>
    </div>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'success'
  size?: 'sm' | 'md' | 'lg' | 'xl'
  disabled?: boolean
  loading?: boolean
  loadingText?: string
  icon?: string
  fullWidth?: boolean
  rounded?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  fullWidth: false,
  rounded: true
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const buttonClasses = computed(() => {
  const baseClasses = [
    'inline-flex',
    'items-center',
    'justify-center',
    'font-semibold',
    'transition-all',
    'duration-200',
    'transform',
    'hover:-translate-y-0.5',
    'focus:outline-none',
    'focus:ring-2',
    'focus:ring-offset-2',
    'disabled:opacity-50',
    'disabled:cursor-not-allowed',
    'disabled:transform-none'
  ]


  const sizeClasses = {
    sm: ['px-3', 'py-2', 'text-sm'],
    md: ['px-4', 'py-3', 'text-sm'],
    lg: ['px-6', 'py-4', 'text-base'],
    xl: ['px-8', 'py-5', 'text-lg']
  }

  const variantClasses = {
    primary: [
      'bg-gradient-to-r',
      'from-blue-600',
      'to-blue-700',
      'text-white',
      'hover:from-blue-700',
      'hover:to-blue-800',
      'shadow-lg',
      'hover:shadow-xl',
      'focus:ring-blue-500'
    ],
    secondary: [
      'bg-gradient-to-r',
      'from-gray-600',
      'to-gray-700',
      'text-white',
      'hover:from-gray-700',
      'hover:to-gray-800',
      'shadow-lg',
      'hover:shadow-xl',
      'focus:ring-gray-500'
    ],
    outline: [
      'border-2',
      'border-gray-300',
      'text-gray-700',
      'bg-white',
      'hover:bg-gray-50',
      'hover:border-gray-400',
      'shadow-sm',
      'hover:shadow-md',
      'focus:ring-gray-500'
    ],
    ghost: [
      'text-gray-700',
      'bg-transparent',
      'hover:bg-gray-100',
      'hover:text-gray-900',
      'focus:ring-gray-500'
    ],
    danger: [
      'bg-gradient-to-r',
      'from-red-600',
      'to-red-700',
      'text-white',
      'hover:from-red-700',
      'hover:to-red-800',
      'shadow-lg',
      'hover:shadow-xl',
      'focus:ring-red-500'
    ],
    success: [
      'bg-gradient-to-r',
      'from-green-600',
      'to-green-700',
      'text-white',
      'hover:from-green-700',
      'hover:to-green-800',
      'shadow-lg',
      'hover:shadow-xl',
      'focus:ring-green-500'
    ]
  }

  // Rounded classes
  const roundedClasses = props.rounded ? ['rounded-xl'] : ['rounded-md']

  // Full width
  const widthClasses = props.fullWidth ? ['w-full'] : []

  return [
    ...baseClasses,
    ...sizeClasses[props.size],
    ...variantClasses[props.variant],
    ...roundedClasses,
    ...widthClasses
  ].join(' ')
})

const iconClasses = computed(() => {
  const sizeClasses = {
    sm: 'h-4 w-4',
    md: 'h-5 w-5',
    lg: 'h-6 w-6',
    xl: 'h-7 w-7'
  }

  const spacingClasses = {
    sm: 'mr-1',
    md: 'mr-2',
    lg: 'mr-3',
    xl: 'mr-3'
  }

  return [
    sizeClasses[props.size],
    spacingClasses[props.size]
  ].join(' ')
})
</script>
