<template>
  <div class="dashboard">
    <main class="container">
      <section class="main">
        <h1>Welcome back, {{ user.name }}!</h1>
        <p class="subtitle">Here's your credit card overview</p>

        <div class="kpis">
          <div class="kpi">
            <div class="kpi-title">Active Cards</div>
            <div class="kpi-main">{{ summary.activeCards }}</div>
            <div class="kpi-sub">out of {{ summary.totalCards }} total cards</div>
          </div>

          <div class="kpi">
            <div class="kpi-title">Total Credit Limit</div>
            <div class="kpi-main">₹{{ formatNumber(summary.totalLimit) }}</div>
            <div class="kpi-sub">Across all cards</div>
          </div>

          <div class="kpi">
            <div class="kpi-title">Available Credit</div>
            <div class="kpi-main success">₹{{ formatNumber(summary.availableCredit) }}</div>
            <div class="kpi-sub">Ready to spend</div>
          </div>

          <div class="kpi">
            <div class="kpi-title">Outstanding Balance</div>
            <div class="kpi-main danger">₹{{ formatNumber(summary.outstanding) }}</div>
            <div class="kpi-sub">Total dues</div>
          </div>
        </div>
      </section>

      <section class="content-grid">
        <div class="left-col">
          <div class="section-header">
            <h3 class="section-title">My Cards</h3>
            <RouterLink to="/cards" class="view-all">View All</RouterLink>
          </div>

          <div class="card-list">
            <div
              v-for="cardItem in cards"
              :key=" cardItem.id"
              class="card-click-wrapper"
              @click.stop="goToCard(cardItem)"
              style="cursor: pointer;"
            >
              <Card
                :card="cardItem"
                :userName="user.name"
                :showMenu="false"
                :actions="cardActions(cardItem)"
                @action.stop="onCardAction"
                @block.stop="onToggleBlock"
              />
            </div>
          </div>
        </div>

        <aside class="right-col">
          <div class="activity">
            <div class="activity-header">
              <h3 class="section-title" >Latest Transactions</h3>
              <RouterLink to="/transactions" class="view-all">View All</RouterLink>
            </div>

            <ul class="tx-list">
              <li v-for="tx in transactions" :key="tx.id" class="tx">
                <div>
                  <div class="tx-title">
                    {{ tx.merchantName }}
                    <span v-if="tx.isBnpl" class="pill">BNPL</span>
                  </div>
                  <div class="tx-sub">
                    {{ tx.category || 'Other' }} • {{ formatDate(tx.date) }}
                  </div>
                  <div v-if="tx.processorName" class="processor-badge">{{ tx.processorName }}</div>
                </div>

                <div :class="['tx-amount', tx.amount > 0 ? 'danger' : '']">
                  -₹{{ formatNumber(Math.abs(tx.amount)) }}
                </div>

              </li>

              <li v-if="transactions.length === 0" class="tx">
                <div>
                  <div class="tx-title">No recent transactions</div>
                  <div class="tx-sub">You have no transaction history yet</div>
                </div>
                <div class="tx-amount">—</div>
              </li>
            </ul>
          </div>

          <div class="quick-actions">
            <h4>Quick Actions</h4>
            <div class="actions">
              <RouterLink to="/apply-card" class="action">+ Apply Card</RouterLink>
              <RouterLink to="/transactions" class="action">↗ New Transaction</RouterLink>
            </div>
          </div>
        </aside>
      </section>
    </main>
  </div>
</template>

<script>
import Card from "../../components/Card.vue";
import { getDashboard } from "../../services/dashboardService";
import { getCards } from "../../services/cards-service";

export default {
  name: "Dashboard",
  components: { Card },
  data() {
    return {
      user: { name: "User" },
      summary: {
        activeCards: 0,
        totalCards: 0,
        totalLimit: 0,
        availableCredit: 0,
        outstanding: 0,
      },
      cards: [],
      transactions: [],
      showFull: {},
      openMenu: null,
      loading: false,
      error: null,
    };
  },
  created() {
    this.loadDashboard();
    document.addEventListener("click", this.globalClick);
  },
  beforeUnmount() {
    document.removeEventListener("click", this.globalClick);
  },
  methods: {
    async loadDashboard() {
      this.loading = true;
      this.error = null;
      try {
        const data = await getDashboard();

        this.user.name = data.userName;
        this.summary = data.summary;
        this.transactions = data.transactions.map(t => this.normalizeTx(t));

        await this.loadCards();
      } catch (err) {
        console.error("Failed to load dashboard:", err);
        this.error = "Failed to load dashboard";
      } finally {
        this.loading = false;
      }
    },
    async loadCards() {
      try {
          const list = await getCards();
          const activeList = list.filter(c => (c.cardStatus || "").toUpperCase() === "ACTIVE");
          this.cards = activeList.map(c => ({
          id: c.id,
          cardNumber: c.cardNumber,
          cardHolderName: c.cardHolderName,
          creditLimit: c.creditLimit,
          availableLimit: c.availableLimit,
          cardStatus: c.cardStatus,
          expiryDate: c.expiryDate,
          cardType: c.cardType,
        }));

        this.cards.forEach(c => { this.showFull[c.id] = false; });
      } catch (err) {
        console.error("Failed to load cards:", err);
        this.cards = [];
      }
    },

    normalizeTx(tx) {
      return {
        id: tx.id,
        cardId: tx.cardId,
        merchantName: tx.merchantName,
        amount: tx.amount,
        date: tx.transactionDate,
        category: tx.category,
        isBnpl: tx.isBnpl,
        status: tx.status,
      };
    },

    formatNumber(value) {
      return Number(value).toLocaleString("en-IN");
    },

    formatDate(isoString) {
      const dateObj = new Date(isoString);
      return dateObj.toLocaleString("en-US", {
        day: "numeric",
        month: "short",
        hour: "2-digit",
        minute: "2-digit"
      });
    },

    toggleMenu(cardId) {
      this.openMenu = this.openMenu === cardId ? null : cardId;
    },

    onToggleBlock(card) {
      card.cardStatus = card.cardStatus === "BLOCKED" ? "ACTIVE" : "BLOCKED";
      this.openMenu = null;
    },

    globalClick(event) {
      if (!event.target.closest(".card-menu")) {
        this.openMenu = null;
      }
    },

    cardActions(card) {
      const id = card.id;
      return [
        { label: "View Details", to: `/cards/${id}` },
        { label: card.cardStatus === "BLOCKED" ? "Unblock" : "Block", value: { type: "toggle-block", cardId: id } }
      ];
    },

    goToCard(card) {
      const id = card.id;
      this.$router.push(`/cards/${id}`).catch(() => (window.location.href = `/cards/${id}`));
    },

    async onCardAction(payload) {
      const to = payload?.to;
      if (typeof to === "string") {
        try { await this.$router.push(to); } catch { window.location.href = to; }
      }
    }
  }
};
</script>
<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700;800&display=swap");
:root {
  --bg: #f7f7f9;
  --card: #ffffff;
  --muted: #7b7b85;
  --accent: #1f6feb;
}
* {
  box-sizing: border-box;
}
.dashboard {
  font-family: "Inter", system-ui, Arial;
  margin: 0;
  background: var(--bg);
  min-height: 100vh;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 36px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}
.brand .logo {
  font-size: 20px;
}
.brand .title {
  font-weight: 700;
}
.nav {
  display: flex;
  gap: 18px;
  color: var(--muted);
}
.nav a {
  cursor: pointer;
  text-decoration: none;
  color: inherit;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #0b2540;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.main h1 {
  font-size: 32px;
  margin: 8px 0;
}
.subtitle {
  color: var(--muted);
  margin-bottom: 22px;
}
.card-number-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 18px;
}
.card-number {
  font-size: 22px;
  letter-spacing: 4px;
  cursor: pointer;
  line-height: 1;
  margin: 0;
}
.card-eye {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: rgba(255,255,255,0.06);
  color: #fff;
  width: 30px;
  height: 30px;
  border-radius: 6px;
  cursor: pointer;
  padding: 0;
  font-size: 16px;
  line-height: 1;
}
.card-eye:hover {
  background: rgba(255,255,255,0.12);
}
.kpis {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}
.kpi {
  background: var(--card);
  padding: 18px;
  border-radius: 12px;
  box-shadow: 0 2px 0 rgba(20, 20, 20, 0.02);
}
.kpi-title {
  color: var(--muted);
  font-weight: 600;
}
.kpi-main {
  font-size: 20px;
  font-weight: 800;
  margin-top: 6px;
}
.kpi-sub {
  color: #9aa0a6;
  margin-top: 8px;
}
.kpi .success {
  color: #12a454;
}
.kpi .danger {
  color: #ff4d4f;
}
.content-grid {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 34px;
  margin-top: 26px;
  align-items: start;
}
.left-col {
  margin: 0 0 12px 6px;
}
.section-title {
  margin: 0 0 12px 6px;
  font-weight: 550;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.view-all {
  background: #ffd60a;
  border: 1px solid #eee;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 14px;
  text-decoration: none;
}
.card-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.card {
  background: #0f2b4a;
  padding: 24px;
  border-radius: 14px;
  color: #fff;
  min-height: 220px;
  position: relative;
  overflow: hidden;
  width: 560px;
}
.card.card-gradient {
  background: linear-gradient(135deg, #07112b 0%, #1a3a8a 40%, #1e6bf0 100%);
}
.card.blocked {
  opacity: 0.95;
}
.card-badge {
  position: absolute;
  left: 18px;
  top: 18px;
  background: rgba(255, 255, 255, 0.08);
  padding: 6px 10px;
  border-radius: 8px;
  font-weight: 700;
}
.card-chip {
  position: absolute;
  right: 48px;
  top: 18px;
  background: rgba(255, 255, 255, 0.12);
  padding: 8px 12px;
  border-radius: 8px;
  font-weight: 700;
}
.card-menu {
  position: absolute;
  right: 18px;
  top: 18px;
  cursor: pointer;
  user-select: none;
}
.card-eye {
  position: absolute;
  left: 18px;
  top: 56px;
  opacity: 0.9;
}
.card-logo {
  margin-top: 12px;
  font-size: 24px;
}
.card-logo img {
  height: 22px;
  display: inline-block;
}
.card-number {
  font-size: 22px;
  letter-spacing: 4px;
  margin-top: 12px;
  cursor: pointer;
}
.card-row {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
}
.small {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 1px;
}
.bold {
  font-weight: 700;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 12px;
}
.amount {
  font-weight: 700;
}
.amount.success {
  color: #25c36a;
}
.right-col .activity {
  background: var(--card);
  padding: 18px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(20, 20, 20, 0.04);
}
.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.activity-header h3 {
  margin: 0;
}
.tx-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.tx {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f2f2f4;
}
.tx:last-child {
  border-bottom: 0;
}
.tx-title {
  font-weight: 700;
}
.tx-sub {
  font-size: 13px;
  color: var(--muted);
}
.tx-amount.danger {
  color: #ff4d4f;
  font-weight: 700;
}
.pill {
  background: #f0f4f8;
  border-radius: 12px;
  padding: 4px 8px;
  margin-left: 6px;
  font-size: 12px;
  color: #333;
}
.quick-actions {
  margin-top: 20px;
}
.quick-actions h4 {
  margin-bottom: 12px;
}
.actions {
  display: flex;
  gap: 12px;
}
.action {
  flex: 1;
  padding: 14px;
  border-radius: 10px;
  border: 1px solid #eee;
  background:#ffd60a;
  text-decoration: none;
  color: inherit;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.dropdown {
  position: absolute;
  right: 0;
  top: 28px;
  background: white;
  color: black;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 6px 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  z-index: 10;
}
.dropdown a,
.dropdown button {
  background: none;
  border: none;
  padding: 4px;
  text-align: left;
  cursor: pointer;
}
@media (max-width: 980px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
  .kpis {
    grid-template-columns: repeat(2, 1fr);
  }
  .card {
    width: 100%;
  }
  html, body {
    max-width: 100%;
    overflow-x: hidden;
  }
  .card {
    width: 100%;
    max-width: 100%;
    box-sizing: border-box;
  }
  .container {
    max-width: 100%;
    overflow-x: hidden;
  }
}
</style>
