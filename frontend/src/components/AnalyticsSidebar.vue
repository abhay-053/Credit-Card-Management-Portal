<template>
  <div class="space-y-4">
    <!-- Category Breakdown -->
    <div class="bg-gradient-to-br from-white to-purple-50 rounded-2xl shadow-lg border border-purple-100 p-4 hover:shadow-xl transition-all duration-300">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center">
          <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center mr-3">
            <svg class="h-5 w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
            </svg>
          </div>
          <h3 class="text-lg font-bold text-gray-900">Categories</h3>
        </div>
      </div>
      
      <div class="space-y-2">
        <div v-for="category in categoryBreakdown" :key="category.name" class="flex items-center justify-between p-2 bg-white rounded-lg hover:shadow-md transition-all duration-200">
          <div class="flex items-center">
            <div :class="category.color" class="w-3 h-3 rounded-full mr-3 shadow-sm"></div>
            <span class="text-sm font-semibold text-gray-700">{{ category.name }}</span>
          </div>
          <span class="text-sm font-bold text-gray-900">{{ category.percentage }}%</span>
        </div>
      </div>
    </div>

    <!-- Monthly Trends -->
    <div class="bg-gradient-to-br from-white to-blue-50 rounded-2xl shadow-lg border border-blue-100 p-4 hover:shadow-xl transition-all duration-300">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center">
          <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-blue-500 to-cyan-500 flex items-center justify-center mr-3">
            <svg class="h-5 w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
            </svg>
          </div>
          <h3 class="text-lg font-bold text-gray-900">Trends</h3>
        </div>
        <span :class="analytics.change >= 0 ? 'text-red-600' : 'text-green-600'" class="text-sm font-bold">
          {{ analytics.change >= 0 ? '+' : '' }}{{ analytics.change }}%
        </span>
      </div>
      
      <div class="grid grid-cols-2 gap-3">
        <div class="text-center p-3 bg-white rounded-lg">
          <div class="text-sm font-bold text-blue-600">₹{{ formatNumber(analytics.thisMonth) }}</div>
          <div class="text-xs text-gray-600">This Month</div>
        </div>
        <div class="text-center p-3 bg-white rounded-lg">
          <div class="text-sm font-bold text-gray-600">₹{{ formatNumber(analytics.lastMonth) }}</div>
          <div class="text-xs text-gray-600">Last Month</div>
        </div>
      </div>
    </div>

    <!-- Top Merchants -->
    <div class="bg-gradient-to-br from-white to-green-50 rounded-2xl shadow-lg border border-green-100 p-4 hover:shadow-xl transition-all duration-300">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center">
          <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-green-500 to-emerald-500 flex items-center justify-center mr-3">
            <svg class="h-5 w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
            </svg>
          </div>
          <h3 class="text-lg font-bold text-gray-900">Top Merchants</h3>
        </div>
      </div>
      
      <div class="space-y-2">
        <div v-for="merchant in topMerchants" :key="merchant.name" class="flex items-center justify-between p-2 bg-white rounded-lg hover:shadow-md transition-all duration-200">
          <div class="flex items-center">
            <div class="h-6 w-6 rounded-lg bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center mr-3">
              <svg class="h-3 w-3 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
              </svg>
            </div>
            <span class="text-sm font-semibold text-gray-700">{{ merchant.name }}</span>
          </div>
          <span class="text-sm font-bold text-gray-900">{{ merchant.count }}</span>
        </div>
      </div>
    </div>

    <!-- Spending Insights -->
    <div class="bg-gradient-to-br from-blue-50 to-indigo-100 rounded-2xl shadow-lg border border-blue-200 p-4 hover:shadow-xl transition-all duration-300">
      <div class="flex items-center mb-4">
        <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-500 flex items-center justify-center mr-3">
          <svg class="h-5 w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
          </svg>
        </div>
        <h3 class="text-lg font-bold text-gray-900">Insights</h3>
      </div>
      
      <div class="space-y-2">
        <div class="flex items-start p-2 bg-white rounded-lg hover:shadow-md transition-all duration-200">
          <div class="flex-shrink-0">
            <div class="h-6 w-6 rounded-lg bg-blue-100 flex items-center justify-center">
              <svg class="h-3 w-3 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
          <div class="ml-3">
            <p class="text-xs font-semibold text-blue-800">20% more on dining this month</p>
          </div>
        </div>
        
        <div class="flex items-start p-2 bg-white rounded-lg hover:shadow-md transition-all duration-200">
          <div class="flex-shrink-0">
            <div class="h-6 w-6 rounded-lg bg-green-100 flex items-center justify-center">
              <svg class="h-3 w-3 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
          <div class="ml-3">
            <p class="text-xs font-semibold text-green-800">Saved ₹2,500 this month</p>
          </div>
        </div>
        
        <div class="flex items-start p-2 bg-white rounded-lg hover:shadow-md transition-all duration-200">
          <div class="flex-shrink-0">
            <div class="h-6 w-6 rounded-lg bg-yellow-100 flex items-center justify-center">
              <svg class="h-3 w-3 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
              </svg>
            </div>
          </div>
          <div class="ml-3">
            <p class="text-xs font-semibold text-yellow-800">Reduce entertainment by 15%</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Analytics {
  thisMonth: number
  lastMonth: number
  change: number
  categoryBreakdown: Array<{
    name: string
    percentage: number
    amount: number
  }>
  topMerchants: Array<{
    name: string
    count: number
    amount: number
  }>
}

interface Props {
  analytics: Analytics
}

const props = defineProps<Props>()

const categoryBreakdown = computed(() => [
  { name: 'Shopping', percentage: 45, color: 'bg-purple-500' },
  { name: 'Food', percentage: 30, color: 'bg-orange-500' },
  { name: 'Travel', percentage: 25, color: 'bg-blue-500' }
])

const topMerchants = computed(() => [
  { name: 'Amazon', count: 12 },
  { name: 'Zomato', count: 8 },
  { name: 'Uber', count: 6 },
  { name: 'Netflix', count: 4 }
])

const formatNumber = (num: number) => {
  return new Intl.NumberFormat('en-IN').format(num)
}
</script>
