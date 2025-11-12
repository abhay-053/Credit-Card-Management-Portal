<template>
  <div class="applications">
    <main class="container">
      <section class="header">
        <div>
          <h1>My Applications</h1>
          <p class="subtitle">Track your credit card application status</p>
        </div>
        <RouterLink to="/apply-card" class="btn-primary">
          + Apply New Card
        </RouterLink>
      </section>

      <div v-if="isLoading">Loading applications...</div>
      <div v-else-if="error" class="error">{{ error }}</div>

      <section v-else class="application-list">
        <div
          v-for="app in allApplications"
          :key="app.id"
          class="application-card"
        >
          <div class="card-header">
            <div class="title">
              <span class="icon">💳</span>
              <h3>{{ app.cardTypeName }}</h3>
            </div>
            <div class="applied-date">
              <span>📅 Applied: {{ app.appliedDate }}</span>
            </div>
          </div>

          <div class="card-body">
            <p><strong>Application ID:</strong> #{{ app.id }}</p>
            <p><strong>Card Network:</strong> {{ app.cardNetworkType }}</p>
            <p>
              <strong>Requested Credit Limit:</strong>
              ₹{{ app.requestedLimit.toLocaleString() }}
            </p>

            <div class="status-row">
              <p class="status-text">
                <strong>Status:</strong>
                <span class="status-pill">{{ app.status }}</span>
              </p>

              <button
                v-if="app.status === 'PENDING'"
                class="btn-takeback"
                @click="handleTakeBack(app.id)"
                :disabled="takingBackId === app.id"
              >
                {{ takingBackId === app.id ? "Taking Back..." : "Take Back" }}
              </button>
            </div>

            <div class="timeline">
              <p>• Application submitted on {{ app.appliedDate }}</p>
              <p v-if="app.reviewerName">
                • Reviewed by <strong>{{ app.reviewerName }}</strong>
                on {{ app.reviewDate }}
              </p>
              <p class="muted">
                • {{ app.status === "UnderReview"
                  ? "Under review – typically takes 2-3 business days"
                  : "Final decision reached" }}
              </p>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import { deleteApplication } from "./../../services/cardApplicationService";

export default {
  name: "MyApplicationsView",

  data() {
    return {
      takingBackId: null,
    };
  },

  computed: {
    ...mapGetters("userApplications", [
      "allApplications",
      "isLoading",
      "error",
    ]),
  },

  created() {
    if(this.allApplications.length === 0) this.fetchAll();
  },

  methods: {
    ...mapActions("userApplications", ["fetchAll"]),

    async handleTakeBack(id) {
      const confirmDelete = confirm(
        "Are you sure you want to take back this application?"
      );
      if (!confirmDelete) return;

      this.takingBackId = id;
      try {
        await deleteApplication(id);
        alert("Application taken back successfully!");
        await this.fetchAll();
      } catch (err) {
        console.error("Error taking back application:", err);
        alert("Failed to take back the application. Please try again.");
      } finally {
        this.takingBackId = null;
      }
    },
  },
};
</script>

<style scoped>
.applications {
  font-family: "Inter", system-ui, Arial;
  min-height: 100vh;
}

.container {
  max-width: 1180px;
  margin: 28px auto;
  padding: 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.subtitle {
  color: #7b7b85;
}

.btn-primary {
  background: #0b0d18;
  color: #fff;
  text-decoration: none;
  padding: 10px 18px;
  border-radius: 8px;
  font-weight: 600;
}

.application-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-card {
  background: #f7f7f9;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.status-text {
  margin: 0;
}

.status-pill {
  background: #0b0d18;
  color: #fff;
  padding: 2px 10px;
  font-size: 12px;
  border-radius: 12px;
  margin-left: 6px;
}

.btn-takeback {
  background: #ff3b30;
  color: #fff;
  border: none;
  padding: 8px 14px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.btn-takeback:hover:not(:disabled) {
  background: #e32d22;
}

.btn-takeback:disabled {
  background: #aaa;
  cursor: not-allowed;
}

.timeline {
  margin-top: 12px;
}

.timeline .muted {
  color: #7b7b85;
}

.applied-date {
  color: #7b7b85;
  font-size: 14px;
}
</style>