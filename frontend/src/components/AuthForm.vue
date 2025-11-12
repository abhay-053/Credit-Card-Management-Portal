<template>
  <div class="auth-container">
    <h2>{{ isLogin ? 'Sign In' : 'Sign Up' }}</h2>
    <form @submit.prevent="handleSubmit">
      <div v-if="!isLogin">
        <input v-model="form.name" placeholder="Full Name" required />
        <input v-model="form.phoneNumber" placeholder="Phone Number" required />
        <input v-model="form.address" placeholder="Address" required />
        <input v-model.number="form.annualIncome" placeholder="Annual Income" required type="number" />
      </div>
      <input v-model="form.email" placeholder="Email" required type="email" />
      <input v-model="form.password" placeholder="Password" required type="password" />

      <div v-if="!isLogin">
        <input v-model="form.confirmPassword" placeholder="Confirm Password" required type="password" />
        <p v-if="form.confirmPassword && form.password !== form.confirmPassword" class="error">
          Passwords do not match
        </p>
      </div>

      <button type="submit">{{ isLogin ? 'Sign In' : 'Sign Up' }}</button>
    </form>

    <p class="toggle">
      {{ isLogin ? "Don't have an account?" : "Already have an account?" }}
      <span @click="toggleForm">{{ isLogin ? 'Sign Up' : 'Sign In' }}</span>
    </p>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default defineComponent({
  setup() {
    const store = useStore();
    const router = useRouter();
    const isLogin = ref(true);

    const form = reactive({
      name: '',
      email: '',
      password: '',
      confirmPassword: '',
      phoneNumber: '',
      address: '',
      annualIncome: 0
    });

    const toggleForm = () => {
      isLogin.value = !isLogin.value;
    };

    const handleSubmit = async () => {
      if (!isLogin.value && form.password !== form.confirmPassword) return;

      if (isLogin.value) {
        await store.dispatch('auth/login', {
          email: form.email,
          password: form.password
        });
      } else {
        await store.dispatch('auth/register', form);
      }

      router.push('/dashboard');
    };

    return { form, isLogin, toggleForm, handleSubmit };
  }
});
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
}

input {
  display: block;
  width: 100%;
  margin: 0.5rem 0;
  padding: 0.5rem;
}

button {
  width: 100%;
  padding: 0.7rem;
  background-color: #007bff;
  color: white;
  border: none;
  margin-top: 1rem;
}

.toggle {
  margin-top: 1rem;
  text-align: center;
}

.toggle span {
  color: #007bff;
  cursor: pointer;
  font-weight: bold;
}

.error {
  color: red;
  font-size: 0.9rem;
}
</style>
