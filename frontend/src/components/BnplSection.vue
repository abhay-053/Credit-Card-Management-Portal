<template>
  <div class="space-y-3 sm:space-y-4">
    <!-- BNPL Summary Card -->
    <div class="bg-white mt-5 rounded-xl sm:rounded-2xl shadow-lg border border-gray-100 p-4 sm:p-6 hover:shadow-xl transition-all duration-300">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center">
          <div class="h-10 w-10 rounded-lg flex items-center justify-center mr-4" style="background: #ffd60a;">
            <svg class="h-6 w-6" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
            </svg>
          </div>
          <h3 class="text-xl font-bold text-gray-900">BNPL Overview</h3>
        </div>
        <div class="text-right">
          <div class="text-3xl font-bold" style="color: #0b2540;">{{ activePlansCount }}</div>
          <div class="text-sm text-gray-600">Active Plans</div>
        </div>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-red-50 rounded-lg p-4 border border-red-100">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm font-medium text-red-700 mb-1">Outstanding</div>
              <div :class="getAmountSize(outstandingBalance)" class="font-bold text-red-600">{{ formatCurrency(outstandingBalance) }}</div>
            </div>
            <div class="w-8 h-8 bg-red-100 rounded-lg flex items-center justify-center">
              <svg class="w-4 h-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
        </div>
        
        <div class="bg-green-50 rounded-lg p-4 border border-green-100">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm font-medium text-green-700 mb-1">Total Paid</div>
              <div :class="getAmountSize(totalPaid)" class="font-bold text-green-600">{{ formatCurrency(totalPaid) }}</div>
            </div>
            <div class="w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center">
              <svg class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
        </div>
        
        <div class="bg-gray-50 rounded-lg p-4 border border-gray-200" style="background: linear-gradient(135deg, #ffd60a 0%, #ffd60a 100%);">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm font-medium mb-1" style="color: #0b2540;">Progress</div>
              <div class="text-2xl font-bold" style="color: #0b2540;">{{ calculateProgress() }}%</div>
            </div>
            <div class="w-8 h-8 rounded-lg flex items-center justify-center" style="background: rgba(11, 37, 64, 0.1);">
              <svg class="w-4 h-4" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Active BNPL Plans -->
    <div v-if="bnplPlans.length > 0" class="bg-white rounded-xl sm:rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
      <div class="px-4 sm:px-6 py-4 bg-gray-50 border-b border-gray-200">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="h-8 w-8 rounded-lg flex items-center justify-center mr-3" style="background: #ffd60a;">
              <svg class="h-5 w-5" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
              </svg>
            </div>
            <h3 class="text-lg font-bold text-gray-900">Active BNPL Plans</h3>
          </div>
          <div class="text-right">
            <div class="text-sm font-medium text-gray-600">{{ bnplPlans.length }} active plans</div>
            <div class="text-xs text-gray-500">Manage your installments</div>
          </div>
        </div>
      </div>
      
      <div class="space-y-4">
        <div v-for="(plan, index) in bnplPlans" :key="plan.id" :class="[
          'rounded-lg border p-4 sm:p-6 transition-all duration-200 border-l-4',
          plan.status === 'COMPLETED' 
            ? 'bg-green-50 border-green-200 border-l-green-500' 
            : 'bg-white border-gray-200 hover:shadow-md',
          plan.status !== 'COMPLETED' ? getPlanBorderColor(index) : ''
        ]">
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-3 sm:mb-4 space-y-2 sm:space-y-0">
            <div class="flex items-center min-w-0 flex-1">
              <div :class="[
                'h-8 w-8 sm:h-10 sm:w-10 rounded-lg flex items-center justify-center mr-2 sm:mr-3 flex-shrink-0',
                plan.status === 'COMPLETED' ? 'bg-green-500' : getPlanIconBg(index)
              ]">
                <svg v-if="plan.status === 'COMPLETED'" class="h-4 w-4 sm:h-5 sm:w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
                <svg v-else class="h-4 w-4 sm:h-5 sm:w-5" :class="getPlanIconColor(index)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                </svg>
              </div>
              <div class="min-w-0 flex-1">
                <div class="text-sm sm:text-base font-semibold text-gray-900 flex flex-col sm:flex-row sm:items-center">
                  <span class="truncate">{{ plan.merchant }}</span>
                  <span class="text-xs font-medium px-2 py-1 rounded-full mt-1 sm:mt-0 sm:ml-2 w-fit" :class="getPlanBadgeBg(index)">{{ getPlanBadgeText(index) }}</span>
                </div>
                <div class="text-xs text-gray-600">{{ plan.tenureMonths }} installments • Started {{ formatDate(plan.startDate) }}</div>
              </div>
            </div>
            <span :class="[
              'inline-flex items-center px-2 sm:px-3 py-1 rounded-full text-xs font-semibold w-fit',
              plan.status === 'COMPLETED' 
                ? 'bg-green-100 text-green-800 border border-green-200' 
                : 'bg-gradient-to-r from-blue-100 to-purple-100 text-blue-800 border border-blue-200'
            ]">
              {{ plan.status === 'COMPLETED' ? 'Completed' : 'Active' }}
            </span>
          </div>

          <!-- Financial Breakdown with Visual Indicators -->
          <div class="space-y-4 mb-4">
            <!-- Progress Ring -->
            <div class="flex items-center justify-between">
              <div class="flex-1">
                <div class="text-sm font-medium text-gray-700 mb-2">Payment Progress</div>
                <div class="flex items-center space-x-4">
                  <div class="relative w-16 h-16">
                    <svg class="w-16 h-16 transform -rotate-90" viewBox="0 0 36 36">
                      <!-- Background circle -->
                      <path
                        d="M18 2.0845
                          a 15.9155 15.9155 0 0 1 0 31.831
                          a 15.9155 15.9155 0 0 1 0 -31.831"
                        fill="none"
                        stroke="#e5e7eb"
                        stroke-width="3"
                      />
                      <!-- Progress circle -->
                      <path
                        d="M18 2.0845
                          a 15.9155 15.9155 0 0 1 0 31.831
                          a 15.9155 15.9155 0 0 1 0 -31.831"
                        fill="none"
                        stroke="#10b981"
                        stroke-width="3"
                        stroke-linecap="round"
                        :stroke-dasharray="`${calculatePlanProgress(plan)}, 100`"
                      />
                    </svg>
                    <div class="absolute inset-0 flex items-center justify-center">
                      <span class="text-xs font-bold" style="color: #0b2540;">{{ calculatePlanProgress(plan) }}%</span>
                    </div>
                  </div>
                  <div class="flex-1">
                    <div class="text-xs text-gray-600">Paid: {{ formatCurrency(plan.paidAmount) }}</div>
                    <div class="text-xs text-gray-600">Remaining: {{ formatCurrency(plan.remainingAmount) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Mini Bar Graphs -->
            <div class="space-y-2">
              <div class="flex items-center justify-between text-xs">
                <span class="text-gray-600">Paid vs Remaining</span>
                <span class="font-medium" style="color: #0b2540;">{{ formatCurrency(plan.totalAmount) }}</span>
              </div>
              <div class="flex h-3 bg-gray-200 rounded-full overflow-hidden">
                <div 
                  class="bg-green-500 h-full transition-all duration-1000"
                  :style="{ width: `${(plan.paidAmount / plan.totalAmount) * 100}%` }"
                ></div>
                <div 
                  class="bg-red-500 h-full transition-all duration-1000"
                  :style="{ width: `${(plan.remainingAmount / plan.totalAmount) * 100}%` }"
                ></div>
              </div>
              <div class="flex justify-between text-xs text-gray-500">
                <span>Paid ({{ Math.round((plan.paidAmount / plan.totalAmount) * 100) }}%)</span>
                <span>Remaining ({{ Math.round((plan.remainingAmount / plan.totalAmount) * 100) }}%)</span>
              </div>
            </div>

            <!-- EMI Amount -->
            <div class="bg-blue-50 rounded-lg p-3 border border-blue-200">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="w-3 h-3 bg-blue-500 rounded-full mr-2"></div>
                  <span class="text-sm font-medium text-gray-700">Monthly EMI</span>
                </div>
                <div class="text-right">
                  <div :class="getAmountSize(plan.monthlyEmi)" class="font-bold text-blue-600">{{ formatCurrency(plan.monthlyEmi) }}</div>
                  <div class="text-xs text-gray-500">Due {{ formatDate(plan.nextDueDate) }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Installment Progress -->
          <div class="bg-gray-50 rounded-lg p-3 mb-4">
            <div class="flex justify-between text-xs font-semibold text-gray-700 mb-2">
              <span>Installment Progress</span>
              <span>{{ plan.paidInstallments }}/{{ plan.tenureMonths }} installments</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div 
                class="bg-gradient-to-r from-green-500 to-blue-500 h-2 rounded-full transition-all duration-500"
                :style="{ width: `${calculateInstallmentProgress(plan)}%` }"
              ></div>
            </div>
          </div>

          <!-- Action Button -->
          <div class="flex justify-center">
            <button 
              @click="payNow(plan)"
              :disabled="plan.status === 'COMPLETED'"
              :class="[
                'w-full px-6 py-3 text-sm font-medium rounded-lg shadow-lg transition-all duration-200',
                plan.status === 'COMPLETED' 
                  ? 'bg-gray-400 text-gray-600 cursor-not-allowed opacity-60' 
                  : 'text-white hover:shadow-xl cursor-pointer'
              ]"
              :style="plan.status !== 'COMPLETED' ? 'background: #0b2540;' : ''"
            >
              {{ plan.status === 'COMPLETED' ? 'Completed' : 'Pay Now' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- No BNPL Plans -->
    <div v-else class="bg-white rounded-lg shadow-sm border p-6 text-center">
      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No BNPL plans</h3>
      <p class="mt-1 text-sm text-gray-500">You don't have any active BNPL plans.</p>
    </div>

    <!-- BNPL Insights -->
    <div class="bg-blue-50 rounded-lg p-4">
      <h4 class="text-sm font-medium text-blue-900 mb-2">BNPL Insights</h4>
      <p class="text-sm text-blue-700">Your BNPL usage increased 15% vs last month.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Button from './ui/Button.vue'

interface BnplPlan {
  id: string
  transactionId?: number
  merchant: string
  status: string
  tenureMonths: number
  totalAmount: number
  paidAmount: number
  remainingAmount: number
  monthlyEmi: number
  paidInstallments: number
  startDate: string
  nextDueDate: string
}

interface Props {
  bnplPlans: BnplPlan[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'pay-emi': [planId: string, amount: number]
}>()

// Computed properties
const activePlansCount = computed(() => {
  return props.bnplPlans.filter(plan => plan.status === 'ACTIVE').length
})

const outstandingBalance = computed(() => {
  return props.bnplPlans
    .filter(plan => plan.status === 'ACTIVE')
    .reduce((sum, plan) => sum + (plan.remainingAmount || 0), 0)
})

const totalPaid = computed(() => {
  return props.bnplPlans
    .reduce((sum, plan) => sum + (plan.paidAmount || 0), 0)
})

// Methods
const formatNumber = (num: number) => {
  return new Intl.NumberFormat('en-IN').format(num)
}

const formatCurrency = (amount: number) => {
  return `₹${formatNumber(amount)}`
}

const getAmountSize = (amount: number) => {
  if (amount >= 10000000) return 'text-sm'
  if (amount >= 100000) return 'text-base'
  if (amount >= 1000) return 'text-lg'
  return 'text-lg'
}

const getPlanBorderColor = (index: number) => {
  const colors = ['border-blue-500', 'border-green-500', 'border-purple-500', 'border-orange-500', 'border-pink-500', 'border-indigo-500']
  return colors[index % colors.length]
}

const getPlanIconBg = (index: number) => {
  const colors = ['bg-gradient-to-br from-blue-100 to-blue-200', 'bg-gradient-to-br from-green-100 to-green-200', 'bg-gradient-to-br from-purple-100 to-purple-200', 'bg-gradient-to-br from-orange-100 to-orange-200', 'bg-gradient-to-br from-pink-100 to-pink-200', 'bg-gradient-to-br from-indigo-100 to-indigo-200']
  return colors[index % colors.length]
}

const calculateProgress = () => {
  const total = totalPaid.value + outstandingBalance.value
  if (total === 0) return 0
  return Math.round((totalPaid.value / total) * 100)
}

const calculatePlanProgress = (plan: BnplPlan) => {
  if (plan.totalAmount === 0) return 0
  return Math.round((plan.paidAmount / plan.totalAmount) * 100)
}

const calculateInstallmentProgress = (plan: BnplPlan) => {
  if (plan.tenureMonths === 0) return 0
  return Math.round((plan.paidInstallments / plan.tenureMonths) * 100)
}

const getPlanIconColor = (index: number) => {
  const colors = ['text-blue-600', 'text-green-600', 'text-purple-600', 'text-orange-600', 'text-pink-600', 'text-indigo-600']
  return colors[index % colors.length]
}

const getPlanBadgeBg = (index: number) => {
  const colors = ['bg-blue-100 text-blue-800', 'bg-green-100 text-green-800', 'bg-purple-100 text-purple-800', 'bg-orange-100 text-orange-800', 'bg-pink-100 text-pink-800', 'bg-indigo-100 text-indigo-800']
  return colors[index % colors.length]
}

const getPlanBadgeText = (index: number) => {
  const texts = ['Premium', 'Standard', 'Elite', 'Gold', 'Platinum', 'Diamond']
  return texts[index % texts.length]
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const viewSchedule = (plan: BnplPlan) => {
  console.log('View schedule for plan:', plan)
  // Emit event or open modal
}

const payNow = (plan: BnplPlan) => {
  // Don't allow payment if plan is completed
  if (plan.status === 'COMPLETED') {
    return
  }
  emit('pay-emi', plan.transactionId?.toString() || plan.id, plan.monthlyEmi)
}
</script>
