<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <!-- Background overlay - only covers content below navbar -->
      <div class="fixed top-16 left-0 right-0 bottom-0 bg-gray-900 bg-opacity-10 backdrop-blur-lg transition-all duration-300" @click="close"></div>

      <!-- Modal panel -->
      <div class="relative inline-block align-bottom bg-white rounded-2xl text-left overflow-hidden shadow-2xl transform transition-all duration-300 sm:my-8 sm:align-middle sm:max-w-lg sm:w-full z-10">
        <!-- Header with close button -->
        <div class="flex items-center justify-between p-4 border-b border-gray-100">
          <div class="flex items-center">
            <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center mr-3">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
              </svg>
            </div>
            <div>
              <h3 class="text-xl font-semibold text-gray-900">New Transaction</h3>
              <p class="text-sm text-gray-500">Create a payment transaction</p>
            </div>
          </div>
          <button @click="close" class="text-gray-400 hover:text-gray-600 transition-colors duration-200">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Form -->
        <form @submit.prevent="submitTransaction" class="p-5 space-y-4">
          <!-- Card Selection -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Select Card</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
                </svg>
              </div>
              <select 
                v-model="form.cardId" 
                class="block w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 bg-white appearance-none"
                required
              >
                <option value="">Choose your card</option>
                <option v-for="card in cards" :key="card.id" :value="card.id">
                  {{ card.cardType }} ****{{ card.lastFour }} ({{ card.cardName }})
                </option>
              </select>
            </div>
          </div>

          <!-- Merchant Selection -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Merchant</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                </svg>
              </div>
              <select 
                v-model="form.merchantAccountId" 
                class="block w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 bg-white appearance-none"
                required
              >
                <option value="">Select merchant</option>
                <option v-for="merchant in merchants" :key="merchant.id" :value="merchant.id">
                  {{ merchant.name }}
                </option>
              </select>
            </div>
          </div>

          <!-- Amount Input -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Amount</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <span class="text-gray-500 text-lg font-medium">₹</span>
              </div>
              <input
                v-model="form.amount"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="0.00"
                class="block w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 bg-white"
                required
              />
            </div>
          </div>

          <!-- Category Selection -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Category</label>
            <div class="relative">
              <select 
                v-model="form.category" 
                class="block w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 bg-white appearance-none"
                required
              >
                <option value="">Select category</option>
                <option value="shopping">🛍️ Shopping</option>
                <option value="food">🍽️ Food & Dining</option>
                <option value="travel">✈️ Travel</option>
                <option value="entertainment">🎬 Entertainment</option>
                <option value="utilities">⚡ Utilities</option>
                <option value="healthcare">🏥 Healthcare</option>
                <option value="education">📚 Education</option>
                <option value="other">📦 Other</option>
              </select>
            </div>
          </div>

          <!-- BNPL Toggle -->
          <div class="bg-gradient-to-r from-purple-50 to-blue-50 rounded-lg p-3 border border-purple-100">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-4 h-4 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                  </svg>
                </div>
                <div>
                  <h4 class="text-sm font-semibold text-gray-900">Buy Now, Pay Later</h4>
                  <p class="text-xs text-gray-600">Split into monthly installments</p>
                </div>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input 
                  v-model="form.isBnpl" 
                  type="checkbox" 
                  class="sr-only peer"
                />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-purple-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-purple-600"></div>
              </label>
            </div>

            <!-- BNPL Options -->
            <div v-if="form.isBnpl" class="mt-4 space-y-3 animate-fadeIn">
              <div>
                <label class="block text-xs font-medium text-gray-700 mb-1">Tenure</label>
                <select 
                  v-model="form.tenureMonths" 
                  class="block w-full px-3 py-2 border border-gray-200 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 bg-white text-sm appearance-none"
                  required
                >
                  <option value="">Select tenure</option>
                  <option value="3">3 months</option>
                  <option value="6">6 months</option>
                  <option value="9">9 months</option>
                  <option value="12">12 months</option>
                </select>
              </div>

              <div v-if="form.tenureMonths && form.amount" class="bg-white rounded-lg p-3 border border-purple-200">
                <div class="flex justify-between items-center">
                  <span class="text-sm text-gray-600">Monthly EMI:</span>
                  <span class="text-lg font-semibold text-purple-600">₹{{ formatNumber(monthlyEmi) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex space-x-3 pt-3">
            <button
              type="button"
              @click="close"
              class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 transition-colors duration-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="loading"
              class="flex-1 px-4 py-2.5 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              <span v-if="!loading">Create Transaction</span>
              <span v-else class="flex items-center justify-center">
                <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Processing...
              </span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useStore } from 'vuex'

interface Card {
  id: number
  cardType: string
  lastFour: string
  cardName: string
}

interface Merchant {
  id: number
  name: string
}

interface Props {
  isOpen: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  close: []
  'transaction-created': []
}>()

const store = useStore()

// Form data
const form = ref({
  cardId: '',
  merchantAccountId: '',
  amount: '',
  category: '',
  isBnpl: false,
  tenureMonths: ''
})

const loading = ref(false)

// Computed properties
const cards = computed(() => store.state?.cards?.cards || [])
const merchants = computed(() => store.state?.merchants?.merchants || [])

const monthlyEmi = computed(() => {
  if (!form.value.amount || !form.value.tenureMonths) return 0
  const amount = parseFloat(form.value.amount)
  const tenure = parseInt(form.value.tenureMonths)
  if (amount <= 0 || tenure <= 0) return 0
  return amount / tenure
})

// Methods
const close = () => {
  emit('close')
}

const formatNumber = (value: number) => {
  return new Intl.NumberFormat('en-IN', { 
    minimumFractionDigits: 0, 
    maximumFractionDigits: 2 
  }).format(value)
}

const submitTransaction = async () => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    // Reset form
    form.value = {
      cardId: '',
      merchantAccountId: '',
      amount: '',
      category: '',
      isBnpl: false,
      tenureMonths: ''
    }
    
    emit('transaction-created')
    close()
  } catch (error) {
    console.error('Error creating transaction:', error)
  } finally {
    loading.value = false
  }
}

// Watch for BNPL toggle
watch(() => form.value.isBnpl, (newValue) => {
  if (!newValue) {
    form.value.tenureMonths = ''
  }
})
</script>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>