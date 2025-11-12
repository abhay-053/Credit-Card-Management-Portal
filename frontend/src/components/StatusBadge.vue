<template>
  <span :class="badgeClasses" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
    <svg v-if="statusIcon" :class="iconClasses" class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
      <path :d="statusIcon"></path>
    </svg>
    {{ statusName }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  status: string
}

const props = defineProps<Props>()

const statusConfig = {
  // Backend enum values (uppercase)
  COMPLETED: { 
    name: 'Completed', 
    class: 'bg-green-100 text-green-800',
    icon: 'M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z'
  },
  PENDING: { 
    name: 'Pending', 
    class: 'bg-yellow-100 text-yellow-800',
    icon: 'M10 12a2 2 0 100-4 2 2 0 000 4z'
  },
  FAILED: { 
    name: 'Failed', 
    class: 'bg-red-100 text-red-800',
    icon: 'M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z'
  },
  REFUNDED: { 
    name: 'Refunded', 
    class: 'bg-blue-100 text-blue-800',
    icon: 'M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z'
  },
  // BNPL specific statuses
  BNPL_ACTIVE: { 
    name: 'BNPL Active', 
    class: 'bg-purple-100 text-purple-800',
    icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z'
  },
  BNPL_COMPLETED: { 
    name: 'BNPL Completed', 
    class: 'bg-green-100 text-green-800',
    icon: 'M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z'
  },
  BNPL_DEFAULTED: { 
    name: 'BNPL Defaulted', 
    class: 'bg-red-100 text-red-800',
    icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z'
  },
  // Legacy lowercase values (for backward compatibility)
  completed: { 
    name: 'Completed', 
    class: 'bg-green-100 text-green-800',
    icon: 'M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z'
  },
  pending: { 
    name: 'Pending', 
    class: 'bg-yellow-100 text-yellow-800',
    icon: 'M10 12a2 2 0 100-4 2 2 0 000 4z'
  },
  failed: { 
    name: 'Failed', 
    class: 'bg-red-100 text-red-800',
    icon: 'M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z'
  },
  refunded: { 
    name: 'Refunded', 
    class: 'bg-blue-100 text-blue-800',
    icon: 'M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z'
  },
  // BNPL lowercase versions
  bnpl_active: { 
    name: 'BNPL Active', 
    class: 'bg-purple-100 text-purple-800',
    icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z'
  },
  bnpl_completed: { 
    name: 'BNPL Completed', 
    class: 'bg-green-100 text-green-800',
    icon: 'M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z'
  },
  bnpl_defaulted: { 
    name: 'BNPL Defaulted', 
    class: 'bg-red-100 text-red-800',
    icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z'
  },
  // Additional statuses
  authorized: { 
    name: 'Authorized', 
    class: 'bg-blue-100 text-blue-800',
    icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'
  },
  settled: { 
    name: 'Settled', 
    class: 'bg-green-100 text-green-800',
    icon: 'M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z'
  },
  declined: { 
    name: 'Declined', 
    class: 'bg-red-100 text-red-800',
    icon: 'M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z'
  }
}

const statusName = computed(() => {
  return statusConfig[props.status as keyof typeof statusConfig]?.name || 'Unknown'
})

const badgeClasses = computed(() => {
  return statusConfig[props.status as keyof typeof statusConfig]?.class || 'bg-gray-100 text-gray-800'
})

const statusIcon = computed(() => {
  return statusConfig[props.status as keyof typeof statusConfig]?.icon || null
})

const iconClasses = computed(() => {
  return 'text-current'
})
</script>
