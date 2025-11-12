<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Notification -->
    <div v-if="showNotification" class="fixed top-4 right-4 z-50 animate-fadeIn">
      <div :class="[
        'px-6 py-4 rounded-lg shadow-lg flex items-center space-x-3',
        notificationType === 'success' ? 'bg-green-500 text-white' : 'bg-red-500 text-white'
      ]">
        <svg v-if="notificationType === 'success'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
        </svg>
        <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
        <span class="font-medium">{{ notificationMessage }}</span>
        <button @click="showNotification = false" class="ml-4 text-white hover:text-gray-200">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>
    </div>
    <!-- Header Section -->
    <div class="bg-white border-b border-gray-100 shadow-sm">
      <div class="w-full py-4 sm:py-6">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-3 sm:space-y-0">
          <div class="flex items-center">
            <button @click="goBack" class="mr-4 p-2 text-gray-400 hover:text-gray-600 transition-colors duration-200">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
              </svg>
            </button>
            <div>
              <h1 class="text-xl sm:text-2xl font-bold text-gray-900">Create New Transaction</h1>
              <p class="text-xs sm:text-sm text-gray-600 mt-1">Fill in the details to process your payment</p>
            </div>
          </div>
          <div class="flex items-center space-x-3">
            <button @click="goBack" class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 transition-all duration-200 shadow-sm">
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="space-y-4 sm:space-y-6">
      <!-- Main Content -->
      <div>
        <!-- Transaction Form Card -->
        <div class="bg-white rounded-xl shadow-lg border border-gray-100 p-4 sm:p-6">
            <!-- Form Header -->
            <div class="flex items-center mb-6">
              <div class="h-8 w-8 rounded-lg flex items-center justify-center mr-3" style="background: #ffd60a;">
                <svg class="h-5 w-5" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
              </div>
              <h3 class="text-lg font-bold text-gray-900">Transaction Details</h3>
            </div>
            
            <!-- Form -->
            <form @submit.prevent="submitTransaction" class="space-y-6">
              <!-- Card Selection -->
              <div class="w-[55%]">
                <label class="block text-sm font-semibold text-gray-700 mb-3">Select Card</label>
                <CardSwitcher
                  :cards="cards"
                  :selected-card-id="form.cardId ? parseInt(form.cardId) : null"
                  :show-available-limit="true"
                  placeholder="Choose your card"
                  @card-selected="selectCard"
                />
              </div>

              <!-- Merchant Selection -->
              <div class="w-[55%]">
                <label class="block text-sm font-semibold text-gray-700 mb-3">Merchant</label>
                <div class="relative">
                  <button
                    @click="toggleMerchantDropdown"
                    class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-200 bg-white hover:bg-gray-50 text-left flex items-center justify-between"
                  >
                    <div class="flex items-center">
                      <svg class="h-5 w-5 text-gray-400 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                      </svg>
                      <span class="text-gray-500">{{ getMerchantDisplayText() }}</span>
                    </div>
                    <svg class="w-4 h-4 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': showMerchantDropdown }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </button>
                  
                  <!-- Custom Merchant Dropdown Overlay -->
                  <div v-if="showMerchantDropdown" class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
                    <button
                      @click="selectMerchant('')"
                      class="w-full px-4 py-3 text-sm text-left hover:bg-yellow-200 hover:text-gray-900 transition-colors duration-200 border-b border-gray-100 text-gray-500 font-medium"
                    >
                      Select merchant
                    </button>
                    <button
                      v-for="merchant in merchants"
                      :key="merchant.id"
                      @click="selectMerchant(merchant.id)"
                      class="w-full px-4 py-3 text-sm text-left hover:bg-yellow-200 hover:text-gray-900 transition-colors duration-200 border-b border-gray-100 last:border-b-0"
                    >
                      <span class="text-gray-700 font-medium">{{ merchant.name }}</span>
                    </button>
                  </div>
                </div>
              </div>

              <!-- Amount Input -->
              <div class="w-[52%]">
                <label class="block text-sm font-semibold text-gray-700 mb-3">Amount</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <span class="text-gray-500 text-lg font-semibold">₹</span>
                  </div>
                  <input
                    v-model="form.amount"
                    @input="validateAmount"
                    type="number"
                    step="0.01"
                    min="0.01"
                    placeholder="0.00"
                    :class="[
                      'block w-full pl-12 pr-4 py-3 border rounded-xl focus:outline-none focus:ring-2 transition-all duration-200 bg-white',
                      amountError ? 'border-red-300 focus:ring-red-400 focus:border-red-400' : 'border-gray-200 focus:ring-yellow-400 focus:border-yellow-400'
                    ]"
                    required
                  />
                  <!-- Amount validation message -->
                  <div v-if="amountError" class="absolute -bottom-6 left-0 text-xs text-red-500">
                    {{ amountError }}
                  </div>
                  <!-- Available limit display -->
                  <div v-if="selectedCardInfo && !amountError" class="absolute -bottom-6 right-0 text-xs text-green-600 font-medium">
                    Available: ₹{{ formatNumber(selectedCardInfo.availableLimit) }}
                  </div>
                  <!-- Card selection indicator -->
                  <div v-if="selectedCardInfo && !amountError" class="absolute -bottom-6 left-0 text-xs text-blue-600 font-medium">
                    ✓ {{ selectedCardInfo.cardType?.networkType || selectedCardInfo.cardType || 'VISA' }} ****{{ selectedCardInfo.lastFour || selectedCardInfo.cardNumber?.slice(-4) || '****' }}
                  </div>
                  <!-- Card selection indicator when there's an error (positioned above error) -->
                  <div v-if="selectedCardInfo && amountError" class="absolute -bottom-9 left-0 text-xs text-blue-600 font-medium">
                    ✓ {{ selectedCardInfo.cardType?.networkType || selectedCardInfo.cardType || 'VISA' }} ****{{ selectedCardInfo.lastFour || selectedCardInfo.cardNumber?.slice(-4) || '****' }}
                  </div>
                  <!-- Available limit when there's an error (positioned above error) -->
                  <div v-if="selectedCardInfo && amountError" class="absolute -bottom-9 right-0 text-xs text-green-600 font-medium">
                    Available: ₹{{ formatNumber(selectedCardInfo.availableLimit) }}
                  </div>
                </div>
              </div>

              <!-- Category Selection -->
              <div class="w-[55%]">
                <label class="block text-sm font-semibold text-gray-700 mb-3">Category</label>
                <div class="relative">
                  <button
                    @click="toggleCategoryDropdown"
                    class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-200 bg-white hover:bg-gray-50 text-left flex items-center justify-between"
                  >
                    <span class="text-gray-500">{{ getCategoryDisplayText() }}</span>
                    <svg class="w-4 h-4 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': showCategoryDropdown }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </button>
                  
                  <!-- Custom Category Dropdown Overlay -->
                  <div v-if="showCategoryDropdown" class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
                    <div class="p-2 space-y-1">
                      <button
                        @click="selectCategory('')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200"
                      >
                        Select category
                      </button>
                      <button
                        @click="selectCategory('shopping')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">🛒</span>
                        <span class="text-gray-700 font-medium">Shopping</span>
                      </button>
                      <button
                        @click="selectCategory('food')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">🍔</span>
                        <span class="text-gray-700 font-medium">Food & Dining</span>
                      </button>
                      <button
                        @click="selectCategory('travel')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">✈️</span>
                        <span class="text-gray-700 font-medium">Travel</span>
                      </button>
                      <button
                        @click="selectCategory('entertainment')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">🎬</span>
                        <span class="text-gray-700 font-medium">Entertainment</span>
                      </button>
                      <button
                        @click="selectCategory('utilities')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">⚡</span>
                        <span class="text-gray-700 font-medium">Utilities</span>
                      </button>
                      <button
                        @click="selectCategory('healthcare')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">🏥</span>
                        <span class="text-gray-700 font-medium">Healthcare</span>
                      </button>
                      <button
                        @click="selectCategory('education')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">📚</span>
                        <span class="text-gray-700 font-medium">Education</span>
                      </button>
                      <button
                        @click="selectCategory('other')"
                        class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                      >
                        <span class="text-lg mr-3">📦</span>
                        <span class="text-gray-700 font-medium">Other</span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- BNPL Toggle -->
              <div class="bg-gradient-to-r from-yellow-50 to-yellow-100 rounded-xl p-6 border border-yellow-200">
                <div class="flex items-center justify-between mb-4">
                  <div class="flex items-center">
                    <div class="w-12 h-12 rounded-xl flex items-center justify-center mr-4" style="background: #ffd60a;">
                      <svg class="w-6 h-6" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                      </svg>
                    </div>
                    <div>
                      <h4 class="text-lg font-semibold text-gray-900">Buy Now, Pay Later</h4>
                      <p class="text-sm text-gray-600">Split your payment into monthly installments</p>
                    </div>
                  </div>
                  <div class="flex flex-col items-end">
                    <label class="relative inline-flex items-center cursor-pointer" :class="{ 'opacity-50 cursor-not-allowed': !isBnplEligible }">
                      <input 
                        v-model="form.isBnpl" 
                        type="checkbox" 
                        class="sr-only peer"
                        :disabled="!isBnplEligible"
                      />
                      <div class="w-14 h-7 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-yellow-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-yellow-500"></div>
                    </label>
                    <p v-if="!isBnplEligible" class="text-xs text-red-500 mt-1 text-right">
                      BNPL not available for your account
                    </p>
                  </div>
                </div>

                <!-- BNPL Options -->
                <div v-if="form.isBnpl" class="space-y-4 animate-fadeIn">
                  <div class="w-[55%]">
                    <label class="block text-sm font-semibold text-gray-700 mb-2">Tenure</label>
                    <div class="relative">
                      <button
                        @click="toggleTenureDropdown"
                        class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-200 bg-white hover:bg-gray-50 text-left flex items-center justify-between"
                      >
                        <span class="text-gray-500">{{ getTenureDisplayText() }}</span>
                        <svg class="w-4 h-4 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': showTenureDropdown }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                        </svg>
                      </button>
                      
                      <!-- Custom Tenure Dropdown Overlay -->
                      <div v-if="showTenureDropdown" class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
                        <div class="p-2 space-y-1">
                          <button
                            @click="selectTenure('')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200"
                          >
                            Select tenure
                          </button>
                          <button
                            @click="selectTenure('3')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                          >
                            <span class="inline-block w-3 h-3 rounded-full bg-purple-500 mr-3"></span>
                            <span class="text-gray-700 font-medium">3 months</span>
                          </button>
                          <button
                            @click="selectTenure('6')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                          >
                            <span class="inline-block w-3 h-3 rounded-full bg-purple-500 mr-3"></span>
                            <span class="text-gray-700 font-medium">6 months</span>
                          </button>
                          <button
                            @click="selectTenure('9')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                          >
                            <span class="inline-block w-3 h-3 rounded-full bg-purple-500 mr-3"></span>
                            <span class="text-gray-700 font-medium">9 months</span>
                          </button>
                          <button
                            @click="selectTenure('12')"
                            class="w-full px-3 py-2 text-sm text-left hover:bg-yellow-500 hover:text-white rounded-lg transition-colors duration-200 flex items-center"
                          >
                            <span class="inline-block w-3 h-3 rounded-full bg-purple-500 mr-3"></span>
                            <span class="text-gray-700 font-medium">12 months</span>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="form.tenureMonths && form.amount" class="bg-gradient-to-r from-yellow-50 to-orange-50 rounded-xl p-4 border border-yellow-200">
                    <div class="flex justify-between items-center mb-3">
                      <span class="text-lg font-semibold text-gray-700">Monthly EMI:</span>
                      <span class="text-2xl font-bold text-green-600">₹{{ formatNumber(monthlyEmi) }}</span>
                    </div>
                    <div class="grid grid-cols-2 gap-4 text-sm">
                      <div>
                        <span class="text-gray-600">Total Amount:</span>
                        <span class="font-semibold text-gray-800">₹{{ formatNumber(parseFloat(form.amount) || 0) }}</span>
                      </div>
                      <div>
                        <span class="text-gray-600">Tenure:</span>
                        <span class="font-semibold text-gray-800">{{ form.tenureMonths }} months</span>
                      </div>
                      <div>
                        <span class="text-gray-600">Interest Rate:</span>
                        <span class="font-semibold text-gray-800">0% (No Interest)</span>
                      </div>
                      <div>
                        <span class="text-gray-600">Total Payable:</span>
                        <span class="font-semibold text-gray-800">₹{{ formatNumber(parseFloat(form.amount) || 0) }}</span>
                      </div>
                    </div>
                    <div class="mt-3 p-2 bg-green-100 rounded-lg">
                      <p class="text-sm text-green-800">
                        <span class="font-semibold">✓ Interest-free EMI</span> - No additional charges for {{ form.tenureMonths }} months
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="flex space-x-4 pt-6">
                <button
                  type="button"
                  @click="goBack"
                  class="flex-1 px-6 py-3 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 transition-all duration-200 shadow-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  :disabled="loading"
                  class="flex-1 px-6 py-3 text-sm font-medium text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-lg hover:shadow-xl"
                  style="background: #ffd60a; color: #0b2540;"
                >
                  <span v-if="!loading">{{ form.isBnpl ? `Create BNPL Transaction (₹${formatNumber(monthlyEmi)}/month)` : 'Create Transaction' }}</span>
                  <span v-else class="flex items-center justify-center">
                    <svg class="animate-spin -ml-1 mr-3 h-5 w-5" style="color: #0b2540;" fill="none" viewBox="0 0 24 24">
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
  </div>

  <!-- Success Modal -->
  <div v-if="showSuccessModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl p-8 max-w-md w-full mx-4 shadow-2xl">
      <!-- Success Icon -->
      <div class="flex justify-center mb-6">
        <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
          <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
        </div>
      </div>

      <!-- Success Message -->
      <div class="text-center mb-6">
        <h3 class="text-2xl font-bold text-gray-900 mb-2">Transaction Created Successfully!</h3>
        <p class="text-gray-600 mb-4">
          Your {{ successModalData.isBnpl ? 'BNPL' : 'regular' }} transaction of 
          <span class="font-semibold text-green-600">₹{{ formatNumber(successModalData.amount) }}</span> 
          has been processed.
        </p>
        
        <div v-if="successModalData.isBnpl" class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
          <div class="flex items-center">
            <svg class="w-5 h-5 text-blue-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <p class="text-sm text-blue-800">
              Your BNPL plan is now active. You can manage your installments from the BNPL overview section.
            </p>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex flex-col sm:flex-row gap-3">
        <button
          @click="createAnotherTransaction"
          class="flex-1 px-6 py-3 bg-gray-100 text-gray-700 rounded-lg font-medium hover:bg-gray-200 transition-colors duration-200"
        >
          Create Another Transaction
        </button>
        <button
          @click="goToTransactions"
          class="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors duration-200"
        >
          View Transactions
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import CardSwitcher from '../../components/CardSwitcher.vue'

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

const router = useRouter()
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
const amountError = ref('')
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Success modal state
const showSuccessModal = ref(false)
const successModalData = ref({
  amount: 0,
  isBnpl: false,
  transactionId: null as number | null
})

// Dropdown state
// Removed showCardDropdown - now handled by CardSwitcher component
const showMerchantDropdown = ref(false)
const showCategoryDropdown = ref(false)
const showTenureDropdown = ref(false)

// Computed properties
const cards = computed(() => store.state?.cards?.cards || [])
const merchants = computed(() => store.state?.merchants?.merchants || [])

const selectedCardInfo = computed(() => {
  if (!form.value.cardId) return null
  const cardId = parseInt(form.value.cardId)
  return cards.value.find((card: any) => card.id === cardId)
})

// Check if user is eligible for BNPL (this would need to be fetched from user profile)
const isBnplEligible = computed(() => {
  // For now, we'll assume all users are eligible unless we fetch this from the backend
  // In a real implementation, this would come from the user profile API
  return true
})

const monthlyEmi = computed(() => {
  if (!form.value.amount || !form.value.tenureMonths) return 0
  const amount = parseFloat(form.value.amount)
  const tenure = parseInt(form.value.tenureMonths)
  if (amount <= 0 || tenure <= 0) return 0
  return amount / tenure
})

// Methods
const goBack = () => {
  router.push('/transactions')
}

const formatNumber = (value: number) => {
  return new Intl.NumberFormat('en-IN', { 
    minimumFractionDigits: 0, 
    maximumFractionDigits: 2 
  }).format(value)
}

const validateAmount = () => {
  const amount = parseFloat(form.value.amount)
  const selectedCard = selectedCardInfo.value
  
  if (!amount || amount <= 0) {
    amountError.value = 'Please enter a valid amount'
    return false
  }
  
  if (selectedCard && amount > selectedCard.availableLimit) {
    amountError.value = `Amount exceeds available limit of ₹${formatNumber(selectedCard.availableLimit)}`
    return false
  }
  
  amountError.value = ''
  return true
}

const showSuccessNotification = (message: string) => {
  notificationMessage.value = message
  notificationType.value = 'success'
  showNotification.value = true
  setTimeout(() => {
    showNotification.value = false
  }, 3000)
}

const showErrorNotification = (message: string) => {
  notificationMessage.value = message
  notificationType.value = 'error'
  showNotification.value = true
  setTimeout(() => {
    showNotification.value = false
  }, 3000)
}

const showSuccessModalFunc = (amount: number, isBnpl: boolean) => {
  successModalData.value = {
    amount,
    isBnpl,
    transactionId: Date.now() // Simple ID for display
  }
  showSuccessModal.value = true
}

const closeSuccessModal = () => {
  showSuccessModal.value = false
}

const goToTransactions = () => {
  closeSuccessModal()
  router.push('/transactions')
}

const createAnotherTransaction = () => {
  closeSuccessModal()
  // Form is already reset, user can create another transaction
}

// Add missing functions for data refresh
const fetchCurrentStatement = async (cardId: number) => {
  try {
    const response = await fetch(`http://localhost:8080/api/statements/current/${cardId}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    if (response.ok) {
      const statement = await response.json()
      console.log('Statement refreshed:', statement)
    }
  } catch (error) {
    console.error('Error fetching statement:', error)
  }
}

const fetchBnplOverview = async (cardId: number) => {
  try {
    const response = await fetch(`http://localhost:8080/api/bnpl/overview/card/${cardId}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    if (response.ok) {
      const overview = await response.json()
      console.log('BNPL overview refreshed:', overview)
    }
  } catch (error) {
    console.error('Error fetching BNPL overview:', error)
  }
}

// Dropdown methods
// Removed toggleCardDropdown - now handled by CardSwitcher component

const toggleMerchantDropdown = () => {
  showMerchantDropdown.value = !showMerchantDropdown.value
  showCategoryDropdown.value = false
  showTenureDropdown.value = false
}

const toggleCategoryDropdown = () => {
  showCategoryDropdown.value = !showCategoryDropdown.value
  showMerchantDropdown.value = false
  showTenureDropdown.value = false
}

const toggleTenureDropdown = () => {
  showTenureDropdown.value = !showTenureDropdown.value
  showMerchantDropdown.value = false
  showCategoryDropdown.value = false
}

const selectCard = (cardId: string) => {
  form.value.cardId = cardId
}

const selectMerchant = (merchantId: string) => {
  form.value.merchantAccountId = merchantId
  showMerchantDropdown.value = false
}

const selectCategory = (category: string) => {
  form.value.category = category
  showCategoryDropdown.value = false
}

const selectTenure = (tenure: string) => {
  form.value.tenureMonths = tenure
  showTenureDropdown.value = false
  
  // Don't auto-submit, let user review the EMI calculation first
  console.log('Tenure selected:', tenure, 'EMI will be:', monthlyEmi.value)
}

// Removed getCardDisplayText - now handled by CardSwitcher component

const getMerchantDisplayText = () => {
  if (!form.value.merchantAccountId) return 'Select merchant'
  const selectedMerchant = merchants.value.find((merchant: any) => merchant.id === form.value.merchantAccountId)
  return selectedMerchant ? selectedMerchant.name : 'Select merchant'
}

const getCategoryDisplayText = () => {
  if (!form.value.category) return 'Select category'
  const categoryMap: Record<string, string> = {
    shopping: '🛒 Shopping',
    food: '🍔 Food & Dining',
    travel: '✈️ Travel',
    entertainment: '🎬 Entertainment',
    utilities: '⚡ Utilities',
    healthcare: '🏥 Healthcare',
    education: '📚 Education',
    other: '📦 Other'
  }
  return categoryMap[form.value.category] || 'Select category'
}

const getTenureDisplayText = () => {
  if (!form.value.tenureMonths) return 'Select tenure'
  return `${form.value.tenureMonths} months`
}

const submitTransaction = async () => {
  if (loading.value) return
  
  // Validate form
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  
  try {
    // Prepare transaction data
    const transactionData = {
      cardId: parseInt(form.value.cardId),
      merchantName: getMerchantDisplayText(),
      amount: parseFloat(form.value.amount),
      category: form.value.category,
      isBnpl: form.value.isBnpl,
      tenureMonths: form.value.isBnpl ? parseInt(form.value.tenureMonths) : null,
      transactionDate: new Date().toISOString().split('T')[0]
    }
    
    console.log('Creating transaction:', transactionData)
    
    // Call the transaction service
    const newTransaction = await store.dispatch('transactions/createTransaction', transactionData)
    
    console.log('Transaction created successfully:', newTransaction)
    
    // Show success modal instead of immediate redirect
    showSuccessModalFunc(parseFloat(form.value.amount), form.value.isBnpl)
    
    // Refresh all data after transaction creation
    if (form.value.cardId) {
      await Promise.all([
        store.dispatch('transactions/fetchTransactions', { 
          cardId: parseInt(form.value.cardId), 
          page: 0, 
          size: 10 
        }),
        store.dispatch('cards/fetchCards'), // Refresh card data to get updated available limit
        fetchCurrentStatement(parseInt(form.value.cardId)), // Refresh statement
        fetchBnplOverview(parseInt(form.value.cardId)) // Refresh BNPL data
      ])
    }
    
    // Reset form
    form.value = {
      cardId: '',
      merchantAccountId: '',
      amount: '',
      category: '',
      isBnpl: false,
      tenureMonths: ''
    }
  } catch (error: any) {
    console.error('Error creating transaction:', error)
    
    // Handle specific error types
    if (error.response?.status === 403 && error.response?.data?.error === 'BNPL Not Available') {
      showErrorNotification('You are not eligible for BNPL transactions. Please contact support to check your eligibility or try a regular transaction instead.')
    } else if (error.response?.data?.message) {
      showErrorNotification(error.response.data.message)
    } else {
      showErrorNotification('Failed to create transaction. Please try again.')
    }
  } finally {
    loading.value = false
  }
}

const validateForm = () => {
  if (!form.value.cardId) {
    showErrorNotification('Please select a card')
    return false
  }
  
  if (!form.value.merchantAccountId) {
    showErrorNotification('Please select a merchant')
    return false
  }
  
  if (!form.value.amount || parseFloat(form.value.amount) <= 0) {
    showErrorNotification('Please enter a valid amount')
    return false
  }
  
  if (!form.value.category) {
    showErrorNotification('Please select a category')
    return false
  }
  
  // Check if amount exceeds card limit
  const selectedCard = cards.value.find((card: any) => card.id === form.value.cardId)
  if (selectedCard && parseFloat(form.value.amount) > selectedCard.availableLimit) {
    showErrorNotification(`Amount exceeds available limit of ₹${formatNumber(selectedCard.availableLimit)}`)
    return false
  }
  
  // BNPL specific validations
  if (form.value.isBnpl) {
    if (!form.value.tenureMonths) {
      showErrorNotification('Please select tenure for BNPL transaction')
      return false
    }
    
    const emi = parseFloat(form.value.amount) / parseInt(form.value.tenureMonths)
    if (emi < 100) {
      showErrorNotification('EMI amount should be at least ₹100')
      return false
    }
  }
  
  return true
}

onMounted(() => {
  // Fetch cards and merchants if not already loaded
  store.dispatch('cards/fetchCards')
  store.dispatch('merchants/fetchMerchants')
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
