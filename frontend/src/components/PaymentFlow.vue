<template>
  <div v-if="showPayment" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4" style="z-index: 10000;">
    <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
      <!-- Payment Header -->
      <div class="px-6 py-4 border-b border-gray-200">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="h-8 w-8 rounded-lg flex items-center justify-center mr-3" style="background: #ffd60a;">
              <svg class="h-5 w-5" style="color: #0b2540;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-bold text-gray-900">Payment</h3>
          </div>
          <button @click="closePayment" class="text-gray-400 hover:text-gray-600">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- Payment Steps -->
      <div class="px-6 py-4">
        <!-- Step 1: Payment Details -->
        <div v-if="currentStep === 1" class="space-y-6">
          <div class="text-center">
            <h4 class="text-lg font-semibold text-gray-900 mb-2">Payment Details</h4>
            <p class="text-sm text-gray-600">Review and confirm your payment</p>
          </div>

          <!-- Payment Summary -->
          <div class="bg-gray-50 rounded-lg p-4">
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm font-medium text-gray-600">Amount to Pay</span>
              <span class="text-lg font-bold" style="color: #0b2540;">{{ formatCurrency(paymentAmount) }}</span>
            </div>
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm font-medium text-gray-600">Payment Type</span>
              <span class="text-sm text-gray-900">{{ paymentType }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm font-medium text-gray-600">Due Date</span>
              <span class="text-sm text-gray-900">{{ formatDate(dueDate) }}</span>
            </div>
          </div>

          <!-- Payment Method Selection -->
          <div class="space-y-3">
            <label class="text-sm font-medium text-gray-700">Payment Method</label>
            <div class="space-y-2">
              <label class="flex items-center p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                <input type="radio" v-model="selectedMethod" value="bank" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300">
                <div class="ml-3">
                  <div class="text-sm font-medium text-gray-900">Bank Transfer</div>
                  <div class="text-xs text-gray-500">Instant transfer from your bank account</div>
                </div>
              </label>
              <label class="flex items-center p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                <input type="radio" v-model="selectedMethod" value="upi" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300">
                <div class="ml-3">
                  <div class="text-sm font-medium text-gray-900">UPI Payment</div>
                  <div class="text-xs text-gray-500">Pay using UPI ID or QR code</div>
                </div>
              </label>
              <label class="flex items-center p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                <input type="radio" v-model="selectedMethod" value="card" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300">
                <div class="ml-3">
                  <div class="text-sm font-medium text-gray-900">Debit/Credit Card</div>
                  <div class="text-xs text-gray-500">Pay using your card details</div>
                </div>
              </label>
            </div>
          </div>

          <!-- Terms and Conditions -->
          <div class="flex items-start">
            <input type="checkbox" v-model="agreeTerms" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded mt-1">
            <label class="ml-2 text-xs text-gray-600">
              I agree to the <a href="#" class="text-blue-600 hover:underline">Terms and Conditions</a> and <a href="#" class="text-blue-600 hover:underline">Privacy Policy</a>
            </label>
          </div>
        </div>

        <!-- Step 2: Processing -->
        <div v-if="currentStep === 2" class="text-center py-8">
          <div class="animate-spin rounded-full h-16 w-16 border-4 border-gray-200 border-t-blue-600 mx-auto mb-4"></div>
          <h4 class="text-lg font-semibold text-gray-900 mb-2">Processing Payment</h4>
          <p class="text-sm text-gray-600">Please wait while we process your payment...</p>
          <div class="mt-4 text-xs text-gray-500">
            <div>Amount: {{ formatCurrency(paymentAmount) }}</div>
            <div>Method: {{ selectedMethod.toUpperCase() }}</div>
          </div>
        </div>

        <!-- Step 3: Success/Failure -->
        <div v-if="currentStep === 3" class="text-center py-8">
          <div v-if="paymentSuccess" class="text-green-600 mb-4">
            <svg class="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <h4 class="text-lg font-semibold text-gray-900 mb-2">Payment Successful!</h4>
            <p class="text-sm text-gray-600">Your payment has been processed successfully.</p>
          </div>
          <div v-else class="text-red-600 mb-4">
            <svg class="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <h4 class="text-lg font-semibold text-gray-900 mb-2">Payment Failed</h4>
            <p class="text-sm text-gray-600">There was an error processing your payment. Please try again.</p>
          </div>
          
          <div class="mt-6 space-y-2 text-xs text-gray-500">
            <div>Transaction ID: {{ transactionId }}</div>
            <div>Amount: {{ formatCurrency(paymentAmount) }}</div>
            <div>Date: {{ new Date().toLocaleString() }}</div>
          </div>
        </div>
      </div>

      <!-- Payment Actions -->
      <div class="px-6 py-4 border-t border-gray-200">
        <div v-if="currentStep === 1" class="flex space-x-3">
          <button 
            @click="closePayment" 
            class="flex-1 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-lg hover:bg-gray-200 transition-colors duration-200"
          >
            Cancel
          </button>
          <button 
            @click="proceedToPayment" 
            :disabled="!selectedMethod || !agreeTerms"
            class="flex-1 px-4 py-2 text-sm font-medium text-white rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            :class="selectedMethod && agreeTerms ? 'shadow-lg hover:shadow-xl' : ''"
            :style="selectedMethod && agreeTerms ? 'background: #0b2540;' : 'background: #9ca3af;'"
          >
            Proceed to Pay
          </button>
        </div>
        
        <div v-if="currentStep === 3" class="flex space-x-3">
          <button 
            @click="closePayment" 
            class="flex-1 px-4 py-2 text-sm font-medium text-white rounded-lg transition-all duration-200"
            style="background: #0b2540;"
          >
            {{ paymentSuccess ? 'Done' : 'Try Again' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  showPayment: boolean
  paymentAmount: number
  paymentType: string
  dueDate: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'close-payment': []
  'payment-success': [transactionId: string, amount: number]
}>()

// Reactive data
const currentStep = ref(1)
const selectedMethod = ref('')
const agreeTerms = ref(false)
const paymentSuccess = ref(false)
const transactionId = ref('')

// Methods
const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR'
  }).format(amount)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const proceedToPayment = async () => {
  currentStep.value = 2
  
  // Simulate payment processing
  await new Promise(resolve => setTimeout(resolve, 3000))
  
  // Simulate success/failure (90% success rate)
  const isSuccess = Math.random() > 0.1
  paymentSuccess.value = isSuccess
  transactionId.value = 'TXN' + Math.random().toString(36).substr(2, 9).toUpperCase()
  
  // Move to success/failure step
  currentStep.value = 3
  
  // Emit success event only if payment was successful
  if (isSuccess) {
    // Add a small delay to ensure the success message is visible
    setTimeout(() => {
      emit('payment-success', transactionId.value, props.paymentAmount)
    }, 100)
  }
}

const closePayment = () => {
  currentStep.value = 1
  selectedMethod.value = ''
  agreeTerms.value = false
  paymentSuccess.value = false
  transactionId.value = ''
  emit('close-payment')
}
</script>
