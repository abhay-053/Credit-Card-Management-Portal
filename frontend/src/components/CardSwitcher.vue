<template>
  <div class="relative">
    <button
      @click="toggleDropdown"
      class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:bg-gray-50 text-left flex items-center justify-between"
    >
      <span class="text-gray-500">{{ displayText }}</span>
      <svg class="w-4 h-4 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': showDropdown }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
      </svg>
    </button>
    
    <!-- Custom Card Dropdown Overlay -->
    <div v-if="showDropdown" class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
      <button
        @click="selectCard('')"
        class="w-full px-4 py-3 text-sm text-left hover:bg-yellow-200 hover:text-gray-900 transition-colors duration-200 border-b border-gray-100 text-gray-500 font-medium"
      >
        {{ showAllOption ? 'All Cards' : 'Choose your card' }}
      </button>
      <button
        v-for="card in cards"
        :key="card.id"
        @click="selectCard(card.id.toString())"
        class="w-full px-4 py-3 text-sm text-left hover:bg-yellow-200 hover:text-gray-900 transition-colors duration-200 border-b border-gray-100 last:border-b-0"
      >
        <div class="flex items-center justify-between">
          <span class="text-gray-700 font-medium">
            {{ card.cardType?.networkType || card.cardType || 'VISA' }} ****{{ card.lastFour || card.cardNumber?.slice(-4) || '****' }}
          </span>
          <span v-if="showAvailableLimit" class="text-xs text-gray-500">
            ₹{{ formatNumber(card.availableLimit || 0) }} available
          </span>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Card {
  id: number
  cardType?: {
    networkType?: string
  }
  cardType?: string
  lastFour?: string
  cardNumber?: string
  availableLimit?: number
}

interface Props {
  cards: Card[]
  selectedCardId?: number | null
  showAllOption?: boolean
  showAvailableLimit?: boolean
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  showAllOption: false,
  showAvailableLimit: false,
  placeholder: 'Select card'
})

const emit = defineEmits<{
  'card-selected': [cardId: string]
}>()

const showDropdown = ref(false)

const displayText = computed(() => {
  if (!props.selectedCardId) {
    return props.showAllOption ? 'All Cards' : props.placeholder
  }
  
  const selectedCard = props.cards.find(card => card.id === props.selectedCardId)
  if (!selectedCard) {
    return props.showAllOption ? 'All Cards' : props.placeholder
  }
  
  const cardType = selectedCard.cardType?.networkType || selectedCard.cardType || 'VISA'
  const lastFour = selectedCard.lastFour || selectedCard.cardNumber?.slice(-4) || '****'
  
  return `${cardType} ****${lastFour}`
})

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const selectCard = (cardId: string) => {
  emit('card-selected', cardId)
  showDropdown.value = false
}

const formatNumber = (value: number) => {
  return new Intl.NumberFormat('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value)
}
</script>