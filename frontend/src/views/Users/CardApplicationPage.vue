<template>
  <div class="apply-card">
    <main class="container">

    
      <section class="hero">
        <div class="hero-header">
          <h1>Apply for Credit Card</h1>
          <RouterLink to="/my-applications" class="btn-history">
            Application History
          </RouterLink>
        </div>
      </section>

     
      <section class="card-selection">
        <h2>Select Your Card</h2>
        <p class="muted">Choose the credit card that best fits your lifestyle</p>

  
        <div v-if="isLoading">Loading card types...</div>
        <div v-else-if="error" class="error">{{ error }}</div>


        <div v-else class="card-options">
          <div
            v-for="card in allCardTypes"
            :key="card.id"
            :class="['option-card', { selected: selectedCard?.id === card.id }]"
            @click="selectCard(card)"
          >
            <div class="option-header">
              <span class="icon">{{ card.networkType === 'AMEX' ? '⭐' : '💳' }}</span>
              <span v-if="selectedCard?.id === card.id" class="checkmark">✔</span>
            </div>

            <h3>{{ card.name }}</h3>
            <p class="muted">{{ card.description }}</p>
            <p class="limit">
              Credit Limit ₹{{ card.minCardLimit.toLocaleString() }}
              – ₹{{ card.maxCardLimit.toLocaleString() }}
            </p>
            <div class="tags">
              <span class="tag">{{ card.networkType }}</span>
            </div>
          </div>
        </div>
      </section>

   
      <section v-if="selectedCard" class="application-form">
        <h2>{{ selectedCard.name }} Application</h2>
        <p class="muted">Complete your application details</p>

        <form @submit.prevent="submitApplication">
          <!-- <div class="form-row">
            <label>Requested Credit Limit</label>
            <input
              type="number"
              v-model="form.creditLimit"
              :min="selectedCard.minCardLimit"
              :max="selectedCard.maxCardLimit"
              required
            />
            <small>
              Range: ₹{{ selectedCard.minCardLimit }} – ₹{{ selectedCard.maxCardLimit }}
            </small>
          </div> -->
          <div class="form-row">
            <label>Requested Credit Limit</label>
            <input
              type="number"
              v-model="form.creditLimit"
              :min="selectedCard.minCardLimit"
              :max="selectedCard.maxCardLimit"
              :readonly="true"      
              class="locked-limit"
            />
            <small>
              Fixed at ₹{{ selectedCard.minCardLimit }}
            </small>
          </div>


          <div class="form-row info" v-if="user">
            <h4>Applicant Information</h4>
            <p><strong>Name:</strong> {{ user.name }}</p>
            <p><strong>Email:</strong> {{ user.email }}</p>
            <p><strong>BNPL Eligible:</strong> {{ user.bnplEligible ? "Yes" : "No" }}</p>
          </div>

          <button type="submit" class="submit-btn">Submit Application</button>
        </form>
      </section>
    </main>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import { applyForCard } from "../../services/cardApplicationService";
import { getUserProfile } from "../../services/userService";
import { getUserIdFromToken } from "../../utils/getTokenData";

export default {
  name: "ApplyCardView",

  data() {
    return {
      user: null,
      selectedCard: null,
      form: {
        creditLimit: "",
      },
    };
  },

  computed: {
    ...mapGetters("cardTypes", ["allCardTypes", "isLoading", "error"]),
  },

  async created() {
    try {
     
      const userId = getUserIdFromToken();
      if (userId) {
        const { data } = await getUserProfile(userId);
        this.user = data;
      }

      if (this.allCardTypes.length === 0) {
        await this.fetchCardTypes();
      }
    } catch (err) {
      console.error("Error fetching user or cards:", err);
    }
  },

  methods: {
    ...mapActions("cardTypes", ["fetchCardTypes"]),
    ...mapActions("userApplications", ["fetchAll"]), 

  selectCard(card) {
    this.selectedCard = card;
    this.form.creditLimit = card.minCardLimit;
  },

    async submitApplication() {
      const cardApplication = {
        userId: this.user.id,
        cardTypeId: this.selectedCard.id,
        requestLimit: this.form.creditLimit,
      };

      try {
        await applyForCard(cardApplication);
        await this.fetchAll();

        alert(
          ` Applied for ${this.selectedCard.name} with credit limit ₹${this.form.creditLimit}`
        );

        this.$router.push("/my-applications");
      } catch (err) {
        console.error("Application failed:", err);

        const backendMessage =
          err.response?.data?.message ||        
          err.response?.data?.error ||           
          err.response?.data?.message?.message || 
          "Something went wrong while submitting your application.";

        alert(`❗ ${backendMessage}`);
      }
    }

  },
};
</script>

<style scoped>
.apply-card {
  font-family: "Inter", system-ui, Arial;
  min-height: 100vh;
}

.container {
  max-width: 1180px;
  margin: 28px auto;
  padding: 0 20px;
}

.hero h1 {
  font-size: 28px;
  margin-bottom: 6px;
}

.subtitle,
.muted {
  color: #7b7b85;
  margin-bottom: 20px;
}

.card-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.option-card {
  background: #f7f7f9;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border 0.2s;
}

.option-card.selected {
  border-color: #1f6feb;
}

.option-card h3 {
  margin: 10px 0;
}

.limit {
  margin: 10px 0;
  font-weight: 600;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: #f0f4f8;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
}

.checkmark {
  color: #1f6feb;
  font-weight: bold;
}

.application-form {
  margin-top: 40px;
  background: #f7f7f9;
  padding: 20px;
  border-radius: 12px;
}

.form-row {
  margin-bottom: 16px;
}

.form-row input {
  display: block;
  padding: 10px;
  width: 100%;
  margin-top: 6px;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.form-row small {
  color: #7b7b85;
}

.submit-btn {
  background: #1f6feb;
  color: #fff;
  border: none;
  padding: 12px 18px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.hero-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.btn-history {
  background: #ffd60a;
  color: #0b2540;
  font-weight: 600;
  padding: 10px 18px;
  font-size: 15px;
  border-radius: 8px;
  border: 2px solid #ffd60a;
  text-decoration: none;
  transition: all 0.2s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.btn-history:hover {
  background: #e5cd56;
  border-color: #e5cd56;
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.18);
}

.btn-history:focus {
  outline: 2px solid rgba(31, 111, 235, 0.3);
  outline-offset: 3px;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>
