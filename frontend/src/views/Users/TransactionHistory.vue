<template>
  <div class="min-h-screen bg-gray-50 overflow-x-hidden">
    <!-- Header Section -->
    <div class="bg-white border-b border-gray-100 shadow-sm">
      <div class="w-full py-4 sm:py-6">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-3 sm:space-y-0">
          <div>
            <h1 class="text-xl sm:text-2xl font-bold text-gray-900">Transaction History</h1>
            <p class="text-xs sm:text-sm text-gray-600 mt-1">View and manage your payment history</p>
          </div>
          <div class="flex flex-col sm:flex-row items-stretch sm:items-center space-y-2 sm:space-y-0 sm:space-x-3">
            <button @click="exportTransactions"
              class="inline-flex items-center justify-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 transition-all duration-200 shadow-sm">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z">
                </path>
              </svg>
              Export PDF
            </button>
            <button @click="goToCreateTransaction"
              class="inline-flex items-center justify-center px-4 py-2 text-sm font-medium text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 transition-all duration-200 shadow-lg hover:shadow-xl"
              style="background: #ffd60a; color: #0b2540;">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6">
                </path>
              </svg>
              New Transaction
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="w-full py-4 sm:py-6 overflow-x-hidden">
      <div class="space-y-4 sm:space-y-6">
        <!-- Main Content -->
        <div>
          <!-- Bill Cycle Information -->
          <div class="bg-white rounded-xl shadow-lg border border-gray-100 p-4 sm:p-6 mb-6">
            <!-- Card Selection Header -->
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6">
              <div class="flex items-center mb-4 sm:mb-0">
                <div class="h-8 w-8 rounded-lg flex items-center justify-center mr-3" style="background: #ffd60a;">
                  <svg class="h-5 w-5" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z">
                    </path>
                  </svg>
                </div>
                <h3 class="text-lg font-bold text-gray-900">Credit Card Bill</h3>
              </div>

              <!-- Card Selection Dropdown -->
              <div class="flex flex-col sm:flex-row items-start sm:items-center space-y-2 sm:space-y-0 sm:space-x-4">
                <div class="w-full sm:w-auto">
                  <label class="block text-sm font-semibold text-gray-700 mb-2">Select Card</label>
                  <div class="relative card-dropdown-container">
                    <button @click="toggleCardDropdown"
                      class="w-full sm:w-64 px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:bg-gray-50 text-left flex items-center justify-between">
                      <span class="text-gray-500">{{ getCardDisplayText() }}</span>
                      <svg class="w-4 h-4 text-gray-400 transition-transform duration-200"
                        :class="{ 'rotate-180': showCardDropdown }" fill="none" stroke="currentColor"
                        viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                      </svg>
                    </button>

                    <!-- Custom Card Dropdown Overlay -->
                    <div v-if="showCardDropdown"
                      class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
                      <div class="p-2 space-y-1">
                        <button @click="selectCard('')"
                          class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200">
                          Select a card...
                        </button>
                        <button v-for="card in cards" :key="card.id" @click="selectCard(card.id)"
                          class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                          <span class="text-lg mr-3">💳</span>
                          <div class="flex-1">
                            <div class="font-medium text-gray-700">
                              {{ card.cardType?.networkType || card.cardType || 'VISA' }} ****{{ card.lastFour ||
                                card.cardNumber?.slice(-4) || '****' }}
                            </div>
                            <div class="text-xs text-gray-500">
                              ₹{{ formatNumber(card.availableLimit || 0) }} available
                            </div>
                          </div>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Statement Date -->
                <div class="text-right">
                  <div class="text-sm font-medium text-gray-600">Statement Date</div>
                  <div class="text-lg font-bold" style="color: #0b2540;">
                    {{ selectedCard ? billSummary.statementDate : 'Select a card' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Empty State when no card selected -->
            <div v-if="!selectedCard" class="text-center py-12">
              <div class="w-20 h-20 mx-auto mb-4 rounded-full bg-gray-100 flex items-center justify-center">
                <svg class="w-10 h-10 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
                </svg>
              </div>
              <h4 class="text-xl font-semibold text-gray-700 mb-2">No Card Selected</h4>
              <p class="text-gray-500 mb-6">Please select a credit card to view its bill details and statement
                information.</p>
              <div class="text-sm text-gray-400">
                <p>• View statement dates and due dates</p>
                <p>• Check available credit and usage</p>
                <p>• Make payments and manage your bill</p>
              </div>
            </div>

            <!-- Financial Metrics when card is selected -->
            <div v-else class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="text-sm font-medium text-gray-600 mb-1">Total Statement Amount</div>
                <div class="text-2xl font-bold" style="color: #0b2540;">
                  {{ formatCurrency(billSummary.totalStatementAmount) }}
                </div>
                <div v-if="billSummary.totalStatementAmount === 0" class="text-xs text-gray-500 mt-1">No transactions
                  this month</div>
              </div>
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="text-sm font-medium text-gray-600 mb-1">Amount Due</div>
                <div class="text-2xl font-bold text-red-600">{{ formatCurrency(billSummary.amountDue) }}</div>
                <div v-if="billSummary.dueDate !== 'N/A'" class="text-xs text-gray-500 mt-1">Due on
                  {{ billSummary.dueDate }}
                </div>
                <div v-else class="text-xs text-gray-500 mt-1">No due date available</div>
                <div v-if="billSummary.paidAmount > 0" class="text-xs text-green-600 mt-1">
                  Paid: {{ formatCurrency(billSummary.paidAmount) }}
                </div>
              </div>
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="text-sm font-medium text-gray-600 mb-1">Available Credit</div>
                <div class="text-2xl font-bold text-green-600">{{ formatCurrency(billSummary.availableCredit) }}</div>
                <div class="text-xs text-gray-500 mt-1">Out of {{ formatCurrency(billSummary.creditLimit) }} limit</div>
              </div>
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="text-sm font-medium text-gray-600 mb-1">Credit Limit Usage</div>
                <div class="flex items-center justify-between mb-2">
                  <div class="text-2xl font-bold" style="color: #0b2540;">{{ billSummary.usagePercentage }}%</div>
                  <div class="text-xs text-gray-500">{{ formatCurrency(billSummary.usedAmount) }} /
                    {{ formatCurrency(billSummary.creditLimit) }}
                  </div>
                </div>
                <!-- Gauge Meter -->
                <div class="relative w-full h-3 bg-gray-200 rounded-full overflow-hidden">
                  <div class="absolute top-0 left-0 h-full rounded-full transition-all duration-1000" :style="{
                    width: billSummary.usagePercentage + '%',
                    background: 'linear-gradient(90deg, #10b981 0%, #f59e0b 50%, #ef4444 100%)'
                  }">
                  </div>
                </div>
                <div v-if="billSummary.spendingComparison !== 'No statement data available'"
                  class="text-xs text-gray-500 mt-1">{{ billSummary.spendingComparison }}</div>
                <div v-else class="text-xs text-gray-500 mt-1">No spending data available</div>
              </div>
            </div>

            <!-- Action Buttons - Only show when card is selected -->
            <div v-if="selectedCard" class="mt-4 flex justify-between items-center">
              <div class="flex items-center text-sm text-gray-600">
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                Post bill payment updated amount due will get reflected after 1-2 hours.
              </div>
              <div class="flex space-x-2">
                <button @click="resetPayments"
                  class="px-4 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-all duration-200">
                  Reset Payments
                </button>
                <button @click="handleBillPayment"
                  class="px-6 py-2 text-sm font-medium text-white rounded-lg shadow-lg hover:shadow-xl transition-all duration-200"
                  style="background: #0b2540;">
                  PAY NOW
                </button>
              </div>
            </div>
          </div>

          <!-- Quick Stats Row -->
          <div class="grid grid-cols-2 sm:grid-cols-2 xl:grid-cols-4 gap-3 sm:gap-4 mb-6">
            <div
              class="bg-white rounded-xl p-4 sm:p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
              <div class="flex items-center justify-between">
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-semibold text-gray-600">Total Transactions</p>
                  <p class="text-2xl font-bold mt-1" style="color: #0b2540;">{{ summary.totalTransactions }}</p>
                </div>
                <div class="h-10 w-10 rounded-lg flex items-center justify-center flex-shrink-0 ml-2"
                  style="background: #ffd60a;">
                  <svg class="h-5 w-5" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2">
                    </path>
                  </svg>
                </div>
              </div>
            </div>

            <div
              class="bg-white rounded-xl p-4 sm:p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
              <div class="flex items-center justify-between">
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-semibold text-gray-600">Total Spent</p>
                  <p :class="getAmountSize(summary.totalSpent)" class="font-bold mt-1 text-red-600">
                    {{ formatCurrency(summary.totalSpent) }}
                  </p>
                  <p class="text-xs text-gray-500 mt-1">{{ formatNumber(summary.totalSpent) }}</p>
                </div>
                <div class="h-10 w-10 rounded-lg flex items-center justify-center flex-shrink-0 ml-2 bg-red-100">
                  <svg class="h-5 w-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1">
                    </path>
                  </svg>
                </div>
              </div>
            </div>

            <div
              class="bg-white rounded-xl p-4 sm:p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
              <div class="flex items-center justify-between">
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-semibold text-gray-600">BNPL Plans</p>
                  <p class="text-2xl font-bold mt-1" style="color: #0b2540;">{{ bnplPlans.length }}</p>
                  <p class="text-xs text-gray-500 mt-1">active plans</p>
                </div>
                <div class="h-10 w-10 rounded-lg flex items-center justify-center flex-shrink-0 ml-2 bg-purple-100">
                  <svg class="h-5 w-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10">
                    </path>
                  </svg>
                </div>
              </div>
            </div>

            <div
              class="bg-white rounded-xl p-4 sm:p-6 shadow-lg border border-gray-100 hover:shadow-xl transition-all duration-300">
              <div class="flex items-center justify-between">
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-semibold text-gray-600">Average Transaction</p>
                  <p :class="getAmountSize(summary.avgTransaction)" class="font-bold mt-1 text-green-600">
                    {{ formatCurrency(summary.avgTransaction) }}
                  </p>
                  <p class="text-xs text-gray-500 mt-1">{{ formatNumber(summary.avgTransaction) }}</p>
                </div>
                <div class="h-10 w-10 rounded-lg flex items-center justify-center flex-shrink-0 ml-2 bg-green-100">
                  <svg class="h-5 w-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z">
                    </path>
                  </svg>
                </div>
              </div>
            </div>
          </div>



          <!-- Analytics Charts Section -->
          <div class="grid grid-cols-1 xl:grid-cols-2 gap-4 sm:gap-6 mb-8">
            <!-- Category Breakdown Chart -->
            <div class="bg-white rounded-xl shadow-lg border border-gray-100 p-6">
              <div class="flex items-center justify-between mb-6">
                <div class="flex items-center">
                  <div class="h-8 w-8 rounded-lg flex items-center justify-center mr-3" style="background: #ffd60a;">
                    <svg class="h-5 w-5" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M20.488 9H15V3.512A9.025 9.025 0 0120.488 9z"></path>
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-gray-900">Spending by Category</h3>
                </div>
              </div>

              <!-- Chart Container -->
              <div class="h-64 flex items-center justify-center">
                <div v-if="!hasCategoryData" class="text-center">
                  <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-100 flex items-center justify-center">
                    <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M20.488 9H15V3.512A9.025 9.025 0 0120.488 9z"></path>
                    </svg>
                  </div>
                  <h4 class="text-lg font-semibold text-gray-700 mb-2">No Spending Data</h4>
                  <p class="text-sm text-gray-500">Start making transactions to see your spending breakdown by category
                  </p>
                </div>
                <canvas v-else ref="categoryChart" class="w-full h-full"></canvas>
              </div>

              <!-- Legend -->
              <div v-if="hasCategoryData" class="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                <div v-for="category in categoryBreakdown" :key="category.name"
                  class="flex items-center justify-between p-2 bg-gray-50 rounded-lg">
                  <div class="flex items-center">
                    <div class="w-3 h-3 rounded-full mr-2" :style="{ backgroundColor: category.hexColor }"></div>
                    <span class="text-sm font-medium text-gray-700 truncate">{{ category.name }}</span>
                  </div>
                  <span class="text-sm font-bold ml-2" style="color: #0b2540;">{{ category.percentage }}%</span>
                </div>
              </div>
            </div>

            <!-- Monthly Trends Chart -->
            <div class="bg-white rounded-xl shadow-lg border border-gray-100 p-6">
              <div class="flex items-center justify-between mb-6">
                <div class="flex items-center">
                  <div class="h-8 w-8 rounded-lg flex items-center justify-center mr-3" style="background: #ffd60a;">
                    <svg class="h-5 w-5" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-gray-900">Monthly Trends</h3>
                </div>
                <div class="text-right">
                  <div class="text-sm font-medium text-gray-600">Growth</div>
                  <div :class="analytics.change >= 0 ? 'text-green-600' : 'text-red-600'" class="text-lg font-bold">
                    {{ analytics.change >= 0 ? '+' : '' }}{{ analytics.change }}%
                  </div>
                </div>
              </div>

              <!-- Chart Container -->
              <div class="h-64 flex items-center justify-center">
                <div v-if="!hasTrendsData" class="text-center">
                  <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-100 flex items-center justify-center">
                    <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
                    </svg>
                  </div>
                  <h4 class="text-lg font-semibold text-gray-700 mb-2">No Trend Data</h4>
                  <p class="text-sm text-gray-500">Start making transactions to see your monthly spending trends</p>
                </div>
                <canvas v-else ref="trendsChart" class="w-full h-full"></canvas>
              </div>

              <!-- Summary Stats -->
              <div v-if="hasTrendsData" class="mt-4 grid grid-cols-2 gap-4">
                <div class="text-center p-3 bg-gray-50 rounded-lg">
                  <div class="text-sm font-medium text-gray-600">This Month</div>
                  <div :class="getAmountSize(analytics.thisMonth)" class="font-bold mt-1" style="color: #0b2540;">
                    {{ formatCurrency(analytics.thisMonth) }}
                  </div>
                </div>
                <div class="text-center p-3 bg-gray-50 rounded-lg">
                  <div class="text-sm font-medium text-gray-600">Last Month</div>
                  <div :class="getAmountSize(analytics.lastMonth)" class="font-bold mt-1 text-gray-600">
                    {{ formatCurrency(analytics.lastMonth) }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Filter Section -->
          <div class="bg-white rounded-3xl shadow-xl border border-gray-100 p-4 sm:p-6 md:p-8 mb-8">
            <div class="space-y-6 sm:space-y-8">
              <!-- Search Bar -->
              <div class="w-full sm:w-2/3 md:w-1/2 mx-auto">
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                    </svg>
                  </div>
                  <input v-model="filters.search" type="text" placeholder="Search transactions..."
                    class="block w-full pl-12 pr-4 py-3 sm:py-4 text-sm border border-gray-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-gray-50 hover:bg-white" />
                </div>
              </div>

              <!-- Filter Controls -->
              <div class="space-y-6">
                <!-- Filter Labels -->
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4 sm:gap-6 md:gap-8">
                  <div class="space-y-2">
                    <label class="text-sm font-semibold text-gray-700">Payment Card</label>
                    <CardSwitcher :cards="cards" :selected-card-id="selectedCardId" :show-all-option="true"
                      @card-selected="selectCard" />
                    <!-- Selected Card Info -->
                    <div v-if="selectedCard" class="text-xs text-blue-600 font-medium mt-1">
                      ✓ {{ selectedCard.cardType?.networkType || selectedCard.cardType || 'VISA' }} ****{{
                        selectedCard.lastFour || selectedCard.cardNumber?.slice(-4) || '****' }}
                      (₹{{ formatNumber(selectedCard.availableLimit || 0) }} available)
                    </div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm font-semibold text-gray-700">Category</label>
                    <div class="relative">
                      <button @click="toggleCategoryDropdown"
                        class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:bg-gray-50 text-left flex items-center justify-between">
                        <span class="text-gray-500">{{ getCategoryDisplayText() }}</span>
                        <svg class="w-4 h-4 text-gray-400 transition-transform duration-200"
                          :class="{ 'rotate-180': showCategoryDropdown }" fill="none" stroke="currentColor"
                          viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7">
                          </path>
                        </svg>
                      </button>

                      <!-- Custom Category Dropdown Overlay -->
                      <div v-if="showCategoryDropdown"
                        class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
                        <div class="p-2 space-y-1">
                          <button @click="selectCategory('')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200">
                            All Categories
                          </button>
                          <button @click="selectCategory('shopping')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">🛒</span>
                            <span class="text-gray-700 font-medium">Shopping</span>
                          </button>
                          <button @click="selectCategory('food')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">🍔</span>
                            <span class="text-gray-700 font-medium">Food & Dining</span>
                          </button>
                          <button @click="selectCategory('travel')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">✈️</span>
                            <span class="text-gray-700 font-medium">Travel</span>
                          </button>
                          <button @click="selectCategory('entertainment')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">🎬</span>
                            <span class="text-gray-700 font-medium">Entertainment</span>
                          </button>
                          <button @click="selectCategory('electronics')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">📱</span>
                            <span class="text-gray-700 font-medium">Electronics</span>
                          </button>
                          <button @click="selectCategory('healthcare')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">🏥</span>
                            <span class="text-gray-700 font-medium">Healthcare</span>
                          </button>
                          <button @click="selectCategory('education')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">📚</span>
                            <span class="text-gray-700 font-medium">Education</span>
                          </button>
                          <button @click="selectCategory('other')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="text-lg mr-3">📦</span>
                            <span class="text-gray-700 font-medium">Other</span>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm font-semibold text-gray-700">Transaction Status</label>
                    <div class="relative">
                      <button @click="toggleStatusDropdown"
                        class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 bg-white hover:bg-gray-50 text-left flex items-center justify-between">
                        <span class="text-gray-500">{{ getStatusDisplayText() }}</span>
                        <svg class="w-4 h-4 text-gray-400 transition-transform duration-200"
                          :class="{ 'rotate-180': showStatusDropdown }" fill="none" stroke="currentColor"
                          viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7">
                          </path>
                        </svg>
                      </button>

                      <!-- Custom Dropdown Overlay -->
                      <div v-if="showStatusDropdown"
                        class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
                        <div class="p-2 space-y-1">
                          <button @click="selectStatus('')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200">
                            All Status
                          </button>
                          <button @click="selectStatus('completed')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="inline-block w-3 h-3 rounded-full bg-green-500 mr-3"></span>
                            <span class="text-green-700 font-medium">Completed</span>
                          </button>
                          <button @click="selectStatus('pending')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="inline-block w-3 h-3 rounded-full bg-yellow-500 mr-3"></span>
                            <span class="text-yellow-700 font-medium">Pending</span>
                          </button>
                          <button @click="selectStatus('failed')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="inline-block w-3 h-3 rounded-full bg-red-500 mr-3"></span>
                            <span class="text-red-700 font-medium">Failed</span>
                          </button>
                          <button @click="selectStatus('refunded')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="inline-block w-3 h-3 rounded-full bg-blue-500 mr-3"></span>
                            <span class="text-blue-700 font-medium">Refunded</span>
                          </button>
                          <button @click="selectStatus('BNPL_ACTIVE')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="inline-block w-3 h-3 rounded-full bg-purple-500 mr-3"></span>
                            <span class="text-purple-700 font-medium">BNPL Active</span>
                          </button>
                          <button @click="selectStatus('BNPL_COMPLETED')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="inline-block w-3 h-3 rounded-full bg-green-500 mr-3"></span>
                            <span class="text-green-700 font-medium">BNPL Completed</span>
                          </button>
                          <button @click="selectStatus('BNPL_DEFAULTED')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center">
                            <span class="inline-block w-3 h-3 rounded-full bg-red-500 mr-3"></span>
                            <span class="text-red-700 font-medium">BNPL Defaulted</span>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Actions Row -->
                <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pt-4">
                  <label class="flex items-center space-x-3 cursor-pointer group" @click="toggleBnpl">
                    <div class="relative">
                      <div
                        class="w-11 h-6 bg-gray-200 rounded-full transition-colors duration-200 group-hover:bg-gray-300"
                        :class="{ 'bg-yellow-500': filters.bnplOnly }">
                        <div
                          class="w-5 h-5 bg-white rounded-full shadow-md transform transition-transform duration-200 mt-0.5"
                          :class="{ 'translate-x-5': filters.bnplOnly, 'translate-x-0.5': !filters.bnplOnly }"></div>
                      </div>
                    </div>
                    <span class="text-sm font-medium text-gray-700">Buy Now, Pay Later only</span>
                  </label>

                  <button @click="clearAllFilters"
                    class="flex items-center space-x-2 px-4 py-2 text-sm font-medium text-gray-600 bg-gray-100 border border-gray-200 rounded-xl hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 transition-all duration-200">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12">
                      </path>
                    </svg>
                    <span>Clear All</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Transaction Table -->
          <TransactionTable :transactions="filteredTransactions" :loading="loading" @refresh="fetchTransactions" />

        </div>

      </div>

      <!-- BNPL Section - Full Width -->
      <div>
        <BnplSection :bnpl-plans="bnplPlans" @pay-emi="handleEmiPayment" />
      </div>
    </div>
  </div>

  <!-- Payment Amount Input Modal -->
  <div v-if="showPaymentInput" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-xl p-6 w-full max-w-md mx-4">
      <h3 class="text-lg font-bold text-gray-900 mb-4">Payment Amount</h3>

      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Amount Due: {{ formatCurrency(billSummary.amountDue) }}
        </label>
        <input v-model.number="customPaymentAmount" type="number" step="0.01" min="0.01" :max="billSummary.amountDue"
          placeholder="Enter payment amount"
          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
      </div>

      <div class="flex space-x-3">
        <button @click="cancelPayment"
          class="flex-1 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-lg hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500">
          Cancel
        </button>
        <button @click="confirmPaymentAmount"
          class="flex-1 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
          Proceed to Pay
        </button>
      </div>
    </div>
  </div>

  <!-- Payment Flow Modal -->
  <PaymentFlow :show-payment="showPayment" :payment-amount="paymentAmount" :payment-type="paymentType"
    :due-date="dueDate" @close-payment="closePayment" @payment-success="onPaymentSuccess" />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useStore } from 'vuex'
// @ts-ignore
import { useRouter } from 'vue-router'
import TransactionTable from '../../components/TransactionTable.vue'
import BnplSection from '../../components/BnplSection.vue'
import PaymentFlow from '../../components/PaymentFlow.vue'
import CardSwitcher from '../../components/CardSwitcher.vue'
import { Chart, registerables } from 'chart.js'
import jsPDF from 'jspdf'

// Register Chart.js components
Chart.register(...registerables)

// Type definitions
interface Transaction {
  id: string
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

interface Statement {
  id: number
  cardId: number
  statementDate: string
  dueDate: string
  statementAmount: string
  paidAmount: string
  amountDue: string
  status: string
  isPaid: boolean
  isOverdue: boolean
}

interface BnplPlan {
  id: string
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
  // Additional fields from API
  transactionId?: number
  merchantName?: string
  progressPercentage?: number
  totalInstallments?: number
  remainingInstallments?: number
}

interface BnplOverview {
  outstandingAmount: number
  totalPaidAmount: number
  totalAmount: number
  progressPercentage: number
  activePlansCount: number
  activePlans: BnplPlan[]
}

const store = useStore()
const router = useRouter()

// Reactive data
const loading = ref(false)
const categoryChart = ref<HTMLCanvasElement | null>(null)
const trendsChart = ref<HTMLCanvasElement | null>(null)
const showPayment = ref(false)
const paymentAmount = ref(0)
const paymentType = ref('')
const dueDate = ref('')
// Statement data from API
const currentStatement = ref<Statement | null>(null)
const paidAmount = ref(0)
const customPaymentAmount = ref(0) // For custom payment input
const showPaymentInput = ref(false) // Show payment amount input
const currentPlanId = ref('') // Store current BNPL plan ID for payment

// Filters
const filters = ref({
  search: '',
  cardId: '',
  category: '',
  status: '',
  bnplOnly: false,
  dateRange: '',
  dateRangeTo: ''
})

const showStatusDropdown = ref(false)
const showCategoryDropdown = ref(false)
const showCardDropdown = ref(false)

// Computed properties
const transactions = computed(() => store.state?.transactions?.transactions || [])
const cards = computed(() => store.state?.cards?.cards || [])

// Selected card state
const selectedCardId = ref<number | null>(null)

// Computed property for selected card
const selectedCard = computed(() => {
  if (!selectedCardId.value) return null
  return cards.value.find((card: any) => card.id === selectedCardId.value) || null
})

// BNPL data from API
const bnplOverview = ref<BnplOverview | null>(null)
const bnplPlans = computed(() => {
  if (!bnplOverview.value?.activePlans) return []

  return bnplOverview.value.activePlans.map(plan => ({
    id: `bnpl-${plan.transactionId}`,
    merchant: plan.merchantName || 'Unknown Merchant',
    status: plan.status,
    tenureMonths: plan.totalInstallments || 6,
    totalAmount: plan.totalAmount,
    paidAmount: plan.paidAmount,
    remainingAmount: plan.remainingAmount,
    monthlyEmi: plan.monthlyEmi,
    paidInstallments: plan.paidInstallments,
    startDate: plan.startDate,
    nextDueDate: plan.nextDueDate,
    // Keep additional fields for compatibility
    transactionId: plan.transactionId,
    merchantName: plan.merchantName,
    progressPercentage: plan.progressPercentage,
    totalInstallments: plan.totalInstallments,
    remainingInstallments: plan.remainingInstallments
  }))
})
const analytics = computed(() => {
  const totalSpent = transactions.value.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
  const now = new Date()
  const currentMonth = new Date(now.getFullYear(), now.getMonth(), 1)
  const lastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1)
  const lastDayOfLastMonth = new Date(now.getFullYear(), now.getMonth(), 0)

  // Calculate current month spending
  const currentMonthTransactions = transactions.value.filter((t: Transaction) => {
    const transactionDate = new Date(t.transactionDate || t.createdAt)
    return transactionDate >= currentMonth
  })

  // Calculate last month spending
  const lastMonthTransactions = transactions.value.filter((t: Transaction) => {
    const transactionDate = new Date(t.transactionDate || t.createdAt)
    return transactionDate >= lastMonth && transactionDate <= lastDayOfLastMonth
  })

  const thisMonthSpent = currentMonthTransactions.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
  const lastMonthSpent = lastMonthTransactions.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
  const change = lastMonthSpent > 0 ? ((thisMonthSpent - lastMonthSpent) / lastMonthSpent) * 100 : 0

  return {
    thisMonth: thisMonthSpent,
    lastMonth: lastMonthSpent,
    change: Math.round(change * 10) / 10,
    categoryBreakdown: [],
    topMerchants: []
  }
})

const categoryBreakdown = computed(() => {
  // Calculate actual category breakdown from transactions
  const categoryTotals: Record<string, number> = {}
  const totalSpent = transactions.value.reduce((sum: number, t: Transaction) => sum + t.amount, 0)

  transactions.value.forEach((t: Transaction) => {
    categoryTotals[t.category] = (categoryTotals[t.category] || 0) + t.amount
  })

  // Predefined beautiful color palette for better visual appeal
  const predefinedColors = [
    '#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57',
    '#ff9ff3', '#54a0ff', '#5f27cd', '#00d2d3', '#ff9f43',
    '#10ac84', '#ee5a24', '#0984e3', '#6c5ce7', '#a29bfe',
    '#fd79a8', '#fdcb6e', '#e17055', '#00b894', '#00cec9',
    '#6c5ce7', '#a29bfe', '#fd79a8', '#fdcb6e', '#e17055'
  ]

  // Generate dynamic colors based on category name for consistency
  const generateColor = (categoryName: string, index: number) => {
    // First try predefined colors for better visual appeal
    if (index < predefinedColors.length) {
      return {
        bg: `bg-[${predefinedColors[index]}]`,
        hex: predefinedColors[index]
      }
    }

    // For additional categories, generate colors using hash
    let hash = 0
    for (let i = 0; i < categoryName.length; i++) {
      hash = categoryName.charCodeAt(i) + ((hash << 5) - hash)
    }

    // Use hash to generate consistent colors
    const hue = Math.abs(hash) % 360
    const saturation = 70 + (Math.abs(hash) % 20) // 70-90% saturation
    const lightness = 45 + (Math.abs(hash) % 20) // 45-65% lightness

    // Convert HSL to hex for better Chart.js compatibility
    const hslToHex = (h: number, s: number, l: number) => {
      l /= 100
      const a = s * Math.min(l, 1 - l) / 100
      const f = (n: number) => {
        const k = (n + h / 30) % 12
        const color = l - a * Math.max(Math.min(k - 3, 9 - k, 1), -1)
        return Math.round(255 * color).toString(16).padStart(2, '0')
      }
      return `#${f(0)}${f(8)}${f(4)}`
    }

    const hexColor = hslToHex(hue, saturation, lightness)

    return {
      bg: `bg-[${hexColor}]`,
      hex: hexColor
    }
  }

  return Object.entries(categoryTotals)
    .map(([category, amount], index) => {
      const colorData = generateColor(category, index)
      return {
        name: category.charAt(0).toUpperCase() + category.slice(1),
        percentage: totalSpent > 0 ? Math.round((amount / totalSpent) * 100) : 0,
        color: colorData.bg,
        hexColor: colorData.hex,
        colorIndex: index
      }
    })
    .sort((a, b) => b.percentage - a.percentage)
    .slice(0, 10) // Show top 10 categories instead of 5
})

// Check if there's data for charts
const hasCategoryData = computed(() => {
  const totalSpent = transactions.value.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
  return totalSpent > 0
})

const hasTrendsData = computed(() => {
  const totalSpent = transactions.value.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
  return totalSpent > 0
})


const summary = computed(() => {
  const totalTransactions = transactions.value.length
  const totalSpent = transactions.value.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
  const bnplTransactions = transactions.value.filter((t: Transaction) => t.isBnpl).length
  const avgTransaction = totalTransactions > 0 ? totalSpent / totalTransactions : 0

  return {
    totalTransactions,
    totalSpent,
    bnplTransactions,
    avgTransaction
  }
})

// Bill summary computed property
const billSummary = computed(() => {
  const currentCard = selectedCard.value || cards.value[0] // Use selected card or first card

  if (!currentCard) {
    // Fallback values if no card selected
    return {
      totalStatementAmount: 0,
      amountDue: 0,
      availableCredit: 0,
      creditLimit: 0,
      usedAmount: 0,
      usagePercentage: 0,
      statementDate: 'Select a card',
      dueDate: 'Select a card',
      spendingComparison: 'No card selected',
      paidAmount: 0
    }
  }

  if (!currentStatement.value) {
    // Fallback values if no statement data for selected card
    return {
      totalStatementAmount: 0,
      amountDue: 0,
      availableCredit: currentCard.availableLimit || 0,
      creditLimit: currentCard.creditLimit || 0,
      usedAmount: (currentCard.creditLimit || 0) - (currentCard.availableLimit || 0),
      usagePercentage: currentCard.creditLimit > 0 ? Math.round(((currentCard.creditLimit - (currentCard.availableLimit || 0)) / currentCard.creditLimit) * 100) : 0,
      statementDate: 'No statement available',
      dueDate: 'No due date available',
      spendingComparison: 'No statement data available',
      paidAmount: 0
    }
  }

  const creditLimit = currentCard.creditLimit || 0
  const statement = currentStatement.value

  // Use the actual available limit from the card data
  const totalSpent = parseFloat(statement.statementAmount) || 0
  const availableLimit = currentCard.availableLimit || 0

  // Calculate spending comparison with previous month
  const now = new Date()
  const previousMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1)
  const lastDayOfPreviousMonth = new Date(now.getFullYear(), now.getMonth(), 0)

  const previousMonthTransactions = transactions.value.filter((t: Transaction) => {
    const transactionDate = new Date(t.transactionDate || t.createdAt)
    return transactionDate >= previousMonth && transactionDate <= lastDayOfPreviousMonth
  })

  const previousMonthSpent = previousMonthTransactions.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
  const spendingChange = previousMonthSpent > 0 ? Math.round(((totalSpent - previousMonthSpent) / previousMonthSpent) * 100) : 0

  // Calculate amount due (statement amount - paid amount)
  const paidAmount = parseFloat(statement.paidAmount) || 0
  const amountDue = Math.max(0, totalSpent - paidAmount)

  return {
    totalStatementAmount: totalSpent,
    amountDue: amountDue,
    availableCredit: availableLimit,
    creditLimit: creditLimit,
    usedAmount: creditLimit - availableLimit,
    usagePercentage: creditLimit > 0 ? Math.round(((creditLimit - availableLimit) / creditLimit) * 100) : 0,
    statementDate: new Date(statement.statementDate).toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    }),
    dueDate: new Date(statement.dueDate).toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    }),
    spendingComparison: `You spent ${Math.abs(spendingChange)}% ${spendingChange >= 0 ? 'more' : 'less'} than last month`,
    paidAmount: paidAmount,
    monthlyTransactions: transactions.value.length
  }
})

const filteredTransactions = computed(() => {
  let filtered = transactions.value

  if (filters.value.search) {
    const search = filters.value.search.toLowerCase()
    filtered = filtered.filter((t: Transaction) =>
      (t.merchantName || t.merchant || '').toLowerCase().includes(search) ||
      (t.transactionId && t.transactionId.toLowerCase().includes(search)) ||
      t.amount.toString().includes(search) ||
      t.category.toLowerCase().includes(search)
    )
  }

  if (filters.value.cardId) {
    filtered = filtered.filter((t: Transaction) => t.cardId === parseInt(filters.value.cardId))
  }

  if (filters.value.category) {
    filtered = filtered.filter((t: Transaction) => t.category === filters.value.category)
  }

  if (filters.value.status) {
    filtered = filtered.filter((t: Transaction) => t.status === filters.value.status)
  }

  if (filters.value.bnplOnly) {
    filtered = filtered.filter((t: Transaction) => t.isBnpl)
  }

  return filtered
})

// Card selection methods
const toggleCardDropdown = () => {
  showCardDropdown.value = !showCardDropdown.value
}

const getCardDisplayText = () => {
  if (!selectedCardId.value) {
    return 'Select a card...'
  }
  const card = cards.value.find((c: any) => c.id === selectedCardId.value)
  if (!card) return 'Select a card...'

  return `${card.cardType?.networkType || card.cardType || 'VISA'} ****${card.lastFour || card.cardNumber?.slice(-4) || '****'} (₹${formatNumber(card.availableLimit || 0)} available)`
}

const selectCard = async (cardId: string | number) => {
  if (cardId === '' || cardId === null || cardId === undefined) {
    // Show all cards - fetch data for first card as default
    filters.value.cardId = ''
    selectedCardId.value = null
    currentStatement.value = null
    const userCards = await store.dispatch('cards/fetchCards')
    if (userCards && userCards.length > 0) {
      await fetchTransactions(userCards[0].id)
    }
  } else {
    // Select specific card
    const cardIdNum = Number(cardId)
    selectedCardId.value = cardIdNum
    filters.value.cardId = cardId.toString()

    // Fetch statement data for selected card
    await fetchStatementForCard(cardIdNum)
    // Fetch transactions for selected card
    await fetchTransactions(cardIdNum)
  }

  // Close dropdown
  showCardDropdown.value = false
}

const onCardChange = () => {
  if (selectedCardId.value) {
    // Fetch statement data for selected card
    fetchStatementForCard(selectedCardId.value)
    // Refresh transactions for selected card
    fetchTransactions()
  } else {
    // Clear statement data when no card selected
    currentStatement.value = null
  }
}

// Methods
const formatNumber = (num: number) => {
  return new Intl.NumberFormat('en-IN').format(num)
}

const formatCurrency = (amount: number) => {
  // Always show full amount with proper formatting
  return `₹${amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

const getAmountSize = (amount: number) => {
  if (amount >= 10000000) return 'text-lg'
  if (amount >= 100000) return 'text-xl'
  if (amount >= 1000) return 'text-2xl'
  return 'text-2xl'
}

const goToCreateTransaction = () => {
  router.push('/create-transaction')
}

const clearAllFilters = () => {
  filters.value = {
    search: '',
    cardId: '',
    category: '',
    status: '',
    bnplOnly: false,
    dateRange: '',
    dateRangeTo: ''
  }
  showStatusDropdown.value = false
  showCategoryDropdown.value = false
}

const toggleStatusDropdown = () => {
  showStatusDropdown.value = !showStatusDropdown.value
  showCategoryDropdown.value = false
}

const selectStatus = (status: string) => {
  filters.value.status = status
  showStatusDropdown.value = false
}

const getStatusDisplayText = () => {
  if (!filters.value.status) return 'Select status'
  const statusMap: Record<string, string> = {
    completed: 'Completed',
    pending: 'Pending',
    failed: 'Failed',
    refunded: 'Refunded',
    bnpl_active: 'BNPL Active',
    bnpl_completed: 'BNPL Completed',
    bnpl_defaulted: 'BNPL Defaulted',
    // Backend enum values
    COMPLETED: 'Completed',
    PENDING: 'Pending',
    FAILED: 'Failed',
    REFUNDED: 'Refunded',
    BNPL_ACTIVE: 'BNPL Active',
    BNPL_COMPLETED: 'BNPL Completed',
    BNPL_DEFAULTED: 'BNPL Defaulted'
  }
  return statusMap[filters.value.status] || 'Select status'
}

const toggleBnpl = () => {
  filters.value.bnplOnly = !filters.value.bnplOnly
}

// Removed toggleCardDropdown - now handled by CardSwitcher component

const toggleCategoryDropdown = () => {
  showCategoryDropdown.value = !showCategoryDropdown.value
  showStatusDropdown.value = false
}


const selectCategory = (category: string) => {
  filters.value.category = category
  showCategoryDropdown.value = false
}

// Removed getCardDisplayText - now handled by CardSwitcher component

const getCategoryDisplayText = () => {
  if (!filters.value.category) return 'Select category'
  const categoryMap: Record<string, string> = {
    shopping: '🛒 Shopping',
    food: '🍔 Food & Dining',
    travel: '✈️ Travel',
    entertainment: '🎬 Entertainment',
    electronics: '📱 Electronics',
    healthcare: '🏥 Healthcare',
    education: '📚 Education',
    other: '📦 Other'
  }
  return categoryMap[filters.value.category] || 'Select category'
}

const fetchStatementForCard = async (cardId: number) => {
  try {
    // Fetch statement for the specific card
    await fetchCurrentStatement(cardId)
    console.log('Fetched statement for card:', cardId)
  } catch (error) {
    console.error('Failed to fetch statement for card:', error)
  }
}

const exportTransactions = () => {
  // Check if a card is selected
  if (!selectedCard.value) {
    alert('Please select a card to export its bill!')
    return
  }

  // Check if there are transactions to export
  if (!filteredTransactions.value || filteredTransactions.value.length === 0) {
    alert('No transactions to export for the selected card!')
    return
  }

  // Generate PDF for selected card
  exportTransactionsPDF()

  // Show success message
  alert(`PDF exported successfully for ${selectedCard.value.cardType?.networkType || selectedCard.value.cardType || 'VISA'} ****${selectedCard.value.lastFour || selectedCard.value.cardNumber?.slice(-4) || '****'}!`)
}

const exportTransactionsPDF = () => {
  const doc = new jsPDF()

  // Add header
  doc.setFontSize(20)
  doc.setTextColor(11, 37, 64) // #0b2540
  doc.text('Credit Card Bill Statement', 14, 22)

  // Add card information
  doc.setFontSize(12)
  doc.setTextColor(11, 37, 64)
  const cardInfo = selectedCard.value ?
    `${selectedCard.value.cardType?.networkType || selectedCard.value.cardType || 'VISA'} ****${selectedCard.value.lastFour || selectedCard.value.cardNumber?.slice(-4) || '****'}` :
    'No Card Selected'
  doc.text(`Card: ${cardInfo}`, 14, 32)

  // Add date
  doc.setFontSize(10)
  doc.setTextColor(100, 100, 100)
  doc.text(`Generated on: ${new Date().toLocaleDateString('en-IN')}`, 14, 40)

  // Add filter information
  let filterText = 'All Transactions'
  if (filters.value.cardId || filters.value.category || filters.value.status || filters.value.bnplOnly) {
    const filterParts = []
    if (filters.value.cardId) {
      const selectedCard = cards.value.find((card: any) => card.id === filters.value.cardId)
      filterParts.push(`Card: ${selectedCard ? selectedCard.cardType : 'Selected'}`)
    }
    if (filters.value.category) {
      filterParts.push(`Category: ${filters.value.category}`)
    }
    if (filters.value.status) {
      filterParts.push(`Status: ${filters.value.status}`)
    }
    if (filters.value.bnplOnly) {
      filterParts.push('BNPL Only')
    }
    filterText = `Filtered: ${filterParts.join(', ')}`
  }

  doc.text(filterText, 14, 38)

  // Create a simple table without autoTable
  let yPosition = 50
  const pageWidth = doc.internal.pageSize.width
  const margin = 14
  const colWidths = [30, 50, 50, 30, 25, 40, 20]
  const colPositions = [margin]

  // Calculate column positions with padding
  for (let i = 1; i < colWidths.length; i++) {
    colPositions.push(colPositions[i - 1] + colWidths[i - 1] + 4) // Add 4 units padding between columns
  }


  // Ensure table fits within page width
  const totalTableWidth = colPositions[colPositions.length - 1] + colWidths[colWidths.length - 1] - margin
  if (totalTableWidth > pageWidth - 2 * margin) {
    // Adjust column widths if table is too wide
    const scaleFactor = (pageWidth - 2 * margin) / totalTableWidth
    for (let i = 0; i < colWidths.length; i++) {
      colWidths[i] = Math.floor(colWidths[i] * scaleFactor)
    }
    // Recalculate positions
    colPositions.length = 1
    for (let i = 1; i < colWidths.length; i++) {
      colPositions.push(colPositions[i - 1] + colWidths[i - 1] + 4)
    }
  }

  // Add table headers
  doc.setFontSize(8)
  doc.setFont('helvetica', 'bold')
  doc.setTextColor(11, 37, 64)

  const headers = ['Date', 'Merchant', 'Amount', 'Category', 'Status', 'Card', 'Type']
  headers.forEach((header, index) => {
    // Ensure text doesn't exceed column width
    let displayText = header
    const maxWidth = colWidths[index] - 2 // Leave 2 units margin
    const textWidth = doc.getTextWidth(header)

    if (textWidth > maxWidth) {
      // Truncate text if too long
      while (doc.getTextWidth(displayText + '...') > maxWidth && displayText.length > 0) {
        displayText = displayText.slice(0, -1)
      }
      displayText += '...'
    }

    if (index === 2) { // Amount column header - right align
      const finalTextWidth = doc.getTextWidth(displayText)
      const xPosition = colPositions[index] + colWidths[index] - finalTextWidth
      doc.text(displayText, xPosition, yPosition)
    } else {
      doc.text(displayText, colPositions[index], yPosition)
    }
  })

  yPosition += 8

  // Add table rows
  doc.setFont('helvetica', 'normal')
  doc.setTextColor(0, 0, 0)

  // Safety check for filteredTransactions
  const transactionsToExport = filteredTransactions.value || []

  transactionsToExport.forEach((transaction: Transaction, index: number) => {
    // Check if we need a new page
    if (yPosition > doc.internal.pageSize.height - 20) {
      doc.addPage()
      yPosition = 20
    }

    // Alternate row background
    if (index % 2 === 0) {
      doc.setFillColor(249, 250, 251)
      doc.rect(margin, yPosition - 4, pageWidth - 2 * margin, 8, 'F')
    }

    // Format amount properly
    const amount = typeof transaction.amount === 'number' ? transaction.amount : parseFloat(transaction.amount) || 0
    const formattedAmount = `Rs. ${amount.toLocaleString('en-IN')}`


    // Safely get merchant name
    const merchantName = transaction.merchantName || transaction.merchant || 'Unknown Merchant'
    const truncatedMerchant = merchantName.length > 15 ? merchantName.substring(0, 15) + '...' : merchantName

    // Safely get category
    const category = transaction.category || 'Other'
    const formattedCategory = category.charAt(0).toUpperCase() + category.slice(1)

    // Safely get status
    const status = transaction.status || 'Unknown'
    const formattedStatus = status.charAt(0).toUpperCase() + status.slice(1)

    // Safely get card info
    const cardType = transaction.cardType || 'Card'
    const lastFour = transaction.lastFour

    // Try to get card info from cards data if lastFour is not available
    let cardDisplay = cardType
    if (lastFour && lastFour !== '0000' && lastFour.length === 4) {
      cardDisplay = `${cardType} ****${lastFour}`
    } else {
      // Try to find the card from cards data
      const userCard = cards.value.find((card: any) => card.id === transaction.cardId)
      if (userCard && userCard.cardNumber) {
        const cardNumber = userCard.cardNumber.toString()
        const lastFourDigits = cardNumber.slice(-4)
        cardDisplay = `${cardType} ****${lastFourDigits}`
      } else if (lastFour && lastFour !== '0000') {
        cardDisplay = `${cardType} ****${lastFour}`
      } else {
        // If no valid lastFour, just show card type
        cardDisplay = cardType
      }
    }

    const rowData = [
      new Date(transaction.createdAt || transaction.transactionDate || new Date()).toLocaleDateString('en-IN'),
      truncatedMerchant,
      formattedAmount,
      formattedCategory,
      formattedStatus,
      cardDisplay,
      transaction.isBnpl ? 'BNPL' : 'Regular'
    ]

    rowData.forEach((cell, cellIndex) => {
      if (cellIndex === 2) { // Amount column - right align
        // Ensure font is set for amount
        doc.setFont('helvetica', 'normal')
        doc.setFontSize(8)

        // Simple right alignment for amount
        const textWidth = doc.getTextWidth(cell)
        const xPosition = colPositions[cellIndex] + colWidths[cellIndex] - textWidth - 2


        doc.text(cell, xPosition, yPosition)
      } else {
        // For other columns, normal truncation
        let displayText = cell
        const maxWidth = colWidths[cellIndex] - 2 // Leave 2 units margin
        const textWidth = doc.getTextWidth(cell)

        if (textWidth > maxWidth) {
          // Truncate text if too long
          while (doc.getTextWidth(displayText + '...') > maxWidth && displayText.length > 0) {
            displayText = displayText.slice(0, -1)
          }
          displayText += '...'
        }

        doc.text(displayText, colPositions[cellIndex], yPosition)
      }
    })

    yPosition += 8
  })

  // Add summary
  yPosition += 10
  doc.setFontSize(10)
  doc.setTextColor(11, 37, 64)
  doc.text(`Total Transactions: ${transactionsToExport.length}`, margin, yPosition)

  // Add total amount
  const totalAmount = transactionsToExport.reduce((sum: number, transaction: Transaction) => sum + Number(transaction.amount || 0), 0)
  doc.text(`Total Amount: Rs. ${totalAmount.toLocaleString('en-IN')}`, margin, yPosition + 10)

  // Add footer
  doc.setFontSize(8)
  doc.setTextColor(100, 100, 100)
  doc.text('Generated by Credit Card Management Portal', margin, doc.internal.pageSize.height - 10)

  // Save the PDF
  doc.save(`transactions_${new Date().toISOString().split('T')[0]}.pdf`)
}

const handleEmiPayment = (planId: string, amount: number) => {
  paymentAmount.value = amount
  paymentType.value = 'BNPL EMI Payment'
  dueDate.value = new Date().toISOString()
  showPayment.value = true

  // Store the plan ID for later use in payment success
  currentPlanId.value = planId
}

const handleBillPayment = () => {
  if (billSummary.value.amountDue <= 0) {
    alert('No amount due to pay!')
    return
  }

  // Show payment amount input first
  showPaymentInput.value = true
  customPaymentAmount.value = billSummary.value.amountDue // Default to full amount
}

const confirmPaymentAmount = () => {
  if (customPaymentAmount.value <= 0) {
    alert('Please enter a valid payment amount!')
    return
  }

  if (customPaymentAmount.value > billSummary.value.amountDue) {
    alert('Payment amount cannot exceed amount due!')
    return
  }

  // Proceed with payment
  paymentAmount.value = customPaymentAmount.value
  paymentType.value = 'Credit Card Bill Payment'
  dueDate.value = billSummary.value.dueDate
  showPaymentInput.value = false
  showPayment.value = true
}

const cancelPayment = () => {
  showPaymentInput.value = false
  customPaymentAmount.value = 0
}

// Function to reset payments (for testing)
const resetPayments = () => {
  paidAmount.value = 0
  currentStatement.value = null
  alert('Payments reset successfully! Please refresh the page to reload statement data.')
}

const closePayment = () => {
  showPayment.value = false
}

const onPaymentSuccess = async (transactionId: string, amount: number) => {
  console.log('Payment successful:', transactionId, amount)

  // Check if this is a BNPL payment or regular statement payment
  const isBnplPayment = paymentType.value === 'BNPL EMI Payment'

  if (isBnplPayment && bnplOverview.value?.activePlans && bnplOverview.value.activePlans.length > 0) {
    // Handle BNPL payment - find the plan that matches the current plan ID
    const selectedPlan = bnplOverview.value.activePlans.find(plan =>
      plan.transactionId === parseInt(currentPlanId.value)
    ) || bnplOverview.value.activePlans[0] // Fallback to first plan

    const success = await makeBnplPayment(selectedPlan.transactionId || 0, amount)
    if (success) {
      // Refresh all data for the currently selected card
      if (selectedCardId.value) {
        await Promise.all([
          fetchBnplOverview(selectedCardId.value),
          fetchCurrentStatement(selectedCardId.value),
          store.dispatch('transactions/fetchTransactions', { cardId: selectedCardId.value })
        ])
      }
      alert(`BNPL payment of ₹${amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} successful!`)
    } else {
      alert('BNPL payment failed. Please try again.')
      return
    }
  } else {
    // Handle regular statement payment
    if (currentStatement.value) {
      const success = await makePayment(currentStatement.value.id, amount)
      if (success) {
        // Refresh statement data for the currently selected card
        if (selectedCardId.value) {
          await fetchCurrentStatement(selectedCardId.value)
        }
        alert(`Payment of ₹${amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} successful!`)
      } else {
        alert('Payment failed. Please try again.')
        return
      }
    }
  }

  // Refresh the current view after payment
  await refreshCurrentView()

  setTimeout(() => {
    showPayment.value = false
  }, 2000)
}

// Method to refresh the current view
const refreshCurrentView = async () => {
  if (selectedCardId.value) {
    await fetchTransactions(selectedCardId.value)
  } else {
    const userCards = await store.dispatch('cards/fetchCards')
    if (userCards && userCards.length > 0) {
      await fetchTransactions(userCards[0].id)
    }
  }
}

// API functions for statement management
const fetchCurrentStatement = async (cardId: number) => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:8080/api/statements/card/${cardId}/current`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    if (response.ok) {
      const statement = await response.json()
      currentStatement.value = statement
      paidAmount.value = parseFloat(statement.paidAmount) || 0
    } else {
      console.error('Failed to fetch statement:', response.statusText)
    }
  } catch (error) {
    console.error('Error fetching statement:', error)
  }
}

const makePayment = async (statementId: number, paymentAmount: number) => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/statements/payment', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        statementId: statementId,
        paymentAmount: paymentAmount,
        paymentMethod: 'CARD',
        notes: 'Online payment'
      })
    })

    if (response.ok) {
      const updatedStatement = await response.json()
      currentStatement.value = updatedStatement
      paidAmount.value = parseFloat(updatedStatement.paidAmount) || 0
      return true
    } else {
      console.error('Payment failed:', response.statusText)
      return false
    }
  } catch (error) {
    console.error('Error making payment:', error)
    return false
  }
}

// BNPL API functions
const fetchBnplOverview = async (cardId: number) => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:8080/api/bnpl/overview/card/${cardId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    if (response.ok) {
      bnplOverview.value = await response.json()
    } else {
      console.error('Failed to fetch BNPL overview:', response.statusText)
    }
  } catch (error) {
    console.error('Error fetching BNPL overview:', error)
  }
}

const makeBnplPayment = async (transactionId: number, paymentAmount: number) => {
  try {
    console.log('Making BNPL payment:', { transactionId, paymentAmount })
    const token = localStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/bnpl/payment', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        transactionId: transactionId,
        paymentAmount: paymentAmount,
        paymentMethod: 'CARD',
        notes: 'BNPL installment payment'
      })
    })

    if (response.ok) {
      console.log('BNPL payment successful')
      return true
    } else {
      const errorText = await response.text()
      console.error('BNPL payment failed:', response.status, errorText)
      return false
    }
  } catch (error) {
    console.error('Error making BNPL payment:', error)
    return false
  }
}

const fetchTransactions = async (cardId?: number) => {
  loading.value = true
  try {
    // First get user's cards
    const userCards = await store.dispatch('cards/fetchCards')

    if (userCards && userCards.length > 0) {
      if (cardId) {
        // Fetch data for specific card
        await store.dispatch('transactions/fetchTransactions', { cardId })
        await fetchCurrentStatement(cardId)
        await fetchBnplOverview(cardId)
      } else if (selectedCardId.value) {
        // Fetch data for selected card
        await store.dispatch('transactions/fetchTransactions', { cardId: selectedCardId.value })
        await fetchCurrentStatement(selectedCardId.value)
        await fetchBnplOverview(selectedCardId.value)
      } else {
        // Show all cards - fetch data for first card as default
        selectedCardId.value = userCards[0].id
        await store.dispatch('transactions/fetchTransactions', { cardId: userCards[0].id })
        await fetchCurrentStatement(userCards[0].id)
        await fetchBnplOverview(userCards[0].id)
      }
    } else {
      console.warn('No cards found for user')
    }
  } catch (error) {
    console.error('Error fetching transactions:', error)
  } finally {
    loading.value = false
  }
}

// Chart initialization methods
const initCategoryChart = () => {
  if (!categoryChart.value || !hasCategoryData.value) return

  const ctx = categoryChart.value.getContext('2d')
  if (!ctx) return

  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: categoryBreakdown.value.map(cat => cat.name),
      datasets: [{
        data: categoryBreakdown.value.map(cat => cat.percentage),
        backgroundColor: categoryBreakdown.value.map(cat => cat.hexColor),
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '60%',
      plugins: {
        legend: {
          display: false
        }
      }
    }
  })
}

const initTrendsChart = () => {
  if (!trendsChart.value || !hasTrendsData.value) return

  const ctx = trendsChart.value.getContext('2d')
  if (!ctx) return

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  const currentMonth = new Date().getMonth()
  const last6Months = months.slice(currentMonth - 5, currentMonth + 1)

  // Calculate actual spending for each month
  const monthlyData = last6Months.map((month, index) => {
    const monthIndex = currentMonth - 5 + index
    const year = new Date().getFullYear()
    const monthStart = new Date(year, monthIndex, 1)
    const monthEnd = new Date(year, monthIndex + 1, 0)

    const monthTransactions = transactions.value.filter((t: Transaction) => {
      const transactionDate = new Date(t.transactionDate || t.createdAt)
      return transactionDate >= monthStart && transactionDate <= monthEnd
    })

    const monthSpent = monthTransactions.reduce((sum: number, t: Transaction) => sum + t.amount, 0)
    return Math.round(monthSpent / 1000) // Convert to thousands
  })

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: last6Months,
      datasets: [{
        label: 'Spending',
        data: monthlyData,
        borderColor: '#ffd60a',
        backgroundColor: 'rgba(255, 214, 10, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#0b2540',
        pointBorderColor: '#ffd60a',
        pointBorderWidth: 2,
        pointRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: 'rgba(0, 0, 0, 0.05)'
          },
          ticks: {
            callback: function (value) {
              return '₹' + value + 'K'
            }
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      }
    }
  })
}

onMounted(async () => {

  await fetchTransactions()
  if (cards.value.length > 0) {
    selectedCardId.value = cards.value[0].id
    if (selectedCardId.value) {
      await fetchStatementForCard(selectedCardId.value)
    }
  }

  await nextTick()
  initCategoryChart()
  initTrendsChart()
})

watch([transactions, analytics], () => {
  nextTick(() => {
    initTrendsChart()
    initCategoryChart()
  })
}, { deep: true })
</script>
