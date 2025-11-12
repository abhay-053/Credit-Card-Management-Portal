<template>
  <article class="card-visual-wrap">
    <div 
      v-if="activeCard" 
      class="credit-card" 
      :class="{ blocked: activeCard.cardStatus === 'BLOCKED', 'cursor-pointer': showToggle}"
      @click="onCardClick">
      <!-- Card Menu -->
      <div v-if="showMenu" class="card-menu" ref="menuRoot">
        <button
          class="menu-btn"
          @click="menuOpen = !menuOpen"
          :aria-expanded="String(menuOpen)"
          aria-label="Card actions"
          title="Card actions"
        >
          ⋮
        </button>

        <div v-if="menuOpen" class="menu-pop" role="menu" aria-label="Card options">
          <div class="menu-panel">
            <button
              v-for="(action, idx) in actions"
              :key="idx"
              class="menu-item"
              @click="handleAction(action, idx)"
              role="menuitem"
            >
              {{ action.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- Toggle Box -->
       <div v-if="showToggle" class="absolute top-2 right-2 flex items-center gap-2">
        <span class="text-sm font-medium">Blocked</span>
        <input 
          type="checkbox" 
          class="toggle-checkbox"
          :checked="activeCard.cardStatus === 'BLOCKED'"
          @click.prevent="() => toggleBlock(activeCard)"
        />
      </div>

      <!-- Status -->
      <div class="status-badge">{{ activeCard.cardStatus }}</div>

      <!-- Eye Button -->
      <button
        class="eye"
        @click="toggleNumberMask"
        :title="isNumberMasked ? 'Show number' : 'Hide number'"
        :aria-pressed="String(!isNumberMasked)"
      >
        <img :src="isNumberMasked ? EyeClosedIcon : EyeOpenIcon" alt="toggle number" />
      </button>

      <!-- Card Number -->
      <div class="number-row" role="group" aria-label="Card number">
        <span class="mask">
          {{ isNumberMasked ? getMaskedCardNumber(activeCard.cardNumber) : getFullCardNumber(activeCard.cardNumber) }}
        </span>
      </div>

      <!-- Card Meta -->
      <div class="meta-row">
        <div class="cardholder-name">{{ activeCard.cardHolderName || 'RAHUL VERMA' }}</div>
        <div class="card-meta">
          <div class="expires">
            <div class="label">VALID THRU</div>
            <div class="value">{{ formatExpiry(activeCard.expiryDate) }}</div>
          </div>
        </div>
      </div>

      <div class="card-divider" aria-hidden="true"></div>

      <!-- Card Limits -->
      <div class="card-limits">
        <div class="limit-left">
          <div class="label small">AVAILABLE LIMIT</div>
          <div class="amt success">₹{{ formatNumber(activeCard.availableLimit) }}</div>
        </div>
        <div class="limit-right">
          <div class="label small">TOTAL LIMIT</div>
          <div class="amt">₹{{ formatNumber(activeCard.creditLimit) }}</div>
        </div>
      </div>

      <div class="card-divider" aria-hidden="true"></div>

      <div class="card-limits">
       <div v-if="activeCard?.cvv" class="cvv-row flex items-center gap-2">
        <div>
          <div class="label small">CVV</div>
          <div class="label small">
            {{ isCvvMasked ? '***' : activeCard.cvv }}
          </div>
        </div>
        <button class="cvv-eye" @click="toggleCvvMask">
          <img :src="isCvvMasked ? EyeClosedIcon : EyeOpenIcon" alt="toggle cvv" />
        </button>
        </div>
        <div class="text-right ml-auto">
          <div class="label small">Network</div>
          <div class="network-type" :class="activeCard.cardType?.networkType?.toLowerCase()">
            {{ activeCard.cardType?.networkType }}
          </div>
        </div>
      </div>
    </div>

    <div v-else class="notice">No card data available.</div>
  </article>


<StatusChangeDialog
  v-if="showStatusDialog"
  v-model="showStatusDialog"
  :current-status="activeCard.cardStatus"
  :requested-status="requestedStatus"
  :card-last4="cardLast4"
  @confirm="confirmStatusChange"
  @cancel="cancelStatusChange"
/>


</template>

<script setup>
import EyeOpenIcon from '../assets/eye-open.svg'
import EyeClosedIcon from '../assets/eye-closed.svg'
import { ref, computed, watch } from 'vue'
import StatusChangeDialog from './StatusChangeDialog.vue'

/* ------------------ Props & Emits ------------------ */
const props = defineProps({
  card: { type: Object, required: false },
  showMenu: { type: Boolean, default: false },
  showToggle: { type: Boolean, default: false },
  actions: { type: Array, default: () => [] }
})
const emit = defineEmits(['action', 'block'])

/* ------------------ State ------------------ */
const isNumberMasked = ref(true)
const isCvvMasked = ref(true)
let numberTimer = null
let cvvTimer = null
const menuOpen = ref(false)
const menuRoot = ref(null)
const localCard = ref(null)
const showStatusDialog = ref(false)
const pendingCard = ref(null)
const requestedStatus = ref('')

/* ------------------ Regex ------------------ */
const REGEX_REMOVE_SPACES = /\s+/g
const REGEX_GROUP_CARD_DIGITS = /(\d{4})(?=\d)/g

/* ------------------ Computed ------------------ */
const activeCard = computed(() => props.card || localCard.value)

const bankLabel = computed(() => {
  const card = activeCard.value
  if (!card) return 'BANK'
  return (card.bank || card.bankName || card.issuer || 'BANK').toUpperCase().slice(0, 18)
})

const bankLabelSub = computed(() => {
  const card = activeCard.value
  return card?.bank && card.bank.length > 6 ? 'Bank' : ''
})

const cardLast4 = computed(() => {
  const card = activeCard.value
  if (!card?.cardNumber) return '1234'
  return card.cardNumber.replace(REGEX_REMOVE_SPACES, '').slice(-4)
})

/* ------------------ Utility Functions ------------------ */
function getMaskedCardNumber(num) {
  if (!num) return '**** **** **** 1234'
  const digits = String(num).replace(REGEX_REMOVE_SPACES, '')
  return '**** **** **** ' + digits.slice(-4)
}

function getFullCardNumber(num) {
  if (!num) return '1111 2222 3333 4444'
  return String(num).replace(REGEX_REMOVE_SPACES, '').replace(REGEX_GROUP_CARD_DIGITS, '$1 ')
}

function formatNumber(value) {
  const num = Number(value || 0)
  return num.toLocaleString('en-IN')
}

function formatExpiry(dateStr) {
  if (!dateStr) return '12/26'
  const [year, month] = dateStr.split('-')
  return `${month}/${year.slice(-2)}`
}


function toggleNumberMask(event) {
  event.stopPropagation()
  isNumberMasked.value = !isNumberMasked.value

  if (numberTimer) clearTimeout(numberTimer)

  if (!isNumberMasked.value) {
    numberTimer = setTimeout(() => {
      isNumberMasked.value = true
    }, 2000)
  }
}

function toggleCvvMask(event) {
  event.stopPropagation()
  isCvvMasked.value = !isCvvMasked.value

  if (cvvTimer) clearTimeout(cvvTimer)

  if (!isCvvMasked.value) {
    cvvTimer = setTimeout(() => {
      isCvvMasked.value = true
    }, 2000)
  }
}

/* ------------------ Event Handlers ------------------ */
// function toggleNumberMask(event) { 
//   event.stopPropagation();
//   isNumberMasked.value = !isNumberMasked.value 
// }

/** Instead of directly emitting, open modal */
function toggleBlock(card) {
  if (!card) return
  pendingCard.value = card
  requestedStatus.value = card.cardStatus === 'BLOCKED' ? 'ACTIVE' : 'BLOCKED'
  showStatusDialog.value = true
}

/** Trigger "view-details" if showToggle is true */
function onCardClick(e) {
  if (!props.showToggle) return
  const target = e.target
  if (
    target.closest('.menu-btn, .menu-pop, .eye, .toggle-checkbox, button')
  ) {
    return
  }
  emit('view-details', activeCard.value?.id)
}


/** Called when user confirms in modal */
function confirmStatusChange() {
  if (!pendingCard.value) return
  emit('block', { card: pendingCard.value, newStatus: requestedStatus.value })
  showStatusDialog.value = false
  pendingCard.value = null
}

/** Called when user cancels modal */
function cancelStatusChange() {
  showStatusDialog.value = false
  pendingCard.value = null
}

function handleAction(action, idx) {
  menuOpen.value = false
  emit('action', { action, idx })
}

/* ------------------ Outside Click Watcher ------------------ */
function onDocumentClick(e) {
  if (!menuRoot.value) return
  if (!menuRoot.value.contains(e.target)) menuOpen.value = false
}
watch(menuOpen, (open) => {
  if (open) document.addEventListener('click', onDocumentClick)
  else document.removeEventListener('click', onDocumentClick)
})


/* ------------------ Expose ------------------ */
defineExpose({
  toggleNumberMask,
  toggleBlock,
  confirmStatusChange,
  cancelStatusChange,
  getMaskedCardNumber,
  getFullCardNumber,
  formatNumber,
  cardLast4,
  activeCard,
  formatExpiry,
  showStatusDialog,
  requestedStatus
})
</script>

<style scoped src="./styles/card.css" />
