<template>
  <div class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
    <!-- Table Header -->
    <div class="px-8 py-6 bg-gradient-to-r from-gray-50 to-gray-100 border-b border-gray-200">
      <h3 class="text-2xl font-bold text-gray-900">Recent Transactions</h3>
      <p class="text-gray-600 mt-1">Manage your payment history</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="p-8 text-center">
      <div class="inline-flex items-center">
        <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Loading transactions...
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="transactions.length === 0" class="p-8 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No transactions</h3>
      <p class="mt-1 text-sm text-gray-500">Get started by creating a new transaction.</p>
    </div>

    <!-- Transaction Table -->
    <div v-else class="overflow-hidden">
      <!-- Desktop Table -->
      <div class="hidden lg:block overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Transaction ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Merchant
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Category
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Card
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Amount
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Date/Time
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-100">
            <tr v-for="transaction in transactions" :key="transaction.id" class="hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 transition-all duration-200 group">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-mono text-gray-900">
                {{ formatTransactionId(transaction.id) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-10 w-10">
                    <div class="h-10 w-10 rounded-xl bg-gradient-to-br from-blue-100 to-purple-100 flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
                      <svg class="h-5 w-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                      </svg>
                    </div>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-semibold text-gray-900">{{ transaction.merchantName || transaction.merchant }}</div>
                    <div v-if="transaction.isBnpl" class="text-xs text-blue-600 font-medium bg-blue-50 px-2 py-1 rounded-full inline-block mt-1">BNPL</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <CategoryTag :category="transaction.category" />
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center mr-3">
                    <svg class="h-4 w-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
                    </svg>
                  </div>
                  <span class="text-sm font-medium text-gray-900">{{ transaction.cardType }} ****{{ transaction.lastFour }}</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="transaction.amount < 0 ? 'text-red-600' : 'text-green-600'" class="text-lg font-bold">
                  {{ transaction.amount < 0 ? '-' : '+' }}₹{{ formatNumber(Math.abs(transaction.amount)) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <StatusBadge :status="transaction.status" />
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDateTime(transaction.createdAt) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile Cards -->
      <div class="lg:hidden">
        <div v-for="transaction in transactions" :key="transaction.id" class="border-b border-gray-100 p-6 hover:bg-gradient-to-r hover:from-blue-50 hover:to-purple-50 transition-all duration-200">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center">
                <div class="h-12 w-12 rounded-xl bg-gradient-to-br from-blue-100 to-purple-100 flex items-center justify-center mr-4">
                  <svg class="h-6 w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                  </svg>
                </div>
                <div>
                  <div class="text-lg font-semibold text-gray-900">{{ transaction.merchantName || transaction.merchant }}</div>
                  <div class="text-sm text-gray-500 font-mono">{{ formatTransactionId(transaction.id) }}</div>
                  <div v-if="transaction.isBnpl" class="text-xs text-blue-600 font-medium bg-blue-50 px-2 py-1 rounded-full inline-block mt-1">BNPL</div>
                </div>
              </div>
              
              <div class="mt-4 flex items-center justify-between">
                <div class="flex items-center space-x-3">
                  <CategoryTag :category="transaction.category" />
                  <StatusBadge :status="transaction.status" />
                </div>
                <span :class="transaction.amount < 0 ? 'text-red-600' : 'text-green-600'" class="text-xl font-bold">
                  {{ transaction.amount < 0 ? '-' : '+' }}₹{{ formatNumber(Math.abs(transaction.amount)) }}
                </span>
              </div>
              
              <div class="mt-4 flex items-center justify-between text-sm text-gray-500">
                <div class="flex items-center">
                  <div class="h-6 w-6 rounded-lg bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center mr-2">
                    <svg class="h-3 w-3 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
                    </svg>
                  </div>
                  <span class="font-medium">{{ transaction.cardType }} ****{{ transaction.lastFour }}</span>
                </div>
                <span class="font-medium">{{ formatDateTime(transaction.createdAt) }}</span>
              </div>
            </div>
          </div>
          
          <div class="mt-4 flex space-x-3">
            <button @click="viewDetails(transaction)" class="text-blue-600 hover:text-blue-800 hover:bg-blue-50 px-4 py-2 rounded-lg transition-all duration-200 font-medium">
              View Details
            </button>
            <button v-if="transaction.status === 'completed'" @click="refundTransaction(transaction)" class="text-red-600 hover:text-red-800 hover:bg-red-50 px-4 py-2 rounded-lg transition-all duration-200 font-medium">
              Refund
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="transactions.length > 0" class="px-6 py-4 border-t border-gray-200">
      <div class="flex items-center justify-between">
        <div class="text-sm text-gray-700">
          Showing <span class="font-medium">1</span> to <span class="font-medium">{{ transactions.length }}</span> of <span class="font-medium">{{ transactions.length }}</span> results
        </div>
        <div class="flex space-x-2">
          <button class="px-3 py-1 border border-gray-300 rounded-md text-sm text-gray-700 hover:bg-gray-50">
            Previous
          </button>
          <button class="px-3 py-1 bg-blue-600 text-white rounded-md text-sm">
            1
          </button>
          <button class="px-3 py-1 border border-gray-300 rounded-md text-sm text-gray-700 hover:bg-gray-50">
            Next
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import CategoryTag from './CategoryTag.vue'
import StatusBadge from './StatusBadge.vue'

interface Transaction {
  id: string | number
  transactionId?: string
  serialNo?: number
  merchant: string
  merchantName?: string
  category: string
  cardId: number
  cardType: string
  lastFour: string
  amount: number
  status: string
  isBnpl: boolean
  createdAt: string
  transactionDate?: string
}

interface Props {
  transactions: Transaction[]
  loading: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  refresh: []
}>()

// Methods
const formatNumber = (num: number) => {
  return new Intl.NumberFormat('en-IN').format(num)
}

const formatTransactionId = (id: string | number) => {
  const numericId = typeof id === 'string' ? parseInt(id) : id
  return `TXN-${numericId.toString().padStart(5, '0')}`
}

const formatDateTime = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-IN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const viewDetails = (transaction: Transaction) => {
  console.log('View details for transaction:', transaction)
  // Emit event or open modal
}

const refundTransaction = (transaction: Transaction) => {
  console.log('Refund transaction:', transaction)
  // Emit event or open refund modal
}
</script>
