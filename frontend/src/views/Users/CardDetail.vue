<template>
  <div class="page-root">
    <main class="container">
      <div class="page-header">
        <div class="title-block">
          <h1 class="page-title">Card Details</h1>
          <p class="page-sub">
            Manage your {{ card?.cardType?.name || card?.network || 'VISA' }} card
            ending with <strong>{{ cardLastFour }}</strong>
          </p>
        </div>
        <div class="renderBack">
          <RouterLink to="/cards" class="back">← Back to Cards</RouterLink>
        </div>
      </div>
      <section class="grid">
        <div class="left">
          <Card
            v-if="card"
            :key="card.id + '-' + card.creditLimit"
            :card="mergedCard"
            :userName="card?.cardHolderName"
            :showMenu="false"
            :actions="cardActionsMenu"
            @action="handleCardAction"
            @block="handleCardBlock"
          />
          <div v-else class="notice">No card data available.</div>
          <div class="actions-card">
            <h3 class="card-title">Card Actions</h3>
            <p class="muted">Manage your card limit</p>
            <div class="action-controls">
              <button
                class="update-btn"
                @click="openLimitModal"
                :disabled="loading"
              >
                ↗ Update Credit Limit
              </button>
            </div>
          </div>
        </div>
        <div class="right">
          <div class="panel overview">
            <h3>Credit Overview</h3>
            <div class="overview-grid">
              <div class="ov-left">
                <div class="row">
                  <div class="rlabel">Credit Limit</div>
                  <div class="rval">
                    ₹{{ formatINR(totalLimit) }}
                  </div>
                </div>
                <div class="row">
                  <div class="rlabel">Outstanding Balance</div>
                  <div class="rval danger">
                    ₹{{ formatINR(outstanding) }}
                  </div>
                </div>
                <div class="row">
                  <div class="rlabel">Available Credit</div>
                  <div class="rval success">
                    ₹{{ formatINR(availableCredit) }}
                  </div>
                </div>
                <div class="util-row">
                  <div class="rlabel">Credit Utilization</div>
                  <div class="util-value util-danger">
                    {{ utilizationPercentage }}%
                  </div>
                </div>
                <div class="progress-wrap">
                  <div
                    class="progress util-danger"
                    :style="{ width: utilizationPercentage + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
          <div class="panel transactions">
            <h3>Recent Transactions</h3>
            <p class="muted">Latest activity on this card</p>
            <ul class="tx-list">
              <li v-for="tx in recentTransactionsList" :key="tx.id" class="tx">
                <div class="tx-left">
                  <div class="tx-merchant">
                    {{ tx.merchantName || 'Unknown' }}
                    <span v-if="tx.isBnpl" class="badge">BNPL</span>
                  </div>
                  <div class="tx-meta">
                    {{ tx.category || 'Other' }} • {{ formatDate(tx.date) }}
                  </div>
                  <div v-if="tx.processorName" class="tx-processor">
                    {{ tx.processorName }}
                  </div>
                </div>
                <div class="tx-right">
                  <div :class="['tx-amount', (Number(tx.amount) < 0) ? 'neg' : 'pos']">
                    {{ formatCurrency(tx.amount) }}
                  </div>
                </div>
              </li>
              <li v-if="recentTransactionsList.length === 0" class="tx">
                <div>
                  <div class="tx-title">No recent transactions</div>
                  <div class="tx-sub">You have no transaction history for this card</div>
                </div>
                <div class="tx-amount">—</div>
              </li>
            </ul>
          </div>
        </div>
      </section>
    </main>
    <UpdateCreditLimitModal
      v-if="card"
      v-model="isLimitModalOpen"
      :currentLimit="Number(totalLimit)"
      :outstanding="outstanding"
      :min-limit="minLimit"
      :max-limit="maxLimit"
      @next="handleLimitNext"
    />
  </div>
</template>
<script>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Card from '../../components/Card.vue'
import UpdateCreditLimitModal from '../../components/UpdateCreditLimitModal.vue'
import {
  getCards,
  getCardTypes,
  updateCardLimit as csUpdateCardLimit
} from '../../services/cards-service'
import { getDashboard } from '../../services/dashboardService'

export default {
  name: 'CardDetail',
  components: { Card, UpdateCreditLimitModal },
  setup () {
    const route = useRoute()
    const router = useRouter()
    const card = ref(null)
    const transactions = ref([])
    const loading = ref(false)
    const minLimit = ref(0)
    const maxLimit = ref(0)

    const isLimitModalOpen = ref(false)

    const totalLimit = computed(() => Number(card.value?.creditLimit || 0))
    const availableCredit = computed(() => Number(card.value?.availableLimit || 0))
    const outstanding = computed(() =>
      Math.max(0, totalLimit.value - availableCredit.value)
    )
    const utilizationPercentage = computed(() => {
      return totalLimit.value
        ? Math.round((outstanding.value / totalLimit.value) * 100)
        : 0
    })

    const cardLastFour = computed(() => {
      if (!card.value) return '1234'
      const num = card.value.cardNumber
      return num ? String(num).replace(/\s+/g, '').slice(-4) : '1234'
    })

    const mergedCard = computed(() => {
      if (!card.value) return null
      return {
        ...card.value,
        fullPan: null,
        cvv: card.value.cvv ?? null
      }
    })

    function normalizeTx(tx = {}) {
      return {
        id: tx.id,
        cardId: tx.cardId,
        merchantName: tx.merchantName || 'Unknown',
        amount: tx.amount ?? 0,
        date: tx.transactionDate || null,
        category: tx.category || null,
        isBnpl: !!tx.isBnpl,
        processorName: null, 
        raw: tx
      }
    }


    const recentTransactionsList = computed(() => {
      return (transactions.value || []).slice(0, 6)
    })

    function formatINR (amount) {
      return Number(amount || 0).toLocaleString('en-IN')
    }

    function formatCurrency (amount) {
      const numericValue = Number(amount || 0)
      const sign = numericValue < 0 ? '-' : ''
      return sign + '₹' + Math.abs(numericValue).toLocaleString('en-IN')
    }

    function formatDate(isoString) {
      if (!isoString) return ''
      const dateObj = new Date(isoString)
      return dateObj.toLocaleString('en-US', {
        day: "numeric",
        month: "short",
        hour: "2-digit",
        minute: "2-digit"
      })
    }

    const cardActionsMenu = computed(() => {
      const id = card.value?.id || ''
      return [
        { label: 'View Details', to: id ? `/cards/${id}` : null },
        { label: 'Manage Limits', to: id ? `/cards/${id}/limits` : null },
        {
          label: card.value?.cardStatus === 'BLOCKED' ? 'Unblock Card' : 'Block Card',
          value: { type: 'toggle-block' }
        }
      ]
    })

    function handleCardAction ({ action }) {
      if (action?.to) router.push(action.to)
    }

    function handleCardBlock (payload) {
      if (payload?.card) {
        payload.card.status = payload.newStatus
      }
    }

    function openLimitModal () {
      if (card.value) {
        isLimitModalOpen.value = true
      }
    }

    async function handleLimitNext (payload) {
      const selectedNewLimit = Number(payload?.newLimit || 0)
      if (!selectedNewLimit) return

      isLimitModalOpen.value = false
      loading.value = true

      const id = card.value?.id || route.params.id
      if (!id) {
        loading.value = false
        alert('Invalid card id')
        return
      }

      try {
        await csUpdateCardLimit(id, selectedNewLimit)

        await loadCard()

        if (card.value) card.value = { ...card.value }

        await nextTick()

        alert('Limit updated successfully')
      } catch (err) {
        console.error('Failed to update limit', err)
        alert('Failed to update limit')
      } finally {
        loading.value = false
      }
    }

    async function loadCard () {
      loading.value = true
      try {
        const id = route.params.id
        if (!id) return

        const cardsList = await getCards()
        const matched = (cardsList || []).find(c => String(c.id) === String(id))

        if (matched) {
          card.value = { ...matched }

          const types = await getCardTypes()
          const typeMatch = types.find(t => String(t.name).trim().toLowerCase() === String(matched.cardType?.name || '').trim().toLowerCase())
          if (typeMatch) {
            minLimit.value = typeMatch.minCardLimit
            maxLimit.value = typeMatch.maxCardLimit
          } else {
            minLimit.value = 0
            maxLimit.value = 0
          }
        }

        const dash = await getDashboard()
        if (dash?.transactions) {
          const cardTx = dash.transactions
            .filter(tx => String(tx.cardId) === String(id))
            .map(normalizeTx)
            .sort((a, b) => {
              const ta = a.date ? new Date(a.date).getTime() : 0
              const tb = b.date ? new Date(b.date).getTime() : 0
              return tb - ta
            })

          transactions.value = cardTx
        } else {
          transactions.value = []
        }
      } catch (err) {
        console.error('loadCard failed', err)
      } finally {
        loading.value = false
      }
    }

    onMounted(loadCard)

    return {
      card,
      transactions,
      loading,
      isLimitModalOpen,
      minLimit,
      maxLimit,
      cardLastFour,
      totalLimit,
      availableCredit,
      outstanding,
      utilizationPercentage,
      mergedCard,
      recentTransactionsList,
      formatINR,
      formatCurrency,
      formatDate,
      cardActionsMenu,
      handleCardAction,
      handleCardBlock,
      openLimitModal,
      handleLimitNext,
      loadCard
    }
  }
}
</script>

<style scoped>
.link-btn {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
  margin-left: 8px;
  font-size: 0.9rem;
}
.link-btn:disabled { opacity: 0.6; cursor: not-allowed; }

:root{
  --bg: #f6f7f9;
  --accent: #0b2540;
  --muted: #6b7280;
  --green: #16a34a;
  --danger: #ef4444;
}
.card-title {
  font-weight: 700;
}

*{box-sizing:border-box}
.page-root{
  font-family: 'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, Arial;
  background:var(--bg);
  min-height:100vh;
  color:#0f1724;
  overflow-x:hidden;
}

.container{
  max-width:1200px;
  margin:24px auto;
  padding:0 16px 80px;
  width:100%;
}
.page-header{
  display:flex;
  gap:16px;
  align-items:center;
  margin-bottom:18px;
  justify-content:space-between;
  flex-wrap:wrap;
}
.back{ border: 1px solid #eee;padding: 6px 10px;border-radius: 8px;  font-size: 14px;color:var(--muted); text-decoration:none; padding:6px 8px; white-space:nowrap; background-color: #ffd60a;}
.title-block{ display:flex; flex-direction:column; min-width:0; }
.page-title{ margin:0; font-size:28px; font-weight:800; word-break:break-word; }
.page-sub{ color:var(--muted); margin-top:6px; font-size:14px }

.grid{
  display:grid;
  grid-template-columns: 1fr minmax(260px, 35%);
  gap:20px;
  align-items:start;
  width:100%;
}
.badge {
  display: inline-block;
  margin-left: 6px;
  padding: 2px 6px;
  font-size: 11px;
  font-weight: 600;
  color: #333;
  background: #eee;
  border-radius: 4px;
}

@media (max-width: 980px){
  .grid{ grid-template-columns: 1fr; gap:16px; }
}

.left{ display:flex; flex-direction:column; gap:14px; min-width:0; }

.actions-card{
  background:#fff; border-radius:12px; padding:16px; box-shadow:0 8px 20px rgba(2,6,23,0.04);
  width:100%;
}
.actions-card h3{ margin:0 0 6px 0; font-size:18px; }
.action-controls{ display:flex; gap:12px; margin-top:8px; flex-wrap:wrap; }
.action-controls button{ flex:1 1 auto; min-width:0; }
.update-btn{
  background: linear-gradient(180deg,#0b2540,#0b2540); color:#fff; padding:10px 14px; border-radius:10px; border:none; font-weight:800;
  box-shadow:0 10px 24px rgba(11,37,64,0.12);
}
.block-btn{
  background:#fff; border:1px solid #e7e7ea; padding:10px 14px; border-radius:10px; font-weight:700;
}

.right{ display:flex; flex-direction:column; gap:12px; min-width:0; }

.panel{
  background:#fff; border-radius:12px; padding:16px; box-shadow: 0 8px 22px rgba(2,6,23,0.04);
  width:100%;
  overflow:hidden;
}
.panel h3{ margin:0 0 8px 0; font-size:16px; font-weight:800; }


.overview-grid{ display:flex; gap:12px; align-items:flex-start; flex-wrap:wrap; }
.ov-left{ flex:1 1 240px; min-width:0 }
.row{ display:flex; justify-content:space-between; align-items:center; padding:10px 0; border-bottom:1px dashed #f3f4f6; }
.row:last-child{ border-bottom:0; }
.rlabel{ color:var(--muted); font-size:14px; white-space:normal; overflow-wrap:break-word; }
.rval{ font-weight:800; font-size:16px; white-space:nowrap; text-overflow:ellipsis; overflow:hidden; max-width:100%; }
.rval.success{ color:var(--green); }
.rval.danger{ color:var(--danger); }

.ov-right{ width:auto; display:flex; flex-direction:column; align-items:flex-end; gap:8px; min-width:0; }
.util-value{ font-weight:800; }
.progress-wrap{ width:100%; background:#f1f5f9; height:12px; border-radius:999px; overflow:hidden; }
.progress{ height:100%; background:#0b2540; width:0%; transition:width .45s ease; }

.kv{ display:flex; justify-content:space-between; align-items:center; padding:12px 0; border-top:1px solid #f3f5f7; }
.kv:first-of-type{ border-top:0; }

.tx-list{ list-style:none; padding:0; margin:8px 0 0; }
.tx{
  display:flex;
  justify-content:space-between;
  align-items:flex-start;
  gap:12px;
  padding:12px 0;
  border-bottom:1px solid #f6f7f9;
  min-width:0;
}
.tx:last-child{ border-bottom:none; }
.tx-left{ min-width:0; max-width:100%; }
.tx-merchant{ font-weight:700; word-break:break-word; }
.tx-meta{ color:var(--muted); font-size:13px; margin-top:6px; white-space:normal; }
.tx-right{ flex:0 0 110px; text-align:right; min-width:90px; }
.tx-amount{ font-weight:800; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.tx-amount.neg{ color:var(--danger); }
.tx-status{ margin-top:8px; font-size:11px; color:var(--muted); background:#f8fafc; padding:6px 10px; border-radius:999px; display:inline-block; }

@media (max-width: 760px){
  .container{ padding:0 12px 120px; }
  .page-title{ font-size:20px; }
  .page-sub{ font-size:13px; }
  .renderBack{ order:2; width:100%; margin-top:8px; }
  .grid{ grid-template-columns: 1fr; gap:12px; }
  .action-controls{ flex-direction:column; }
  .action-controls button{ width:100%; }
  .panel{ padding:12px; }
  .panel h3{ font-size:15px; }
  .overview-grid{ flex-direction:column; gap:10px; }
  .ov-right{ width:100%; align-items:flex-start; }
  .row{ padding:8px 0 }
  .rlabel{ font-size:13px }
  .rval{ font-size:15px; }
  .tx-right{ flex:0 0 95px; min-width:80px; }
}

@media (max-width:420px){
  .page-title{ font-size:18px }
  .page-sub{ font-size:12px }
  .progress-wrap{ height:10px }
  .tx-meta{ font-size:12px }
  .tx-right{ flex:0 0 85px; min-width:70px; }
}

.rval.success{ color:#16a34a !important; }
.rval.danger{ color:#ef4444 !important; }
.tx-amount.pos{ color:#ef4444  !important; }
.tx-amount.neg{ color:#ef4444 !important; }

.util-row{ display:flex; justify-content:space-between; align-items:center; gap:12px; margin-bottom:10px; }
.util-value{ font-weight:800; font-size:15px; color:#0f1724; }
.util-danger{ color:#ef4444 !important; }
.progress.util-danger {
  background: #ef4444 !important; 
}

button, a, input, select, textarea{ max-width:100%; }
</style>